package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ICCheckoutItem(
        @SerializedName("barcode") val barcode: String,
        @SerializedName("price") val price: Long,
        @SerializedName("origin_price") val origin_price: Long,
        @SerializedName("quantity") val quantity: Int,
        @SerializedName("item_id") val item_id: Long,
        @SerializedName("product_id") val product_id: Long,
        @SerializedName("stock") val stock: Int,
        @SerializedName("can_add_to_cart") val can_add_to_cart: Boolean,
        @SerializedName("image") val image: ICThumbnail?,
        @SerializedName("name") val name: String
)