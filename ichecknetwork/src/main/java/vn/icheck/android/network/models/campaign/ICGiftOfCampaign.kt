package vn.icheck.android.network.models.campaign

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICGiftOfCampaign : Serializable {

    @Expose
    val id: String? = null

    @Expose
    val name: String? = null

    @Expose
    val title: String? = null

    @Expose
    val logo: String? = null

    @Expose
    val image: String? = null

    @Expose
    val businessName: String? = null

    @Expose
    val entityId: Long? = null

    @Expose
    val businessId: Long? = null

    @Expose
    val productId: Long? = null

    @Expose
    val type: Int? = null

    @Expose
    val businessType: Long? = null

    @Expose
    val rewardTotal: Long? = null

    @Expose
    val rewardValue: Long? = null

    @Expose
    val icoin: Long? = null

    @Expose
    val icoinIcon: String? = null
}