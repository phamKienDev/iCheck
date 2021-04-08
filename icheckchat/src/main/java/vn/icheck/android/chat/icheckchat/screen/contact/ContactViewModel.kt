package vn.icheck.android.chat.icheckchat.screen.contact

import vn.icheck.android.chat.icheckchat.base.BaseViewModelChat
import vn.icheck.android.chat.icheckchat.network.ChatRepository

class ContactViewModel : BaseViewModelChat() {
    private val repository = ChatRepository()

    fun getContact(phoneList: MutableList<String>) = request { repository.getContact(phoneList) }

    fun getListFriend(id: Long, offset: Int) = request { repository.getListFriend(id, offset) }

    fun getSystemSetting(key: String?, keyGroup: String?) = request { repository.getSystemSetting(key, keyGroup) }
}