package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

class ICReqCriteriaReview(
        @Expose var criteriaId: Long,
        @Expose var point: Float,
        @Expose var criteriaSetId: Long
)