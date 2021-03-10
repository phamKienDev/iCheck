package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import kotlin.Any

class ICEnterprise_Search {
    @Expose
    var id: Long? = null
    @Expose
    var name: String? = null
    @Expose
    var avatar: String? = null
    @Expose
    var cover: String? = null
    @Expose
    var avatar_thumbnails: ICEnterprise_Search.ObjectAvatarThumbnails? = null

    class ObjectAvatarThumbnails {
        @Expose
        var original: String? = null
        @Expose
        var square: String? = null
        @Expose
        var thumbnail: String? = null
        @Expose
        var small: String? = null
        @Expose
        var medium: String? = null
    }

    @Expose
    var cover_thumbnails: ICEnterprise_Search.ObjectCoverThumbnails? = null

    class ObjectCoverThumbnails {
        @Expose
        var original: String? = null
        @Expose
        var square: String? = null
        @Expose
        var thumbnail: String? = null
        @Expose
        var small: String? = null
        @Expose
        var medium: String? = null
    }

    @Expose
    var email: String? = null
    @Expose
    var phone: String? = null
    @Expose
    var gln_code: String? = null
    @Expose
    var gln_type: String? = null
    //    @Expose
//    var meta:Int? = null
    @Expose
    var country_id: Long? = null
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
    var blocked: Boolean? = null
    @Expose
    var blocked_reason: String? = null
    @Expose
    var verified: Boolean? = null
    @Expose
    var verify_by_user_id: Long? = null
    @Expose
    var created_at: String? = null
    @Expose
    var updated_at: String? = null
    @Expose
    var review_count: Int? = null
    @Expose
    var rating: Float? = null
    @Expose
    var country: ICEnterprise_Search.ObjectCountry? = null

    class ObjectCountry {
        @Expose
        var id: Int? = null
        @Expose
        var name: String? = null
        @Expose
        var code: String? = null
    }

    @Expose
    var city: Any? = null
    @Expose
    var district: String? = null
    @Expose
    var featured: Boolean? = null
}