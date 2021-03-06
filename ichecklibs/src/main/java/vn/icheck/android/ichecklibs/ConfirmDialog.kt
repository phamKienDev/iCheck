package vn.icheck.android.ichecklibs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import vn.icheck.android.ichecklibs.databinding.DialogConfirmBinding
import vn.icheck.android.ichecklibs.util.beGone
import vn.icheck.android.ichecklibs.util.beVisible

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
) : Dialog(context, R.style.DialogTheme) {

    lateinit var binding:DialogConfirmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        setCancelable(isCancelable)
        setCanceledOnTouchOutside(isCancelable)

        onInitView()
    }


    fun onInitView() {
        if (colorTitle != null)
            binding.txtTitle.setTextColor(getColor(colorTitle))

        if (!title.isNullOrEmpty()) {
            binding.txtTitle.beVisible()
            binding.txtTitle.text = Html.fromHtml(title)
        } else {
            binding.txtTitle.beGone()
        }

        if (colorMessage != null)
            binding.txtMessage.setTextColor(getColor(colorMessage))

        if (!message.isNullOrEmpty()) {
            binding.txtMessage.beVisible()
            binding.txtMessage.text = Html.fromHtml(message)
        } else {
            binding.txtMessage.beGone()
        }


        if (!disagree.isNullOrEmpty())
            binding.btnDisagree.text = Html.fromHtml(disagree)

        if (!agree.isNullOrEmpty())
            binding.btnAgree.text = Html.fromHtml(agree)

        if (colorDisagree != null)
            binding.btnDisagree.setTextColor(getColor(colorDisagree))

        if (colorAgree != null)
            binding.btnAgree.setTextColor(getColor(colorAgree))

        binding.btnDisagree.setOnClickListener {
            dismiss()
            onDisagree()
        }

        binding.btnAgree.setOnClickListener {
            dismiss()
            onAgree()
        }
    }

    private fun getColor(color: Int): Int {
        return when (color) {
            R.color.colorPrimary -> {
                ColorManager.getPrimaryColor(context)
            }
            R.color.colorSecondary -> {
                ColorManager.getSecondaryColor(context)
            }
            else -> {
                ContextCompat.getColor(context, color)
            }
        }
    }

    protected abstract fun onDisagree()
    protected abstract fun onAgree()
    protected abstract fun onDismiss()
}