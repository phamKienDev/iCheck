package vn.icheck.android.screen.user.social_chat

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.view.LayoutInflater
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.annotation.StringDef
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.android.parcel.Parcelize
import vn.icheck.android.base.activity.ViewBindingActivity
import vn.icheck.android.constant.Constant
import vn.icheck.android.constant.ICK_REQUEST_CAMERA
import vn.icheck.android.databinding.ActivitySocialChatBinding
import vn.icheck.android.loyalty.helper.ActivityHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.model.chat.*
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.screen.scan.V6ScanditActivity
import vn.icheck.android.util.ick.logDebug
import vn.icheck.android.util.ick.showSimpleSuccessToast



class SocialChatActivity : ViewBindingActivity<ActivitySocialChatBinding>() {

    private val requestChooseFile = 2
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    companion object {
        fun createRoomChat(context: Context, members: Array<ChatMember>, roomId: String? = null, barcode: String? = null) {
            val i = Intent(context, SocialChatActivity::class.java)
            i.putExtra("members", members)
            i.putExtra("roomId", roomId)
            i.putExtra("barcode", barcode)
            context.startActivity(i)
        }

        fun createRoomChat(context: Context, userId: Long?, roomId: String? = null, barcode: String? = null) {
            val i = Intent(context, SocialChatActivity::class.java)
            val arrMember = arrayListOf<ChatMember>()
            arrMember.add(ChatMember(SessionManager.session.user?.id, role = ADMIN))
            arrMember.add(ChatMember(userId))
            createRoomChat(context, arrMember.toTypedArray(), roomId, barcode)
            context.startActivity(i)
        }

        fun createPageChat(context: Context, pageId: Long?, roomId: String? = null, barcode: String? = null) {
            val i = Intent(context, SocialChatActivity::class.java)
            val arrMember = arrayListOf<ChatMember>()
            arrMember.add(ChatMember(SessionManager.session.user?.id, role = ADMIN, type = USER))
            arrMember.add(ChatMember(pageId, type = PAGE, role = MEMBER))
            createRoomChat(context, arrMember.toTypedArray(), roomId, barcode)
            context.startActivity(i)
        }

        const val OPEN_SCAN = 1
    }

    override val bindingInflater: (LayoutInflater) -> ActivitySocialChatBinding
        get() = ActivitySocialChatBinding::inflate
    var url = if(vn.icheck.android.BuildConfig.FLAVOR.contentEquals("dev")) "https://chat.dev.icheck.vn" else "https://chat.icheck.vn"
    override fun setup() {

        val query = arrayListOf<String?>()
        val members = arrayListOf<HashMap<String, Any?>>()
        intent.getParcelableArrayExtra("members")?.let {
            for (item in it) {
                if (item is ChatMember) {
                    val member = hashMapOf<String, Any?>()
                    member.put("\"id\"", item.id)
                    member.put("\"type\"", item.type)
                    member.put("\"role\"", item.role)
                    members.add(member)
                }
            }
        }

        query.add("token=${SessionManager.session.token.toString()}")
        query.add("firebase_token=${SessionManager.session.firebaseToken.toString()}")
        if (!intent.getStringExtra("roomId").isNullOrEmpty()) {
            query.add("room_id=${intent.getStringExtra("roomId")}")
        } else {
            query.add("members=${members.toString().replace("=", ":")}")
        }
        query.add("label={\"id\":${SessionManager.session.user?.id},\"type\":\"user\"}")
        query.add("callback=icheck://back")
        if (!intent.getStringExtra("barcode").isNullOrEmpty()) {
            query.add("barcode=${intent.getStringExtra("barcode")}")
        }

//        query.add("label=${hashMapOf<String, Any?>("id" to SessionManager.session.user?.id, "type" to USER).toString()}")
        binding.container.setInitialScale(1)
        binding.container.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                this@SocialChatActivity.showSimpleSuccessToast(message)
                return true
            }

            override fun onConsoleMessage(message: String?, lineNumber: Int, sourceID: String?) {
                logDebug(message + " -- From line "
                        + lineNumber + " of "
                        + sourceID)
                super.onConsoleMessage(message, lineNumber, sourceID)
            }

            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                logDebug(consoleMessage?.message() + " -- From line "
                        + consoleMessage?.lineNumber() + " of "
                        + consoleMessage?.sourceId())
                return super.onConsoleMessage(consoleMessage)
            }

            override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    fileChooserParams?.createIntent()?.let { intent ->
//                        val chooserIntent = Intent.createChooser(intent, getString(R.string.chon_anh))
//                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, listOf(Intent(MediaStore.ACTION_IMAGE_CAPTURE)).toTypedArray())
                        ActivityHelper.startActivityForResult(this@SocialChatActivity, intent, requestChooseFile)
                        this@SocialChatActivity.filePathCallback = filePathCallback
                    }
                    return true
                }
                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
            }

//            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//            override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
//
//                fileChooserParams?.createIntent()?.let { intent ->
//                    ActivityHelper.startActivity(this@SocialChatActivity, intent)
//                    this@SocialChatActivity.filePathCallback = filePathCallback
//                }
//                return false
//            }
        }
        binding.container.webViewClient = object : WebViewClient() {


            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                if (request?.url.toString().contains("icheck://back")) {
                    finish()
                }
                return super.shouldOverrideUrlLoading(view, request)

            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url.toString().contains("icheck://back")) {
                    finish()
                    Thread.sleep(400)
                    return true
                }
                if (url.toString().contains("unsafe:icheck")) {
                    Thread.sleep(400)
                    FirebaseDynamicLinksActivity.startTargetPath(this@SocialChatActivity, url.toString().replace("unsafe:", ""))
                    return true
                }
                if (url.toString().contains("icheck:")) {
                    Thread.sleep(400)
                    FirebaseDynamicLinksActivity.startTargetPath(this@SocialChatActivity, url.toString())
                    return true
                }
                return super.shouldOverrideUrlLoading(view, url)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
//                DialogHelper.showLoading(this@SocialChatActivity)
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
//                DialogHelper.closeLoading(this)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
//                DialogHelper.closeLoading(this)
            }

            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                logDebug("error code: $errorCode description: $description")
                super.onReceivedError(view, errorCode, description, failingUrl)
            }
        }
        binding.container.settings.javaScriptEnabled = true
        binding.container.settings.setDomStorageEnabled(true)
        binding.container.settings.setLoadWithOverviewMode(true)
        binding.container.settings.setUseWideViewPort(true)
        binding.container.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY)
        binding.container.setScrollbarFadingEnabled(false)
        binding.container.addJavascriptInterface(JsInterface(this, OPEN_SCAN), "jsHandler")
        url += "?${query.joinToString(separator = "&")}"
        logDebug(url)
        binding.container.loadUrl(url)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            OPEN_SCAN -> {
                if (resultCode == RESULT_OK) {
                    val barcode = data?.getStringExtra(Constant.DATA_2)
                    val isQr = data?.getBooleanExtra(Constant.DATA_1, false)
                    if (!barcode.isNullOrEmpty()) {
                        if (isQr == false) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                binding.container.evaluateJavascript("onScanBarcode($barcode);", null)
                            } else {
                                binding.container.loadUrl("javascript:onScanBarcode($barcode);$url")
                            }
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                binding.container.evaluateJavascript("onScanQRCode($barcode);", null)
                            } else {
                                binding.container.loadUrl("javascript:onScanQRCode($barcode);$url")
                            }
                        }
                    }

                }
            }
            requestChooseFile -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    filePathCallback?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data))
                    filePathCallback = null
                }
            }
        }
    }

    inner class JsInterface(val context: FragmentActivity, val requestCode: Int) {

        @JavascriptInterface
        fun postMessage(message: String) {
            if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    requestPermissions(arrayOf(Manifest.permission.CAMERA), ICK_REQUEST_CAMERA)
                } else {
                    ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.CAMERA), ICK_REQUEST_CAMERA)
                }
            } else {
                V6ScanditActivity.scanOnlyChat(context, requestCode)
            }

        }

        @JavascriptInterface
        fun alert(message: String) {
            context.showSimpleSuccessToast(message)
        }
    }
}

