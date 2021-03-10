package vn.icheck.android.screen.user.store_sell_history

import vn.icheck.android.network.models.history.ICStoreNear

interface StoreSellHistoryView {
    fun onLoadMore()
    fun onRefresh()
    fun onClickItem(listData: MutableList<ICStoreNear>, id: Long?)
}