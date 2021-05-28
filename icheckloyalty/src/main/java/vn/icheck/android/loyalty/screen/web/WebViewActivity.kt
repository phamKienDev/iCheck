package vn.icheck.android.loyalty.screen.web

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.util.Patterns
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_web_view_loyalty.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import kotlinx.coroutines.launch
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.helper.ActivityHelper
import vn.icheck.android.loyalty.network.SessionManager
import vn.icheck.android.loyalty.sdk.LoyaltySdk
import java.util.*

class WebViewActivity : BaseActivityGame() {

    companion object {
        fun start(fragmentActivity: FragmentActivity?, url: String?, title: String? = null, isHeader: Boolean? = null) {
            if (fragmentActivity == null || url.isNullOrEmpty()) {
                return
            }

            val intent = Intent(fragmentActivity, WebViewActivity::class.java)
            intent.putExtra(ConstantsLoyalty.DATA_1, url)

            if (title != null)
                intent.putExtra(ConstantsLoyalty.DATA_3, title)

            if (isHeader != null) {
                intent.putExtra(ConstantsLoyalty.DATA_4, isHeader)
            }

            ActivityHelper.startActivity(fragmentActivity, intent)
        }
    }

    override val getLayoutID: Int
        get() = R.layout.activity_web_view_loyalty

    override fun onInitView() {
        var url = intent?.getStringExtra(ConstantsLoyalty.DATA_1)
        var title = intent?.getStringExtra(ConstantsLoyalty.DATA_3)

        if (url.isNullOrEmpty()) {
            onBackPressed()
            return
        }

        if (Patterns.WEB_URL.matcher(url).matches()) {
            if (!url.startsWith("http")) {
                url = "http://$url"
            }
        } else {
            if (url.length > 4 && url.substring(0, 4).toLowerCase(Locale.getDefault()) == "url:") {
                url = url.removeRange(0, 4)
            } else {
                if (title.isNullOrEmpty()) {
                    title = getString(R.string.noi_dung_ma_qr)
                }
            }
        }

        setupToolbar(url, title)
        setupWebView(url, title)
    }

    private fun setupToolbar(url: String, title: String?) {
        if (title.isNullOrEmpty()) {
            txtTitle.text = url
        } else {
            txtTitle.text = title
        }

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(url: String, title: String?) {
        webViewUrl.settings.javaScriptEnabled = true
        @Suppress("DEPRECATION")
        webViewUrl.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        webViewUrl.settings.domStorageEnabled = true
        webViewUrl.settings.allowFileAccessFromFileURLs = true
        webViewUrl.settings.useWideViewPort = true
        webViewUrl.settings.loadWithOverviewMode = true
        webViewUrl.settings.allowUniversalAccessFromFileURLs = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webViewUrl.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webViewUrl.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
        }

        if (url.startsWith("http")) {
            if (intent?.getBooleanExtra(ConstantsLoyalty.DATA_4, false) == true) {
                lifecycleScope.launch {
                    val header = hashMapOf<String, String>()
                    header["source"] = "icheck"

                    SessionManager.session.user?.let { user ->
                        header["userId"] = user.id.toString()
                        header["icheckId"] = "i-${user.id}"

                        if (!user.name.isNullOrEmpty()) {
                            header["name"] = user.name.toString()
                        }

                        if (!user.phone.isNullOrEmpty()) {
                            header["phone"] = user.phone.toString()
                        }

                        if (!user.email.isNullOrEmpty()) {
                            header["email"] = user.email.toString()
                        }
                    }

                    webViewUrl.loadUrl(url, header)
                }
            } else {
                webViewUrl.loadUrl(url)
            }
        } else {
            webViewUrl.loadData(url, "text/html; charset=utf-8", "UTF-8")
        }

        var loadingFinished = true
        var redirect = false

        webViewUrl.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(webView: WebView, request: WebResourceRequest): Boolean {
                if (!loadingFinished) {
                    redirect = true
                }
                loadingFinished = false

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    try {
                        if (request.url.toString().startsWith("http")) {
                            request.url.toString().apply {
                                when {
                                    this.contains("icheck.page.link") -> {
                                        LoyaltySdk.openActivity(this)
                                    }
                                    this.contains("icheckdev.com.vn") -> {
                                        LoyaltySdk.openActivity(this)
                                    }
                                    this.contains("vn.icheck.android") -> {
                                        LoyaltySdk.openActivity(this)
                                    }
                                    this.contains("vn.icheck.android") -> {
                                        LoyaltySdk.openActivity(this)
                                    }
                                    else -> {
                                        webView.loadUrl(this)
                                    }
                                }
                            }
                        } else if (request.url.toString().startsWith("intent://")) {
                            val intent = Intent.parseUri(request.url.toString(), Intent.URI_INTENT_SCHEME)
                            if (intent != null) {
                                webView.stopLoading()

                                val fallbackUrl = intent.getStringExtra("browser_fallback_url")
                                if (!fallbackUrl.isNullOrEmpty()) {
                                    webView.loadUrl(fallbackUrl)
                                } else {
                                    packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)?.let {
                                        ActivityHelper.startActivity(this@WebViewActivity, intent)
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

                loadingFinished = false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                if (!redirect) {
                    loadingFinished = true
                }

                if (loadingFinished && !redirect) {
                    if (title.isNullOrEmpty()) {
                        if (!view?.title.isNullOrEmpty()) {
                            txtTitle.text = view?.title
                        }
                    }
                }
            }
        }
    }
}