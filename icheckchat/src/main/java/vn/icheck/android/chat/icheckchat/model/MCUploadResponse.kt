package vn.icheck.android.chat.icheckchat.model

import com.google.gson.annotations.SerializedName

data class MCUploadResponse(
        @SerializedName("fileId")
        val fileId: String,
        @SerializedName("url")
        val src: String,
        @SerializedName("fileName")
        val fileName: String
)