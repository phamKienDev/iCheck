package vn.icheck.android.chat.icheckchat.screen.detail

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import vn.icheck.android.chat.icheckchat.base.BaseViewModelChat
import vn.icheck.android.chat.icheckchat.model.*
import vn.icheck.android.chat.icheckchat.network.ChatRepository
import java.io.File

class ChatSocialDetailViewModel : BaseViewModelChat() {
    private val repository = ChatRepository()

    fun getChatMessage(key: String, success: (obj: DataSnapshot) -> Unit, error: (error: DatabaseError) -> Unit) = firebaseHelper.getMessageDetail(key, success, error)

    fun uploadImage(file: File, success: (obj: MCUploadResponse) -> Unit, cancel: (error: MCBaseResponse) -> Unit) = repository.uploadMedia(file, success, cancel)

    fun getProductBarcode(barcode: String) = request { repository.getProductBarcode(barcode) }

    fun getPackageSticker() = request { repository.getPackageSticker() }

    fun getSticker(packageId: Long) = request { repository.getSticker(packageId) }

    fun sendMessage(key: String, memberType: String, obj: MCDetailMessage) = request { repository.sendMessage(key, memberType, obj) }

    fun getChatRoom(key: String, success: (snapshot: DataSnapshot) -> Unit, cancel: (error: DatabaseError) -> Unit) = firebaseHelper.getChatRoom(key, success, cancel)

    fun unBlockMessage(key: String, toId: String, toType: String) = request { repository.unBlockMessage(key, toId, toType) }

    fun createRoom(members: MutableList<MCMember>) = request { repository.createRoomChat(members) }

    fun getChatSender(child: String, success: (snapshot: DataSnapshot) -> Unit, cancel: (error: DatabaseError) -> Unit) = firebaseHelper.getChatSender(child, success, cancel)

    fun markReadMessage(senderId: String, roomId: String) = request { repository.markReadMessage(senderId, roomId) }
}