package vn.icheck.android.screen.dialog

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.dialog_notification_firebase1.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseDialog
import vn.icheck.android.chat.icheckchat.base.view.setGoneView
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.util.beVisible
import vn.icheck.android.loyalty.base.setVisible
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.popup.PopupInteractor
import vn.icheck.android.network.models.ICPopup
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.util.ick.beInvisible

abstract class DialogNotificationFirebaseAds(
    context: Activity,
    private val document: String?,
    private val url: String?,
    private val bitmap: Bitmap?,
    private val schema: String?,
    private val popup: ICPopup?
) : BaseDialog(context, R.style.DialogTheme) {

    private var isLoadFirst = false

    override val getLayoutID: Int
        get() = R.layout.dialog_notification_firebase1
    override val getIsCancelable: Boolean
        get() = false

    companion object {
        private fun loadImage(activity: FragmentActivity, image: String, schema: String?, popup: ICPopup?, onDissmis: () -> Unit) {
            activity.lifecycleScope.launch(Dispatchers.IO) {
                Glide.with(ICheckApplication.getInstance())
                    .asBitmap()
                    .timeout(30000)
                    .load(image)
                    .transform(FitCenter(), RoundedCorners(SizeHelper.size10))
                    .listener(object : RequestListener<Bitmap> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
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
                                    object : DialogNotificationFirebaseAds(activity, null, null, resource, schema, popup) {
                                        override fun onDismiss() {

                                        }

                                    }.show()
                                }
                            }
                            return false
                        }
                    })
                    .submit()
                    .get()
            }
        }

        fun showPopupAds(activity: FragmentActivity, popup: ICPopup) {
            when (popup.displayType) {
                "url" -> {
                    if (!popup.url.isNullOrEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            object : DialogNotificationFirebaseAds(activity, null, popup.url, null, null, popup) {
                                override fun onDismiss() {

                                }

                            }.show()
                        }
                    }
                }
                "image" -> {
                    if (!popup.image.isNullOrEmpty()) {
                        loadImage(activity, popup.image!!, null, popup) {

                        }
                    }
                }
                else -> {
                    if (!popup.document.isNullOrEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            object : DialogNotificationFirebaseAds(activity, popup.document, null, null, null, popup) {
                                override fun onDismiss() {
                                }
                            }.show()

                        }
                    }
                }
            }
        }

        fun showPopupFirebase(activity: FragmentActivity, image: String?, document: String?, url: String?, schema: String?) {
            if (image.isNullOrEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    object : DialogNotificationFirebaseAds(activity, document, url, null, schema, null) {
                        override fun onDismiss() {
                        }
                    }.show()
                }

            } else {
                loadImage(activity, image, schema, null) {

                }
            }
        }
    }


    override fun onInitView() {
        setGoneView(imageView, layoutText, layoutWeb)

        when {
            bitmap != null -> {
                imageView.beVisible()
                setImageView(bitmap)

                imageView.setOnClickListener {
                    if (popup != null) {
                        clickPopupAds(popup)
                    } else {
                        dismiss()
                        ICheckApplication.currentActivity()?.let { activity ->
                            FirebaseDynamicLinksActivity.startDestinationUrl(activity, schema)
                        }
                    }
                }
            }
            document != null -> {
                setupWebViewHtml(document)
            }
            url != null -> {
                setupWebViewUrl()
                layoutWeb.setVisible()

                if (Constant.isMarketingStamps(url)) {
                    val header = hashMapOf<String, String>()
                    val urlBuilder = Uri.parse(url).buildUpon()

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

                    webViewUrl.loadUrl(urlBuilder.build().toString(), header)
                } else {
                    webViewUrl.loadUrl(url)
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

    private fun setImageView(resource: Bitmap) {
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
                        imageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
                    } else {
                        // max height
                        imageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    }
                }
            }
            imageView.setImageBitmap(resource)
        }
    }

    private fun clickPopupAds(popup: ICPopup) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            checkSchemePopupAds(popup)
        } else {
            ICheckApplication.currentActivity()?.let { activity ->
                popup.id?.let {
                    if (!popup.deeplink.isNullOrEmpty() || !popup.path.isNullOrEmpty()) {
                        DialogHelper.showLoading(activity)

                        PopupInteractor().clickPopup(it, object : ICNewApiListener<ICResponse<Any>> {
                            override fun onSuccess(obj: ICResponse<Any>) {
                                DialogHelper.closeLoading(activity)
                                checkSchemePopupAds(popup)
                            }

                            override fun onError(error: ICResponseCode?) {
                                DialogHelper.showLoading(activity)
                                checkSchemePopupAds(popup)
                            }
                        })
                    } else {
                        checkSchemePopupAds(popup)
                    }
                }
            }
        }
    }

    private fun checkSchemePopupAds(popup: ICPopup) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (!popup.deeplink.isNullOrEmpty()) {
                dismiss()
                if (popup.deeplinkParams.isNullOrEmpty()) {
                    FirebaseDynamicLinksActivity.startDestinationUrl(activity, popup.deeplink)
                } else {
                    FirebaseDynamicLinksActivity.startTarget(activity, popup.deeplink, popup.deeplinkParams)
                }
            } else if (!popup.path.isNullOrEmpty()) {
                dismiss()
                WebViewActivity.start(ICheckApplication.currentActivity(), popup.path)
            }
        }
    }


    private fun setupWebViewHtml(htmlText: String) {
        webViewHtml.apply {
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
                cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                // Full with
//                loadWithOverviewMode = true
//                useWideViewPort = true
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

            loadDataWithBaseURL(null, vn.icheck.android.ichecklibs.Constant.getHtmlTextNotPadding(htmlText), "text/html", "utf-8", "")
            isVerticalScrollBarEnabled = true
            var isPageLoaded = false

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    isPageLoaded = false
                    isLoadFirst = true
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

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    isPageLoaded = newProgress == 100

                    if (isShowing && layoutText != null && newProgress >= 80) {
                        if (isLoadFirst) {
                            layoutText.beInvisible()
                            Handler().postDelayed({
                                layoutText.beVisible()
                                layoutText.background = ContextCompat.getDrawable(context, R.drawable.bg_white_corners_10)
                            }, 200)
                            isLoadFirst = false

                        }
                    }
                }
            }
        }
    }


    private fun setupWebViewUrl() {
        webViewUrl.apply {

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
                    isLoadFirst = true
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

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    isPageLoaded = newProgress == 100
                    if (isShowing && layoutWeb != null && newProgress >= 70) {
                        if (isLoadFirst) {
                            layoutWeb.beInvisible()
                            Handler().postDelayed({
                                layoutWeb.beVisible()
                                layoutWeb.background = ContextCompat.getDrawable(context, R.drawable.bg_white_corners_10)
                            }, 200)
                            isLoadFirst = false
                        }
                    }
                }
            }
        }
    }

    protected abstract fun onDismiss()
}
