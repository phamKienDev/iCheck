package vn.icheck.android.component.commentpost

data class ICCommentPostMore(
        val parentID: Long,
        val totalCount: Int,
        var currentCount: Int,
        var offset: Int,
        var notIDs: String,
        var isLoading: Boolean = false
)