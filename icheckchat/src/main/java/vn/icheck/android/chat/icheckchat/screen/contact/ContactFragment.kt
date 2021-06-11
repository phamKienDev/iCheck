package vn.icheck.android.chat.icheckchat.screen.contact

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import vn.icheck.android.chat.icheckchat.R
import vn.icheck.android.chat.icheckchat.base.BaseFragmentChat
import vn.icheck.android.chat.icheckchat.base.ConstantChat
import vn.icheck.android.chat.icheckchat.base.ConstantChat.USER_ID
import vn.icheck.android.chat.icheckchat.base.recyclerview.IRecyclerViewCallback
import vn.icheck.android.chat.icheckchat.base.view.*
import vn.icheck.android.chat.icheckchat.databinding.FragmentContactBinding
import vn.icheck.android.chat.icheckchat.dialog.ConfirmContactDialog
import vn.icheck.android.chat.icheckchat.helper.NetworkHelper.LIMIT
import vn.icheck.android.chat.icheckchat.helper.ShareHelperChat
import vn.icheck.android.chat.icheckchat.model.MCStatus
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.util.PermissionHelper
import java.util.*

class ContactFragment : BaseFragmentChat<FragmentContactBinding>(), IRecyclerViewCallback {
    private var clickGetData = false
    private var isCreated = false

    companion object {
        const val REQUEST_CONTACT = 1
        private val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        fun newInstance(isUserLogged: Boolean): ContactFragment {
            val fragment = ContactFragment()

            val bundle = Bundle()
            bundle.putBoolean(Constant.DATA_1, isUserLogged)
            fragment.arguments = bundle

            return fragment
        }
    }

    private val adapter = ContactAdapter(this)

    private val viewModel by viewModels<ContactViewModel>()

    var offset = 0

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentContactBinding {
        return FragmentContactBinding.inflate(inflater, container, false)
    }

    override fun onInitView() {

    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter
    }

    private fun initSwipeLayout() {
        binding.swipeRefresh.post {
            getData()
        }

        binding.swipeRefresh.setOnRefreshListener {
            clickGetData = false
            getData()
        }
    }

    private fun requestContact(): Boolean {
        return if (PermissionHelper.isAllowPermission(requireContext(), Manifest.permission.READ_CONTACTS)) {
            true
        } else {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CONTACT)
            false
        }
    }

    private fun checkSyncContact() {
        if (requestContact()) {
            showDialog()
        }
    }

    private fun showDialog() {
        object : ConfirmContactDialog(requireContext(), {
            clickGetData = true
            if (ShareHelperChat.getBoolean(ConstantChat.USER_LOGIN)) {
                syncContact()
            } else {
                requireContext().showToastError("Bạn chưa đăng nhập! Vui lòng đăng nhập!")
            }
        }, {
            getSystemSetting()
        }) {}.show()
    }

    private fun syncContact() {
        if (!requestContact()){
            clickGetData = false
            return
        }

        binding.swipeRefresh.isRefreshing = true

        viewModel.getContact(getContacts()).observe(this@ContactFragment, {
            when (it.status) {
                MCStatus.ERROR_NETWORK -> {
                    binding.swipeRefresh.isRefreshing = false
                    requireContext().showToastError(it.message)
                    clickGetData = false
                }
                MCStatus.ERROR_REQUEST -> {
                    binding.swipeRefresh.isRefreshing = false
                    requireContext().showToastError(it.message)
                    clickGetData = false
                }
                MCStatus.SUCCESS -> {
                    getData()
                }
            }
        })
    }

    private fun getContacts(): ArrayList<String> {
        val contentResolver: ContentResolver = requireActivity().contentResolver
        val allContacts = ArrayList<String>()
        contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null)?.use { cursor ->
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (cursor.moveToNext()) {
                val number = cursor.getString(numberIndex)
                allContacts.add(number)
            }
        }
        return allContacts
    }

    private fun getData(isLoadMore: Boolean = false) {
        if (!isLoadMore) {
            offset = 0
        }

        viewModel.getListFriend(ShareHelperChat.getLong(USER_ID), offset).observe(this@ContactFragment, {
            binding.swipeRefresh.isRefreshing = false

            when (it.status) {
                MCStatus.ERROR_NETWORK -> {
                    requireContext().showToastError(it.message)
                }
                MCStatus.ERROR_REQUEST -> {
                    requireContext().showToastError(it.message)
                }
                MCStatus.SUCCESS -> {
                    offset += LIMIT

                    if (!isLoadMore) {
                        if (!it.data?.data?.rows.isNullOrEmpty()) {
                            binding.recyclerView.setVisible()
                            binding.layoutNoData.setGone()
                            binding.btnMergeRequest.setVisible()

                            adapter.setListData(it.data?.data?.rows ?: mutableListOf())

                            if (clickGetData) {
                                requireContext().showToastSuccess(getString(R.string.dong_bo_danh_ba_thanh_cong))
                            }
                        } else {
                            binding.recyclerView.setGone()
                            binding.btnMergeRequest.setGone()
                            binding.layoutNoData.setVisible()
                        }
                    } else {
                        adapter.addListData(it.data?.data?.rows ?: mutableListOf())
                    }
                }
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CONTACT) {
            if (PermissionHelper.checkResult(grantResults)) {
                showDialog()
            } else {
                requireContext().showToastError("Bạn chưa cấp đủ quyền!")
            }
        }
    }

    private fun getSystemSetting() {
        viewModel.getSystemSetting("app-support.privacy-url", "app-support").observe(this@ContactFragment, {
            when (it.status) {
                MCStatus.ERROR_NETWORK -> {
                    requireContext().showToastError(it.message)
                }
                MCStatus.ERROR_REQUEST -> {
                    requireContext().showToastError(it.message)
                }
                MCStatus.SUCCESS -> {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.data?.data?.rows?.firstOrNull()?.value)))
                }
            }
        })
    }

    override fun onMessageClicked() {

    }

    override fun onLoadMore() {
        getData(true)
    }

    fun checkLoginOrLogOut(isLogin: Boolean) {
        if (!isLogin) {
            binding.swipeRefresh.isEnabled = false

            if (ShareHelperChat.getBoolean(ConstantChat.USER_LOGIN)) {
                binding.recyclerView.setGone()
                binding.btnMergeRequest.setGone()
                setVisibleView(binding.layoutNoData, binding.btnRequest, binding.tvMessageContact)
            } else {
                setGoneView(binding.recyclerView, binding.btnMergeRequest, binding.tvMessageContact, binding.btnRequest)
                binding.layoutNoData.setVisible()
            }
        } else {
            binding.swipeRefresh.isEnabled = true
            getData()
        }
    }

    override fun onResume() {
        super.onResume()

        if (!isCreated) {
            isCreated = true

            if (ShareHelperChat.getBoolean(ConstantChat.USER_LOGIN)) {
                binding.swipeRefresh.isEnabled = true

                binding.recyclerView.setGone()
                binding.btnMergeRequest.setGone()

                setVisibleView(binding.layoutNoData, binding.btnRequest, binding.tvMessageContact)
            } else {
                binding.swipeRefresh.isEnabled = false

                setGoneView(binding.recyclerView, binding.btnMergeRequest, binding.tvMessageContact, binding.btnRequest)

                binding.layoutNoData.setVisible()
            }

            initRecyclerView()
            initSwipeLayout()

            binding.btnRequest.setOnClickListener {
                checkSyncContact()
            }

            binding.btnMergeRequest.setOnClickListener {
                checkSyncContact()
            }
        }
    }
}