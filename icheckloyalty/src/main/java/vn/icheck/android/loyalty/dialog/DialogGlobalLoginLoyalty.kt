package vn.icheck.android.loyalty.dialog

import android.content.Context
import android.os.Build
import android.text.Html
import kotlinx.android.synthetic.main.dialog_loyalty_require_login.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.dialog.base.BaseDialog
import vn.icheck.android.loyalty.helper.SizeHelper
import vn.icheck.android.loyalty.helper.WidgetHelper

abstract class DialogGlobalLoginLoyalty(context: Context, private val bannerSrc: String?, val title: String, val sub: String) : BaseDialog(context, R.style.DialogTheme) {
    override val getLayoutID: Int
        get() = R.layout.dialog_loyalty_require_login
    override val getIsCancelable: Boolean
        get() = false

    override fun onInitView() {
        btn_close.setOnClickListener {
            dismiss()
        }
        btn_register.setOnClickListener {
            onRegister()
            dismiss()
        }
        btn_login.setOnClickListener {
            onLogin()
            dismiss()
        }
        btnFacebook.setOnClickListener {
            onFacebook()
            dismiss()
        }

        WidgetHelper.loadImageUrlRounded(banner, bannerSrc, SizeHelper.size10)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tv_title.text = Html.fromHtml(title, Html.FROM_HTML_MODE_COMPACT)
        } else {
            tv_title.text = Html.fromHtml(title)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tv_sub_title.text = Html.fromHtml(sub, Html.FROM_HTML_MODE_COMPACT)
        } else {
            tv_sub_title.text = Html.fromHtml(sub)
        }
    }

    protected abstract fun onRegister()
    protected abstract fun onLogin()
    protected abstract fun onFacebook()
}