package vn.icheck.android.screen.user.recharge_phone.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.models.recharge_phone.ICRechargePhone

interface IRechargePhoneView :BaseActivityView {
    fun onGetDataError(typeMessage: Int)
    fun onGetDataSuccess(obj: MutableList<ICRechargePhone>)
    fun onGetPointIcheckSuccess(pointIcheck: Long)
}