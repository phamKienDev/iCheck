package vn.icheck.android.chat.icheckchat.screen.conversation

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import vn.icheck.android.chat.icheckchat.base.BaseViewModelChat
import vn.icheck.android.chat.icheckchat.network.ChatRepository

class ListConversationViewModel : BaseViewModelChat() {

    val conversationError = MutableLiveData<String>()

    fun getConversation(lastTimeStamp: Long, success: (snapshot: DataSnapshot) -> Unit, cancel: (error: DatabaseError) -> Unit) = firebaseHelper.getConversation(lastTimeStamp, success, cancel)

    fun getChangeConversation(onAdd: (snapshot: DataSnapshot) -> Unit) = firebaseHelper.getChangeConversation(onAdd)

    fun getChatSender(child: String, success: (snapshot: DataSnapshot) -> Unit, cancel: (error: DatabaseError) -> Unit) = firebaseHelper.getChatSender(child, success, cancel)

    fun getChatRoom(key: String, success: (snapshot: DataSnapshot) -> Unit, cancel: (error: DatabaseError) -> Unit) = firebaseHelper.getChatRoom(key, success, cancel)
}