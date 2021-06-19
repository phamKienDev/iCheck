package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import vn.icheck.android.network.base.ICResponseCode

data class ICLayoutData<T>(
        @Expose val layout: MutableList<ICLayout>?,
        @Expose val data: T?
) : ICResponseCode()