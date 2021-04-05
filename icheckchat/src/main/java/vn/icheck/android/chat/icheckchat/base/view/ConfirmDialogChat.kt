package vn.icheck.android.chat.icheckchat.base.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import vn.icheck.android.chat.icheckchat.R
import vn.icheck.android.chat.icheckchat.databinding.DialogConfirmChatBinding

abstract class ConfirmDialogChat(context: Context,
                                 private val title: String?,
                                 private val message: String?,
                                 private val disagree: String?,
                                 private val agree: String?,
                                 private val isCancelable: Boolean,
                                 private val colorDisagree: Int? = null,
                                 private val colorAgree: Int? = null,
                                 private val colorTitle: Int? = null,
                                 private val colorMessage: Int? = null) : Dialog(context) {
    val binding = DialogConfirmChatBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window?.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        setCancelable(isCancelable)
        setCanceledOnTouchOutside(isCancelable)

        onInitView()
    }

    private fun onInitView() {
        if (colorTitle != null)
            binding.txtTitle.setTextColor(ContextCompat.getColor(context, colorTitle))

        if (!title.isNullOrEmpty()) {
            binding.txtTitle.setVisible()
            binding.txtTitle.text = Html.fromHtml(title)
        } else {
            binding.txtTitle.setGone()
        }

        if (colorMessage != null)
            binding.txtMessage.setTextColor(ContextCompat.getColor(context, colorMessage))

        if (!message.isNullOrEmpty()) {
            binding.txtMessage.setVisible()
            binding.txtMessage.text = Html.fromHtml(message)
        } else {
            binding.txtMessage.setGone()
        }


        if (!disagree.isNullOrEmpty())
            binding.btnDisagree.text = Html.fromHtml(disagree)

        if (!agree.isNullOrEmpty())
            binding.btnAgree.text = Html.fromHtml(agree)

        if (colorDisagree != null)
            binding.btnDisagree.setTextColor(ContextCompat.getColor(context, colorDisagree))

        if (colorAgree != null)
            binding.btnAgree.setTextColor(ContextCompat.getColor(context, colorAgree))

        binding.btnDisagree.setOnClickListener {
            dismiss()
            onDisagree()
        }

        binding.btnAgree.setOnClickListener {
            dismiss()
            onAgree()
        }
    }

    protected abstract fun onDisagree()
    protected abstract fun onAgree()
    protected abstract fun onDismiss()
}