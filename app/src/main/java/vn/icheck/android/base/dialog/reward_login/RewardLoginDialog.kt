package vn.icheck.android.base.dialog.reward_login

import android.content.Context
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseDialog
import vn.icheck.android.ichecklibs.ViewHelper

abstract class RewardLoginDialog(context: Context) : BaseDialog(context, R.style.DialogTheme) {

    private var isStartLogin = false

    override val getLayoutID: Int
        get() = R.layout.dialog_reward_login

    override val getIsCancelable: Boolean
        get() = true

    override fun onInitView() {
        findViewById<AppCompatTextView>(R.id.btnLogin)?.setOnClickListener {
            isStartLogin = true
            dismiss()
            onLogin()
        }

        findViewById<AppCompatTextView>(R.id.btnRegister)?.apply {
            background = ViewHelper.bgOutlinePrimary1Corners4(context)
            setOnClickListener {
                isStartLogin = true
                dismiss()
                onRegister()
            }
        }

        findViewById<AppCompatTextView>(R.id.tvClose)?.setOnClickListener {
            dismiss()
            onDismiss()
        }

        setOnDismissListener {
            if (!isStartLogin)
                onDismiss()
        }
    }

    protected abstract fun onLogin()
    protected abstract fun onRegister()
    protected abstract fun onDismiss()
}