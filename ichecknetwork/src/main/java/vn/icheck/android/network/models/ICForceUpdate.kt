package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICForceUpdate(
        @Expose val ios: MutableList<String>? = null,
        @Expose val android: MutableList<String>? = null,
        @Expose val ios_all: String? = null,
        @Expose val android_all: String? = null
)