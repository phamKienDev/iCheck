package vn.icheck.android.screen.user.product_of_shop_history

import vn.icheck.android.network.models.history.ICProductOfShopHistory

interface ProductOfShopHistoryView {
    fun onLoadMore()
    fun onRefresh()
    fun onClickItem(item: ICProductOfShopHistory)
}