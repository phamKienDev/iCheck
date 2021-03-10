package vn.icheck.android.loyalty.dialog

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.dialog_not_enough_point.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.dialog.base.BaseDialog

abstract class DialogNotEnoughPoints(
        context: Context,
        val title: String,
        val message: String?,
        val image: Int,
        val button: String,
        val isShow: Boolean,
        val backgroundButton: Int,
        private val colorTextButton: Int
) : BaseDialog(context, R.style.DialogTheme) {
    override val getLayoutID: Int
        get() = R.layout.dialog_not_enough_point
    override val getIsCancelable: Boolean
        get() = false

    override fun onInitView() {
        imgTop.visibility = if (isShow) {
            View.VISIBLE
        } else {
            View.GONE
        }

        tvTitle.text = title

        tvMessage.text = message

        imgDefault.setImageResource(image)

        btnDefault.text = button
        btnDefault.setTextColor(ContextCompat.getColor(context, colorTextButton))

        btnDefault.setBackgroundResource(backgroundButton)

        imgClose.setOnClickListener {
            dismiss()
        }

        btnDefault.setOnClickListener {
            dismiss()
            onClickButton()
        }
    }

    protected abstract fun onClickButton()
}