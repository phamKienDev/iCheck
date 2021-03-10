package vn.icheck.android.screen.user.support.view

import vn.icheck.android.base.activity.BaseActivityView

interface IContactAndSupportView : BaseActivityView {

    fun onCallPhone(phone: String)
    fun onSendEmail(email: String)
}