package vn.icheck.android.activities

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.*
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.net.wifi.WifiNetworkSuggestion
import android.net.wifi.hotspot2.PasspointConfiguration
import android.os.Bundle
import android.os.Handler
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_qr_result.*
import kotlinx.android.synthetic.main.activity_web_view.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.activities.base.BaseICActivity
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.fragments.scan.QrScanViewModel
import vn.icheck.android.room.entity.ICQrScan
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.ToastUtils
import java.util.*
import kotlin.collections.HashMap

class QrResultActivity : BaseICActivity() {

    companion object {

        const val TYPE_URL = 1
        const val TYPE_DOCUMENT = 2
        const val TYPE_PHONE_NUMBER = 3
        const val TYPE_SMS = 4
        const val TYPE_MAIL = 5
        const val TYPE_COORDINATE = 6
        const val TYPE_CONTACT = 7
        const val TYPE_CALENDAR = 8
        const val TYPE_WIFI = 9
        const val TYPE_UNDEFINED = 0

        fun startAct(context: Context, code: String) {
            val start = Intent(context, QrResultActivity::class.java)
            val data = hashMapOf<String, String>()
            data.put("qr", code)
            start.putExtra("data", data)
            start.putExtra("param_url", code)
            context.startActivity(start)
        }
    }

    private var QR_TYPE = TYPE_UNDEFINED
    lateinit var qrScanViewModel: QrScanViewModel

    // Register the permissions callback, which handles the user's response to the
// system permissions dialog. Save the return value, an instance of
// ActivityResultLauncher. You can use either a val, as shown in this snippet,
// or a lateinit var in your onAttach() or onCreate() method.
    val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
        setContentView(R.layout.activity_qr_result)
        qrScanViewModel = ViewModelProvider(this).get(QrScanViewModel::class.java)
        requiresLogin = false
        img_back.setOnClickListener {
            onBackPressed()
        }
        try {
            val map = intent.getSerializableExtra("data") as HashMap<String, String>
            val qr = map.get("qr")
            if (qr != null) {
                QR_TYPE = getQrType(qr)
                handleQr(QR_TYPE, qr, m_webview)
            }
        } catch (e: Exception) {
            finish()
        }
    }

    private fun getQrType(qr: String): Int {
//        Toast.makeText(this, qr, Toast.LENGTH_SHORT).show()
        if (qr.startsWith("http", true) or qr.startsWith("https", true) or qr.startsWith("URL", true)) {
            return TYPE_URL
        }
        if (qr.startsWith("smsto", true)) {
            return TYPE_SMS
        }
        if (qr.startsWith("MATMSG:TO", true) or qr.startsWith("mailto:email", true)) {
            return TYPE_MAIL
        }
        if (qr.startsWith("tel", true)) {
            return TYPE_PHONE_NUMBER
        }
        if (qr.startsWith("geo", true)) {
            return TYPE_COORDINATE
        }
        if (qr.startsWith("BEGIN:VCARD", true)) {
            return TYPE_CONTACT
        }
        if (qr.startsWith("BEGIN:VEVENT", true)) {
            return TYPE_CALENDAR
        }
        if (qr.startsWith("WIFI", true)) {
            return TYPE_WIFI
        }
        return TYPE_UNDEFINED
    }

    private fun handleQr(type: Int, data: String, webView: WebView) {
        when (type) {
            TYPE_URL -> {
                Handler().postDelayed({
                    webView.webViewClient = MyWebClient(tv_title)
                    webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
                    webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
                    webView.settings.javaScriptEnabled = true
                    webView.settings.useWideViewPort = true
                    webView.settings.loadWithOverviewMode = true
                    webView.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
                    webView.loadUrl(data)
                }, 800)
            }
            TYPE_SMS -> {
                Handler().postDelayed({
                    try {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setData(Uri.parse("smsto:" + data.split(":").get(1)))
                        intent.putExtra("sms_body", data.split(":").get(2))
                        startActivity(intent)
                        finish()
                    } catch (e: Exception) {
                    }
                }, 800)
            }
            TYPE_MAIL -> {
                Handler().postDelayed({
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/html"
                    intent.putExtra(Intent.EXTRA_EMAIL, data.split("MATMSG:TO").get(1).split(";").get(0))
                    intent.putExtra(Intent.EXTRA_SUBJECT, data.split("MATMSG:TO").get(1).split(";").get(1))
                    intent.putExtra(Intent.EXTRA_TEXT, data.split("MATMSG:TO").get(1).split(";").get(2))
                    startActivity(intent)
                    finish()
                }, 800)
            }
            TYPE_PHONE_NUMBER -> {
                Handler().postDelayed({
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse(data.replace(" ", "").toLowerCase())
                    startActivity(intent)
                    finish()
                }, 800)
            }
            TYPE_UNDEFINED -> {
                Handler().postDelayed({
                    val intent = Intent(this@QrResultActivity, WebViewActivity::class.java)
                    intent.putExtra(Constant.DATA_1, data)
                    ActivityUtils.startActivityAndFinish(this@QrResultActivity, intent)
                }, 800)
            }
            TYPE_CONTACT -> {
                Handler().postDelayed({
                    try {
                        val arr = data.split(";")
                        val phone = arr.single {
                            it.contains("TEL", true)
                        }.replace("TEL:", "", true)
                        val name = arr.single {
                            it.contains("VCARD:N", true)
                        }.replace("VCARD:N:", "", true)

                        val intent = Intent(Intent.ACTION_INSERT)
                        intent.type = ContactsContract.Contacts.CONTENT_TYPE
                        intent.putExtra(ContactsContract.Intents.Insert.NAME, name)
                        intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone)
                        startActivity(intent)
                    } catch (e: Exception) {
                    }
                }, 800)
            }
            TYPE_COORDINATE -> {
                Handler().postDelayed({
                    val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(data))
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                    finish()
                }, 800)
            }
            TYPE_CALENDAR -> {
                Handler().postDelayed({
                    try {
                        val intent = Intent(Intent.ACTION_EDIT)
                        intent.type = "vnd.android.cursor.item/event"
                        val arr = data.split(";")
                        val SUMMARY = arr.single {
                            it.contains("SUMMARY", true)
                        }.replace("SUMMARY:", "", true)
                        intent.putExtra(CalendarContract.Events.TITLE, SUMMARY)
                        intent.putExtra(CalendarContract.Events.ALL_DAY, false)// periodicity
                        val DESCRIPTION = arr.single {
                            it.contains("URL", true)
                        }.replace("URL:", "", false)
                        intent.putExtra(CalendarContract.Events.DESCRIPTION, DESCRIPTION)
                        startActivity(intent)
                        finish()
                    } catch (e: Exception) {
                    }
                }, 800)
            }
            TYPE_WIFI -> {
                Handler().postDelayed({
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
                        val arr = data.split(";")
                        val ssid = arr.single {
                            it.contains("WIFI", true)
                        }.replace("wifi:s:", "", true)
                        val key = arr.single {
                            it.contains("P:", true)
                        }.replace("p:", "", true)
                        // do post connect processing here
                        if (ContextCompat.checkSelfPermission(this@QrResultActivity, Manifest.permission.CHANGE_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) {
                            val nwSpecifier = WifiNetworkSpecifier.Builder()
                                    .setSsid(ssid)
                                    .setWpa2Passphrase(key)
                                    .build()
                            val nw = NetworkRequest.Builder()
                                    .addTransportType(TRANSPORT_WIFI)
                                    .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                                    .setNetworkSpecifier(nwSpecifier)
                                    .build()
                            connectivityManager.requestNetwork(nw, object : ConnectivityManager.NetworkCallback() {
                                override fun onAvailable(network: Network) {
                                    finish()
                                }
                            })
                        } else {
                            requestPermissionLauncher.launch(Manifest.permission.CHANGE_NETWORK_STATE)
                        }


                    } else {
                        val wifiConfig = WifiConfiguration()
                        val arr = data.split(";")
                        val ssid = arr.single {
                            it.contains("WIFI", true)
                        }.replace("wifi:s:", "", true)
                        wifiConfig.SSID = String.format("\"%s\"", ssid)
                        val key = arr.single {
                            it.contains("P:", true)
                        }.replace("p:", "", true)
                        wifiConfig.preSharedKey = String.format("\"%s\"", key)
                        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
                        val netId = wifiManager.addNetwork(wifiConfig)
                        wifiManager.disconnect()
                        wifiManager.enableNetwork(netId, true)
                        wifiManager.reconnect()
                        finish()
                    }

                }, 800)
            }
        }
        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.REFRESH_DATA_HISTORY_QR))
    }

    inner class MyWebClient(val tv: TextView) : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String?) {
            super.onPageFinished(view, url)
            tv.text = view.title

        }
    }
}
