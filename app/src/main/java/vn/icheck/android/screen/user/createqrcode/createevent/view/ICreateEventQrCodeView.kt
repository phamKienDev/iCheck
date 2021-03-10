package vn.icheck.android.screen.user.createqrcode.createevent.view

import vn.icheck.android.screen.user.createqrcode.base.view.IBaseCreateQrCodeView

interface ICreateEventQrCodeView : IBaseCreateQrCodeView {

    fun onInvalidEventName(error: String)
    fun onInvalidEventAddress(error: String)
    fun onInvalidEventLink(error: String)
    fun onInvalidStartDate(error: String)
    fun onInvalidEndDate(error: String)
    fun onInvalidStartDateSuccess()
    fun onInvalidEndDateSuccess()
    fun onInvalidEventLinkSuccess()
    fun onInvalidEventAddressSuccess()
    fun onInvalidEventNameSuccess()
}