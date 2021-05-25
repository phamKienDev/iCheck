package vn.icheck.android.network.model.kyc

import com.google.gson.annotations.SerializedName

data class KycResponse(

		@field:SerializedName("reason")
		val reason: Any? = null,

		@field:SerializedName("updated_at")
		val updatedAt: String? = null,

		@field:SerializedName("user_id")
		val userId: String? = null,

		@field:SerializedName("kyc_level")
		val kycLevel: Any? = null,

		@field:SerializedName("created_at")
		val createdAt: String? = null,

		@field:SerializedName("id")
		val id: String? = null,

		@field:SerializedName("value")
		val kycValue: KycValue? = null,

		@field:SerializedName("deleted_at")
		val deletedAt: Any? = null,

		@field:SerializedName("client_id")
		val clientId: Any? = null,

		@field:SerializedName("status")
		val status: Int? = null
)

data class KycDocumentsItem(

		@field:SerializedName("document")
		val document: List<String?>? = null,

		@field:SerializedName("documentName")
		val documentName: String? = null,

		@field:SerializedName("type")
		val type: Int? = null
)

data class KycValue(

		@field:SerializedName("requestKycLevel")
		val requestKycLevel: Int? = null,

		@field:SerializedName("kycDocuments")
		val kycDocuments: List<KycDocumentsItem?>? = null
)
