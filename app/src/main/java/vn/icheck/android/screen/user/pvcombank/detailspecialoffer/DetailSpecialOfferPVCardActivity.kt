package vn.icheck.android.screen.user.pvcombank.detailspecialoffer

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_detail_special_offer_pvcard.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.ViewHelper

class DetailSpecialOfferPVCardActivity : BaseActivityMVVM() {

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_special_offer_pvcard)
        DialogHelper.showLoading(this)

        setupView()
        listener()
        setupWebView()
    }

    private fun setupView() {
        btnAction.background = ViewHelper.bgPrimaryCorners4(this)
    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(){
        val myUrl = "https://www.google.com/"

        if (myUrl.startsWith("http")) {
            webViewUrl.loadUrl(myUrl)
        } else {
            webViewUrl.loadData(Constant.getHtmlData(myUrl), "text/html; charset=utf-8", "UTF-8")
        }

        var loadingFinished = true
        var redirect = false
        webViewUrl.settings.javaScriptEnabled = true
        webViewUrl.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                loadingFinished = false
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                if (!loadingFinished) {
                    redirect = true
                }

                loadingFinished = false

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request?.url?.let { url ->
                        view?.loadUrl(url.toString())
                    }
                }
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (!redirect) {
                    loadingFinished = true
                }

                if (loadingFinished && !redirect) {
                    DialogHelper.closeLoading(this@DetailSpecialOfferPVCardActivity)
                }

                if (!view?.title.isNullOrEmpty()) {
                    txtTitle.text = view?.title
                }
            }
        }
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)

        when (event.type) {
            ICMessageEvent.Type.FINISH_ALL_PVCOMBANK -> {
                onBackPressed()
            }
            else -> {}
        }
    }
}