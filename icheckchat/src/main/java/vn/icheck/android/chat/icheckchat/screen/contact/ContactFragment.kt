package vn.icheck.android.chat.icheckchat.screen.contact

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import vn.icheck.android.chat.icheckchat.base.BaseFragmentChat
import vn.icheck.android.chat.icheckchat.base.ConstantChat.USER_ID
import vn.icheck.android.chat.icheckchat.base.recyclerview.IRecyclerViewCallback
import vn.icheck.android.chat.icheckchat.base.view.setGone
import vn.icheck.android.chat.icheckchat.base.view.setVisible
import vn.icheck.android.chat.icheckchat.base.view.showToastError
import vn.icheck.android.chat.icheckchat.databinding.FragmentContactBinding
import vn.icheck.android.chat.icheckchat.dialog.ConfirmContactDialog
import vn.icheck.android.chat.icheckchat.helper.NetworkHelper.LIMIT
import vn.icheck.android.chat.icheckchat.helper.ShareHelperChat
import vn.icheck.android.chat.icheckchat.model.MCStatus
import java.util.*

class ContactFragment : BaseFragmentChat<FragmentContactBinding>(), IRecyclerViewCallback {

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

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentContactBinding {
        return FragmentContactBinding.inflate(inflater, container, false)
    }

    override fun onInitView() {
        viewModel = ViewModelProvider(this@ContactFragment)[ContactViewModel::class.java]

        initRecyclerView()
        initSwipeLayout()

        binding.btnRequest.setOnClickListener {
            showDialog()
        }
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter
    }

    private fun initSwipeLayout() {
        binding.swipeRefresh.post {
            if (requestContact()){
                getData()
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            if (requestContact()){
                getData()
            }
        }
    }

    private fun getData() {
        binding.swipeRefresh.isRefreshing = true

        viewModel.getContact(getContacts()).observe(this@ContactFragment, {
            when (it.status) {
                MCStatus.ERROR_NETWORK -> {
                    requireContext().showToastError(it.message)
                }
                MCStatus.ERROR_REQUEST -> {
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

                            adapter.setListData(it.data?.data?.rows ?: mutableListOf())
                        } else {
                            binding.recyclerView.setGone()
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
            //TODO mở Web View
        }) {

        }.show()
    }

    override fun onMessageClicked() {

    }

    override fun onLoadMore() {
        getListFriend(true)
    }
}