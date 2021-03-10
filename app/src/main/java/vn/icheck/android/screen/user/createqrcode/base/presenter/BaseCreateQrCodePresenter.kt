package vn.icheck.android.screen.user.createqrcode.base.presenter

import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentPresenter
import vn.icheck.android.helper.ValidHelper
import vn.icheck.android.screen.user.createqrcode.base.view.IBaseCreateQrCodeView

/**
 * Created by VuLCL on 10/5/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class BaseCreateQrCodePresenter(val view: IBaseCreateQrCodeView) : BaseFragmentPresenter(view) {

    fun validText(text: String) {
        val validText = ValidHelper.validContent(view.mContext, text)

        if (validText != null) {
            showError(validText)
            return
        }

        view.onValidSuccess(text)
    }

    fun validLink(link: String) {
        val validLink = ValidHelper.validLink(view.mContext, link)

        if (validLink != null) {
            showError(validLink)
            return
        }

//        if (link.length >= 8 && link.subSequence(0, 8) == "https://") {
            view.onValidSuccess(link)
//        } else if (link.length >= 7 && link.subSequence(0, 7) == "http://") {
//            listener.onValidSuccess(link)
//        } else {
//            listener.onValidSuccess(listener.mContext!!.getString(R.string.qr_code_open_link_format, link))
//        }
    }

    fun validPhone(phone: String) {
        val validPhone = ValidHelper.validAllPhoneNumber(view.mContext, phone)

        if (validPhone != null) {
            showError(validPhone)
            return
        }

        view.onValidSuccess(view.mContext!!.getString(R.string.qr_code_call_phone_format, phone))
    }
}