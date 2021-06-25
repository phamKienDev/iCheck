package vn.icheck.android.ichecklibs.web

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_web_view_libs.*
import kotlinx.android.synthetic.main.toolbar_light_blue.*
import vn.icheck.android.ichecklibs.*
import vn.icheck.android.ichecklibs.util.PermissionHelper
import vn.icheck.android.ichecklibs.util.dpToPx
import java.net.URL
import java.util.*
import java.util.regex.Pattern

class WebViewActivity : AppCompatActivity() {
    private var countUrl = 0
    private var url = ""

    private var webViewRequest: PermissionRequest? = null
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    private val requestPermission = 1
    private val requestChooseFile = 2

    companion object {
        fun start(fragmentActivity: FragmentActivity?, url: String?, isScan: Int? = null, title: String? = null, isMarketing: Boolean? = null) {
            if (fragmentActivity == null || url.isNullOrEmpty()) {
                return
            }

            val intent = Intent(fragmentActivity, WebViewActivity::class.java)
            intent.putExtra(Constant.DATA_1, url)

            if (isScan != null)
                intent.putExtra(Constant.DATA_2, isScan)

            if (title != null)
                intent.putExtra(Constant.DATA_3, title)

            if (isMarketing != null) {
                intent.putExtra(Constant.DATA_4, isMarketing)
            }

            fragmentActivity.startActivity(intent)
        }

        fun start(activity: Activity?, url: String?, isScan: Int? = null, title: String? = null) {
            if (activity == null || url.isNullOrEmpty()) {
                return
            }

            val intent = Intent(activity, WebViewActivity::class.java)
            intent.putExtra(Constant.DATA_1, url)

            if (isScan != null)
                intent.putExtra(Constant.DATA_2, isScan)

            if (title != null)
                intent.putExtra(Constant.DATA_3, title)

            activity.startActivity(intent)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view_libs)

        url = intent?.getStringExtra(Constant.DATA_1) ?: ""
        var isScan = intent?.getIntExtra(Constant.DATA_2, 0) ?: 0
        var title = intent?.getStringExtra(Constant.DATA_3)

        if (url.isEmpty()) {
            onBackPressed()
            return
        }

        if (Patterns.WEB_URL.matcher(url).matches()) {
            if (!url.startsWith("http")) {
                url = "http://$url"
            }
        } else {
            if (url.length > 4 && url.substring(0, 4).toLowerCase(Locale.getDefault()) == "url:") {
                url = url.removeRange(0, 4)
            } else {
                isScan = 0

                if (title.isNullOrEmpty()) {
                    title = getString(R.string.noi_dung_ma_qr)
                }
            }
        }

        setupToolbar(url, title)
//        checkUrl(url, isScan)
        setupWebView(url, title)
    }

    private fun setupToolbar(url: String, title: String?) {
        if (title.isNullOrEmpty()) {
            txtTitle.text = url
        } else {
            txtTitle.text = title
        }

        imgBack.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(url: String, title: String?) {
        webViewUrl.settings.apply {
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

        if (url.startsWith("http") || url.startsWith("https")) {
            if (intent?.getBooleanExtra(Constant.DATA_4, false) == true) {
                val header = hashMapOf<String, String>()
                val urlBuilder = Uri.parse(url).buildUpon()

                header["source"] = "icheck"
                urlBuilder.appendQueryParameter("source", "icheck")

//                SessionManager.session.user?.let { user ->
//                    header["userId"] = user.id.toString()
//                    urlBuilder.appendQueryParameter("userId", user.id.toString())
//
//                    header["icheckId"] = "i-${user.id}"
//                    urlBuilder.appendQueryParameter("icheckId", "i-${user.id}")
//
//                    if (!user.name.isNullOrEmpty()) {
//                        header["name"] = user.name.toString()
//                        urlBuilder.appendQueryParameter("name", user.name.toString())
//                    }
//
//                    if (!user.phone.isNullOrEmpty()) {
//                        header["phone"] = user.phone.toString()
//                        urlBuilder.appendQueryParameter("phone", user.phone.toString())
//                    }
//
//                    if (!user.email.isNullOrEmpty()) {
//                        header["email"] = user.email.toString()
//                        urlBuilder.appendQueryParameter("email", user.email.toString())
//                    }
//                }
//                if (APIConstants.LATITUDE != 0.0 && APIConstants.LONGITUDE != 0.0) {
//                    header["lat"] = APIConstants.LATITUDE.toString()
//                    urlBuilder.appendQueryParameter("lat", APIConstants.LATITUDE.toString())
//                    header["lon"] = APIConstants.LONGITUDE.toString()
//                    urlBuilder.appendQueryParameter("lon", APIConstants.LONGITUDE.toString())
//                }

                webViewUrl.loadUrl(urlBuilder.build().toString(), header)
            } else {
                webViewUrl.loadUrl(url)
            }
        } else {
            webViewUrl.settings.defaultFontSize = 14
            webViewUrl.loadData(Constant.getHtmlData(url), "text/html; charset=utf-8", "UTF-8")
        }

        webViewUrl.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request?.url?.let { url ->
                        handleNewUrl(view, url)
                    }
                }
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                countUrl++
//                if (!checkUrlPVCombank(url)) {
//                    layoutCenter.visibility = View.GONE
//
//                    if (title.isNullOrEmpty()) {
//                        if (!view?.title.isNullOrEmpty()) {
//                            txtTitle.text = view?.title
//                        }
//                    }
//                }
            }
        }

        webViewUrl.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                if (request != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    for (permission in request.resources) {
                        when (permission) {
                            PermissionRequest.RESOURCE_VIDEO_CAPTURE -> {
                                if (PermissionHelper.checkPermission(this@WebViewActivity, Manifest.permission.CAMERA, requestPermission)) {
                                    confirmAllowCamera(request)
                                } else {
                                    webViewRequest = request
                                }
                                return
                            }
                        }
                    }
                }

                super.onPermissionRequest(request)
            }

            override fun onGeolocationPermissionsShowPrompt(origin: String?, callback: GeolocationPermissions.Callback?) {
                DialogHelper.showConfirm(this@WebViewActivity, null, getString(R.string.s_muon_biet_vi_tri_cua_ban, origin?:""), getString(R.string.tu_choi), getString(R.string.cho_phep), true, null, R.color.colorSecondary, object : ConfirmDialogListener {
                    override fun onDisagree() {
                        callback?.invoke(origin, false, false)

                    }

                    override fun onAgree() {
                        callback?.invoke(origin, true, true)

                    }
                })
            }

            override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: WebChromeClient.FileChooserParams?): Boolean {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    fileChooserParams?.createIntent()?.let { intent ->
//                        val chooserIntent = Intent.createChooser(intent, getString(R.string.chon_anh))
//                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, listOf(Intent(MediaStore.ACTION_IMAGE_CAPTURE)).toTypedArray())
                        this@WebViewActivity.startActivityForResult(intent, requestChooseFile)
                        this@WebViewActivity.filePathCallback = filePathCallback
                    }
                    return true
                }
                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
            }
        }
    }

    private fun confirmAllowCamera(request: PermissionRequest?) {
        DialogHelper.showConfirm(this@WebViewActivity, null, getString(R.string.s_muon_su_dung_camera_cua_ban, URL(webViewUrl.url).host), getString(R.string.tu_choi), getString(R.string.cho_phep), true, null, R.color.colorSecondary, object : ConfirmDialogListener {
            override fun onDisagree() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request?.deny()
                }
            }

            override fun onAgree() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request?.grant(request.resources)
                }
            }
        })
    }

    private fun handleNewUrl(view: WebView?, uri: Uri) {
        if (uri.scheme.equals("http") || uri.scheme.equals("https")) {
            view?.loadUrl(uri.toString())
        } else if (uri.scheme.equals("intent")) {
            val appPackage = getAppPackageFromUri(uri)

            if (appPackage != null) {
                val appIntent = packageManager.getLaunchIntentForPackage(appPackage)
                appIntent?.data = uri

                if (appIntent != null) {
                    startActivity(appIntent)
                } else {
                    view?.loadUrl("https://play.google.com/store/apps/details?id=$appPackage")
                }
            }
        }
    }

//    private fun checkUrlPVCombank(url: String?): Boolean {
//        return when {
//            url.toString().startsWith(CreatePVCardActivity.redirectUrl ?: " ") -> {
//                CreatePVCardActivity.redirectUrl = null
//                val cookies = CookieManager.getInstance().getCookie(url)
//                if (cookies != null) {
//                    SettingManager.setSessionIdPvcombank(cookies)
//                }
//
//                if (countUrl > 2) {
//                    startActivityAndFinish<ListPVCardActivity, Int>(Constant.DATA_1, 1)
//                } else {
//                    startActivityAndFinish<HomePVCardActivity>()
//                }
//
//                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.FINISH_CREATE_PVCOMBANK))
//                true
//            }
//            url.toString().startsWith(HomePVCardActivity.redirectUrl ?: " ") -> {
//                HomePVCardActivity.redirectUrl = null
//                finishActivity()
//                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_KYC_SUCCESS))
//                true
//            }
//
//            else -> false
//        }
//    }

    private fun getAppPackageFromUri(intentUri: Uri): String? {
        val pattern = Pattern.compile("package=(.*?);")
        val matcher = pattern.matcher(intentUri.getFragment())
        return if (matcher.find()) matcher.group(1) else null
    }

//    private fun checkUrl(url: String, isScan: Int) {
//        if (isScan != 1) {
//            return
//        }
//
//        val host = Uri.parse(url).host ?: "-------"
//
//        val pattern = Pattern.compile(SettingManager.trustDomain.firstOrNull()?.regex ?: "")
//        val matches = pattern.matcher(host).find()
//
//        if (!matches) {
//            val hotline = SettingManager.clientSetting?.hotline ?: "0902195488"
//            val spannable = SpannableString(getString(R.string.ma_qr_khong_phai_do_icheck_phat_hanh, hotline))
//
//            spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.lightBlue)), 67, 67 + hotline.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//            spannable.setSpan(object : ClickableSpan() {
//                override fun onClick(p0: View) {
//                    val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$hotline"))
//                    startActivity(callIntent)
//                }
//
//                override fun updateDrawState(ds: TextPaint) {
//                    super.updateDrawState(ds)
//                    ds.isUnderlineText = false
//                }
//            }, 67, 67 + hotline.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//
//            layoutNote.visibility = View.VISIBLE
//            tvNote.text = spannable
//            tvNote.movementMethod = LinkMovementMethod.getInstance()
//        }
//
//        imgClose.setOnClickListener {
//            layoutNote.visibility = View.GONE
//        }
//    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == requestPermission) {
                if (webViewRequest != null) {
                    if (PermissionHelper.checkResult(grantResults)) {
                        confirmAllowCamera(webViewRequest)
                    } else {
                        webViewRequest!!.deny()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            requestChooseFile -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    filePathCallback?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data))
                    filePathCallback = null
                }
            }
        }
    }

    override fun onBackPressed() {
        if (webViewUrl.canGoBack() && !url.contains("dev.qcheck.vn")) {
            webViewUrl.goBack()
        } else {
            super.onBackPressed()
        }
    }
}