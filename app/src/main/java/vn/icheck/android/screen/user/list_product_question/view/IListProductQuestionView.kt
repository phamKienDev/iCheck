package vn.icheck.android.screen.user.list_product_question.view

import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.component.commentpost.ICCommentPostMore
import vn.icheck.android.network.models.ICProductQuestion

interface IListProductQuestionView : IRecyclerViewCallback {
    fun onLoadMoreAnswer(obj:ICCommentPostMore)
    fun onAnswer(obj: ICProductQuestion)
    fun onEdit(obj: ICProductQuestion)
    fun onDelete(obj: ICProductQuestion)
    fun hideKeyboard()
}