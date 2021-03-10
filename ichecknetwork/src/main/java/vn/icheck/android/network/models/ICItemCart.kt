package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICItemCart(
        @Expose var barcode: String?,
        @Expose var quantity: Int,
        @Expose val price: Long,
        @Expose val origin_price: Long,
        @Expose val stock: Int,
        @Expose val item_id: Long,
        @Expose val product_id: Long,
        @Expose val name: String,
        @Expose val image: ICThumbnail?,
        @Expose val thumbnails: ICThumbnail?,
        @Expose val can_add_to_cart: Boolean?,
        @Expose val attributes: MutableList<ICAttributes>?
)