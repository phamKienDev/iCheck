package vn.icheck.android.screen.user.listproductcategory.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.ICCategory
import vn.icheck.android.network.models.ICProduct

interface IListProductCategoryView : BaseActivityView {
    fun onGetCollectionIDError()
    fun onGetDataError(error: String)
    fun onGetProductSuccess(obj: MutableList<ICProduct>, isLoadMore: Boolean)
    fun onLoadMore()
    fun onGetDataTryAgain()
    fun onGetListCategorySuccess(obj: MutableList<ICCategory>)
}