package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ICChatCodeResponse(
        @Expose
        @SerializedName("status")
        val status: Int,
        @Expose
        @SerializedName("data")
        val dataRep: DataRep
)

data class DataRep(
        @SerializedName("roomId")
        @Expose
        val roomId: String
)