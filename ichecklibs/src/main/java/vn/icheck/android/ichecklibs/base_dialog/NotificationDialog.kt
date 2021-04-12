package vn.icheck.android.ichecklibs.base_dialog

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import vn.icheck.android.ichecklibs.R


abstract class NotificationDialog(
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


    fun hideTitle() {
        findViewById<AppCompatTextView>(R.id.txtTitle).visibility = View.GONE
    }

    protected abstract fun onDone()
}