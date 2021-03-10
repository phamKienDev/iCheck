package vn.icheck.android.loyalty.dialog

import android.content.Context
import android.text.Html
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.dialog_confirm_loyalty.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.setGone
import vn.icheck.android.loyalty.base.setVisible
import vn.icheck.android.loyalty.dialog.base.BaseDialog

abstract class ConfirmLoyaltyDialog(
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
        get() = R.layout.dialog_confirm_loyalty

    override val getIsCancelable: Boolean
        get() = isCancelable

    @Suppress("DEPRECATION")
    override fun onInitView() {
        if (colorTitle != null)
            txtTitle.setTextColor(ContextCompat.getColor(context, colorTitle))

        if (!title.isNullOrEmpty()) {
            txtTitle.setVisible()
            txtTitle.text = Html.fromHtml(title)
        } else {
            txtTitle.setGone()
        }

        if (colorMessage != null)
            txtMessage.setTextColor(ContextCompat.getColor(context, colorMessage))

        if (!message.isNullOrEmpty()) {
            txtMessage.setVisible()
            txtMessage.text = Html.fromHtml(message)
        } else {
            txtMessage.setGone()
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