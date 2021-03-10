package vn.icheck.android.screen.account.registerfacebookphone.view

import vn.icheck.android.base.activity.BaseActivityView

/**
 * Created by VuLCL on 9/17/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
interface RegisterFacebookPhoneView : BaseActivityView {

    fun onGetPhoneError()

    fun onErrorOtp(errorMessage: String)
    fun onErrorPassword(errorMessage: String)
    fun onErrorRePassword(errorMessage: String)

    fun onShowLoading()
    fun onCloseLoading()

    fun onCountDownOtp()
    fun onRegisterSuccess()
    fun onLoginSuccess()
}

