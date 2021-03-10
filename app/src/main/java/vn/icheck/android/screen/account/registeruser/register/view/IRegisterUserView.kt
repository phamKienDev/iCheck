package vn.icheck.android.screen.account.registeruser.register.view

import vn.icheck.android.base.activity.BaseActivityView

interface IRegisterUserView : BaseActivityView {

    fun onErrorPhone(errorMessage: String)
    fun onErrorLastName(errorMessage: String)
    fun onErrorFirstName(errorMessage: String)
    fun onErrorPassword(errorMessage: String)
    fun onErrorRePassword(errorMessage: String)

    fun onNoInternet()
    fun showLoading()
    fun closeLoading()

    fun onCheckPhoneSuccess(json: String)
}