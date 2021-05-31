package vn.icheck.android.screen.user.social_chat.popup

import android.content.Context
import kotlinx.android.synthetic.main.request_contact_popup.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseDialog
import vn.icheck.android.ichecklibs.ViewHelper

class RequestContactPopup(context: Context,val onAgree:()->Unit,val onTerm:() -> Unit) : BaseDialog(context, R.style.DialogTheme) {

    override val getLayoutID: Int
        get() = R.layout.request_contact_popup
    override val getIsCancelable: Boolean
        get() = false

    override fun onInitView() {
        btnDisagree.background=ViewHelper.bgWhitePressRadiusBottomLeft8(context)
        btnAgree.background=ViewHelper.bgWhitePressRadiusBottomRight8(context)

        btnDisagree.setOnClickListener {
            dismiss()
        }
        btnAgree.setOnClickListener {
            onAgree.invoke()
            dismiss()
        }
        tv_dksd.setOnClickListener {
            onTerm.invoke()
        }
    }
}