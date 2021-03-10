package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ICPostHistory(
        @Expose
        @SerializedName("scan_lat")
        val scanLat: Long,
        @Expose
        @SerializedName("scan_lng")
        val scanLng: Long,
        @Expose
        @SerializedName("id")
        val id: Long,
        @Expose
        @SerializedName("user_id")
        val userId: Long,
        @Expose
        @SerializedName("product_id")
        val productId: Long,
        @Expose
        @SerializedName("price")
        val price: Long,
        @Expose
        @SerializedName("scan_address")
        val scanAddress: String,
        @Expose
        @SerializedName("created_at")
        val createdAt: String,
        @Expose
        @SerializedName("updated_at")
        val updatedAt: String,
        @Expose
        @SerializedName("product")
        val product: Product
)

data class Product(
        @Expose
        @SerializedName("id")
        val id: Long,
        @Expose
        @SerializedName("barcode")
        val barcode: String,
        @Expose
        @SerializedName("price")
        val price: Long,
        @Expose
        @SerializedName("name")
        val name: String,
        @Expose
        @SerializedName("image")
        val image: String
)