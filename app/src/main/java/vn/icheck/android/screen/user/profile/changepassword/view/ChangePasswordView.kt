package vn.icheck.android.screen.user.profile.changepassword.view

import vn.icheck.android.base.activity.BaseActivityView

/**
 * Created by VuLCL on 11/7/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
interface ChangePasswordView : BaseActivityView {

    fun onErrorOldPassword(error: String)
    fun onErrorPassword(error: String)
    fun onErrorRePassword(error: String)

    fun onShowLoading()
    fun onCloseLoading()
    fun onChangePasswordSuccess()
}

