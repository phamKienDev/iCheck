package vn.icheck.android.loyalty.dialog

import android.content.Context
import kotlinx.android.synthetic.main.dialog_guide_play_game.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.dialog.base.BaseDialog

abstract class DialogGuidePlayGame(context: Context) : BaseDialog(context, R.style.DialogTheme) {
    override val getLayoutID: Int
        get() = R.layout.dialog_guide_play_game
    override val getIsCancelable: Boolean
        get() = false

    override fun onInitView() {
        imgClose.setOnClickListener {
            dismiss()
        }

        btnScanNow.setOnClickListener {
            dismiss()
            onClick()
        }
    }

    abstract fun onClick()
}