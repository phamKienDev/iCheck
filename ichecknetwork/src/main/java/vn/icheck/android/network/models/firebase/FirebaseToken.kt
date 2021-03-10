package vn.icheck.android.network.models.firebase

import com.google.gson.annotations.SerializedName

data class FirebaseToken(
        @SerializedName("data")
        val data:String?
)