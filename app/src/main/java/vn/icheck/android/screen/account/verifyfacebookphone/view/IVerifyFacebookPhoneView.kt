package vn.icheck.android.screen.account.verifyfacebookphone.view

import vn.icheck.android.base.activity.BaseActivityView

/**
 * Created by VuLCL on 9/18/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
interface IVerifyFacebookPhoneView : BaseActivityView {

    fun onErrorPhone(errorMessage: String)
    fun onSetUserInfo(name: String?, avatar: String?)

    fun onShowLoading()
    fun onCloseLoading()

    fun onRegisterFacebookPhone(facebookToken: String?)
}

