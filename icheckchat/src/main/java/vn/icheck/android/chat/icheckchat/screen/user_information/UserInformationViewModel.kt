package vn.icheck.android.chat.icheckchat.screen.user_information

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import vn.icheck.android.chat.icheckchat.base.BaseViewModelChat
import vn.icheck.android.chat.icheckchat.network.ChatRepository

class UserInformationViewModel : BaseViewModelChat() {
    private val repository = ChatRepository()

    fun getImage(lastTimeStamp: Long, key: String, success: (snapshot: DataSnapshot) -> Unit, cancel: (error: DatabaseError) -> Unit) = firebaseHelper.getImageChatDetail(lastTimeStamp, key, success, cancel)

    fun getChatRoom(key: String, success: (snapshot: DataSnapshot) -> Unit, cancel: (error: DatabaseError) -> Unit) = firebaseHelper.getChatRoom(key, success, cancel)

    fun getChatSender(key: String, success: (snapshot: DataSnapshot) -> Unit, cancel: (error: DatabaseError) -> Unit) = firebaseHelper.getChatSender(key, success, cancel)

    fun deleteConversation(key: String) = request { repository.deleteConversation(key) }

    fun blockMessage(key: String, toId: String, toType: String) = request { repository.blockMessage(key, toId, toType) }

    fun turnOffNotification(key: String, memberId: String, memberType: String) = request { repository.turnOffNotification(key, memberId, memberType) }

    fun turnOnNotification(key: String, memberId: String, memberType: String) = request { repository.turnOnNotification(key, memberId, memberType) }
}