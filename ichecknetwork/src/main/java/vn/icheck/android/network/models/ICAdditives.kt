package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICAdditives(
        @Expose val id: Long,
        @Expose val name: String,
        @Expose val code: String,
        @Expose val type: String,
        @Expose val ins: String?,
        @Expose val inse: String?,
        @Expose val cas: String,
        @Expose val description: String?,
        @Expose val content: String
) : Serializable