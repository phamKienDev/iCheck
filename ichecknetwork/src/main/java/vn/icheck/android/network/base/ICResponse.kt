package vn.icheck.android.network.base

import com.google.gson.annotations.SerializedName

class ICResponse<T>(
        @SerializedName("data") val data: T?
) : ICResponseCode()