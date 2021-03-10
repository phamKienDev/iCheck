package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName
import vn.icheck.android.network.base.ICResponseCode

data class ICLayoutData<T>(
        @SerializedName("layout") val layout: MutableList<ICLayout>?,
        @SerializedName("data") val data: T?
) : ICResponseCode()