package vn.icheck.android.ichecklibs.take_media

import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_pick_photo_option.*
import vn.icheck.android.ichecklibs.R
import vn.icheck.android.ichecklibs.ViewHelper

abstract class PickCameraOptionDialog(val context: Context) {
    fun show() {
        val dialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)

        dialog.setContentView(R.layout.dialog_pick_photo_option)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)

        dialog.txtDocument.background= ViewHelper.bgWhitePressRadiusBottom8(dialog.context)
        dialog.txtCancel.background= ViewHelper.bgWhitePressRadius8(dialog.context)

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