package vn.icheck.android.screen.user.createqrcode.createmessage.view

import vn.icheck.android.screen.user.createqrcode.base.view.IBaseCreateQrCodeView

interface ICreateMessageQrCodeView : IBaseCreateQrCodeView {

    fun onInvalidPhone(error: String)
    fun onInvalidContent(error: String)
    fun onInvalidPhoneSuccess()
    fun onInvalidContentSuccess()
}