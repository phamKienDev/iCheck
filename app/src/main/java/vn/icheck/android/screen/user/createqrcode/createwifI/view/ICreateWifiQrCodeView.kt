package vn.icheck.android.screen.user.createqrcode.createwifI.view

import vn.icheck.android.screen.user.createqrcode.base.view.IBaseCreateQrCodeView

interface ICreateWifiQrCodeView : IBaseCreateQrCodeView {

    fun onInValidName(error: String)
    fun onInValidPassword(error: String)
    fun onInValidPasswordSuccess()
    fun onInValidNameSuccess()
}