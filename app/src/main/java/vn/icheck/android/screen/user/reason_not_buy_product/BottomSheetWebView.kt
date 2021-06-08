package vn.icheck.android.screen.user.reason_not_buy_product

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.FragmentManager
import vn.icheck.android.ICheckApplication
import vn.icheck.android.databinding.BottomSheetDialogReasonNotBuyProductBinding
import vn.icheck.android.ichecklibs.base_dialog.BaseBottomSheetDialogFragment

class BottomSheetWebView : BaseBottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetDialogReasonNotBuyProductBinding

    private var url: String = ""

    companion object {
        fun show(fragmentManager: FragmentManager, url: String) {
            if (fragmentManager.findFragmentByTag(BottomSheetWebView::class.java.simpleName)?.isAdded != true) {
                BottomSheetWebView().apply {
                    setUrl(url)
                    show(fragmentManager, BottomSheetWebView::class.java.simpleName)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = BottomSheetDialogReasonNotBuyProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ICheckApplication.currentActivity()?.let { activity ->
            binding.layoutContainer.minimumHeight = activity.findViewById<View>(android.R.id.content).rootView.height
        }

        binding.webView.settings.javaScriptEnabled = true
        @Suppress("DEPRECATION")
        binding.webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.allowFileAccessFromFileURLs = true
        binding.webView.settings.allowUniversalAccessFromFileURLs = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            binding.webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
        }

        binding.webView.loadUrl(url)

        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request?.url?.let { url ->
                        view?.loadUrl(url.toString())
                    }
                }
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                
                binding.viewCenter?.let { viewCenter ->
                    binding.layoutContent.removeView(viewCenter)
                }
            }
        }
    }

    fun setUrl(url: String) {
        this.url = url
    }
}