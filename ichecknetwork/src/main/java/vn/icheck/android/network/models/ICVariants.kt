package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICVariants(
        @Expose
        var id: Long = 0,
        @Expose
        var type: String? = null,
        @Expose
        var shop_id: Long = 0,
        @Expose
        var product_id: Long = 0,
        @Expose
        var is_hot: Boolean = false,
        @Expose
        var name: String? = null,
        @Expose
        var images: MutableList<String>? = null,
        @Expose
        var barcode: String? = null,
        @Expose
        var quantity: Long = 0,
        @Expose
        var price: Long = 0,
        @Expose
        var point: Int = 0,
        @Expose
        var special_price: Long = 0,
        @Expose
        var special_to_date: String? = null,
        @Expose
        var special_from_date: String? = null,
        @Expose
        var sale_off: Boolean = false,
        @Expose
        var image_thumbs: MutableList<ICThumbnail>? = null,
        @Expose
        var is_active: Boolean = false,
        @Expose
        var verified: String? = null,
        @Expose
        var cancel_verify_reason: String? = null,
        @Expose
        var blocked: Boolean = false,
        @Expose
        var block_reason: String? = null,
        @Expose
        var attributes: MutableList<ICAttributes>? = null,
        @Expose
        var created_at: String? = null,
        @Expose
        var updated_at: String? = null,
        @Expose
        var being_used: Boolean = false,
        @Expose
        var barcode_verified: Boolean = false,
        @Expose
        var verified_by_page_id: String? = null)
