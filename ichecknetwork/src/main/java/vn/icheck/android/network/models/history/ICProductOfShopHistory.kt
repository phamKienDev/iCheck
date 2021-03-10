package vn.icheck.android.network.models.history

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ICProductOfShopHistory(

        @Expose val image: String? = null,

        @Expose val productId: Int? = null,

        @Expose val reviewCount: Int? = null,

        @Expose val price: Long? = null,

        @Expose val name: String? = null,

        @Expose val rating: Float? = null,

        @Expose val verified: Boolean? = null,

        @Expose val id: Long? = null,

        @Expose val shopId: Int? = null,

        @Expose val state: String? = null,

        @Expose val barcode: String? = null,

        @Expose val status: String? = null) : Serializable
