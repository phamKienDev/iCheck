package vn.icheck.android.screen.user.coinhistory

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.ICCoinHistory

interface ICoinHistoryView  {
    fun onLoadmore()
    fun onTryAgain()
}