package vn.icheck.android.screen.user.history_loading_card.history_buy_topup.view

import vn.icheck.android.network.models.recharge_phone.ICRechargePhone

interface IHistoryBuyTopupView {
    fun onLoadMore()
    fun onRefresh()
    fun onClickLoadNow(item: ICRechargePhone)
}