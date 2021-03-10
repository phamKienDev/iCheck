package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

class ICHistory_Product {
    @Expose
    var id: Long? = null
    @Expose
    var product_id: Long? = null
    @Expose
    var user_id: Long? = null
    @Expose
    var device_id: String? = null
    @Expose
    var scans: Long? = null
    @Expose
    var price: Long = -1
    @Expose
    var scan_lat: Double? = null
    @Expose
    var scan_lng: Double? = null
    @Expose
    var scan_address: String? = null
    @Expose
    var created_at: String? = null
    @Expose
    var updated_at: String? = null
    @Expose
    var product: ICHistory_Product.ObjectProduct? = null
    @Expose
    var shop: ICHistory_Product.ObjectShop? = null
    @Expose
    var variant: ICHistory_Product.ObjectVariant? = null

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
        var verified: Boolean? = null
        @Expose
        var disable_contribution: Boolean? = null
        @Expose
        var type: Int? = null
        @Expose
        var should_hide_fields: Boolean? = null
    }

    class ObjectShop {
        @Expose
        var id: Long? = null
        @Expose
        var name: String? = null
        @Expose
        var avatar: String? = null
        @Expose
        var address: String? = null
        @Expose
        var city_id: Int? = null
        @Expose
        var district_id: Int? = null
        @Expose
        var ward_id: Long? = null
        @Expose
        var verified: Boolean? = null
        @Expose
        var location: ObjectShop.ObjectLocation? = null
        @Expose
        var is_online: Boolean? = null
        @Expose
        var is_offline: Boolean? = null
        @Expose
        var rating: Float? = null
        @Expose
        var review_count: Int? = null
        @Expose
        var city: ObjectShop.ObjectCity? = null
        @Expose
        var district: ObjectShop.ObjectDistrict? = null
        @Expose
        var ward: ObjectShop.ObjectWard? = null
        @Expose
        var avatar_thumbnails: ObjectShop.ObjectAvatar? = null
        @Expose
        var cover_thumbnails: ObjectShop.ObjectCoverAvatar? = null
        @Expose
        var hide_all_product: Boolean? = null
        @Expose
        var page_id: Int? = null
        @Expose
        var distance: ObjectShop.ObjectDistance? = null

        class ObjectLocation {
            @Expose
            var lat: Double? = null
            @Expose
            var lon: Double? = null
        }

        class ObjectCity {
            @Expose
            var id: Int? = null
            @Expose
            var name: String? = null
            @Expose
            var country_id: Int? = null
        }

        class ObjectDistrict {
            @Expose
            var id: Int? = null
            @Expose
            var name: String? = null
            @Expose
            var country_id: Int? = null
        }

        class ObjectWard {
            @Expose
            var id: Int? = null
            @Expose
            var name: String? = null
            @Expose
            var country_id: Int? = null
        }

        class ObjectAvatar {
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

        class ObjectCoverAvatar {
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

        class ObjectDistance {
            @Expose
            var unit: String? = null
            @Expose
            var value: Double? = null
        }
    }

    class ObjectVariant {
        @Expose
        var id: Long? = null
        @Expose
        var can_add_to_cart: Boolean? = null
        @Expose
        var price: Long = 0
        @Expose
        var special_price:Long = 0
        @Expose
        var sale_off:Boolean? = null
    }
}