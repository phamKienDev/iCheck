package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose
import vn.icheck.android.network.models.ICShop
import vn.icheck.android.network.models.ICShopVariant
import java.io.Serializable

data class ICShopVariantStamp(
        @Expose
        var id: Long? = null,
        @Expose
        var type: String? = null,
        @Expose
        var shop_id: Long? = null,
        @Expose
        var product_id: Long? = null,
        @Expose
        var product: ICShopVariant? = null,
        @Expose
        var name: String? = null,
//        @Expose
//        var images: MutableList<String>? = null,
        @Expose
        var barcode: String? = null,
        @Expose
        var quantity: Long? = null,
        @Expose
        var price: Long? = null,
//        @Expose
//        val point: Int? = null,
        @Expose
        var special_price: Long? = null,
//        @Expose
//        var special_to_date: String? = null,
//        @Expose
//        var special_from_date: String? = null,
//        @Expose
//        var image_thumbs: MutableList<String>? = null,
        @Expose
        var sale_off:Boolean? = null,
        @Expose
        var shop: ICShop? = null,
        @Expose
        var is_active: Boolean? = null,
        @Expose
        var verified: String? = null,
//        @Expose
//        var cancel_verify_reason: Int? = null,
        @Expose
        var blocked: Boolean? = null,
//        @Expose
//        var block_reason: Int? = null,
//        @Expose
//        var attributes: MutableList<String>? = null,
        @Expose
        var created_at: String? = null,
        @Expose
        var updated_at: String? = null,
        @Expose
        var being_used: Boolean? = null,
        @Expose
        var barcode_verified: Boolean? = null,
        @Expose
        var verified_by_page_id: Int? = null
) : Serializable