package vn.icheck.android.screen.user.social_chat

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import vn.icheck.android.callback.ISettingListener
import vn.icheck.android.databinding.FragmentPhoneBookBinding
import vn.icheck.android.helper.SettingHelper
import vn.icheck.android.network.models.ICClientSetting
import vn.icheck.android.screen.user.social_chat.popup.RequestContactPopup
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.ick.logDebug
import java.util.ArrayList

class SocialPhoneBookFragment:Fragment() {
    companion object{
        const val REQUEST_CONTACT = 1
        private val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        )
    }

    private var _binding: FragmentPhoneBookBinding? = null
    private val binding get() = _binding!!
    private val socialChatViewModel by activityViewModels<SocialChatViewModel>()
    val phoneBookAdapter = PhoneBookAdapter()
    var dialog:RequestContactPopup? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentPhoneBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rcvPhoneBook.adapter = phoneBookAdapter
        if (socialChatViewModel.getSyncPhoneBook()) {
            showSynced()
            lifecycleScope.launch {
                socialChatViewModel.getListFriend().collectLatest {
                    phoneBookAdapter.submitData(it)
                }
            }
        } else {
            showNotSynced()
        }
    }

    private fun showConfirmSync() {
        if (dialog == null || dialog?.isShowing == false) {
            dialog = RequestContactPopup(requireContext(), {
                if (requestContact()) {
                    socialChatViewModel.setSyncPhoneBook()
                    showSynced()
                    getContacts().let {
                        socialChatViewModel.syncPhoneBook(it).observe(viewLifecycleOwner, {
                            lifecycleScope.launch {
                                socialChatViewModel.getListFriend().collectLatest {
                                    phoneBookAdapter.submitData(it)
                                }
                            }
                        })
                    }
                } else {
                    showNotSynced()
                }
            },{
                SettingHelper.getSystemSetting("app-support.privacy-url", "app-support", object : ISettingListener {
                    override fun onRequestError(error: String) {
                        logDebug(error)
                    }

                    override fun onGetClientSuccess(list: MutableList<ICClientSetting>?) {
                        WebViewActivity.start(requireActivity(), list?.firstOrNull()?.value, null, "Điều khoản sử dụng")
                    }
                })
            })
            dialog?.show()
        }

//        DialogHelper.showConfirm(requireActivity(),
//                "Đồng bộ danh bạ",
//                "iCheck muốn truy cập danh bạ của bạn và đồng bộ lên máy chủ phục vụ cho việc gợi ý kết bạn và trò chuyện",
//                "Để sau", "Đồng ý", true, null, R.color.lightBlue, object : ConfirmDialogListener {
//            override fun onDisagree() {
//
//            }
//
//            override fun onAgree() {
//                if (requestContact()) {
//                    socialChatViewModel.setSyncPhoneBook()
//                    showSynced()
//                    getContacts().let {
//                        socialChatViewModel.syncPhoneBook(it).observe(viewLifecycleOwner, {
//                            lifecycleScope.launch {
//                                socialChatViewModel.getListFriend().collectLatest {
//                                    phoneBookAdapter.submitData(it)
//                                }
//                            }
//                        })
//                    }
//                } else {
//                    showNotSynced()
//                }
//            }
//        })
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

    private fun showNotSynced() {
        binding.tvSyncPhoneBook.beVisible()
        binding.tvNoResult.beVisible()
        binding.tvNoResultDesc.beVisible()
        binding.tvSyncPhoneBookRcv.beGone()
        binding.tvSyncPhoneBookRcv.beGone()
        binding.rcvPhoneBook.beGone()
        binding.tvSyncPhoneBook.setOnClickListener {
            showConfirmSync()
        }
    }

    private fun showSynced() {
        binding.tvSyncPhoneBook.beGone()
        binding.tvNoResult.beGone()
        binding.rcvPhoneBook.beVisible()
        binding.tvNoResultDesc.beGone()
        binding.tvSyncPhoneBookRcv.beVisible()
        binding.tvSyncPhoneBookRcv.beVisible()
        binding.tvSyncPhoneBook.setOnClickListener {
            showConfirmSync()
        }
        binding.tvSyncPhoneBookRcv.setOnClickListener {
            showConfirmSync()
        }

    }

    private fun requestContact():Boolean {
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

}