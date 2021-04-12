package vn.icheck.android.chat.icheckchat.screen.contact

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import vn.icheck.android.chat.icheckchat.R
import vn.icheck.android.chat.icheckchat.base.BaseFragmentChat
import vn.icheck.android.chat.icheckchat.base.ConstantChat.USER_ID
import vn.icheck.android.chat.icheckchat.base.recyclerview.IRecyclerViewCallback
import vn.icheck.android.chat.icheckchat.base.view.*
import vn.icheck.android.chat.icheckchat.databinding.FragmentContactBinding
import vn.icheck.android.chat.icheckchat.dialog.ConfirmContactDialog
import vn.icheck.android.chat.icheckchat.helper.NetworkHelper.LIMIT
import vn.icheck.android.chat.icheckchat.helper.ShareHelperChat
import vn.icheck.android.chat.icheckchat.model.MCStatus
import java.util.*

class ContactFragment(val isUserLogged: Boolean) : BaseFragmentChat<FragmentContactBinding>(), IRecyclerViewCallback {

    companion object {
        const val REQUEST_CONTACT = 1
        private val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        )
    }

    private val adapter = ContactAdapter(this)

    private lateinit var viewModel: ContactViewModel

    var offset = 0

    var isCreated = false

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentContactBinding {
        return FragmentContactBinding.inflate(inflater, container, false)
    }

    override fun onInitView() {
        viewModel = ViewModelProvider(this@ContactFragment)[ContactViewModel::class.java]

        if (isUserLogged) {
            binding.recyclerView.setGone()
            binding.btnMergeRequest.setGone()

            setVisibleView(binding.layoutNoData, binding.btnRequest, binding.tvMessageContact)
        } else {
            setGoneView(binding.recyclerView, binding.btnMergeRequest, binding.tvMessageContact, binding.btnRequest)

            binding.layoutNoData.setVisible()
        }

        initRecyclerView()

        binding.btnRequest.setOnClickListener {
            showDialog()
        }

        binding.btnMergeRequest.setOnClickListener {
            showDialog()
        }
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter
    }

    private fun initSwipeLayout() {
        binding.swipeRefresh.post {
            if (requestContact()) {
                getData()
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            if (requestContact()) {
                getData()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (!isCreated) {
            isCreated = true
            initSwipeLayout()
        }
    }

    private fun getData() {
        binding.swipeRefresh.isRefreshing = true

        viewModel.getContact(getContacts()).observe(this@ContactFragment, {
            when (it.status) {
                MCStatus.ERROR_NETWORK -> {
                    binding.swipeRefresh.isRefreshing = false
                    requireContext().showToastError(it.message)
                }
                MCStatus.ERROR_REQUEST -> {
                    binding.swipeRefresh.isRefreshing = false
                    requireContext().showToastError(it.message)
                }
                MCStatus.SUCCESS -> {
                    getListFriend()
                }
            }
        })
    }

    private fun getListFriend(isLoadMore: Boolean = false) {
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
                            requireContext().showToastSuccess(getString(R.string.dong_bo_danh_ba_thanh_cong))
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

    private fun requestContact(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.READ_CONTACTS
                ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CONTACT)
            false
        } else {
            true
        }
    }

    private fun showDialog() {
        object : ConfirmContactDialog(requireContext(), {
            initSwipeLayout()
        }, {
            getSystemSetting()
        }) {

        }.show()
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
        getListFriend(true)
    }

    fun checkLoginOrLogOut(isLogin: Boolean) {
        if (!isLogin) {
            if (isUserLogged) {
                binding.recyclerView.setGone()
                binding.btnMergeRequest.setGone()
                binding.tvMessageContact.setVisible()
                binding.btnRequest.setVisible()
                binding.layoutNoData.setVisible()
            } else {
                binding.recyclerView.setGone()
                binding.btnMergeRequest.setGone()
                binding.tvMessageContact.setGone()
                binding.btnRequest.setGone()
                binding.layoutNoData.setVisible()
            }
        } else {
            if (requestContact()) {
                getData()
            }
        }
    }
}