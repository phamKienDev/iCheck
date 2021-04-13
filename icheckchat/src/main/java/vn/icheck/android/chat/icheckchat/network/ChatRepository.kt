package vn.icheck.android.chat.icheckchat.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.icheck.android.chat.icheckchat.R
import vn.icheck.android.chat.icheckchat.base.ConstantChat
import vn.icheck.android.chat.icheckchat.base.ConstantChat.CHAT_HOST
import vn.icheck.android.chat.icheckchat.base.ConstantChat.PATH
import vn.icheck.android.chat.icheckchat.base.ConstantChat.SOCIAL_HOST
import vn.icheck.android.chat.icheckchat.base.ConstantChat.UPLOAD_HOST
import vn.icheck.android.chat.icheckchat.helper.NetworkHelper.LIMIT
import vn.icheck.android.chat.icheckchat.helper.ShareHelperChat
import vn.icheck.android.chat.icheckchat.model.*
import java.io.File

class ChatRepository {

    suspend fun getContact(phoneList: MutableList<String>): MCResponse<Boolean> {
        val body = hashMapOf<String, Any>()
        body["phoneList"] = phoneList
        body["isAppend"] = true

        val url = "$SOCIAL_HOST$PATH/relationships/phone-contacts"

        return MCNetworkClient.getNewSocialApi().getContact(url, body)
    }

    suspend fun getListFriend(id: Long, offset: Int): MCResponse<MCListResponse<MCUser>> {
        val body = hashMapOf<String, Any>()
        body["offset"] = offset
        body["limit"] = LIMIT

        val url = "$SOCIAL_HOST$PATH/users/$id/friends"

        return MCNetworkClient.getNewSocialApi().getListFriend(url, body)
    }

    suspend fun createRoomChat(members: MutableList<MCMember>): MCResponse<MCRoom> {
        val body = hashMapOf<String, Any>()
        body["members"] = members

        val url = CHAT_HOST + "rooms/single"

        return MCNetworkClient.getNewSocialApi().createRoomChat(url, body)
    }

    suspend fun deleteConversation(id: String): MCResponse<String> {
        val body = hashMapOf<String, Any>()
        body["userId"] = FirebaseAuth.getInstance().uid.toString().toLong()
        body["userType"] = "user"

        val url = CHAT_HOST + "conversations/$id"

        return MCNetworkClient.getNewSocialApi().deleteConversation(url, body)
    }

    suspend fun blockMessage(key: String, toId: String, toType: String): MCResponse<String> {
        val body = hashMapOf<String, Any>()
        body["fromUserId"] = FirebaseAuth.getInstance().uid?.toLong() ?: ""
        body["fromUserType"] = "user"
        body["toId"] = toId.toLong()
        body["toType"] = toType

        val url = CHAT_HOST + "rooms/$key/block"

        return MCNetworkClient.getNewSocialApi().blockMessage(url, body)
    }

    suspend fun unBlockMessage(key: String, toId: String, toType: String): MCResponse<String> {
        val body = hashMapOf<String, Any>()
        body["fromUserId"] = FirebaseAuth.getInstance().uid?.toLong() ?: ""
        body["fromUserType"] = "user"
        body["toId"] = toId.toLong()
        body["toType"] = toType

        val url = CHAT_HOST + "rooms/$key/unblock"

        return MCNetworkClient.getNewSocialApi().unBlockMessage(url, body)
    }

    suspend fun turnOffNotification(key: String, memberId: String, memberType: String): MCResponse<String> {
        val body = hashMapOf<String, Any>()
        body["memberId"] = memberId.toLong()
        body["memberType"] = memberType

        val url = CHAT_HOST + "rooms/$key/unsubscribe"

        return MCNetworkClient.getNewSocialApi().turnOffNotification(url, body)
    }

    suspend fun turnOnNotification(key: String, memberId: String, memberType: String): MCResponse<String> {
        val body = hashMapOf<String, Any>()
        body["memberId"] = memberId.toLong()
        body["memberType"] = memberType

        val url = CHAT_HOST + "rooms/$key/subscribe"

        return MCNetworkClient.getNewSocialApi().turnOnNotification(url, body)
    }

    suspend fun markReadMessage(senderId: String, roomId: String): MCResponse<String> {
        val body = hashMapOf<String, Any>()
        body["senderId"] = senderId
        body["roomId"] = roomId

        val url = CHAT_HOST + "conversations/mark-read"

        return MCNetworkClient.getNewSocialApi().markReadMessage(url, body)
    }

    suspend fun sendMessage(key: String, memberType: String, obj: MCDetailMessage): MCResponse<String> {
        val body = hashMapOf<String, Any>()
        body["memberId"] = FirebaseAuth.getInstance().currentUser?.uid?.toLong()
                ?: ShareHelperChat.getLong(ConstantChat.USER_ID)

        body["roomId"] = key

        if (!obj.messageId.isNullOrEmpty()) {
            body["messageId"] = obj.messageId!!
        }

        body["memberType"] = memberType

        val message = hashMapOf<String, Any>()

        if (!obj.content.isNullOrEmpty()) {
            message["text"] = obj.content!!.replace("\r", "\n")
        }
        if (obj.sticker != null && obj.sticker is MCSticker) {
            message["sticker"] = obj.sticker!!
        }
        if (!obj.listMedia.isNullOrEmpty()) {
            message["media"] = obj.listMedia!!
        }
        if (obj.product != null) {

            val product = hashMapOf<String, Any>()

            if (!obj.product?.barcode.isNullOrEmpty()) {
                product["barcode"] = obj.product?.barcode!!
            }
            if (!obj.product?.image.isNullOrEmpty()) {
                product["image"] = obj.product?.image!!
            }
            if (!obj.product?.name.isNullOrEmpty()) {
                product["name"] = obj.product?.name!!
            }
            if (obj.product?.price != null) {
                product["price"] = obj.product?.price!!
            }
            if (obj.product?.productId != null) {
                product["productId"] = obj.product?.productId!!
            }
            if (!obj.product?.state.isNullOrEmpty()) {
                product["state"] = obj.product?.state!!
            }

            message["product"] = product
        }

        body["message"] = message

        val url = CHAT_HOST + "chat-details"

        return MCNetworkClient.getNewSocialApi().sendMessage(url, body)
    }

    suspend fun getProductBarcode(barcode: String): MCResponse<MCBasicInfo> {
        val url = SOCIAL_HOST + "${PATH}/products/barcode/$barcode"

        return MCNetworkClient.getNewSocialApi().getProductBarcode(url)
    }

    suspend fun getPackageSticker(): MCResponse<MCListResponse<MCSticker>> {
        val url = CHAT_HOST + "sticker-packages"

        return MCNetworkClient.getNewSocialApi().getPackageSticker(url)
    }

    suspend fun getSticker(packageId: Long): MCResponse<MCListResponse<MCSticker>> {
        val params = hashMapOf<String, Any>()
        params["packageId"] = packageId

        val url = CHAT_HOST + "stickers"

        return MCNetworkClient.getNewSocialApi().getSticker(url, params)
    }

    suspend fun getSystemSetting(key: String?, keyGroup: String?): MCResponse<MCListResponse<MCClientSetting>> {
        val params = hashMapOf<String, Any>()
        if (!key.isNullOrEmpty()) {
            params["key"] = key
        }
        if (!keyGroup.isNullOrEmpty()) {
            params["keyGroup"] = keyGroup
        }

        val url = SOCIAL_HOST + "${PATH}/cms/system-settings"

        return MCNetworkClient.getNewSocialApi().getSystemSetting(url, params)
    }

    suspend fun uploadMedia(file: File):MCResponse<MCUploadResponse> {

        val requestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        var fileName = file.toString()
        if (!fileName.endsWith(".mp4") && !fileName.endsWith(".png") && !fileName.endsWith(".jpg") && !fileName.endsWith(".gif")) {
            fileName += ".jpg"
        }
        val body = MultipartBody.Part.createFormData("key", fileName, requestBody)

        val url = UPLOAD_HOST + "upload/stream"
        return MCNetworkClient.getNewSocialApi(60).uploadImage(url, body)
    }
}