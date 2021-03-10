package vn.icheck.android.network.models

data class ICProductReviewsV2(
        val customerCriteria: List<ICCriteriaReview>? = null,
        var expressiveCount: Int = 0,
        val targetId: Int? = null,
        val targetType: String? = null,
        val involveType: String? = null,
        val media: List<ICMedia?>? = null,
        val disabeNotify: Any? = null,
        val content: String = "",
        val commentCount: Int = 0,
        val avgPoint: Float = 0f,
        val shareCount: Int? = null,
        val createdAt: String? = null,
        var expressive: String? = null,
        val id: Long? = null,
        val page: ICPage? = null,
        val viewCount: Int? = null,
        val user: ICUser? = null,
        val updatedAt: String? = null,
        var marginTop: Int = 0
)
