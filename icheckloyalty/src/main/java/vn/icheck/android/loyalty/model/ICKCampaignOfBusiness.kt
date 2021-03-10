package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICKCampaignOfBusiness : Serializable {

    @Expose
    var id: Long? = null

    @Expose
    var start_at: String? = null

    @Expose
    var end_at: String? = null

    @Expose
    var name: String? = null

    @Expose
    var image: ICThumbnail? = null

    @Expose
    var customer_status: ICKCustomer? = null

    @Expose
    var points: Long? = null

    @Expose
    var point_name: String? = null

    @Expose
    var target_type: String? = null

    @Expose
    var url: String? = null

    @Expose
    var type: String? = null

    @Expose
    var description: String? = null

    @Expose
    var status: String? = null

    @Expose
    var has_chance_code: Boolean? = null

    @Expose
    var status_time: String? = null

    @Expose
    var status_time_title: String? = null

    @Expose
    var status_title: String? = null
}