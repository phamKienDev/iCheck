package vn.icheck.android.screen.user.createqrcode.createcontact.view

import vn.icheck.android.screen.user.createqrcode.base.view.IBaseCreateQrCodeView

interface ICreateContactQrCodeView : IBaseCreateQrCodeView {

    fun onInvalidPhone(error: String)
    fun onInvalidAddress(error: String)
    fun onInvalidLastName(error: String)
    fun onInvalidEmail(error: String)
    fun onInvalidFirstName(error: String)
    fun onInvalidPhoneSuccess()
    fun onInvalidFirstNameSuccess()
    fun onInvalidLastNameSuccess()
}