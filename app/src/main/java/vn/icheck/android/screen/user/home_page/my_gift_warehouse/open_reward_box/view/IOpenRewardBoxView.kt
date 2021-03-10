package vn.icheck.android.screen.user.home_page.my_gift_warehouse.open_reward_box.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.ICBoxReward
import vn.icheck.android.network.models.ICUnBox_Gift

interface IOpenRewardBoxView : BaseActivityView {
    fun onGetDataIdSuccess(item: ICBoxReward)
    fun onGetDataError(type: Int)
    fun onUnboxGiftSuccess(obj: MutableList<ICUnBox_Gift>, numberUnboxIcheck: Int)
}