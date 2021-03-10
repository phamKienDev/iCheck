package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICShippingMethos(
        @Expose var shipping_amount: Long,
//        @SerializedName("estimated_delivery_date_to")
        @Expose var estimated_delivery_date_to: String?,
        @Expose var id: Int?,
        @Expose var name: String?,
        @Expose var method: List<ICMethos>?
) {
    data class ICMethos(
            @Expose var id: Int?,
            @Expose var name: String?
    )
}