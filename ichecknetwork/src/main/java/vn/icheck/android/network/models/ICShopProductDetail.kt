package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
data class ICShopProductDetail(
        @Expose
        var id: Long = 0,
        @Expose
        var type: String? = null,
        @Expose
        var name: String? = null,
        @Expose
        var shop_id: Int = 0,
        @Expose
        var created_at: String? = null,
        @Expose
        var updated_at: String? = null,
        @Expose
        var is_active: Boolean = false,
        @Expose
        var verified: String? = null,
        @Expose
        var cancel_verify_reason: String? = null,
        @Expose
        var price_min: Long = 0,
        @Expose
        var price_max: Long = 0,
        @Expose
        var price: Long = 0,
        @Expose
        var description: String? = null,
        @Expose
        var html: String?=null,
        @Expose
        var shop: ICShop,
        @Expose
        var thumbnails: ICThumbnail? = null,
        @Expose
        var rating: Float = 0F,
        @Expose
        var review_count: Int = 0,
        @Expose
        var sales: Int = 0,
        @Expose
        var variants: MutableList<ICVariants> = mutableListOf(),
        @Expose
        var discount: Long = 0,
        @Expose
        var custom_attributes:MutableList<ICCustomAttributes>?=null,
        @Expose
        var package_height: Float = 0F,
        @Expose
        var package_length: Float = 0F,
        @Expose
        var package_weight: Float = 0F,
        @Expose
        var package_width: Float = 0F,
        @Expose
        var quantity: Int = 0,
        @Expose
        var can_add_to_cart: Boolean = false,
        @Expose
        var categories: MutableList<ICCategory>? = null,
        @Expose
        var attributes: MutableList<ICAttributes>? = null
)








