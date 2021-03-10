package vn.icheck.android.screen.user.suggest_store_history

import vn.icheck.android.network.models.history.ICSuggestStoreHistory

interface SuggestStoreHistoryView {
    fun onLoadMore()
    fun onRefresh()
    fun onClickShowProduct(item: ICSuggestStoreHistory)
    fun onClickGotoMap(item: ICSuggestStoreHistory)
}