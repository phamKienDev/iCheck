package vn.icheck.android.network.models.product.detail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import vn.icheck.android.network.models.AvatarThumbnails
import vn.icheck.android.network.models.ICShopVariant
import java.io.Serializable

class ICProductVariant(
        @Expose
        val count: Int?,
        @Expose
        val rows: MutableList<ProductRow>
) : Serializable {

    data class ProductRow(
            @Expose val name: String,
            @Expose val id: Long,
            @Expose val special_price: Long,
            @Expose val price: Long,
            @Expose val bestPrice: Long,
            @Expose val label: String,
            @Expose val avatar: String,
            @Expose val address: String,
            @Expose val isOnline: Boolean,
            @Expose val isOffline: Boolean,
            @Expose val distance: Distance,
            @Expose val sale_off: Boolean,
            @Expose val quantity: Long,
            @Expose val shop: VariantShop,
            @Expose val product_id: Long
    ) : Serializable

    data class VariantShop(
            @Expose val name: String,
            @Expose val phone: String,
            @Expose val email: String,
            @Expose val address: String,
            @Expose val is_online: Boolean,
            @Expose val is_offline: Boolean,
            @Expose val avatar_thumbnails: AvatarThumbnails,
            @Expose var location: Location,
            @Expose val distance: Distance,
            @Expose val id: String,
            @Expose val rating: Float,
            @Expose val city: City?,
            @Expose val district: City?,
            @Expose val ward: City?,
            @Expose val verified: Boolean
    ) : Serializable

    class City(
            @Expose val id: Long,
            @Expose val name: String
    ) : Serializable

    data class Distance(
            @Expose val unit: String,
            @Expose val value: Double
    ) : Serializable

    data class Location(
            @Expose var lat: Double? = null,
            @Expose var lon: Double? = null
    ) : Serializable
}