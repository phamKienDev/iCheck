package vn.icheck.android.base.dialog.notify.progress

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseDialog

abstract class ProgressDialog(
        context: Context,
        private val title: String?,
        private val message: String?,
        private val isCancelable: Boolean) : BaseDialog(context, R.style.DialogTheme) {

    override val getIsCancelable: Boolean
        get() = isCancelable

    override fun onInitView() {
        if (!title.isNullOrEmpty())
            findViewById<AppCompatTextView>(R.id.txtTitle).text = title

        if (!message.isNullOrEmpty())
            findViewById<AppCompatTextView>(R.id.txtMessage).text = message

        setOnDismissListener {
            onDismiss()
        }
    }

    protected abstract fun onDismiss()
}