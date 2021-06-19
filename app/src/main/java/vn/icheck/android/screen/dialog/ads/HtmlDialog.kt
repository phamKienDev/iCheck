package vn.icheck.android.screen.dialog.ads

import android.annotation.SuppressLint
import android.content.Context
import kotlinx.android.synthetic.main.dialog_html.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseDialog

class HtmlDialog(context: Context, private val data: String, private val isUrl: Boolean) : BaseDialog(context, R.style.DialogTheme) {

    override val getLayoutID: Int
        get() = R.layout.dialog_html

    override val getIsCancelable: Boolean
        get() = false

    override fun onInitView() {
        initData()
        initListener()
    }

    private fun initData() {
        @SuppressLint("SetJavaScriptEnabled")
        webViewUrl?.settings?.javaScriptEnabled = true

        if (isUrl) {
            txtTitle?.text = data
            webViewUrl?.loadUrl(data)
        } else {
            webViewUrl?.loadDataWithBaseURL("", data, "text/html", "UTF-8", "")
        }
    }

    private fun initListener() {
        imgClose?.setOnClickListener {
            dismiss()
        }
    }
}