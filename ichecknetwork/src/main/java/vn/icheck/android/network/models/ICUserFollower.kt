package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICUserFollower(
        @Expose val id: Long,
        @Expose val created_at: String,
        @Expose val account: ICAccount
)