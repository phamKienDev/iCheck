package vn.icheck.android.screen.user.createqrcode.createemail.presenter

import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentPresenter
import vn.icheck.android.helper.ValidHelper
import vn.icheck.android.screen.user.createqrcode.createemail.view.ICreateEmailQrCodeView

/**
 * Created by VuLCL on 10/7/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CreateEmailQrCodePresenter(val view: ICreateEmailQrCodeView) : BaseFragmentPresenter(view) {

    fun validData(email: String, title: String, message: String) {
        var isSuccess = true

//        val validMessage = ValidHelper.validContent(view.mContext, message)
//
//        if (validMessage != null) {
//            isSuccess = false
//            view.onInvalidContent(validMessage)
//        }

        val validEmail = ValidHelper.validEmail(view.mContext, email)

        if (validEmail != null) {
            isSuccess = false
            view.onInvalidEmail(validEmail)
        }

        if (!isSuccess) {
            return
        }

        view.onValidSuccess(view.mContext!!.getString(R.string.qr_code_send_emal_format, email, title, message))
    }
}