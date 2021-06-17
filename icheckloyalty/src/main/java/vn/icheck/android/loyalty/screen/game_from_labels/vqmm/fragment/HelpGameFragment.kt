package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_help_game.*
import vn.icheck.android.ichecklibs.util.spToPx
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.viewmodel.LuckyGameViewModel

class HelpGameFragment : Fragment() {

    val args: HelpGameFragmentArgs by navArgs()
    val luckyGameViewModel: LuckyGameViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_help_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.description

        setupWebView()
        setData()
        setupListener()
    }

    private fun setupWebView() {
        @Suppress("SetJavaScriptEnabled")
        webView.settings.javaScriptEnabled = true
        @Suppress("DEPRECATION")
        webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.allowUniversalAccessFromFileURLs = true
        webView.settings.defaultFontSize = 16

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
        }
    }

    private fun setData() {
        val html = args.description

        if (html.isNotEmpty()) {
            webView.loadDataWithBaseURL(null, html, "text/html; charset=utf-8", "UTF-8", null)
        } else {
//            DialogHelper.showNotification(requireContext(), R.string.thong_bao, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
//                override fun onDone() {
//                    imgBack.performClick()
//                }
//            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        luckyGameViewModel.updatePlay(args.currentCount)
    }

    private fun setupListener() {
        imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}