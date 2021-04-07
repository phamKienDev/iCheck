package vn.icheck.android.screen.user.product_detail.product.dialog

import android.content.Context
import kotlinx.android.synthetic.main.dialog_contact_business.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialog
import vn.icheck.android.chat.icheckchat.screen.detail.ChatSocialDetailActivity
import vn.icheck.android.constant.Constant
import vn.icheck.android.util.ick.beGone

class ContactBusinessDialog(context: Context) : BaseBottomSheetDialog(context, R.layout.dialog_contact_business, true) {

    fun show(id: Long?, phone: String?, email: String?) {
        if (id == null && phone.isNullOrBlank() && email.isNullOrBlank()) {
            return
        }

        if (!phone.isNullOrEmpty()) {
            dialog.tvPhone.text = phone
        } else {
            dialog.layoutPhone.beGone()
        }

        if (!email.isNullOrEmpty()) {
            dialog.tvEmail.text = email
        } else {
            dialog.layoutEmail.beGone()
        }

        if (id == null) {
            dialog.layoutChat.beGone()
        }

        dialog.tvPhone.setOnClickListener {
            if (!dialog.tvPhone.text.isNullOrEmpty()) {
                Constant.callPhone(dialog.tvPhone.text.toString())
            }
        }

        dialog.tvEmail.setOnClickListener {
            if (!dialog.tvEmail.text.isNullOrEmpty()) {
                Constant.sendEmail(dialog.tvEmail.text.toString())
            }
        }

        dialog.btnChat.setOnClickListener {
//            SocialChatActivity.createPageChat(dialog.context, id)
            ChatSocialDetailActivity.createRoomChat(dialog.context, id ?: -1, "page")
        }

        dialog.imgCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}