package vn.icheck.android.screen.user.map_scan_history

import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.network.models.history.ICStoreNear

interface StoreSellMapHistoryView:IRecyclerViewCallback {
    fun onClickShop(item: ICStoreNear)
}