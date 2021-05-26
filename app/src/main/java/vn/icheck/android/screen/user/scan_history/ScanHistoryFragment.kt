@file:Suppress("DEPRECATION")

package vn.icheck.android.screen.user.scan_history

import android.Manifest
import android.animation.Animator
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.*
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.os.Handler
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_scan_history.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.WrapContentLinearLayoutManager
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.dialog.notify.internal_stamp.InternalStampDialog
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.*
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.ichecklibs.util.showShortErrorToast
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICValidStampSocial
import vn.icheck.android.network.models.history.ICBigCorp
import vn.icheck.android.network.util.DeviceUtils
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.DetailStampHoaPhatActivity
import vn.icheck.android.screen.user.detail_stamp_thinh_long.home.DetailStampThinhLongActivity
import vn.icheck.android.screen.user.detail_stamp_v5.home.DetailStampV5Activity
import vn.icheck.android.screen.user.detail_stamp_v6.home.DetailStampV6Activity
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.StampDetailActivity
import vn.icheck.android.screen.user.history_search.HistorySearchActivity
import vn.icheck.android.screen.user.home.HomeActivity
import vn.icheck.android.screen.user.scan_history.adapter.ScanHistoryAdapter
import vn.icheck.android.screen.user.scan_history.view.IScanHistoryView
import vn.icheck.android.screen.user.scan_history.view_model.ScanHistoryViewModel
import vn.icheck.android.screen.user.shipping.ship.ShipActivity
import vn.icheck.android.screen.user.wall.IckUserWallActivity
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.util.ick.*
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.ContactUtils
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import java.net.URL
import java.util.*

class ScanHistoryFragment : BaseFragmentMVVM(), View.OnClickListener, IScanHistoryView {

    companion object {
        var adapter: ScanHistoryAdapter? = null
        var sort: Int? = null
        var listIdBigCorp = mutableListOf<Any>()
        var listType = mutableListOf<Any>()
    }

    val viewModel: ScanHistoryViewModel by activityViewModels()

    private var isShow = false

    private var codeQr = ""

    private val requestPhone = 2
    private var phoneNumber: String = ""

    private var isCreateView = false

    //    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private val permissionLocation = 90
    private val requestLoginCart = 98

    private val checkAllowPermission: Boolean
        get() {
            return PermissionHelper.isAllowPermission(Manifest.permission.ACCESS_COARSE_LOCATION, context)
                    && PermissionHelper.isAllowPermission(Manifest.permission.ACCESS_FINE_LOCATION, context)
        }

    private fun checkPermission() {
        @Suppress("DEPRECATION")
        requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), permissionLocation)
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
    }

    override val getLayoutID: Int
        get() = R.layout.fragment_scan_history

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    private fun initView() {
        viewModel.getData()

        swipe_container.setOnRefreshListener {
            getData()
        }
    }

    fun getData(isLogout: Boolean? = false) {
        swipe_container.isRefreshing = false
        viewModel.getListScanHistory(sort, listIdBigCorp, listType, false)

        if (isLogout == false) {
            viewModel.getCartCount().observe(this, {
                if (it.data != null) {
                    val i = it.data
                    when {
                        i ?: 0 > 9 -> {
                            tvCountCart.text = "9+"
                            tvCountCart.beVisible()
                        }
                        i ?: 0 > 0 -> {
                            tvCountCart.text = "$i"
                            tvCountCart.beVisible()
                        }
                        else -> {
                            tvCountCart.beGone()
                        }
                    }
                }
            })
        }
    }

    private fun initRecyclerView() {
        adapter = ScanHistoryAdapter(this)
        rcvHistory.layoutManager = WrapContentLinearLayoutManager(context)
        rcvHistory.adapter = adapter
    }

    private fun listenerData() {
        viewModel.onSetData.observe(this, {
            swipe_container.isRefreshing = false
            adapter?.setListData(it)
        })

        viewModel.onAddData.observe(this, {
            swipe_container.isRefreshing = false
            adapter?.addListData(it)
        })

        viewModel.onAddBigCorp.observe(this, {
            swipe_container.isRefreshing = false
            adapter?.addData(it)
        })

        viewModel.onErrorListData.observe(this, {
            swipe_container.isRefreshing = false
            adapter?.setErrorListData(it)
        })

        viewModel.onError.observe(this, {
            swipe_container.isRefreshing = false
            adapter?.setError(it)
        })

        viewModel.onErrorString.observe(this, {
            showShortError(it)
        })

        viewModel.statusCode.observe(this, {
            when (it) {
                ICMessageEvent.Type.ON_SHOW_LOADING -> {
                    DialogHelper.showLoading(this)
                }
                ICMessageEvent.Type.ON_CLOSE_LOADING -> {
                    DialogHelper.closeLoading(this)
                }
                else -> {
                }
            }
        })

        viewModel.stampHoaPhat.observe(this, {
            ActivityUtils.startActivity<DetailStampHoaPhatActivity, String>(requireActivity(), Constant.DATA, codeQr)
        })

        viewModel.stampThinhLong.observe(this, {
            ActivityUtils.startActivity<DetailStampThinhLongActivity, String>(requireActivity(), Constant.DATA, codeQr)
        })

        viewModel.checkStampSocial.observe(this, {
            if (it.code.isNullOrEmpty()) {
                checkStampQr(codeQr)
            } else {
                ActivityUtils.startActivity<StampDetailActivity, String>(requireActivity(), Constant.DATA, it.code!!)
            }
        })

        viewModel.showDialogSuggestApp.observe(this, {
            showDialogSuggestApp(it, it.code ?: codeQr)
        })

        viewModel.stampFake.observe(this, {
            DialogHelper.showNotification(requireContext(), it, false, object : NotificationDialogListener {
                override fun onDone() {
                    DialogHelper.closeLoading(this@ScanHistoryFragment)
                }
            })
        })

        viewModel.errorQr.observe(this, {
            checkStampQr(it)
        })
    }

    private fun showDialogSuggestApp(obj: ICValidStampSocial, codeStamp: String) {
        object : InternalStampDialog(requireContext(), obj.suggest_apps!!, obj.code) {
            override fun onDismiss() {

            }

            override fun onGoToDetail(code: String?) {
                ActivityUtils.startActivity<StampDetailActivity, String>(requireActivity(), Constant.DATA, codeStamp)
            }

            override fun onGoToSms(target: String?, content: String?) {
                startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$target")).apply {
                    putExtra("sms_body", content)
                })
            }

            override fun onGoToEmail(target: String?, content: String?) {
                startActivity(Intent.createChooser(Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:$target")
                }, "Send Email"))
            }

            override fun onGoToLink(target: String?, content: String?) {
                if (target != null) {
                    startActivity(Intent().apply {
                        action = Intent.ACTION_VIEW
                        data = Uri.parse(target)
                    })
                }
            }

            override fun onGoToPhone(target: String?) {
                if (target != null) {
                    phoneNumber = target
                    if (PermissionHelper.checkPermission(activity!!, Manifest.permission.CALL_PHONE, requestPhone)) {
                        ContactUtils.callFast(activity!!, target)
                    }
                }
            }
        }.show()
    }

    private fun checkStampQr(it: String) {
        when {
            it.startsWith("u-") || it.startsWith("U-") -> {
                if (it.count { "-".contains(it) } == 1) {
                    try {
                        val userID = it.split("-")[1]
                        if (userID.isNotEmpty() && ValidHelper.validNumber(userID)) {
                            IckUserWallActivity.create(userID.toLong(), requireActivity())
                        }
                    } catch (e: Exception) {
                    }
                }
            }
            Constant.isMarketingStamps(it) -> {
                WebViewActivity.start(requireActivity(), it, 1, null, true)
            }
            it.contains("dev-qcheck.icheck.vn") || it.contains("qcheck-dev.vn") || it.contains("qcheck.vn") || it.contains("qrcode.icheck.com.vn") -> {
                ActivityUtils.startActivity<StampDetailActivity, String>(requireActivity(), Constant.DATA, it)
            }
            it.contains("ktra.vn") -> {
                var path = URL(it).path

                if (path.isNotEmpty() && path.first() == '/') {
                    path = path.removeRange(0, 1)
                }

                if (!path.contains("/") && !path.contains("?") && !path.contains(".")) {
                    startActivity(Intent(requireContext(), WebViewActivity::class.java).apply {
                        putExtra(Constant.DATA_1, getString(R.string.stamp_v3_format, path, DeviceUtils.getUniqueDeviceId()))
                        putExtra(Constant.DATA_2, 1)
                    })
                } else {
                    ActivityUtils.startActivity<DetailStampV6Activity, String>(requireActivity(), Constant.DATA, it)
                }
            }
            it.contains("cg.icheck.com.vn") -> {
                ActivityUtils.startActivity<DetailStampV5Activity, String>(requireActivity(), Constant.DATA, it)
            }
            it.startsWith("http") || it.startsWith("https") -> {
                WebViewActivity.start(requireActivity(), it, 1)
            }
            else -> {
                handleQr(getQrType(it), it)
            }
        }
    }

    private fun getQrType(qr: String): Int {
        if (qr.startsWith("http", true) or qr.startsWith("https", true) or qr.startsWith("URL", true)) {
            return Constant.TYPE_URL
        }
        if (qr.startsWith("smsto", true)) {
            return Constant.TYPE_SMS
        }
        if (qr.startsWith("MATMSG:TO", true) or qr.startsWith("mailto:email", true) or qr.startsWith("MAILTO:", true)) {
            return Constant.TYPE_MAIL
        }
        if (qr.startsWith("tel", true)) {
            return Constant.TYPE_PHONE_NUMBER
        }
        if (qr.startsWith("geo", true)) {
            return Constant.TYPE_COORDINATE
        }
        if (qr.startsWith("BEGIN:VCARD", true)) {
            return Constant.TYPE_CONTACT
        }
        if (qr.startsWith("BEGIN:VEVENT", true)) {
            return Constant.TYPE_CALENDAR
        }
        if (qr.startsWith("WIFI", true)) {
            return Constant.TYPE_WIFI
        }
        return Constant.TYPE_UNDEFINED
    }

    @Suppress("DEPRECATION")
    private fun handleQr(type: Int, data: String) {
        when (type) {
            Constant.TYPE_URL -> {
                WebViewActivity.start(requireActivity(), data, 0, "Chi tiết Qr Code")
            }
            Constant.TYPE_SMS -> {
                Handler().postDelayed({
                    try {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse("smsto:" + data.split(":")[1])
                        intent.putExtra("sms_body", data.split(":")[2])
                        startActivity(intent)
                    } catch (e: Exception) {
                    }
                }, 800)
            }
            Constant.TYPE_MAIL -> {
                try {
                    var mailTo = ""
                    var subject = ""
                    var body = ""
                    if (data.startsWith("MATMSG:TO:")) {
                        mailTo = data.split("MATMSG:TO:")[1].split(";")[0]
                        subject = data.split("MATMSG:TO:")[1].split(";")[1].split(":")[1]
                        body = data.split("MATMSG:TO:")[1].split(";")[2].split(":")[1]
                    } else {
                        mailTo = data.split("MAILTO:")[1].split("?")[0]
                        subject = data.split("MAILTO:")[1].split("?")[1].split("subject=")[1].split("&")[0]
                        body = data.split("MAILTO:")[1].split("?")[1].split("subject=")[1].split("&")[1].split("body=")[1]
                    }


                    Handler().postDelayed({
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/html"
                        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(mailTo))
                        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
                        intent.putExtra(Intent.EXTRA_TEXT, body)
                        startActivity(intent)
                    }, 800)
                } catch (e: Exception) {
                    logError(e)
                }
            }
            Constant.TYPE_PHONE_NUMBER -> {
                Handler().postDelayed({
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse(data.replace(" ", "").toLowerCase(Locale.getDefault()))
                    startActivity(intent)
                }, 800)
            }
            Constant.TYPE_UNDEFINED -> {
                Handler().postDelayed({
                    val intent = Intent(requireContext(), WebViewActivity::class.java)
                    intent.putExtra(Constant.DATA_1, data)
                    startActivity(intent)
                }, 800)
            }
            Constant.TYPE_CONTACT -> {
                Handler().postDelayed({
                    try {
                        var name = ""
                        var email = ""
                        var phone = ""
                        var note = ""
                        var addresses = ""

                        val arr = data.split("\n")

                        for (i in arr.indices) {
                            if (arr[i].contains("N:", true)) {
                                name = arr[i].replace("N:", "", true).replace(";", " ", true)
                            }
                            if (arr[i].contains("TEL:", true)) {
                                phone = arr[i].replace("TEL:", "", true)
                            }
                            if (arr[i].contains("EMAIL:", true)) {
                                email = arr[i].replace("EMAIL:", "", true)
                            }
                            if (arr[i].contains("ADR:", true)) {
                                addresses = arr[i].replace("ADR:", "", true).replace(";", "", true)
                            }
                            if (arr[i].contains("NOTE:", true)) {
                                note = arr[i].replace("NOTE:", "", true)
                            }
                        }

                        val intent = Intent(Intent.ACTION_INSERT)
                        intent.type = ContactsContract.Contacts.CONTENT_TYPE
                        intent.putExtra(ContactsContract.Intents.Insert.NAME, name)
                        intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone)
                        intent.putExtra(ContactsContract.Intents.Insert.EMAIL, email)
                        intent.putExtra(ContactsContract.Intents.Insert.NOTES, note)
                        intent.putExtra(ContactsContract.Intents.Insert.COMPANY, addresses)
                        startActivity(intent)
                    } catch (e: Exception) {
                        Log.d("TAG", "handleQr: ${e.message}")
                    }
                }, 800)
            }
            Constant.TYPE_COORDINATE -> {
                Handler().postDelayed({
                    val a = data.replace("GEO:", "")
                    val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(a))
                    mapIntent.setPackage("com.google.android.apps.maps")

                    try {
                        startActivity(mapIntent)
                    } catch (e: Exception) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=$a"))
                            startActivity(intent)
                        } catch (exception: ActivityNotFoundException) {
                            requireContext().showShortErrorToast("Không tìm thấy ứng dụng google map!")
                        }
                    }
                }, 800)
            }
            Constant.TYPE_CALENDAR -> {
                Handler().postDelayed({
                    try {
                        val intent = Intent(Intent.ACTION_EDIT)
//                        intent.type = "vnd.android.cursor.item/event"
                        intent.data = CalendarContract.Events.CONTENT_URI

                        val arr = data.split("\n")
                        val summary = arr.single {
                            it.contains("SUMMARY", true)
                        }.replace("SUMMARY:", "", true)

                        val location = arr.single {
                            it.contains("LOCATION", true)
                        }.replace("LOCATION:", "", true)

                        val start = TimeHelper.convertDateVnToMillisecond2(arr.single {
                            it.contains("DTSTART", true)
                        }.replace("DTSTART:", "", true))

                        val end = TimeHelper.convertDateVnToMillisecond2(arr.single {
                            it.contains("DTEND", true)
                        }.replace("DTEND:", "", true))

                        intent.putExtra(CalendarContract.Events.TITLE, summary)
                        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start)
                        intent.putExtra(CalendarContract.Events.DTSTART, start)
                        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end)
                        intent.putExtra(CalendarContract.Events.DTEND, end)
                        intent.putExtra(CalendarContract.Events.ALL_DAY, false)// periodicity
                        val description = arr.single {
                            it.contains("URL", true)
                        }.replace("URL:", "", false)
                        intent.putExtra(CalendarContract.Events.DESCRIPTION, description)
                        startActivity(intent)
                    } catch (e: Exception) {
                    }
                }, 800)
            }
            Constant.TYPE_WIFI -> {
                Handler().postDelayed({
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val connectivityManager = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
                        val arr = data.split(";")
                        val ssid = arr.single {
                            it.contains("WIFI", true)
                        }.replace("wifi:s:", "", true)
                        val key = arr.single {
                            it.contains("P:", true)
                        }.replace("p:", "", true)
                        // do post connect processing here
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CHANGE_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) {
                            val nwSpecifier = WifiNetworkSpecifier.Builder()
                                    .setSsid(ssid)
                                    .setWpa2Passphrase(key)
                                    .build()
                            val nw = NetworkRequest.Builder()
                                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                                    .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                                    .setNetworkSpecifier(nwSpecifier)
                                    .build()
                            connectivityManager?.requestNetwork(nw, object : ConnectivityManager.NetworkCallback() {
                                override fun onAvailable(network: Network) {

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
                        val wifiManager = ICheckApplication.getInstance().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                        val netId = wifiManager.addNetwork(wifiConfig)
                        wifiManager.disconnect()
                        wifiManager.enableNetwork(netId, true)
                        wifiManager.reconnect()
                    }

                }, 800)
            }
        }
    }

    override fun onClickQrType(item: String?) {
        codeQr = item ?: ""
        viewModel.checkQrStampSocial(codeQr)
    }

    override fun onValidStamp(item: String?) {
        codeQr = item ?: ""
        viewModel.checkQrStampSocial(codeQr)
    }

    override fun onCloseDrawer() {
    }

    override fun unCheckAllFilterHistory() {

    }

    override fun onMessageErrorMenu() {
        viewModel.getData()
    }

    override fun onLoadmore() {
        viewModel.getListScanHistory(sort, listIdBigCorp, listType, true)
    }

    override fun onClickBigCorp(item: ICBigCorp) {
        listIdBigCorp.clear()
        if (item.id != null) {
            listIdBigCorp.add(item.id!!)
        }
        if (item.name != "Tất cả") {
            adapter?.hideSuggestShop()
        } else {
            adapter?.showSuggest()
        }
        viewModel.getListScanHistory(sort, listIdBigCorp, listType, false)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imgMenu -> {
                activity?.let {
                    if (it is HomeActivity) {
                        it.openSlideMenu()
                    }
                }
            }
            R.id.imgSearch -> {
                startActivity<HistorySearchActivity>()
            }
            R.id.imgCart -> {
                if (SessionManager.isUserLogged) {
                    startActivity<ShipActivity, Boolean>(Constant.CART, true)
                } else {
                    onRequireLogin(requestLoginCart)
                }
            }
            R.id.btnGps -> {
                if (!checkAllowPermission) {
                    checkPermission()
                } else if (NetworkHelper.checkGPS(requireActivity())) {
                    getData()
                }
            }
            R.id.tvFilter -> {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.INIT_MENU_HISTORY))
            }
            R.id.layoutParentSort -> {
                showOrHideSort()
            }
            R.id.tvSort -> {
                showOrHideSort()
            }
            R.id.btnNearest -> {
                checkTextOnTick(btnNearest, btnfurthest, isNearest = true, isFurthest = false)
                showCategory(false)
                tvSort.text = ("Sắp xếp: Mới nhất")
                sort = 2
                viewModel.getListScanHistory(sort, listIdBigCorp, listType, false)
            }
            R.id.btnfurthest -> {
                checkTextOnTick(btnNearest, btnfurthest, isNearest = false, isFurthest = true)
                showCategory(false)
                tvSort.text = ("Sắp xếp: Cũ nhất")
                sort = 1
                viewModel.getListScanHistory(sort, listIdBigCorp, listType, false)
            }
        }
    }

    private var heightLayoutSort = 0
    private fun showOrHideSort() {
        if (heightLayoutSort == 0) {
            heightLayoutSort = layoutSort.height
        }
        if (layoutParentSort.visibility == View.INVISIBLE) {
            layoutParentSort.visibility = View.VISIBLE
            showCategory(true)
        } else {
            showCategory(false)
        }

        isShow = !isShow
    }

    private fun showCategory(isFirst: Boolean) {
        layoutSort.clearAnimation()
        if (isFirst) {
            WidgetUtils.changeViewHeight(layoutSort, 0, heightLayoutSort, 500)
        } else {
            WidgetUtils.changeViewHeight(layoutSort, layoutSort.height, 0, 500, object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    layoutParentSort.visibility = View.INVISIBLE
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }
            })
        }
    }

//     fun checkSelectBigCorp() {
//        if (listIdBigCorp.isNullOrEmpty()) {
//            if (!adapter?.listData.isNullOrEmpty()) {
//                for (i in 0 until adapter?.listData!!.size) {
//                    if (adapter!!.listData[i].type == ICViewTypes.LIST_BIG_CORP) {
//                        val holder = rcvHistory.findViewHolderForAdapterPosition(i)
//                        if (holder != null && holder is ListBigCorpHolder) {
//                            holder.selectedPos = 0
//                            adapter?.notifyItemChanged(i)
//                        }
//                    }
//                }
//            }
//        }
//    }

    private fun checkTextOnTick(btnNearest: AppCompatTextView, btnfurthest: AppCompatTextView, isNearest: Boolean, isFurthest: Boolean) {
        if (isNearest && !isFurthest) {
            btnNearest.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkbox_single_on_light_blue_24_px, 0)
            btnfurthest.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        } else {
            btnNearest.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            btnfurthest.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkbox_single_on_light_blue_24_px, 0)
        }
    }

    override fun onRequireLoginSuccess(requestCode: Int) {
        super.onRequireLoginSuccess(requestCode)

        when (requestCode) {
            requestLoginCart -> {
                startActivity<ShipActivity, Boolean>(Constant.CART, true)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ICMessageEvent) {
        when (event.type) {
            ICMessageEvent.Type.MESSAGE_ERROR -> {
                getData()
            }
            ICMessageEvent.Type.ON_TICK_HISTORY -> {
                imgFilterHis.setImageResource(R.drawable.ic_filter_24px)
                imgDot.beVisible()
                tvFilter.setTextColor(Color.parseColor("#057DDA"))
            }
            ICMessageEvent.Type.ON_UNTICK_HISTORY -> {
                imgFilterHis.setImageResource(R.drawable.ic_filter_gray_24_px)
                imgDot.beInvisible()
                tvFilter.setTextColor(Color.parseColor("#757575"))
            }
            ICMessageEvent.Type.ON_LOG_IN -> {
                listType.clear()
                listIdBigCorp.clear()
                sort = null

                getData()
            }
            ICMessageEvent.Type.ON_LOG_OUT -> {
                listType.clear()
                listIdBigCorp.clear()
                sort = null
                tvCountCart.beGone()
                getData(true)
            }
            else -> {
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (!isCreateView) {
            layoutContainer.setPadding(0, getStatusBarHeight, 0, 0)
            swipe_container.setColorSchemeColors(ContextCompat.getColor(ICheckApplication.getInstance(), R.color.colorPrimary), ContextCompat.getColor(ICheckApplication.getInstance(), R.color.colorPrimary), ContextCompat.getColor(ICheckApplication.getInstance(), R.color.colorPrimary))

            if (!checkAllowPermission || !NetworkHelper.isOpenedGPS(requireContext())) {
                containerGps.visibility = View.VISIBLE
                layoutData.visibility = View.GONE
                swipe_container.visibility = View.GONE
            } else {
                containerGps.visibility = View.GONE
                layoutData.visibility = View.VISIBLE
                swipe_container.visibility = View.VISIBLE
                checkTextOnTick(btnNearest, btnfurthest, isNearest = true, isFurthest = false)
                initRecyclerView()
                listenerData()
                initView()
                getData()
                isCreateView = true
            }
            WidgetUtils.setClickListener(this, imgMenu, imgCart, imgSearch, btnGps, tvFilter, tvSort, btnNearest, btnfurthest, layoutParentSort)
        } else {
            if (ICMessageEvent.BarcodeHistoryChanged && PermissionHelper.isAllowPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) && PermissionHelper.isAllowPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) && NetworkHelper.isConnected(requireContext())) {
                getData()
            }
        }
        ICMessageEvent.BarcodeHistoryChanged = false

        if (SessionManager.isUserLogged) {
            viewModel.getCartCount().observe(this, {
                if (it.data != null) {
                    val i = it.data
                    when {
                        i ?: 0 > 9 -> {
                            tvCountCart.text = "9+"
                            tvCountCart.beVisible()
                        }
                        i ?: 0 > 0 -> {
                            tvCountCart.text = "$i"
                            tvCountCart.beVisible()
                        }
                        else -> {
                            tvCountCart.beGone()
                        }
                    }
                }
            })
        } else {
            tvCountCart.beGone()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        @Suppress("DEPRECATION")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == permissionLocation) {
            if (PermissionHelper.checkResult(grantResults)) {
                EventBus.getDefault().post(ICMessageEvent.Type.ON_CHECK_UPDATE_LOCATION)

                if (NetworkHelper.checkGPS(requireActivity())) {
                    onResume()
                }
            } else {
                ToastUtils.showShortError(context, R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
            }
        }

        if (requestCode == requestPhone) {
            if (PermissionHelper.checkResult(grantResults)) {
                ContactUtils.callFast(requireActivity(), phoneNumber)
            } else {
                ToastUtils.showShortError(context, R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
            }
        }
    }
}