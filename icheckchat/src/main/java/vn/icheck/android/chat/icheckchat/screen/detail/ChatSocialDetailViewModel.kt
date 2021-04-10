package vn.icheck.android.chat.icheckchat.screen.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.coroutines.*
import vn.icheck.android.chat.icheckchat.base.BaseViewModelChat
import vn.icheck.android.chat.icheckchat.model.*
import vn.icheck.android.chat.icheckchat.network.ChatRepository
import java.io.File
import java.lang.Exception

class ChatSocialDetailViewModel : BaseViewModelChat() {
    private val repository = ChatRepository()
    val listMediaData = MutableLiveData<MutableList<MCUploadResponse>>()

    fun getChatMessage(key: String, success: (obj: DataSnapshot) -> Unit, error: (error: DatabaseError) -> Unit) = firebaseHelper.getMessageDetail(key, success, error)

    fun getChangeMessageChat(key: String, onAdd: (obj: DataSnapshot) -> Unit, timeStart: Long) = firebaseHelper.getChangeMessageChat(key, onAdd, timeStart)

    fun uploadImage(files: MutableList<File>) {
        viewModelScope.launch {
            val listMedia = mutableListOf<MCUploadResponse>()
            val listCall = mutableListOf<Deferred<Any?>>()
            files.forEach {
                listCall.add(async {
                    try {
                        val response = withTimeout(60000){repository.uploadMedia(it)}
                        if (!response.data?.src.isNullOrEmpty()) {
                            listMedia.add(response.data!!)
                        }
                    } catch (e: Exception) {
                    }
                })
            }

            listCall.awaitAll()
            listMediaData.postValue(listMedia)
        }
    }


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