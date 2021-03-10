package vn.icheck.android.screen.user.home_page.my_gift_warehouse.open_reward_box_tet.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.ICBoxReward
import vn.icheck.android.network.models.ICUnBox_Gift

/**
 * Created by PhongLH on 3/23/2020.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
interface IOpenRewardBoxTetView : BaseActivityView {
    fun onGetDataIdSuccess(item: ICBoxReward)
    fun onGetDataError(type: Int)
    fun onUnboxGiftSuccess(obj: MutableList<ICUnBox_Gift>, numberUnboxIcheck: Int)
}