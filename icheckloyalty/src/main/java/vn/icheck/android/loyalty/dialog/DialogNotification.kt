package vn.icheck.android.loyalty.dialog

import android.content.Context
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.dialog.base.BaseDialog

abstract class DialogNotification(
        context: Context,
        private val title: String?,
        private val message: String?,
        private val done: String?,
        private val isCancelable: Boolean) : BaseDialog(context, R.style.DialogTheme) {

    override val getLayoutID: Int
        get() = R.layout.dialog_notification

    override val getIsCancelable: Boolean
        get() = isCancelable

    override fun onInitView() {
        if (!title.isNullOrEmpty())
            findViewById<AppCompatTextView>(R.id.txtTitle).text = title

        if (!message.isNullOrEmpty())
            findViewById<AppCompatTextView>(R.id.txtMessage).text = message

        findViewById<AppCompatButton>(R.id.btnDone)?.run {
            if (!done.isNullOrEmpty())
                text = done

            setOnClickListener {
                dismiss()
                onDone()
            }
        }
    }

    protected abstract fun onDone()
}