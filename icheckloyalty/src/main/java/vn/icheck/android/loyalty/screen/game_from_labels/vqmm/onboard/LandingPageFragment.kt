package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.onboard

import vn.icheck.android.loyalty.dialog.DialogNotification
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_onboard_landing.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.room.database.LoyaltyDatabase
import vn.icheck.android.loyalty.room.entity.ICKCampaignId
import vn.icheck.android.loyalty.sdk.LoyaltySdk

class LandingPageFragment(private val landingPage: String?, private val titleButton: String?, private val schema: String?, private val idCampaign: String?) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_onboard_landing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_agree.text = titleButton

        btn_agree.setOnClickListener {
            if (!idCampaign.isNullOrEmpty()) {
                LoyaltyDatabase.getDatabase(ApplicationHelper.getApplicationByReflect()).idCampaignDao().insertIDCampaign(ICKCampaignId(idCampaign.toLong()))
            }
            if (schema != null) {
                activity?.finish()
                LoyaltySdk.openActivity(schema)
            }
        }

        if (!landingPage.isNullOrEmpty()) {
            loadWebView(landingPage)
        } else {
            object : DialogNotification(requireContext(), getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, getString(R.string.ok), false) {
                override fun onDone() {
                    findNavController().popBackStack()
                }
            }.show()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWebView(url: String) {
        webViewUrl.settings.javaScriptEnabled = true
        @Suppress("DEPRECATION")
        webViewUrl.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        webViewUrl.settings.domStorageEnabled = true
        webViewUrl.settings.allowFileAccessFromFileURLs = true
        webViewUrl.settings.useWideViewPort = true
        webViewUrl.settings.loadWithOverviewMode = true
        webViewUrl.settings.allowUniversalAccessFromFileURLs = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webViewUrl.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
        }

        webViewUrl.loadUrl(url)

        webViewUrl.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.loadUrl(request.url.toString())
                }
                return true
            }
        }
    }
}