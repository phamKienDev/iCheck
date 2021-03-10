package vn.icheck.android.screen.user.createqrcode.createmessage.presenter

import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentPresenter
import vn.icheck.android.helper.ValidHelper
import vn.icheck.android.screen.user.createqrcode.createmessage.view.ICreateMessageQrCodeView

/**
 * Created by VuLCL on 10/7/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CreateMessageQrCodePresenter(val view: ICreateMessageQrCodeView) : BaseFragmentPresenter(view) {

    fun validData(phone: String, message: String) {
        var isSuccess = true

        val validPhone = ValidHelper.validAllPhoneNumber(view.mContext, phone)

        if (validPhone != null) {
            isSuccess = false
            view.onInvalidPhone(validPhone)
        } else {
            view.onInvalidPhoneSuccess()
        }

        val validMessage = ValidHelper.validContent(view.mContext, message)

        if (validMessage != null) {
            isSuccess = false
            view.onInvalidContent(validMessage)
        } else {
            view.onInvalidContentSuccess()
        }

        if (!isSuccess) {
            return
        }

        view.onValidSuccess(view.mContext!!.getString(R.string.qr_code_send_sms_format, phone, message))
    }
}