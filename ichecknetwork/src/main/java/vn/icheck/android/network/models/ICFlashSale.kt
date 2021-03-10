package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ICFlashSale(
        @Expose
        @SerializedName("id")
        var id: Int,
        @Expose
        @SerializedName("title")
        val title: String,
        @Expose
        @SerializedName("startTime")
        val startTime: String,
        @Expose
        @SerializedName("endTime")
        val endTime: String,
        @Expose
        @SerializedName("products")
        var products: List<Products>) {

    data class Products(
            @Expose
            @SerializedName("id")
            var id: Int,
            @Expose
            @SerializedName("name")
            val name: String,
            @Expose
            @SerializedName("barcode")
            val barcode: String,
            @Expose
            @SerializedName("price")
            var price: Int,
            @Expose
            @SerializedName("bestprice")
            var bestprice: Int,
            @Expose
            @SerializedName("media")
            var media: Media,
            @Expose
            @SerializedName("verified")
            var verified: Boolean,
            @Expose
            @SerializedName("owner")
            var owner: Owner,
            @Expose
            @SerializedName("hasSold")
            var hasSold: Int)

    data class Owner(
            @Expose
            @SerializedName("id")
            var id: Int,
            @Expose
            @SerializedName("name")
            val name: String,
            @Expose
            @SerializedName("avatar")
            val avatar: String)

    data class Media(
            @Expose
            @SerializedName("content")
            val content: String,
            @Expose
            @SerializedName("type")
            val type: String)
}
