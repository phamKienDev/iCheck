package vn.icheck.android.network.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import vn.icheck.android.network.model.upload.V2UploadResponse
import vn.icheck.android.network.models.upload.UploadResponse

interface UploadApi {
    @Headers("multipart: true")
    @Multipart
    @POST("upload/stream")
    suspend fun postImage( @Part body: MultipartBody.Part): V2UploadResponse
}