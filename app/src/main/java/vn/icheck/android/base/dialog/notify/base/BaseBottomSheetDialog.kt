package vn.icheck.android.base.dialog.notify.base

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import vn.icheck.android.R

open class BaseBottomSheetDialog {
    var dialog: BottomSheetDialog

    constructor(context: Context, isCancelable: Boolean) {
        dialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        dialog.window?.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        setCancelable(isCancelable)
    }

    constructor(context: Context, layout: Int, isCancelable: Boolean) {
        dialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        dialog.setContentView(layout)
        dialog.window?.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        setCancelable(isCancelable)
    }

    constructor(context: Context, layout: View, isCancelable: Boolean) {
        dialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        dialog.setContentView(layout)
        dialog.window?.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        setCancelable(isCancelable)
    }

    private fun setCancelable(isCancelable: Boolean) {
        dialog.setCanceledOnTouchOutside(isCancelable)
        dialog.setCancelable(isCancelable)
    }
}