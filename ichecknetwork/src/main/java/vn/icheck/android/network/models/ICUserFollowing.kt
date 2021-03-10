package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ICUserFollowing(
        @Expose val id: Long?,
        @Expose val account_id: Long?,
        @Expose val object_id: Long?,
        @Expose val object_type: String?,
        @Expose val created_at: String?,
        @SerializedName("object") val user: ICUser?
)