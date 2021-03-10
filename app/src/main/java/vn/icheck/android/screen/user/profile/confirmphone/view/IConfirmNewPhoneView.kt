package vn.icheck.android.screen.user.profile.confirmphone.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.ICUser

/**
 * Created by VuLCL on 11/7/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
interface IConfirmNewPhoneView : BaseActivityView {
    fun onGetDataError()
    fun onGetDataSuccess(phone: String)

    fun onCountDownOtp()
    fun onErrorOtp(errorMessage: String)

    fun onShowLoading()
    fun onCloseLoading()
    fun onUpdateSuccess()
}