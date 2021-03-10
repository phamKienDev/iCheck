package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ICCriteria(
        @SerializedName("product_evaluation")
        val productEvaluation: ProductEvaluation?,
        @SerializedName("percent_customer_will_suggest")
        val percentSuggest: Float?,
        @SerializedName("total_reviews")
        val totalReviews:Int?,
        @SerializedName("product_evaluation_gather")
        val productGather: List<ProductEvaluation>?,
        @SerializedName("customer_evaluation")
        val customerEvaluation: CustomerEvaluation?,
        @SerializedName("product_criteria_set")
        val productCriteriaSet: List<ProductCriteriaSet>?,
        @SerializedName("force_review")
        var forceReview:Boolean,
        @SerializedName("should_review")
        val shouldReview: ShouldReview?
):Serializable

data class ShouldReview(
        @SerializedName("verified_seconds")
        val verifiedSeconds:Int?,
        @SerializedName("unverified_seconds")
        val unverifiedSecond:Int?
):Serializable

data class ProductEvaluation(
        @SerializedName("average_point")
        val averagePoint: Float?,
        @SerializedName("criteria")
        val criteria: Criteria
): Serializable

data class CustomerEvaluation(
        @SerializedName("id")
        val id: Long,
        @SerializedName("message")
        val message: String,
        @SerializedName("average_point")
        val averagePoint: Float,
        @SerializedName("customer_criteria_value")
        val customerCriteria: List<CriteriaValue>,
        @SerializedName("image_thumbs")
        val imageThumbs: List<ICProductReviews.ImageThumbs>
):Serializable

data class ProductCriteriaSet(
        @SerializedName("criteria")
        val criteria: Criteria,
        val position: Int,
        val weight:Float,
        val point: Int?
):Serializable

data class CriteriaValue(
        @SerializedName("point")
        val point: Int,
        @SerializedName("criteria_id")
        val criteria_id: Long
):Serializable

data class Criteria(
        @SerializedName("name")
        val name: String,
        val id: Long,

        val code:String
):Serializable