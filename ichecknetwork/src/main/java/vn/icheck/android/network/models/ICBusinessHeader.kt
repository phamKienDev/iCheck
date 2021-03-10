package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICBusinessHeader(
        @Expose var id: Long = 0,
        @Expose var name: String? = null,
        @Expose var avatar: String? = null,
        @Expose var cover: String? = null,
        @Expose var avatar_thumbnails: ICThumbnail? = null,
        @Expose var cover_thumbnails: ICThumbnail? = null,
        @Expose var email: String? = null,
        @Expose var phone: String? = null,
        @Expose var gln_code: String? = null,
        @Expose var gln_type: String? = null,
//        @Expose var meta: String? = null,
        @Expose var country_id: Long = 0,
        @Expose var address: String? = null,
        @Expose var address1: String? = null,
        @Expose var city_id: Long = 0,
        @Expose var district_id: Long = 0,
        @Expose var website: String? = null,
        @Expose var prefix: String? = null,
//        @Expose var tax: Int = 0,
        @Expose var blocked: Boolean = false,
        @Expose var blocked_reason: String? = null,
        @Expose var verified: Boolean = false,
        @Expose var verify_by_user_id: Long = 0,
        @Expose var created_at: String? = null,
        @Expose var updated_at: String? = null,
        @Expose var review_count: Int = 0,
        @Expose var rating: Float = 0F,
        @Expose var country: ICCountry? = null,
//        @Expose var city: String? = null,
//        @Expose var district: String? = null,
        @Expose var featured: Boolean = false,
        @Expose var scan_count: Int = 0,
        @Expose var product_count: Int = 0
)