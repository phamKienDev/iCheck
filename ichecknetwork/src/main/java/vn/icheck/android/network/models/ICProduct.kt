package vn.icheck.android.network.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import vn.icheck.android.network.models.detail_stamp_v6_1.ICObjectDistributor

@Parcelize
data class ICProduct(
        @Expose var sourceId: Long = 0,
        @Expose var id: Long = 0,
        @Expose var name: String? = null,
        @Expose var price: Long = 0,
        @Expose var special_price: Long = 0,
        @Expose var image: String? = null,
        @Expose var barcode: String? = null,
        @Expose var rating: Float = 0F,
        @Expose var review_count: Int = 0,
        @Expose var thumbnails: ICThumbnail? = null,
        @Expose var variants: List<ICShopVariant>? = null,
//        @Expose var vendor: String? = null,
//        @Expose var distributors: ICObjectDistributor? = null,
//        @Expose var other_pages: List<String>? = null,
        @Expose var isDisable_contribution: Boolean = false,
        @Expose var type: String? = null,
        @Expose var isShould_hide_fields: Boolean = false,
        @Expose var verified: Boolean = false,
        @Expose var owner: ICOwner? = null,
        @Expose val media: List<ICMedia>? = null,
        @Expose val status: String? = null,
        @Expose val state: String? = null,
        @SerializedName("categories") val categories: List<ICCategory>? = null,
        @Expose var productId: Long? = null
) : Serializable, Parcelable