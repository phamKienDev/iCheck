package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ICProductReviews(
        val rows: List<ReviewsRow>
) : Serializable {

    data class ReviewsRow(
            val id: Long,
            @SerializedName("customer_id")
            val customerId: Long,
            var message: String,
            var useful: Long,
            var unuseful: Long,
            var unUseful: Long,
//            @SerializedName("object_id")
//            val objectId: Long,
//            @SerializedName("object_type")
//            val objectType: String,
            @SerializedName("image_thumbs")
            val imageThumbs: List<ImageThumbs>,
            val owner: Owner,
//            @SerializedName("updated_at")
//            val updateAt: String,
            @SerializedName("average_point")
            val averagePoint: Float,
            @SerializedName("customer_criteria_value")
            val customerCriteria: List<ProductCriteriaSet>,
            @SerializedName("comments_count")
            var commentCounts: Int,
            @SerializedName("comments")
            var comments: List<Comments>?,
            @SerializedName("created_at")
            val createAt: String,
            @SerializedName("action_useful")
            var actionUseful: String?
    ) : Serializable

    data class ImageThumbs(
            val thumbnail: String?,
            val original:String?,
            val small:String?
    ) : Serializable

    data class Owner(
            val name: String?,
            val avatar: String?,
            @SerializedName("avatar_thumbnails")
            val avatarThumb: ImageThumbs?,
            val type:String?,
            val id:Long?
    ) : Serializable

    data class Comments(
            @SerializedName("owner")
            var owner: Owner?,
            @SerializedName("image_thumbs")
            val imageThumbs: List<ImageThumbs>,
            @SerializedName("activity_value")
            val activityValue: String,
            @SerializedName("created_at")
            val createAt: String,
            @SerializedName("owner_type")
            val ownerType:String,
            @SerializedName("customer_id")
            val customerId:Long
    ) : Serializable
}