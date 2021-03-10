package vn.icheck.android.screen.dialog

import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_my_profile_setting.*
import vn.icheck.android.R

abstract class MyProfileSettingDialog(val context: Context) {

    fun show() {
        val dialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)

        dialog.setContentView(R.layout.dialog_my_profile_setting)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)

        dialog.txtChangeAvatar.setOnClickListener {
            dialog.dismiss()
            onChangeAvatar()
        }

        dialog.txtChangeCover.setOnClickListener {
            dialog.dismiss()
            onChangeCover()
        }

        dialog.txtUpdateInfo.setOnClickListener {
            dialog.dismiss()
            onUpdateInfo()
        }

        dialog.txtCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            onDismiss()
        }

        dialog.show()
    }

    protected abstract fun onChangeAvatar()
    protected abstract fun onChangeCover()
    protected abstract fun onUpdateInfo()
    protected abstract fun onDismiss()
}