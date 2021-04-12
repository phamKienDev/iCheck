package vn.icheck.android.ichecklibs.base_dialog

import android.content.Context
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.dialog_scan_loyalty_error.*
import vn.icheck.android.ichecklibs.R
import vn.icheck.android.ichecklibs.beGone
import vn.icheck.android.ichecklibs.beVisible

abstract class DialogScanLoyaltyError(
        context: Context,
        private val icon: Int,
        private val title: String,
        private val message: String,
        private val button: Int?,
        private val msgButton: String?,
        private val showButton: Boolean,
        private val backgroundButton: Int,
        private val colorButton: Int
) : BaseDialog(context, R.style.DialogTheme) {

    override val getLayoutID: Int
        get() = R.layout.dialog_scan_loyalty_error
    override val getIsCancelable: Boolean
        get() = false

    override fun onInitView() {
        imgIcon.setImageResource(icon)

        tvTitle.text = title

        tvMessage.text = message

        /**
         * showButton = true -> Hiện button là image
         * showButton = false -> Hiện button là textView
         */
        if (showButton) {
            btnScan.beVisible()
            btnDefault.beGone()
            layoutBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.black_60))
        } else {
            btnScan.beGone()
            btnDefault.beVisible()
            layoutBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
        }

        btnDefault.setBackgroundResource(backgroundButton)
        btnDefault.setTextColor(ContextCompat.getColor(context, colorButton))

        btnScan.run {
            setImageResource(button ?: 0)

            setOnClickListener {
                onClickButton()
            }
        }

        btnDefault.run {
            text = msgButton

            setOnClickListener {
                onClickButton()
            }
        }

        imgClose.setOnClickListener {
            dismiss()
        }

        setOnDismissListener {
            onDismiss()
        }
    }

    protected abstract fun onClickButton()
    protected abstract fun onDismiss()
}