package vn.icheck.android.screen.account.registeruser.inputotp.view

import vn.icheck.android.base.activity.BaseActivityView

interface IRegisterUserOtpView : BaseActivityView {

    fun onGetDataError()
    fun onGetDataSuccess(phone: String)

    fun onCountDownOtp()
    fun onErrorOtp(errorMessage: String)

    fun onNoInternet(type: Int)
    fun onShowLoading()
    fun onCloseLoading()
    fun onRegisterSuccess(phone: String)
}