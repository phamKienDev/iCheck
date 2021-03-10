package vn.icheck.android.network.models.product_detail

import com.google.gson.annotations.SerializedName
import vn.icheck.android.network.models.ICMedia
import vn.icheck.android.network.models.ICPage
import vn.icheck.android.network.models.ICProductContribution
import vn.icheck.android.network.models.ICProductInformations

data class IckProductDetailLayoutModel(
        @SerializedName("statusCode") val statusCode: String,
        @SerializedName("message") val message: String,
        val layout: List<LayoutItem>?,
        val data: DataItem
) {
    data class LayoutItem(
            val id: String,
            val key: String,
            val request: RequestItem?
    )

    data class RequestItem(
            val type: String,
            val url: String
    )

    data class DataItem(
            val id: Long,
            val name: String?,
            val barcode: String?,
            val price: Long?,
            @SerializedName("bestprice")
            val bestPrice: Long?,
            val verified: Boolean?,
            val rating: Int?,
            val attachments: List<ICMedia>?,
            val media: List<ICMedia>?,
            val owner: ICPage?,
            val vendors: MutableList<ICPage>?,
            val distributors: MutableList<ICPage>?,
            val information: MutableList<ICProductInformations>?,
            val contribution: ICProductContribution?,
            val certificates: List<String>?,
            val criterias: List<Criterias>?,
            val basicInfo: BasicInfo?
    )

    data class BasicInfo(
            val name: String?,
            val price: Long?,
            val status:String?,
            val state:String?,
            val reviewCount:Int?,
            val questionCount:Int?,
            val scanCount:Int?
    )

    data class Criterias(
            val id: Long?,
            val name: String?,
            val position: Int?,
            var point:Float?
    )
}