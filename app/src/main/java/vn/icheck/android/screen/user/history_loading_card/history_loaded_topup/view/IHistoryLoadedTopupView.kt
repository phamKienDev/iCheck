package vn.icheck.android.screen.user.history_loading_card.history_loaded_topup.view

import vn.icheck.android.base.fragment.BaseFragmentView
import vn.icheck.android.network.models.recharge_phone.IC_RESP_HistoryBuyTopup

interface IHistoryLoadedTopupView {
    fun onLoadMore()
    fun onRefresh()
}