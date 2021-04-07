package vn.icheck.android.ichecklibs.base_dialog

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import vn.icheck.android.ichecklibs.R


open class BaseBottomSheetDialogFragment: BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme).also { dialog ->
                dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                dialog.setOnShowListener {
                    val bottomSheet = dialog.findViewById<FrameLayout>(R.id.design_bottom_sheet)
                    val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet!!)
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
}