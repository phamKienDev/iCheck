package vn.icheck.android.screen.account.verifyforgetpasswordphone.view

import vn.icheck.android.base.activity.BaseActivityView

/**
 * Created by VuLCL on 9/18/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
interface IVerifyPhoneView : BaseActivityView {

    fun onSetPhone(phone: String)
    fun onErrorPhone(errorMessage: String)

    fun onNoInternet()
    fun onShowLoading()
    fun onCloseLoading()
    fun onResetPassword()

//    fun onValidAccountKit(phone: String)
//    fun onValidPhoneSuccess(obj: String)
}

