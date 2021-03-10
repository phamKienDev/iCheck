package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICActorV2(
        @Expose val id: Int,
        @Expose val type: String,
        @Expose val name: String,
        @Expose val avatar: String,
        @Expose val level: Int,
        @Expose val verified: Boolean?
)
