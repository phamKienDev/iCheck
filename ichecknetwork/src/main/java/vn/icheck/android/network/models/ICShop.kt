package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICShop (
    @Expose
    var id: Long = 0,
    @Expose
    var name: String? = null,
    @Expose
    var avatar: String? = null,
    @Expose
    var cover: String? = null,
    @Expose
    var phone: String? = null,
    @Expose
    var email: String? = null,
    @Expose
    var address: String? = null,
    @Expose
    var address1: String? = null,
    @Expose
    var country_id: Int? = null,
    @Expose
    var city_id: Int? = null,
    @Expose
    var district_id: Int? = null,
    @Expose
    var ward_id: Int? = null,
    @Expose
    var blocked: Boolean? = null,
    @Expose
    var blocked_reason: String? = null,
    @Expose
    var verified_by_user_id: Long? = null,
    @Expose
    var verified: Boolean? = null,
    @Expose
    var location: ICLocation? = null,
    @Expose
    var is_online: Boolean? = null,
    @Expose
    var is_offline: Boolean? = null,
    @Expose
    var created_at: String? = null,
    @Expose
    var updated_at: String? = null,
    @Expose
    var rating: Double = 0.0,
    @Expose
    var review_count: Long? = null,
    @Expose
    var sales: Int = 0,
    @Expose
    var status: Int? = null,
    @Expose
    var avatar_thumbnails: ICThumbnail? = null,
    @Expose
    var cover_thumbnails: ICThumbnail? = null,
    @Expose
    var hide_all_product: Boolean? = null,
    @Expose
    var page_id: Int? = null,
    @Expose
    var min_order_value: Long? = null,
    @Expose
    var country: ICCountry? = null,
    @Expose
    val city: ICProvince? = null,
    @Expose
    val district: ICDistrict? = null,
    @Expose
    val ward: ICWard? = null,
    @Expose
    val distance: ICDistance? = null,
    @Expose
    val pageId: Int? = null,
    @Expose
    val price: Long? = null
): Serializable