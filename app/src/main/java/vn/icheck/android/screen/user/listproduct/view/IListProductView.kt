package vn.icheck.android.screen.user.listproduct.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.ICProduct
import vn.icheck.android.network.models.ICProductTrend

interface IListProductView : BaseActivityView {

    fun onSetCollectionName(name: String)
    fun onGetCollectionIDError()
    fun onGetProductError(error: String)
    fun onGetProductSuccess(list: MutableList<ICProductTrend>, isLoadMore: Boolean)
    fun onLoadMoreProduct()
    fun onLayoutMessageClicked()
    fun onProductClicked(product: ICProduct)
}