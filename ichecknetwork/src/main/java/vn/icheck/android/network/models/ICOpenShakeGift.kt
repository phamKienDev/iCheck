package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ICOpenShakeGift(
        @Expose
        var index: Int?,

        //0-chuc may man,1-vat pham,3-point
        @Expose
        var type: Int?,

        @Expose
        var image: String?,
        @Expose
        var name: String?,
        @Expose
        var code: String?,
        @Expose
        var campaignId: String?,
        @Expose
        var icoin: Long?,
        @Expose
        var icoin_icon: String?,
        @SerializedName("rewardType")
        var rewardType: String? = null,
        @SerializedName("rewardId")
        var rewardId: String? = null
) : Serializable