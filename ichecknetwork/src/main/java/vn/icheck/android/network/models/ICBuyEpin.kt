package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ICBuyEpin(
        @SerializedName("statusCode")
        val statusCode:Int?,
        @SerializedName("message")
        val message:String?,
        @SerializedName("data")
        val epinData: EpinData?

) {
        data class EpinData(
                @SerializedName("denomination")
                val denomination:String,
                @SerializedName("pin")
                val pin: String,
                @SerializedName("serial")
                val serial: String
        ):Serializable
}