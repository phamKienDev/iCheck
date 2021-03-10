package vn.icheck.android.loyalty.dialog

import android.content.Context
import kotlinx.android.synthetic.main.dialog_oot_game_v2.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.dialog.base.BaseDialog

abstract class DialogOotGame(context: Context, val title: String, val message: String, val icon: Int) : BaseDialog(context, R.style.DialogTheme) {
    override val getLayoutID: Int
        get() = R.layout.dialog_oot_game_v2
    override val getIsCancelable: Boolean
        get() = false

    override fun onInitView() {
        imgClose.setOnClickListener {
            dismiss()
        }

        btnScan.setOnClickListener {
            dismiss()
            onClick()
        }

        imgIcon.setImageResource(icon)
        tvTitle.text = title
        tvMessage.text = message
    }

    abstract fun onClick()
}