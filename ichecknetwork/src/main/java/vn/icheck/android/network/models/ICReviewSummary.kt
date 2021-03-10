package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName
import vn.icheck.android.network.models.criterias.ICReviewBottom

data class ICReviewSummary(
        @SerializedName("avgPoint") val averagePoint: Float,
        @SerializedName("objectCriteria") val objectCriterias: List<ICReviewBottom>,
        @SerializedName("ratingCount") val ratingCount: Float,
        @SerializedName("willShare") val willShare: Float
)