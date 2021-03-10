package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ICUploadResponse(
    @Expose
    @SerializedName("fileId")
    val fileId: String,
    @Expose
    @SerializedName("uploadSessionId")
    val uploadSessionId: String,
    @Expose
    @SerializedName("uploadUrl")
    val uploadUrl: String,
    @Expose
    @SerializedName("size")
    val size: String
)