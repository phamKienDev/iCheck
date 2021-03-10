package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICSupport(
        @Expose val name: String? = null,
        @Expose val address: String? = null,
        @Expose val contact: String? = null,
        @Expose val description: String? = null,
        @Expose val contact_type: String? = null
)