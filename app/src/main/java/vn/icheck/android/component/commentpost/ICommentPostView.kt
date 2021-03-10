package vn.icheck.android.component.commentpost

import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.network.models.ICCommentPost

interface ICommentPostView: IRecyclerViewCallback {

    fun onLoadMoreChildComment(obj: ICCommentPostMore)
    fun onAnswer(obj: ICCommentPost)
    fun onEditComment(obj: ICCommentPost)
    fun onDelete(obj: ICCommentPost)
}