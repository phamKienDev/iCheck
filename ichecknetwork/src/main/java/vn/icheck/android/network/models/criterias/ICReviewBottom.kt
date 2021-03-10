package vn.icheck.android.network.models.criterias

import com.google.gson.annotations.Expose
import vn.icheck.android.network.models.ICCriteriaReview
import java.io.Serializable

data class ICReviewBottom(
        @Expose var message: String? = null,
        @Expose var averagePoint: Float = 0F,
        @Expose var customerCriterias: List<ICCriteriaReview>? = null
) : Serializable