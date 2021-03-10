package vn.icheck.android.network.models.upload

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UploadResponse(
        @SerializedName("fileId")
        val fileId: String,
        @SerializedName("url")
        val src: String,
        @SerializedName("fileName")
        val fileName: String
)