package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName

data class ICProductMyReview(
        @SerializedName("myReview") var myReview: ICPost? = null,
        @SerializedName("criteria") val criteria: MutableList<ICCriteriaReview>? = null,
        @SerializedName("link") var link:String?=null
)