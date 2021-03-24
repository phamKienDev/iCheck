package vn.icheck.android.chat.icheckchat.network

import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
import vn.icheck.android.chat.icheckchat.model.*

interface MCNetworkApi {

    @PUT("/api/user/firebase/verify")
    fun updateFirebaseToken(@Body requestBody: HashMap<String, Any>): Observable<MCResponse<String>>

    @GET
    suspend fun getProductBarcode(@Url url: String): MCResponse<MCBasicInfo>

    @Headers("multipart: true")
    @Multipart
    @POST
    fun uploadImage(@Url url: String, @Part body: MultipartBody.Part): Call<MCResponse<MCUploadResponse>>

    @GET
    suspend fun getPackageSticker(@Url url: String): MCResponse<MCListResponse<MCSticker>>

    @GET
    suspend fun getSticker(@Url url: String, @QueryMap params: HashMap<String, Any>): MCResponse<MCListResponse<MCSticker>>

    @POST
    suspend fun sendMessage(@Url url: String, @Body params: HashMap<String, Any>): MCResponse<String>

    @POST
    suspend fun createRoomChat(@Url url: String, @Body params: HashMap<String, Any>): MCResponse<MCRoom>

    @HTTP(method = "DELETE", hasBody = true)
    suspend fun deleteConversation(@Url url: String, @Body body: HashMap<String, Any>): MCResponse<String>

    @POST
    suspend fun blockMessage(@Url url: String, @Body body: HashMap<String, Any>): MCResponse<String>

    @POST
    suspend fun unBlockMessage(@Url url: String, @Body body: HashMap<String, Any>): MCResponse<String>

    @POST
    suspend fun turnOffNotification(@Url url: String, @Body body: HashMap<String, Any>): MCResponse<String>

    @POST
    suspend fun turnOnNotification(@Url url: String, @Body body: HashMap<String, Any>): MCResponse<String>

    @PUT
    suspend fun markReadMessage(@Url url: String, @Body body: HashMap<String, Any>): MCResponse<String>

    @POST
    suspend fun getContact(@Url url: String, @Body body: HashMap<String, Any>): MCResponse<Boolean>

    @GET
    suspend fun getListFriend(@Url url: String, @QueryMap query: HashMap<String, Any>): MCResponse<MCListResponse<MCUser>>
}