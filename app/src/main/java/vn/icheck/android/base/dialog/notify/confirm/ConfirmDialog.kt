package vn.icheck.android.base.dialog.notify.confirm

import android.content.Context
import android.graphics.Color
import android.text.Html
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.dialog_confirm.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseDialog
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible

abstract class ConfirmDialog(
        context: Context,
        private val title: String?,
        private val message: String?,
        private val disagree: String?,
        private val agree: String?,
        private val isCancelable: Boolean,
        private val colorDisagree: Int? = null,
        private val colorAgree: Int? = null,
        private val colorTitle: Int? = null,
        private val colorMessage: Int? = null
) : BaseDialog(context, R.style.DialogTheme) {

    override val getLayoutID: Int
        get() = R.layout.dialog_confirm

    override val getIsCancelable: Boolean
        get() = isCancelable

    @Suppress("DEPRECATION")
    override fun onInitView() {
        if (colorTitle != null)
            txtTitle.setTextColor(ContextCompat.getColor(context, colorTitle))

        if (!title.isNullOrEmpty()) {
            txtTitle.beVisible()
            txtTitle.text = Html.fromHtml(title)
        } else {
            txtTitle.beGone()
        }

        if (colorMessage != null)
            txtMessage.setTextColor(ContextCompat.getColor(context, colorMessage))

        if (!message.isNullOrEmpty()) {
            txtMessage.beVisible()
            txtMessage.text = Html.fromHtml(message)
        } else {
            txtMessage.beGone()
        }


        if (!disagree.isNullOrEmpty())
            btnDisagree.text = Html.fromHtml(disagree)

        if (!agree.isNullOrEmpty())
            btnAgree.text = Html.fromHtml(agree)

        if (colorDisagree != null)
            btnDisagree.setTextColor(ContextCompat.getColor(context, colorDisagree))

        if (colorAgree != null)
            btnAgree.setTextColor(ContextCompat.getColor(context, colorAgree))

        btnDisagree.setOnClickListener {
            dismiss()
            onDisagree()
        }

        btnAgree.setOnClickListener {
            dismiss()
            onAgree()
        }
    }

    protected abstract fun onDisagree()
    protected abstract fun onAgree()
    protected abstract fun onDismiss()
}