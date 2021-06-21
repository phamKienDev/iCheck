package vn.icheck.android.screen.user.information_product

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.webkit.WebSettings
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_information_product.*
import kotlinx.android.synthetic.main.toolbar_blue_v2.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.util.spToPx
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.util.kotlin.WidgetUtils

class InformationProductActivity : BaseActivityMVVM() {
    lateinit var viewModel: InformationProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information_product)

        viewModel = ViewModelProvider(this).get(InformationProductViewModel::class.java)

        viewModel.getData(intent)

        initView()
    }

    fun initView() {
        imgBack.setImageResource(R.drawable.ic_cancel_blue_24px)

        imgBack.setOnClickListener {
            onBackPressed()
        }

        WidgetUtils.loadImageUrlFitCenter(imgAction, intent?.getStringExtra(Constant.DATA_3), WidgetUtils.defaultHolder, R.drawable.bg_error_emty_attachment)

        viewModel.liveData.observe(this, Observer {
            txtTitle.apply {
                text = if (!it.title.isNullOrEmpty()) {
                    it.title
                } else {
                    context.getString(R.string.thong_tin_chi_tiet)
                }
            }

            imgAction.beVisible()

            setupWebView(it.content)
        })

        viewModel.onError.observe(this, Observer {
            DialogHelper.showConfirm(this, it.message, false, object : ConfirmDialogListener {
                override fun onDisagree() {
                    onBackPressed()
                }

                override fun onAgree() {
                    viewModel.getInformationProduct()
                }
            })
        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(url: String?) {
        webViewUrl.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
            defaultFontSize = 14.spToPx()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webViewUrl.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
        } else {
            webViewUrl.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        }

        if (!url.isNullOrEmpty()) {
            webViewUrl.loadDataWithBaseURL(null, Constant.getHtmlData(url), "text/html", "utf-8", "")
        }

//        webView.webViewClient = object : WebViewClient() {
//            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    view.loadUrl(request.url.toString())
//                }
//                return false
//            }
//        }
    }
}
