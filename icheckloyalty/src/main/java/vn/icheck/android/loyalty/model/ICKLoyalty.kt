package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICKLoyalty : Serializable {

    @Expose
    val id: Long? = null

    @Expose
    val type: String? = null

    @Expose
    val name: String? = null

    @Expose
    val target_type: String? = null

    @Expose
    val image: ICThumbnail? = null

    @Expose
    val description: String? = null

    @Expose
    val status: String? = null

    @Expose
    val creator_id: Long? = null

    @Expose
    val owner_id: Long? = null

    @Expose
    val business_owner_id: Long? = null

    @Expose
    val has_chance_code: Boolean? = null

    @Expose
    val introduction_image: ICThumbnail? = null

    @Expose
    val introduction_link: Any? = null

    @Expose
    val export_gift_from: String? = null

    @Expose
    val export_gift_to: String? = null

    @Expose
    val start_at: String? = null

    @Expose
    val end_at: String? = null

    @Expose
    val created_at: String? = null

    @Expose
    val updated_at: String? = null

    @Expose
    val box: ICKBox? = null

    @Expose
    val owner: ICKOwner? = null

    @Expose
    val status_title: String? = null

    @Expose
    val status_time: String? = null

    @Expose
    val status_time_title: String? = null

    @Expose
    val statusCode: Long? = null

    @Expose
    val campaign_package_code: MutableList<CampaignPackageCodeItem>? = null

    var content: String? = null
}