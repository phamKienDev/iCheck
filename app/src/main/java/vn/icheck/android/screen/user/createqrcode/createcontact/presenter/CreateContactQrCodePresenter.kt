package vn.icheck.android.screen.user.createqrcode.createcontact.presenter

import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentPresenter
import vn.icheck.android.helper.ValidHelper
import vn.icheck.android.screen.user.createqrcode.createcontact.view.ICreateContactQrCodeView

/**
 * Created by VuLCL on 10/7/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CreateContactQrCodePresenter(val view: ICreateContactQrCodeView) : BaseFragmentPresenter(view) {

    fun validData(phone: String, firstName: String, middleName: String, lastName: String, email: String, address: String, note: String) {
        var isSuccess = true

        val validPhone = ValidHelper.validAllPhoneNumber(view.mContext, phone)

        if (validPhone != null) {
            isSuccess = false
            view.onInvalidPhone(validPhone)
        } else {
            view.onInvalidPhoneSuccess()
        }

        val validFirstName = ValidHelper.validFirstName(view.mContext, firstName)

        if (validFirstName != null) {
            isSuccess = false
            view.onInvalidFirstName(validFirstName)
        } else {
            view.onInvalidFirstNameSuccess()
        }

//        val validMiddleName = ValidHelper.validMiddleName(view.mContext, middleName)
//
//        if (validMiddleName != null) {
//            isSuccess = false
//            view.onInvalidFMiddleName(validMiddleName)
//        }

        val validLastName = ValidHelper.validLastName(view.mContext, lastName)

        if (validLastName != null) {
            isSuccess = false
            view.onInvalidLastName(validLastName)
        } else {
            view.onInvalidLastNameSuccess()
        }

//        val validEmail = ValidHelper.validEmail(view.mContext, email)
//
//        if (validEmail != null) {
//            isSuccess = false
//            view.onInvalidEmail(validEmail)
//        }

//        val validAddress = ValidHelper.validAddress(view.mContext, address)
//
//        if (validAddress != null) {
//            isSuccess = false
//            view.onInvalidAddress(validAddress)
//        }

        if (!isSuccess) {
            return
        }

        val middle = if (middleName.isNotEmpty()) {
            "$middleName;"
        } else {
            ""
        }

        val name = "$lastName;$middle;$firstName"

        val mAddress = if (address.trim().isNotEmpty()) {
            "ADR:;;$address\n"
        } else {
            ""
        }

        val mNote = if (note.trim().isNotEmpty()) {
            "NOTE:$note\n"
        } else {
            ""
        }

        val code = "BEGIN:VCARD\n" +
                "N:$name\n" +
                "TEL:$phone\n" +
                "EMAIL:$email\n" +
                mAddress +
                mNote +
                "END:VCARD"

        view.onValidSuccess(code)
    }
}