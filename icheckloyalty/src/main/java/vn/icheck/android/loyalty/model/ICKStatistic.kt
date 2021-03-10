package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICKStatistic : Serializable {
    @Expose
    val id: Long? = null

    @Expose
    val campaign_id: Long? = null

    @Expose
    val points: Long? = null

    @Expose
    val business_owner_id: Long? = null

    @Expose
    val total_points: Long? = null

    @Expose
    val phone: String? = null

    @Expose
    val email: String? = null

    @Expose
    val name: String? = null

//    @Expose
//    val avatar: ICThumbnail? = null

    @Expose
    val city_name: String? = null

    @Expose
    val district_name: String? = null

    @Expose
    val address: String? = null

    @Expose
    val owner: ICKOwner? = null

    @Expose
    val point_loyalty: ICKPointLoyalty? = null
}