package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName

data class ICComments(
        @SerializedName("rows")
        val comments: List<ICProductReviews.Comments>
)