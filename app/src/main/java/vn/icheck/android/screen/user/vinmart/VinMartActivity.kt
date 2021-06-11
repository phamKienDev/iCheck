package vn.icheck.android.screen.user.vinmart

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_vin_mart.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.constant.Constant
import vn.icheck.android.util.kotlin.ActivityUtils
import java.util.regex.Pattern

class VinMartActivity : BaseActivityMVVM() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vin_mart)
        onInitView()
    }

    fun onInitView() {
        val url = intent?.getStringExtra(Constant.DATA_1)

        if (url.isNullOrEmpty()) {
            onBackPressed()
            return
        }

        setupToolbar()
        setupWebView(url)
    }

    private fun setupToolbar() {
        txtTitle.setText(R.string.mua_san_online)

        imgBack.visibility = View.GONE
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(url: String) {
        webViewUrl.settings.javaScriptEnabled = true
        @Suppress("DEPRECATION")
        webViewUrl.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        webViewUrl.settings.domStorageEnabled = true
        webViewUrl.settings.allowFileAccessFromFileURLs = true
        webViewUrl.settings.allowUniversalAccessFromFileURLs = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webViewUrl.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
        }

        if (url.startsWith("http")) {
            webViewUrl.loadUrl(url)
        } else {
            webViewUrl.loadData(url, "text/html; charset=utf-8", "UTF-8");
        }

        webViewUrl.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request?.url?.let { url ->
                        handleNewUrl(view, url)
//                        view?.loadUrl(url.toString())
                    }
                }
                return true
            }
        }
    }

    private fun handleNewUrl(view: WebView?, uri: Uri) {
        val path = uri.path
        if (path == "/vnpayshop/backapp") {
            ActivityUtils.finishActivity(this)
        } else if ((uri.scheme ?: "").startsWith("http")) {
            view?.loadUrl(uri.toString())
        } else if (uri.getScheme().equals("intent")) {
            val appPackage = getAppPackageFromUri(uri)

            if (appPackage != null) {
                val appIntent = packageManager.getLaunchIntentForPackage(appPackage)
                appIntent?.data = uri

                if (appIntent != null) {
                    startActivity(appIntent)
                } else {
                    view?.loadUrl("https://play.google.com/store/apps/details?id=$appPackage")
                }
            }
        }
    }

    private fun getAppPackageFromUri(intentUri: Uri): String? {
        val pattern = Pattern.compile("package=(.*?);")
        val matcher = pattern.matcher(intentUri.getFragment())
        return if (matcher.find()) matcher.group(1) else null
    }

    override fun onBackPressed() {
        if (webViewUrl.canGoBack()) {
            webViewUrl.goBack()
        } else {
            super.onBackPressed()
        }
    }
}