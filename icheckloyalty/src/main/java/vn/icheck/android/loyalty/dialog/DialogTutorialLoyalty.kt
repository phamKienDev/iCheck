package vn.icheck.android.loyalty.dialog

import android.content.Context
import android.os.Build
import android.text.Html
import kotlinx.android.synthetic.main.dialog_tutorial_loyalty.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.dialog.base.BaseDialog

abstract class DialogTutorialLoyalty(context: Context, private val backgroundButton: Int) : BaseDialog(context, R.style.DialogTheme) {
    override val getLayoutID: Int
        get() = R.layout.dialog_tutorial_loyalty
    override val getIsCancelable: Boolean
        get() = false

    override fun onInitView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvTutorial.text = Html.fromHtml(context.getString(R.string.tutorial_loyalty), Html.FROM_HTML_MODE_LEGACY)
        } else {
            tvTutorial.text = Html.fromHtml(context.getString(R.string.tutorial_loyalty))
        }

        btnDefault.setBackgroundResource(backgroundButton)

        btnDefault.setOnClickListener {
            dismiss()
        }

        imgClose.setOnClickListener {
            dismiss()
        }
    }
}