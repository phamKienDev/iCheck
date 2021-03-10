package vn.icheck.android.screen.account.resetpassword.view

import vn.icheck.android.base.activity.BaseActivityView

/**
 * Created by VuLCL on 9/18/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
interface IResetPasswordView : BaseActivityView {

    fun onGetDataError()
    fun onGetDataSuccess()

    fun onNoInternet(type: Int)
    fun showLoading()
    fun closeLoading()

    fun onCountDownOtp()

    fun onErrorOtp(errorMessage: String)
    fun onErrorPassword(errorMessage: String)
    fun onErrorRePassword(errorMessage: String)

    fun onResetPasswordSuccess()
}

