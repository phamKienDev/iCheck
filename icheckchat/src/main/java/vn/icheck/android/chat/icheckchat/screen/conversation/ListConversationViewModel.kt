package vn.icheck.android.chat.icheckchat.screen.conversation

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import vn.icheck.android.chat.icheckchat.base.BaseViewModelChat
import vn.icheck.android.chat.icheckchat.network.ChatRepository

class ListConversationViewModel : BaseViewModelChat() {
    private val repository = ChatRepository()

    val conversationError = MutableLiveData<String>()

    fun getConversation(isLoadMore: Boolean = false, success: (snapshot: DataSnapshot) -> Unit, cancel: (error: DatabaseError) -> Unit) = firebaseHelper.getConversation(isLoadMore, success, cancel)

    fun getChatSender(child: String, success: (snapshot: DataSnapshot) -> Unit, cancel: (error: DatabaseError) -> Unit) = firebaseHelper.getChatSender(child, success, cancel)

    fun getChatRoom(key: String, success: (snapshot: DataSnapshot) -> Unit, cancel: (error: DatabaseError) -> Unit) = firebaseHelper.getChatRoom(key, success, cancel)
}