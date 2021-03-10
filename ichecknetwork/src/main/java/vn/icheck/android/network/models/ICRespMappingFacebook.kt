package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICRespMappingFacebook(
        @Expose val id: Long,
        @Expose val provider: String,
        @Expose val user_id: Long,
        @Expose val social_id: String
)