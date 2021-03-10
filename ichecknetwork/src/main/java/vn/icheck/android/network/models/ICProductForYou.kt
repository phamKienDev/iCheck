package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlin.Any

class ICProductForYou(
        @Expose
        @SerializedName("id")
        val id: Int,
        @Expose
        @SerializedName("name")
        val name: String,
        @Expose
        @SerializedName("barcode")
        val barcode: String,
        @Expose
        @SerializedName("price")
        val price: Long = 0,
        @Expose
        @SerializedName("bestPrice")
        val bestPrice: Long = 0,
        @Expose
        @SerializedName("media")
        val media: ICMedia,
        @Expose
        @SerializedName("verified")
        val verified: Boolean,
        @Expose
        @SerializedName("owner")
        val owner: ICOwner,
        @Expose
        @SerializedName("rating")
        val rating: Any?)




