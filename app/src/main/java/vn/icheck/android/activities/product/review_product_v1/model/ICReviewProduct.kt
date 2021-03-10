package vn.icheck.android.screen.user.review_product.model

import vn.icheck.android.network.models.ICCriteria
import vn.icheck.android.network.models.ICProductReviews

data class ICReviewProduct(
        var type: Int = 0,
        var criteria: ICCriteria? = null,
        var reviews: ICProductReviews.ReviewsRow? = null
)