package vn.icheck.android.screen.user.history_loading_card.home.presenter

import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.base.model.ICFragment
import vn.icheck.android.screen.user.history_loading_card.history_buy_topup.HistoryBuyTopupFragment
import vn.icheck.android.screen.user.history_loading_card.history_loaded_topup.HistoryLoadedTopupFragment
import vn.icheck.android.screen.user.history_loading_card.home.view.IHistoryCardView

/**
 * Created by PhongLH on 1/31/2020.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
class HistoryCardPresenter(val view: IHistoryCardView) : BaseActivityPresenter(view) {
    val listFragment: MutableList<ICFragment>
        get() {
            val list = mutableListOf<ICFragment>()

            list.add(ICFragment(view.mContext.getString(R.string.mua_the), HistoryBuyTopupFragment()))
            list.add(ICFragment(view.mContext.getString(R.string.nap_the), HistoryLoadedTopupFragment()))

            return list
        }
}