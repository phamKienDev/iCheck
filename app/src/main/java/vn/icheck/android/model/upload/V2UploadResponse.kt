package vn.icheck.android.model.upload

import com.google.gson.annotations.SerializedName

data class V2UploadResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class Data(

	@field:SerializedName("fileName")
	val fileName: String? = null,

	@field:SerializedName("url")
	val url: String? = null,

	@field:SerializedName("fileId")
	val fileId: Int? = null
)
