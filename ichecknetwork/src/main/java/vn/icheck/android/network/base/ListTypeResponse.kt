package vn.icheck.android.network.base

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ListTypeResponse<T>(
        @SerializedName("data") val data: ListResponse<T>?,
        @SerializedName("statusCode") val statusCode: String?,
        @SerializedName("message") var message: String?
) {
    fun isSuccess() = statusCode == "200"
}

data class ListResponse<T>(
        @SerializedName("count") var count: Int?,
        @SerializedName("rows") var rows: List<T>?
)