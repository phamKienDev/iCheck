package vn.icheck.android.util.kotlin

import android.app.Activity
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.network.util.DeviceUtils
import vn.icheck.android.util.ick.logError

object HideWebUtils {

    fun showWeb( screenName: String, barcode: String? = null, productID: Long? = null) {
        Handler().postDelayed({
            SettingManager.clientSetting?.fp_url?.let { url ->
                ICheckApplication.currentActivity()?.let { activity ->
                    (activity.findViewById<View>(android.R.id.content).rootView as ViewGroup).apply {
                        var mUrl = url

                        val bcryptHash = BCrypt.hashpw("icheck", BCrypt.gensalt(10))
                        mUrl += "?bvc=$bcryptHash"
                        mUrl += "&screen=$screenName"
                        mUrl += "&device_id=${DeviceUtils.getUniqueDeviceId()}"
                        mUrl += "&w=" + if (NetworkHelper.isConnectedWifi(activity)) "1" else "0"

                        SessionManager.session.user?.id?.let { userID ->
                            mUrl += "&u_id=$userID"
                        }
                        if (!barcode.isNullOrEmpty()) {
                            mUrl += "&barcode=$barcode"
                        }
                        if (productID != null) {
                            mUrl += "&product_id=$productID"
                        }

                        runWebView(activity, mUrl)
                    }
                }
            }
        }, 300)
    }

    private fun runWebView(activity: Activity, url: String) {
        try {
            (activity.findViewById<View>(android.R.id.content).rootView as ViewGroup).apply {
                findViewById<WebView>(R.id.baseWebView)?.apply {
                    loadUrl(url)
                }.run {
                    addView(WebView(activity).apply {
                        id = R.id.baseWebView
                        layoutParams =
                                ViewGroup.LayoutParams(SizeHelper.size1, SizeHelper.size1)
                        visibility = View.INVISIBLE

                        settings.javaScriptEnabled = true
                        @Suppress("DEPRECATION")
                        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                        settings.domStorageEnabled = true
                        settings.allowFileAccessFromFileURLs = true
                        settings.useWideViewPort = true
                        settings.loadWithOverviewMode = true
                        settings.allowUniversalAccessFromFileURLs = true

                        loadUrl(url)
                    })
                }
            }
        } catch (e: Exception) {
            logError(e)
        }
    }
}