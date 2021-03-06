package vn.icheck.android.screen.dialog

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.dialog_notification_firebase.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.util.beVisible
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.popup.PopupInteractor
import vn.icheck.android.network.models.ICPopup
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.util.ick.beInvisible

class DialogFragmentNotificationFirebaseAds : DialogFragment() {

    private var document: String? = null
    private var url: String? = null
    private var bitmap: Bitmap? = null
    private var schema: String? = null
    private var popup: ICPopup? = null

    private var isLoadFirst = false
    private var urlLoading = ""

    fun setData(document: String?, url: String?, bitmap: Bitmap?, schema: String?, popup: ICPopup?) {
        this.document = document
        this.url = url
        this.bitmap = bitmap
        this.schema = schema
        this.popup = popup
    }

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
                                if (!activity.isDestroyed) {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        DialogFragmentNotificationFirebaseAds().apply {
                                            setData(null, null, resource, schema, popup)
                                            setStyle(STYLE_NORMAL, R.style.DialogTheme)
                                        }.show(activity.supportFragmentManager, null)
                                    }
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
                        if (!activity.isDestroyed) {
                            CoroutineScope(Dispatchers.Main).launch {
                                DialogFragmentNotificationFirebaseAds().apply {
                                    setData(null, popup.url, null, null, popup)
                                }.show(activity.supportFragmentManager, null)
                            }
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
                        if (!activity.isDestroyed) {
                            CoroutineScope(Dispatchers.Main).launch {
                                DialogFragmentNotificationFirebaseAds().apply {
                                    setData(popup.document, null, null, null, popup)
                                    setStyle(STYLE_NORMAL, R.style.DialogTheme)
                                }.show(activity.supportFragmentManager, null)
                            }
                        }
                    }
                }
            }
        }


        fun showPopupFirebase(activity: FragmentActivity, image: String?, document: String?, url: String?, schema: String?) {
            if (image.isNullOrEmpty()) {
                if (!activity.isDestroyed) {
                    CoroutineScope(Dispatchers.Main).launch {
                        DialogFragmentNotificationFirebaseAds().apply {
                            setData(document, url, null, schema, null)
                            setStyle(STYLE_NORMAL, R.style.DialogTheme)
                        }.show(activity.supportFragmentManager, null)
                    }
                }
            } else {
                loadImage(activity, image, schema, null) {

                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (dialog != null && dialog!!.window != null) {
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        }
        return inflater.inflate(R.layout.dialog_notification_firebase, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)

        if (imageView == null) {
            return
        }

        when {
            bitmap != null -> {
                imageView?.beVisible()
                setImageView(bitmap!!)

                imageView?.setOnClickListener {
                    if (popup != null) {
                        clickPopupAds(popup!!)
                    } else {
                        dismiss()
                        ICheckApplication.currentActivity()?.let { activity ->
                            FirebaseDynamicLinksActivity.startDestinationUrl(activity, schema)
                        }
                    }
                }
            }

            document != null -> {
                Handler().postDelayed({
                    setupWebViewHtml(document!!)
                }, 200)
            }
            url != null -> {
                imgClose.layoutParams = LinearLayout.LayoutParams(SizeHelper.size32, SizeHelper.size32).apply {
                    setMargins(0, SizeHelper.size60, 0, SizeHelper.size20)
                }
                layoutWeb.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    setMargins(0, 0, 0, SizeHelper.size80)
                }

                if (Constant.isMarketingStamps(url!!)) {
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

                    setupWebViewUrl(urlBuilder.build().toString(), header)
                } else {
                    setupWebViewUrl(url!!, null)
                }
            }
        }

        imgClose.setOnClickListener {
            dismiss()
        }
    }


    private fun setImageView(resource: Bitmap) {
        CoroutineScope(Dispatchers.Main).launch {
            val maxHeight = container.height - SizeHelper.size52
            val ratioHeight = container.height.toDouble() / resource.height.toDouble()
            val ratioWidth = container.width.toDouble() / resource.width.toDouble()
            when {
                resource.width > container.width && resource.height <= container.height -> {
                    // ???nh r???ng qu?? m??n h??nh -> max with, wrap height
                    imageView.layoutParams =
                            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                }
                resource.height > container.height && resource.width <= container.width -> {
                    //  ???nh d??i qu?? m??n h??nh ->  max height, wrap with
                    imageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, maxHeight)
                }
                resource.width > resource.height && resource.width > container.width -> {
                    //  ???nh r???ng qu?? m??n h??nh && ???nh c?? chi???u r???ng l???n h??n-> max with, wrap height
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
                    /* ???nh c?? chi???u r???ng & chi???u d??i ?????u b?? h??n m??n h??nh
                                                -> t??nh t??? l??? chi???u n??o g???n full m??n h??nh s??? l???y chi???u ???? l?? MATCH_PARENT
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
        webViewHtml?.apply {
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
            requestFocusFromTouch()
            isVerticalScrollBarEnabled = true
            isHorizontalScrollBarEnabled = true

            loadDataWithBaseURL(null, vn.icheck.android.ichecklibs.Constant.getHtmlTextNotPadding(htmlText), "text/html", "utf-8", "")
            var isPageLoaded = false

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    isPageLoaded = false
                    isLoadFirst = true
                }

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    if (isPageLoaded && urlLoading != url) {
                        if (url?.startsWith("http") == true) {
                            ICheckApplication.currentActivity()?.let { activity ->
                                urlLoading = url
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

                    if (isAdded && layoutText != null && newProgress >= 80) {
                        if (isLoadFirst) {
                            layoutText.beInvisible()
                            Handler().postDelayed({
                                layoutText.beVisible()
                                layoutText.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_white_corners_10)
                            }, 200)
                            isLoadFirst = false

                        }
                    }
                }
            }
        }
    }

    private fun setupWebViewUrl(url: String, header: Map<String, String>?) {
        webView?.apply {
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

            if (header != null) {
                loadUrl(url, header)
            } else {
                loadUrl(url)
            }

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    isPageLoaded = false
                    isLoadFirst = true
                }

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    if (isPageLoaded && urlLoading != url) {
                        if (url?.startsWith("http") == true) {
                            ICheckApplication.currentActivity()?.let { activity ->
                                urlLoading = url
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
                    if (isAdded && layoutWeb != null && newProgress >= 70) {
                        if (isLoadFirst) {
                            layoutWeb.beInvisible()
                            Handler().postDelayed({
                                layoutWeb.beVisible()
                                layoutWeb.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_white_corners_10)
                            }, 200)
                            isLoadFirst = false
                        }
                    }
                }
            }
        }
    }

}