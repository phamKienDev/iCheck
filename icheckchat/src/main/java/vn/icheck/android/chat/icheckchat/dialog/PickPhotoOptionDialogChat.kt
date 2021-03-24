package vn.icheck.android.chat.icheckchat.dialog

import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_pick_photo_option_chat.*
import vn.icheck.android.chat.icheckchat.R

abstract class PickPhotoOptionDialogChat(val context: Context) {

    fun show() {
        val dialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)

        dialog.setContentView(R.layout.dialog_pick_photo_option_chat)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)

        dialog.txtCamera.setOnClickListener {
            dialog.dismiss()
            onCamera()
        }

        dialog.txtDocument.setOnClickListener {
            dialog.dismiss()
            onDocument()
        }

        dialog.txtCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    protected abstract fun onCamera()
    protected abstract fun onDocument()
}