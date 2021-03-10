package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

class ICBusiness {
    @Expose
    var id: Long = 0

    @Expose
    var name: String? = null

    @Expose
    var avatar: String? = null

    @Expose
    var cover: String? = null

    @Expose
    var avatar_thumbnails: ICThumbnail? = null

    @Expose
    var cover_thumbnails: ICThumbnail? = null

    @Expose
    var email: String? = null

    @Expose
    var facebook: String? = null

    @Expose
    var youtube: String? = null

    @Expose
    var phone: String? = null

    @Expose
    var label: String? = null

    @Expose
    var gln_code: String? = null

    @Expose
    var gln_type: String? = null

    @Expose
    var meta: ICMeta? = null

    @Expose
    var country_id: Long = 0

    @Expose
    var address: String? = null

    @Expose
    var address1: String? = null

    @Expose
    var city_id: Long? = null

    @Expose
    var district_id: Long? = null

    @Expose
    var website: String? = null

    @Expose
    var prefix: String? = null

    @Expose
    var tax: String? = null

    @Expose
    var isBlocked: Boolean = false

    @Expose
    var blocked_reason: String? = null

    @Expose
    var isVerified: Boolean = false

    @Expose
    var verify_by_user_id: Long? = null

    @Expose
    var created_at: String? = null

    @Expose
    var updated_at: String? = null

    @Expose
    var review_count: Long = 0

    @Expose
    var rating: Float = 0F

    @Expose
    var country: ICCountry? = null

//    @Expose
//    var city: String? = null

//    @Expose
//    var district: String? = null

    @Expose
    var isFeatured: Boolean = false
    var bookmark_id: Long? = null
    var follow: Float? = null
}
