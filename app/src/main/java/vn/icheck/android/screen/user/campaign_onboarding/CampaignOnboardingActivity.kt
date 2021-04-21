package vn.icheck.android.screen.user.campaign_onboarding

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import kotlinx.android.synthetic.main.activity_campaign_onboarding.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.tracking.insider.InsiderHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICCampaignOnboarding
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.ui.layout.CustomGridLayoutManager
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.StatusBarUtils
import java.lang.IllegalArgumentException
import java.util.*

class CampaignOnboardingActivity : BaseActivityMVVM() {
    lateinit var adapter: CampaignOnboardingAdapter
    lateinit var viewModel: CampaignOnboardingViewModel

    private var btnPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campaign_onboarding)
        StatusBarUtils.setOverStatusBarLight(this)

        viewModel = ViewModelProvider(this).get(CampaignOnboardingViewModel::class.java)
        initToolbar()
        initRecyclerview()
        initData()
    }

    private fun initToolbar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initRecyclerview() {
        adapter = CampaignOnboardingAdapter()
        recyclerview.adapter = adapter
        recyclerview.layoutManager = CustomGridLayoutManager(this, 1, LinearLayoutManager.HORIZONTAL, false)
        PagerSnapHelper().attachToRecyclerView(recyclerview)
    }

    private fun initData() {
        viewModel.getData(intent)

        val typeReward = intent.getStringExtra(Constant.DATA_2) ?: ""
        if (typeReward == "CODE") {
            layoutButton.beGone()
        }

        viewModel.onState.observe(this) {
            when (it.type) {
                ICMessageEvent.Type.ON_NO_INTERNET -> {
                    adapter.setError(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), null, R.string.thu_lai))
                }
                ICMessageEvent.Type.MESSAGE_ERROR -> {
                    adapter.setError(it.data as ICError)
                }
                else -> {
                }
            }
        }

        viewModel.onBoardingData.observe(this, {
            //check type onboarding
            when (it?.type) {
                0 -> {
                    finish()
                }
                1 -> {
                    webView.beVisible()
                    recyclerview.beGone()

                    setUpWebView(it.onboardLanding ?: "")

                    btnActionBack.beGone()
                    var color = it.btnColor ?: if (vn.icheck.android.ichecklibs.Constant.primaryColor.isNotEmpty()) {
                        vn.icheck.android.ichecklibs.Constant.primaryColor
                    } else {
                        "#057DDA"
                    }
                    if (!color.startsWith("#")) {
                        color = "#$color"
                    }
                    try {
                        btnActionContinue.background = ViewHelper.createShapeDrawable(Color.parseColor(color), SizeHelper.size4.toFloat())
                    } catch (e: IllegalArgumentException) {
                        btnActionContinue.background = ViewHelper.createShapeDrawable(Color.parseColor("#057DDA"), SizeHelper.size4.toFloat())
                    }
                    btnActionContinue.text = it.btnName ?: ""

                    btnActionContinue.setOnClickListener { view ->
                        FirebaseDynamicLinksActivity.startDestinationUrl(this, it.cta)
                    }
                }
                else -> {
                    webView.beGone()
                    recyclerview.beVisible()
                    layoutCenter.beGone()

                    adapter.setListData(it?.onboardCTA ?: mutableListOf())

                    if (it.onboardCTA?.size == 1) {
                        btnPosition = 1
                    }
                    checkActionButton(it)
                    btnActionBack.setOnClickListener { view ->
                        btnPosition--
                        checkActionButton(it)
                    }

                    btnActionContinue.setOnClickListener { view ->
                        btnPosition++
                        checkActionButton(it)
                    }
                }
            }
        })
    }

    private fun checkActionButton(it: ICCampaignOnboarding?) {
        when {
            btnPosition <= 0 -> {
                btnActionBack.beGone()

                btnActionContinue.text = getString(R.string.tiep_tuc)
                btnActionContinue.setBackgroundResource(R.drawable.bg_corners_4_light_blue_solid)
                recyclerview.scrollToPosition(0)
            }
            btnPosition < it?.onboardCTA?.size?.minus(1) ?: 0 -> {
                btnActionBack.beVisible()

                btnActionContinue.text = getString(R.string.tiep_tuc)
                btnActionContinue.setBackgroundResource(R.drawable.bg_corners_4_light_blue_solid)
                recyclerview.scrollToPosition(btnPosition)
            }
            btnPosition == it?.onboardCTA?.size?.minus(1) ?: 0 -> {
                btnActionBack.beGone()
                var color = it?.btnColor ?: "#057DDA"
                if (!color.startsWith("#")) {
                    color = "#$color"
                }
                try {
                    btnActionContinue.background = ViewHelper.createShapeDrawable(Color.parseColor(color), SizeHelper.size4.toFloat())
                } catch (e: IllegalArgumentException) {
                    btnActionContinue.background = ViewHelper.createShapeDrawable(Color.parseColor("#057DDA"), SizeHelper.size4.toFloat())
                }
                btnActionContinue.text = it?.btnName ?: ""
                recyclerview.scrollToPosition(it?.onboardCTA?.size?.minus(1) ?: 0)
            }
            else -> {
                TrackingAllHelper.tagCampaignCTAClicked(viewModel.campaignId)
                FirebaseDynamicLinksActivity.startDestinationUrl(this, it?.cta)
                finish()
            }
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    fun setUpWebView(url: String) {
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

        var loadingFinished = true
        var redirect = false

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
            setAppCacheEnabled(true)
            loadsImagesAutomatically = true
            javaScriptCanOpenWindowsAutomatically = true
            allowFileAccess = true
            mediaPlaybackRequiresUserGesture = false
            loadWithOverviewMode = true
            useWideViewPort = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
            layoutAlgorithm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
            } else {
                WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            }
            setGeolocationEnabled(true)
        }

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
                    layoutCenter.beGone()
                }
            }
        }
        if (myUrl.startsWith("http")) {
            webView.loadUrl(myUrl)
        } else {
            webView.loadData(Constant.getHtmlData(myUrl), "text/html; charset=utf-8", "UTF-8")
        }
    }
}