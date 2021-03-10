package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

class ICHistory_Shop {
    @Expose
    var id: Long? = null
    @Expose
    var name: String? = null
    @Expose
    var avatar: String? = null
    @Expose
    var cover: String? = null
    @Expose
    var phone: String? = null
    @Expose
    var email: String? = null
    @Expose
    var address: String? = null
    @Expose
    var country_id: Long? = null
    @Expose
    var city_id: Long? = null
    @Expose
    var district_id: Long? = null
    @Expose
    var ward_id: Long? = null
    @Expose
    var blocked: Boolean? = null
    @Expose
    var blocked_reason: String? = null
    @Expose
    var verified_by_user_id: Long? = null
    @Expose
    var verified: Boolean? = null
    @Expose
    var location: ICHistory_Shop.ObjectLocation? = null

//    var isClick = false

    class ObjectLocation {
        @Expose
        var lat: Double? = null
        @Expose
        var lon: Double? = null
    }

    @Expose
    var is_online: Boolean? = null
    @Expose
    var is_offline: Boolean? = null
    @Expose
    var created_at: String? = null
    @Expose
    var updated_at: String? = null
    @Expose
    var products: MutableList<ICHistory_Shop.ObjectProduct>? = null

    class ObjectProduct {
        @Expose
        var id: Long? = null
        @Expose
        var name: String? = null
        @Expose
        var price: Long? = null
        @Expose
        var image: String? = null
        @Expose
        var barcode: String? = null
        @Expose
        var rating: Float? = null
        @Expose
        var review_count: Int? = null
        @Expose
        var thumbnails: ObjectProduct.ObjectThumbnails? = null

        class ObjectThumbnails {
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
        var owner: String? = null
        @Expose
        var vendor: String? = null
        //        @Expose
//        var distributors: MutableList<String>? = null
//        @Expose
//        var other_pages: MutableList<String>? = null
        @Expose
        var disable_contribution: Boolean? = null
        @Expose
        var type: Int? = null
        @Expose
        var verified: Boolean? = null
        @Expose
        var should_hide_fields: Boolean? = null
        @Expose
        var history_id: Long? = null
        @Expose
        var scan_at: String? = null
        @Expose
        var item_id: Long? = null
        @Expose
        var can_add_to_cart: Boolean? = null
    }

    @Expose
    var rating: Float? = null
    @Expose
    var review_count: Int? = null
    @Expose
    var sales: Int? = null
    @Expose
    var status: Int? = null
    @Expose
    var avatar_thumbnails: ICHistory_Shop.ObjectAvatarThumbnails? = null

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
    var cover_thumbnails: ICHistory_Shop.ObjectCoverThumbnails? = null

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
    var hide_all_product: Boolean? = null
    @Expose
    var page_id: Int? = null
    @Expose
    var min_order_value: Long? = null
    @Expose
    var distance: ICHistory_Shop.ObjectDistance? = null

    class ObjectDistance {
        @Expose
        var unit: String? = null
        @Expose
        var value: Double? = null
    }
}