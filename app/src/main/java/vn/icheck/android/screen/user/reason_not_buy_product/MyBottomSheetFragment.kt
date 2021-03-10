package vn.icheck.android.screen.user.reason_not_buy_product

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import vn.icheck.android.R

class MyBottomSheetFragment : BottomSheetDialogFragment() {
    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val bottomSheetDialog = dialog as BottomSheetDialog
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_reason_not_buy_product)
        try {
            val behaviorField = bottomSheetDialog.javaClass.getDeclaredField("behavior")
            behaviorField.isAccessible = true
            val behavior = behaviorField[bottomSheetDialog] as BottomSheetBehavior<*>
            behavior.setBottomSheetCallback(object : BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}