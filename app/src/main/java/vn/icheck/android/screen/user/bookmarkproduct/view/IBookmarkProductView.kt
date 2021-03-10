package vn.icheck.android.screen.user.bookmarkproduct.view

import vn.icheck.android.base.fragment.BaseFragmentView
import vn.icheck.android.network.models.ICHistory_Product

interface IBookmarkProductView : BaseFragmentView {
    fun onGetListProductBookmarkError(error: String)
    fun onGetListProductBookmarkSuccess(obj: MutableList<ICHistory_Product>, isLoadMore: Boolean)
    fun onLoadMore()
    fun onLayoutMessageClicked()
}