package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import vn.icheck.android.network.models.history.ICProductScanHistory
import vn.icheck.android.network.models.product.detail.ICProductVariant
import java.io.Serializable

data class ICShopVariant(
        @Expose val bestPrice: Long = 0,
        @Expose val label: String = "",
        @Expose val avatar: String = "",
        @Expose val address: String = "",
        @Expose val isOnline: Boolean = false,
        @Expose val isOffline: Boolean = false,
        @Expose val distance: ICDistance = ICDistance(),

        @Expose val id: Long = 0,
        @Expose val type: String? = null,
        @Expose val shop_id: Long? = null,
        @Expose val product_id: Long? = null,
        @Expose val product: ICProductScanHistory? = null,
        @Expose val name: String? = null,
        @Expose val images: MutableList<String>? = null,
        @Expose val barcode: String? = null,
        @Expose val quantity: Int = 0,
        @Expose val price_min: Long? = null,
        @Expose val price_max: Long? = null,
        @Expose var price: Long = 0,
        @Expose val thumbnails: ICThumbnail? = null,
        @Expose val review_count: Long? = null,
        @Expose val discount: Long? = null,
        @Expose val rating: Long? = null,
        @Expose val point: Int? = null,
        @Expose var special_price: Long = 0,
        @Expose val special_to_date: String? = null,
        @Expose val special_from_date: String? = null,
        @Expose val image_thumbs: List<ICThumbnail>? = null,
        @Expose val shop: ICShop? = null,
        @Expose val is_active: Boolean = false,
        @Expose val is_hot: Boolean = false,
        @Expose val verified: String? = null,
        @Expose val cancel_verify_reason: String? = null,
        @Expose val blocked: Boolean = false,
        @Expose val block_reason: String? = null,
//      @Expose   val attributes
        @Expose val created_at: String? = null,
        @Expose val updated_at: String? = null,
        @Expose val being_used: Boolean = false,
        @Expose var sale_off: Boolean = false,
        @Expose val barcode_verified: Boolean = false,
        @Expose val verified_by_page_id: String? = null
) : Serializable