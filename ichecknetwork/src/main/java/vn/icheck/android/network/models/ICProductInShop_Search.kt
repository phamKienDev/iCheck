package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

class ICProductInShop_Search {
    @Expose
    var id: Long? = null
    @Expose
    var name: String? = null
    @Expose
    var shop_id: Long? = null
    @Expose
    var created_at: String? = null
    @Expose
    var updated_at: String? = null
    @Expose
    var price_min: Long? = null
    @Expose
    var price_max: Long? = null
    @Expose
    var price: Long? = null
    @Expose
    var shop: ICProductInShop_Search.ObjectShop? = null

    class ObjectShop {
        @Expose
        var id: Long? = null
        @Expose
        var name: String? = null
        @Expose
        var avatar: String? = null
        @Expose
        var rating: Float? = null
        @Expose
        var review_count: Int? = null
        @Expose
        var avatar_thumbnails: ObjectShop.ObjectAvatarThumbnails? = null

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
        var cover_thumbnails: ObjectShop.ObjectCoverThumbnail? = null

        class ObjectCoverThumbnail {
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
    }

    @Expose
    var thumbnails: ICProductInShop_Search.ObjectThumbnails? = null

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
    var rating: Float? = null
    @Expose
    var review_count: Int? = null
    @Expose
    var sales: Int? = null
    @Expose
    var variants: MutableList<ICProductInShop_Search.ListVariants>? = null

    class ListVariants {
        @Expose
        var id: Long? = null
        @Expose
        var type: String? = null
        @Expose
        var shop_id: Long? = null
        @Expose
        var product_id: Long? = null
        @Expose
        var is_hot: Boolean? = null
        @Expose
        var name: String? = null
        @Expose
        var images: MutableList<String>? = null
        @Expose
        var barcode: String? = null
        @Expose
        var quantity: Long? = null
        @Expose
        var price: Long? = null
        @Expose
        var point: Long? = null
        @Expose
        var special_price: Long? = null
        @Expose
        var special_to_date: String? = null
        @Expose
        var special_from_date: String? = null
        @Expose
        var sale_off: Boolean? = null
        @Expose
        var image_thumbs: MutableList<ListVariants.ListImageThumbs>? = null

        class ListImageThumbs {
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
        var is_active: Boolean? = null
        @Expose
        var verified: String? = null
        @Expose
        var block_reason: String? = null
        @Expose
        var blocked: Boolean? = null
        @Expose
        var attributes: MutableList<ListVariants.ListAttributes>? = null

        class ListAttributes {
            @Expose
            var code: String? = null
            @Expose
            var name: String? = null
            @Expose
            var value: String? = null
        }

        @Expose
        var created_at: String? = null
        @Expose
        var updated_at: String? = null
        @Expose
        var being_used: Boolean? = null
        @Expose
        var barcode_verified:Boolean? = null
    }

    @Expose
    var discount: Int? = null
    @Expose
    var custom_attributes: MutableList<ICProductInShop_Search.ListCustomAttributes>? = null

    class ListCustomAttributes

    @Expose
    var quantity: Long? = null
    @Expose
    var can_add_to_cart: Boolean? = null
    @Expose
    var categories: MutableList<ICProductInShop_Search.ListCaterogies>? = null

    class ListCaterogies {
        @Expose
        var id: Int? = null
        @Expose
        var name: String? = null
        @Expose
        var image: String? = null
        @Expose
        var parent_id: Long? = null
        @Expose
        var position: Int? = null
        @Expose
        var status: Int? = null
        @Expose
        var slug: String? = null
        @Expose
        var attribute_set_id: Long? = null
        @Expose
        var thumbnails: ListCaterogies.ObjectThumbnails? = null

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
    }

    @Expose
    var attributes:MutableList<ICProductInShop_Search.ObjectAttributes>? = null

    class ObjectAttributes
}