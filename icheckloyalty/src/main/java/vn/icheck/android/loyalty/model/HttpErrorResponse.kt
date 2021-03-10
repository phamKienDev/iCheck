package vn.icheck.android.loyalty.model

import com.google.gson.annotations.SerializedName

data class HttpErrorResponse(

        @field:SerializedName("data")
        val data: ErrorData? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("statusCode")
        val statusCode: Int? = null,

        @field:SerializedName("status")
        val status: String? = null
)

data class ErrorData(

        @field:SerializedName("message")
        val message: String? = null
)