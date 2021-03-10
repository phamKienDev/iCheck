package vn.icheck.android.screen.user.map_scan_history

import vn.icheck.android.network.models.history.ICStoreNear

interface StoreSellMapHistoryView {
    fun onClickShop(item: ICStoreNear)
}