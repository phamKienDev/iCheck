package vn.icheck.android.network.models.v1

import com.google.gson.annotations.SerializedName

data class ICRelatedProductV1(
        @SerializedName("rows")
        val rows: List<RelatedProductRow>
) {
    data class RelatedProductRow(
            @SerializedName("id")
            val id: Long,
            @SerializedName("name")
            val name: String?,
            @SerializedName("price")
            val price: Long,
            @SerializedName("image")
            val image: String,
            @SerializedName("rating")
            val rating: Float,
            @SerializedName("thumbnails")
            val thumbnails: Thumbnails?,
            @SerializedName("barcode")
            val barcode: String,
            @SerializedName("verified")
            var verified: Boolean?,
            @SerializedName("should_hide_fields")
            val shouldHide:Boolean
    )

    data class Thumbnails(
            @SerializedName("original")
            val original: String?,
            @SerializedName("small")
            val small:String?
    )
}