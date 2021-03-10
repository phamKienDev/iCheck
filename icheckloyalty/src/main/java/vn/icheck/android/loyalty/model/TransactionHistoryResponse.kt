package vn.icheck.android.loyalty.model

import com.google.gson.annotations.SerializedName

data class TransactionHistoryResponse(

        @field:SerializedName("reason")
        val reason: String? = null,

        @field:SerializedName("created_at")
        val createdAt: String? = null,

        @field:SerializedName("type")
        val type: Type? = null,

        @field:SerializedName("point")
        val point: Long? = null,

        @field:SerializedName("status")
        val status: Status? = null
)

data class Type(

        @field:SerializedName("code")
        val code: String? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("id")
        val id: String? = null
)

data class Status(

        @field:SerializedName("code")
        val code: String? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("id")
        val id: Int? = null
)