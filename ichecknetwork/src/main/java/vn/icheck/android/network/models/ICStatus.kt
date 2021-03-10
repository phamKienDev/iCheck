package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import vn.icheck.android.network.base.ICBaseResponse

data class ICStatus(
        @Expose val status: Boolean?,
        @Expose val timestamp: String?
) : ICBaseResponse()