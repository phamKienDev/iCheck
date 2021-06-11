package vn.icheck.android.screen.user.reason_not_buy_product

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.bottom_sheet_dialog_reason_not_buy_product.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R

class BottomSheetWebView(context: Context) : FrameLayout(context) {

    private val mBottomSheetDialog: BottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialog)
    private var mCurrentWebViewScrollY = 0

    init {
        inflateLayout(context)
        setupBottomSheetBehaviour()
        setupWebView()
    }

    private fun inflateLayout(context: Context) {
        inflate(context, R.layout.bottom_sheet_dialog_reason_not_buy_product, this)

        ICheckApplication.currentActivity()?.let { activity ->
            layoutContainer.minimumHeight = activity.findViewById<View>(android.R.id.content).rootView.height
        }

        mBottomSheetDialog.setContentView(this)

        mBottomSheetDialog.window?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.setBackgroundResource(android.R.color.transparent);
    }

    private fun setupBottomSheetBehaviour() {
        (parent as? View)?.let { view ->
            BottomSheetBehavior.from(view).let { behaviour ->
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED

                behaviour.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onSlide(bottomSheet: View, slideOffset: Float) {

                    }

                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_DRAGGING && mCurrentWebViewScrollY > 0) {
                            // this is where we check if webview can scroll up or not and based on that we let BottomSheet close on scroll down
                            behaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                        } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            close()
                        }
                    }
                })
            }
        }
    }

    private fun setupWebView() {
        webView.onScrollChangedCallback = object : ObservableWebView.OnScrollChangeListener {
            override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
                mCurrentWebViewScrollY = t
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun showWithUrl(url: String) {
        mBottomSheetDialog.show()

        webView.settings.javaScriptEnabled = true
        @Suppress("DEPRECATION")
        webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.settings.allowUniversalAccessFromFileURLs = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
        }

        webView.loadUrl(url)

        var loadingFinished = true
        var redirect = false

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                if (!loadingFinished) {
                    redirect = true;
                }
                loadingFinished = false;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request?.url?.let { url ->
                        view?.loadUrl(url.toString())
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
                    layoutCenter.visibility = View.GONE
                }
            }
        }
    }

    fun close() {
        mBottomSheetDialog.dismiss()
    }
}