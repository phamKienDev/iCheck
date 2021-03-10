package vn.icheck.android.network.models

import vn.icheck.android.network.models.criterias.ICReviewBottom
import kotlin.Any

data class ICReviewSearch(
        val pinned: Boolean? = null,
        val targetId: Int? = null,
        val targetType: String? = null,
        val involveType: String? = null,
        var likeCount: Int = 0,
        var expressiveCount: Int = 0,
        var expressived: String? = null,
        var expressive: String? = null,
        val pageId: Long? = null,
        val userId: Long? = null,
        val disabeNotify: Any? = null,
        val content: String? = null,
        val commentCount: Int? = null,
        val shareCount: Int? = null,
        val avgPoint: String? = null,
        val createdAt: String? = null,
        val deletedAt: Any? = null,
        val attachment: List<String?>? = null,
        val media: List<ICMedia?>? = null,
        val meta: Meta? = null,
        val id: Long = 0,
        val viewCount: Int? = null,
        val page: ICPage? = null,
        val involveId: Int? = null,
        val user: ICTargetUser? = null,
        val updatedAt: String? = null,
        val customerCriteria: MutableList<ICCriteriaReview>? = null
)

data class Meta(
        val criteria: List<ICCriteriaReview?>? = null,
        val product: ICProductV2? = null
)

