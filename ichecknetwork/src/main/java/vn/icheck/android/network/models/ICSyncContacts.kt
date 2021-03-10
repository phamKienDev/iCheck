package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ICSyncContacts(
        @Expose
        @SerializedName("followed")
        val followed : IntArray?
)