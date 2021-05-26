package vn.icheck.android.screen.dialog

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.dialog_notification_firebase.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseDialog
import vn.icheck.android.chat.icheckchat.base.view.setGoneView
import vn.icheck.android.chat.icheckchat.base.view.setVisible
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.Constant.getHtmlData
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.popup.PopupInteractor
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.util.ick.spToPx

abstract class DialogNotificationFirebaseAds(
    context: Activity, private val image: String?, private val htmlText: String?, private val link: String?, private val schema: String?,
    private val schemaParams: String? = null,
    private val idAds: Long? = null, // phân biệt popup firebase vs popup quảng cáo
) : BaseDialog(context, R.style.DialogTheme) {

    override val getLayoutID: Int
        get() = R.layout.dialog_notification_firebase
    override val getIsCancelable: Boolean
        get() = false

    override fun onInitView() {
        setGoneView(imageView, textView, layoutWeb)

        when {
            image != null -> {
                imageView.setVisible()

                CoroutineScope(Dispatchers.IO).launch {
                    Glide.with(ICheckApplication.getInstance())
                        .asBitmap()
                        .timeout(30000)
                        .load(image)
                        .listener(object : RequestListener<Bitmap> {
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                                Log.d("onLoad", "onLoadFailed: false")
                                dismiss()
                                return false
                            }

                            override fun onResourceReady(
                                resource: Bitmap?,
                                model: Any?,
                                target: Target<Bitmap>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                if (resource != null) {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        val maxHeight = container.height - SizeHelper.size52
                                        val ratioHeight = container.height.toDouble() / resource.height.toDouble()
                                        val ratioWidth = container.width.toDouble() / resource.width.toDouble()
                                        when {
                                            resource.width > container.width && resource.height <= container.height -> {
                                                // ảnh rộng quá màn hình -> max with, wrap height
                                                imageView.layoutParams =
                                                    LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                                            }
                                            resource.height > container.height && resource.width <= container.width -> {
                                                //  ảnh dài quá màn hình ->  max height, wrap with
                                                imageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, maxHeight)
                                            }
                                            resource.width > resource.height && resource.width > container.width -> {
                                                //  ảnh rộng quá màn hình && ảnh có chiều rộng lớn hơn-> max with, wrap height
                                                imageView.layoutParams =
                                                    LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                                            }
                                            resource.height > resource.width && resource.height > container.height -> {
                                                if (ratioWidth > ratioHeight) {
                                                    // max with
                                                    imageView.layoutParams =
                                                        LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
                                                } else {
                                                    // max height
                                                    imageView.layoutParams =
                                                        LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                                                }
                                            }
                                            resource.height < container.height && resource.width < container.width -> {
                                                /* ảnh có chiều rộng & chiều dài đều bé hơn màn hình
                                                -> tính tỉ lệ chiều nào gần full màn hình sẽ lấy chiều đó là MATCH_PARENT
                                                 */
                                                if (ratioWidth > ratioHeight) {
                                                    // max with
                                                    imageView.layoutParams =
                                                        LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
                                                } else {
                                                    // max height
                                                    imageView.layoutParams =
                                                        LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                                                }
                                            }
                                        }
                                        imageView.setImageBitmap(resource)
                                    }
                                }
                                return false
                            }
                        })
                        .submit()
                        .get()
                }

                imageView.setOnClickListener {
                    if (idAds != null) {
                        clickPopupAds(idAds)
                    } else {
                        dismiss()
                        ICheckApplication.currentActivity()?.let { activity ->
                            FirebaseDynamicLinksActivity.startDestinationUrl(activity, schema)
                        }
                    }
                }
            }
            htmlText != null -> {
                setupWebView()
                webView.settings.defaultFontSize = 14f.spToPx().toInt()
                textView.setVisible()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webViewHtml.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING;
                } else {
                    webViewHtml.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL;
                }
                webViewHtml.loadDataWithBaseURL(null, getHtmlData(htmlText), "text/html", "utf-8", "")
            }
            link != null -> {
                setupWebView()
                layoutWeb.setVisible()

                if (vn.icheck.android.constant.Constant.isMarketingStamps(link)) {
                    val header = hashMapOf<String, String>()
                    val urlBuilder = Uri.parse(link).buildUpon()

                    header["source"] = "icheck"
                    urlBuilder.appendQueryParameter("source", "icheck")

                    SessionManager.session.user?.let { user ->
                        header["userId"] = user.id.toString()
                        urlBuilder.appendQueryParameter("userId", user.id.toString())

                        header["icheckId"] = "i-${user.id}"
                        urlBuilder.appendQueryParameter("icheckId", "i-${user.id}")

                        if (!user.name.isNullOrEmpty()) {
                            header["name"] = user.name.toString()
                            urlBuilder.appendQueryParameter("name", user.name.toString())
                        }

                        if (!user.phone.isNullOrEmpty()) {
                            header["phone"] = user.phone.toString()
                            urlBuilder.appendQueryParameter("phone", user.phone.toString())
                        }

                        if (!user.email.isNullOrEmpty()) {
                            header["email"] = user.email.toString()
                            urlBuilder.appendQueryParameter("email", user.email.toString())
                        }
                    }
                    if (APIConstants.LATITUDE != 0.0 && APIConstants.LONGITUDE != 0.0) {
                        header["lat"] = APIConstants.LATITUDE.toString()
                        urlBuilder.appendQueryParameter("lat", APIConstants.LATITUDE.toString())
                        header["lon"] = APIConstants.LONGITUDE.toString()
                        urlBuilder.appendQueryParameter("lon", APIConstants.LONGITUDE.toString())
                    }

                    webView.loadUrl(urlBuilder.build().toString(), header)
                } else {
                    webView.loadUrl(link)
                }
            }
        }

        imgClose.setOnClickListener {
            dismiss()
        }

        setOnDismissListener {
            onDismiss()
        }
    }

    private fun clickPopupAds(id: Long) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            checkSchemePopupAds()
        } else {
            ICheckApplication.currentActivity()?.let { activity ->
                DialogHelper.showLoading(activity)

                PopupInteractor().clickPopup(id, object : ICNewApiListener<ICResponse<Any>> {
                    override fun onSuccess(obj: ICResponse<Any>) {
                        DialogHelper.closeLoading(activity)
                        checkSchemePopupAds()
                    }

                    override fun onError(error: ICResponseCode?) {
                        DialogHelper.closeLoading(activity)
                        checkSchemePopupAds()
                    }
                })
            }
        }
    }

    private fun checkSchemePopupAds() {
        dismiss()
        if (!schema.isNullOrEmpty()) {
            if (schema.startsWith("http")) {
                WebViewActivity.openChrome(schema)
            } else {
                ICheckApplication.currentActivity()?.let { activity ->
                    if (!schemaParams.isNullOrEmpty()) {
                        FirebaseDynamicLinksActivity.startTarget(activity, schema, schemaParams)
                    } else {
                        FirebaseDynamicLinksActivity.startDestinationUrl(activity, schema)
                    }
                }
            }
        }
    }

    private fun setupWebView() {
        webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccessFromFileURLs = true
                allowUniversalAccessFromFileURLs = true
                setAppCacheEnabled(true)
                loadsImagesAutomatically = true
                javaScriptCanOpenWindowsAutomatically = true
                allowFileAccess = true
                mediaPlaybackRequiresUserGesture = false
                // Full with
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

            var isPageLoaded = false

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    isPageLoaded = false
                }

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    if (isPageLoaded) {
                        if (url?.startsWith("http") == true) {
                            ICheckApplication.currentActivity()?.let { activity ->
                                WebViewActivity.start(activity, url)
                            }
                            return true
                        }
                    }
                    return super.shouldOverrideUrlLoading(view, url)
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    isPageLoaded = newProgress == 100
                }
            }
        }
    }

    protected abstract fun onDismiss()
}