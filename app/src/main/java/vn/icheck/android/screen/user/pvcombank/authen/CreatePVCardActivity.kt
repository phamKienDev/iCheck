package vn.icheck.android.screen.user.pvcombank.authen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_pvcombank.*
import kotlinx.android.synthetic.main.toolbar_pvcombank.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.WidgetUtils
import java.util.*

class CreatePVCardActivity : BaseActivityMVVM(), View.OnClickListener {
    private var typeCreate = true
    lateinit var viewModel: CreatePVCardViewModel

    companion object {
        var redirectUrl: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pvcombank)

        viewModel = ViewModelProvider(this).get(CreatePVCardViewModel::class.java)
        initView()
        initData()
    }

    private fun initView() {
        typeCreate = intent.getBooleanExtra(Constant.DATA_1, true)

        tvContinue.background = ViewHelper.bgPrimaryCorners4(this)

        if (typeCreate) {
            txtTitle.text = getString(R.string.tao_the_moi)
            tvContinue.text = getString(R.string.tiep_tuc)
            checkbox.beGone()
            tvSkip.beGone()
            containerButton.beGone()
            setButtonCoutinue()
            viewModel.getSettingPVCombank("pvcombank.new-card-privilege")
        } else {
            txtTitle.text = getString(R.string.uy_quyen_ung_dung)
            tvContinue.text = getString(R.string.dong_y)
            checkbox.beGone()
            containerButton.beGone()
            tvSkip.beVisible()
            viewModel.getSettingPVCombank("pvcombank.icheck-authorize")
        }

        checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            setButtonCoutinue()
        }

        WidgetUtils.setClickListener(this, imgBack, tvSkip, tvContinue, checkbox)
    }

    private fun initData() {
        viewModel.onLinkCreate.observe(this, Observer {
            it.description?.let { url -> initWebView(url) }
        })

        viewModel.onLinkAuth.observe(this, Observer {
            redirectUrl = it.redirectUrl
            WebViewActivity.start(this, it.authUrl)
        })

        viewModel.onError.observe(this, Observer {
            showShortError(it.message ?: "")
        })

        viewModel.onState.observe(this, Observer {
            when (it.type) {
                ICMessageEvent.Type.ON_SHOW_LOADING -> {
                    DialogHelper.showLoading(this)
                }
                ICMessageEvent.Type.ON_CLOSE_LOADING -> {
                    DialogHelper.closeLoading(this)
                }
            }
        })
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        when (event.type) {
            ICMessageEvent.Type.FINISH_CREATE_PVCOMBANK -> {
                finish()
            }
            else -> {
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(url: String) {
        DialogHelper.showLoading(this)
        var myUrl = url
        if (Patterns.WEB_URL.matcher(myUrl).matches()) {
            if (!url.startsWith("http")) {
                myUrl = "http://$url"
            }
        } else {
            if (url.length > 4 && url.substring(0, 4).toLowerCase(Locale.getDefault()) == "url:") {
                myUrl = url.removeRange(0, 4)
            }
        }

        if (myUrl.startsWith("http")) {
            webView.loadUrl(myUrl)
        } else {
            webView.loadData(Constant.getHtmlData(myUrl), "text/html; charset=utf-8", "UTF-8")
        }

        var loadingFinished = true
        var redirect = false

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
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
                    DialogHelper.closeLoading(this@CreatePVCardActivity)
                    containerButton.beVisible()
                    if (txtTitle.text == getString(R.string.tao_the_moi)) {
                        checkbox.beVisible()
                    }
                }
            }
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> {
                onBackPressed()
            }
            R.id.tvSkip -> {
                onBackPressed()
            }
            R.id.tvContinue -> {
                if (typeCreate)
                    startActivity<CreatePVCardActivity, Boolean>(Constant.DATA_1, false)
                else
                    viewModel.getLinkFormAuth()
            }
        }
    }

    private fun setButtonCoutinue() {
        if (checkbox.isChecked) {
            tvContinue.background = ViewHelper.bgPrimaryCorners4(this@CreatePVCardActivity)
            tvContinue.isEnabled = true
        } else {
            tvContinue.setBackgroundResource(R.drawable.bg_corner_gray_4)
            tvContinue.isEnabled = false
        }
    }
}
