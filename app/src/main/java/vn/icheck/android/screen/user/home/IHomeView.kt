package vn.icheck.android.screen.user.home

import vn.icheck.android.base.activity.BaseActivityView

interface IHomeView : BaseActivityView {
    fun onUpdateUserInfo()
    fun showDialogUpdate()
    fun onUpdateMessageCount(count: String?)
    fun onLogoutFalse()
}