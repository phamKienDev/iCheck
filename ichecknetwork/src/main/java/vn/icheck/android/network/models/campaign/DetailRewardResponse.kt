package vn.icheck.android.network.models.campaign

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DetailRewardResponse(

        @field:SerializedName("data")
        val data: DetailRewardData? = null,

        @field:SerializedName("statusCode")
        val statusCode: String? = null
):Parcelable

@Parcelize
data class DetailRewardData(

        @field:SerializedName("image")
        val image: String? = null,

//        @field:SerializedName("orderId")
//        val orderId: String? = null,

        @field:SerializedName("receiveAt")
        val receiveAt: String? = null,

        @field:SerializedName("rewardType")
        val rewardType: String? = null,

        @field:SerializedName("logo")
        val shopImage: String? = null,

        @field:SerializedName("businessName")
        val shopName: String? = null,

        @field:SerializedName("remainTime")
        val remainTime: String? = null,

        @field:SerializedName("campaignBanner")
        val campaignBanner: String? = null,

        @field:SerializedName("dataRps")
        val dataRps: DataRps? = null,

        @field:SerializedName("expiredAt")
        val expiredAt: String? = null,

        @field:SerializedName("id")
        val id: String? = null,

        @field:SerializedName("state")
        val state: Int? = null,

        @field:SerializedName("campaignId")
        val campaignId: String ? = null,

        @field:SerializedName("shopType")
        val shopType: Int? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("desc")
        val desc: String? = null,

        @field:SerializedName("address")
        val address: String? = null,

        @field:SerializedName("reasonOther")
        val reasonOther: String? = null,

        @field:SerializedName("cancelTime")
        val cancelTime: String? = null,

        @field:SerializedName("confirmTime")
        val confirmTime: String? = null,

        @SerializedName("landingUrl")
        val landingUrl:String? = null,

        @SerializedName("usingState")
        var usingState:Int? = null
):Parcelable

@Parcelize
data class DataRps(
        @field:SerializedName("expiredDate")
        val expiredDate: String? = null,

        @field:SerializedName("pin")
        val pin: String? = null,

        @field:SerializedName("serial")
        val serial: String? = null,

        @field:SerializedName("shipTime")
        val shipTime:String? = null
):Parcelable
