package vn.icheck.android.screen.user.productlanding

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_product_landing.*
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.ICAds
import vn.icheck.android.util.kotlin.ActivityUtils

class LandingPageActivity : FragmentActivity() {
    private var timer: CountDownTimer? = null

    companion object {
        fun start(activity: FragmentActivity, ads: ICAds) {
            val intent = Intent(activity, LandingPageActivity::class.java)

            if (!ads.html.isNullOrEmpty()) {
                intent.putExtra(Constant.DATA_1, ads.html)
            } else if (!ads.destination_url.isNullOrEmpty()) {
                intent.putExtra(Constant.DATA_2, ads.destination_url)
            } else {
                return
            }

            intent.putExtra(Constant.DATA_3, ads.landing_duration)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.none, R.anim.none)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_landing)

        setupWebView()
        setupCountDown()
        setupListener()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val html = intent?.getStringExtra(Constant.DATA_1)
        val url = intent?.getStringExtra(Constant.DATA_2)

        webViewUrl.settings.javaScriptEnabled = true
        @Suppress("DEPRECATION")
        webViewUrl.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        webViewUrl.settings.domStorageEnabled = true
        webViewUrl.settings.allowFileAccessFromFileURLs = true
        webViewUrl.settings.allowUniversalAccessFromFileURLs = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webViewUrl.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
        }

        if (!html.isNullOrEmpty()) {
            webViewUrl.loadDataWithBaseURL(null, html, "text/html", "utf-8", "")
        } else if (!url.isNullOrEmpty()) {
            webViewUrl.loadUrl(url)
        } else {
            onBackPressed()
        }

        webViewUrl.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.loadUrl(request.url.toString())
                }
                return false
            }
        }
    }

    private fun setupCountDown() {
        val totalDuration = (intent?.getIntExtra(Constant.DATA_3, 10) ?: 10) * 1000
        progressBar.max = totalDuration
        progressBar.progress = 0

        timer = object : CountDownTimer(totalDuration.toLong(), 10) {
            override fun onFinish() {
                imgClose.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                tvCount.visibility = View.GONE

                ActivityUtils.finishActivity(this@LandingPageActivity)
            }

            override fun onTick(millisecond: Long) {
                val count = (millisecond / 1000).toInt()
                tvCount.text = count.toString()
                progressBar.progress = (totalDuration - millisecond).toInt()
                Log.e("setupCountDown", "$totalDuration - $millisecond - $count")
            }
        }

        timer?.start()
    }

    private fun setupListener() {
        webViewUrl.setOnTouchListener { _, _ ->
            timer?.cancel()
            timer = null

            imgClose.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            tvCount.visibility = View.GONE

            false
        }

        imgClose.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.none, R.anim.none)
        }
    }

    override fun onBackPressed() {
        if (webViewUrl.canGoBack()) {
            webViewUrl.goBack()
        } else {
            if (imgClose.visibility == View.VISIBLE) {
                finish()
                overridePendingTransition(R.anim.none, R.anim.none)
            }
        }
    }

    override fun onDestroy() {
        timer?.cancel()
        timer = null
        super.onDestroy()
    }
}