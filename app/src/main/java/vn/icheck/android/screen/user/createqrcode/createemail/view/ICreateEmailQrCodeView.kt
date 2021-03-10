package vn.icheck.android.screen.user.createqrcode.createemail.view

import vn.icheck.android.screen.user.createqrcode.base.view.IBaseCreateQrCodeView

interface ICreateEmailQrCodeView : IBaseCreateQrCodeView {

    fun onInvalidEmail(error: String)
    fun onInvalidTitle(error: String)
    fun onInvalidContent(error: String)
}