package vn.icheck.android.screen.user.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.Gravity
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.children
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.ick_left_menu.*
import kotlinx.android.synthetic.main.right_menu_history.*
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeoutOrNull
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.RelationshipManager
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.adapter.ViewPagerAdapter
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.dialog.notify.confirm.ConfirmDialog
import vn.icheck.android.base.model.ICFragment
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.ISettingListener
import vn.icheck.android.chat.icheckchat.screen.ChatSocialFragment
import vn.icheck.android.chat.icheckchat.screen.conversation.ListConversationFragment
import vn.icheck.android.chat.icheckchat.sdk.ChatSdk
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.constant.ICK_REQUEST_CAMERA
import vn.icheck.android.helper.*
import vn.icheck.android.ichecklibs.util.showLongErrorToast
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.network.models.ICClientSetting
import vn.icheck.android.network.models.history.ICBigCorp
import vn.icheck.android.network.models.history.ICTypeHistory
import vn.icheck.android.network.util.DeviceUtils
import vn.icheck.android.screen.account.icklogin.IckLoginActivity
import vn.icheck.android.screen.account.icklogin.viewmodel.IckLoginViewModel
import vn.icheck.android.screen.checktheme.CheckThemeViewModel
import vn.icheck.android.screen.dialog.PermissionDialog
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.screen.info.AppInfoActivity
import vn.icheck.android.screen.scan.V6ScanditActivity
import vn.icheck.android.screen.user.bookmark.BookMarkV2Activity
import vn.icheck.android.screen.user.bookmark_history.BookmarkHistoryActivity
import vn.icheck.android.screen.user.coinhistory.CoinHistoryActivity
import vn.icheck.android.screen.user.contact.ContactActivity
import vn.icheck.android.screen.user.createqrcode.home.CreateQrCodeHomeActivity
import vn.icheck.android.screen.user.history_loading_card.home.HistoryCardActivity
import vn.icheck.android.screen.user.home_page.HomePageFragment
import vn.icheck.android.screen.user.newslistv2.ListNewsFragment
import vn.icheck.android.screen.user.orderhistory.OrderHistoryActivity
import vn.icheck.android.screen.user.rank_of_user.RankOfUserActivity
import vn.icheck.android.screen.user.scan_history.ScanHistoryFragment
import vn.icheck.android.screen.user.scan_history.adapter.ScanMenuHistoryAdapter
import vn.icheck.android.screen.user.scan_history.model.ICScanHistory
import vn.icheck.android.screen.user.scan_history.view.IScanHistoryView
import vn.icheck.android.screen.user.scan_history.view_model.ScanHistoryViewModel
import vn.icheck.android.screen.user.setting.SettingsActivity
import vn.icheck.android.screen.user.wall.IckUserWallActivity
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.screen.user.welcome.WelcomeActivity
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.tracking.teko.TekoHelper
import vn.icheck.android.util.ick.*
import vn.icheck.android.util.kotlin.HideWebUtils
import vn.icheck.android.util.kotlin.StatusBarUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import java.io.File

@AndroidEntryPoint
class HomeActivity : BaseActivityMVVM(), IHomeView, IScanHistoryView, View.OnClickListener {
    private lateinit var ringtoneHelper: RingtoneHelper
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private val presenter = HomePresenter(this@HomeActivity)

    private val requestProfile = 1
    private val requestUpdateProfile = 2
    private val requestLoginProfile = 4
    private val requestLoginCoinHistory = 5
    private val requestLoginOrderHistory = 6
    private val requestLoginOrderHistoryConfirm = 7
    private val requestLoginOrderHistoryDelivery = 8
    private val requestLoginOrderHistoryReview = 9
    private val requestLoginHistoryCard = 10
    private val requestLoginBookmark = 11
    private val requestLoginChat = 12
    private val requestLoginRank = 13
    private val requestLoginXu = 14
    private val requestManagerPage = 15

    private val requestLocationPermission = 12

    private var isScan = false

    private val historyViewModel: ScanHistoryViewModel by viewModels()
    private val ickLoginViewModel: IckLoginViewModel by viewModels()
    private lateinit var viewModel: HomeViewModel

    private var adapterMenu = ScanMenuHistoryAdapter(this)

    private val registerClickable = object : ClickableSpan() {
        override fun onClick(p0: View) {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivityForResult<IckLoginActivity, Int>("requestCode", 1, 1)
//            this@HomeActivity.simpleStartForResultActivity(IckLoginActivity::class.java, 1)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = true
        }
    }

    private val loginClickable = object : ClickableSpan() {
        override fun onClick(p0: View) {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity<IckLoginActivity>()
//            this@HomeActivity simpleStartActivity IckLoginActivity::class.java
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = true
        }
    }

    companion object {
        var isOpen: Boolean? = false
        var INSTANCE: HomeActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        onInitView()
    }

    override fun isHomeActivity(): Boolean {
        return true
    }

    @SuppressLint("RtlHardcoded")
    fun onInitView() {
        StatusBarUtils.setOverStatusBarDark(this)
        logDebug("home create")
        isOpen = true
        WelcomeActivity.isWelcome = false

        setupViewPager()
        setupTabListener()
        checkPermission()
        setupViewModel()
        checkData()
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT)
        setListenerDrawerLayout()
        presenter.checkVersionApp()
        presenter.registerNotificationCount()
        presenter.registerMessageCount()
        CartHelper().getCartSocial()

        ringtoneHelper = RingtoneHelper(this)
        AndroidSchedulers.mainThread()
        INSTANCE = this
    }


    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.downloadTheme()

        historyViewModel.onAddDataMenu.observe(this, Observer {
            adapterMenu.addItem(it)
        })

        historyViewModel.onUpdateDataMenu.observe(this, Observer {
            adapterMenu.updateItem(it)
        })
    }

    private fun setupViewPager() {
        val listPage = mutableListOf<ICFragment>()

        listPage.add(ICFragment(null, HomePageFragment()))
        listPage.add(ICFragment(null, ListNewsFragment.newInstance(false)))
        listPage.add(ICFragment(null, ScanHistoryFragment()))
//        listPage.add(ICFragment(null, SocialChatFragment()))
        listPage.add(ICFragment(null, ChatSocialFragment().apply {
            setDataFromHome(object : ListConversationFragment.Companion.ICountMessageListener {
                override fun getCountMessage(count: Long) {

                    val tvChatCount = findViewById<AppCompatTextView>(R.id.tvChatCount)
                    tvChatCount.post {
                        tvChatCount.visibility = if (count != 0L) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }

                        tvChatCount.text = if (count > 9) {
                            "+9"
                        } else {
                            "$count"
                        }
                    }
                }

                override fun onClickLeftMenu() {
                    openSlideMenu()
                }
            }, SessionManager.isUserLogged)
        }))

        viewPager.offscreenPageLimit = 5
        viewPager.setPagingEnabled(false)
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, listPage)
        selectTab(intent?.getIntExtra(Constant.DATA_1, 1) ?: 1)
    }

    private fun selectTab(position: Int) {
        when (position) {
            1 -> {
                if (!isChecked(tvHome)) {
//                    TrackingAllHelper.trackHomePageViewed()
                    viewPager.setCurrentItem(0, false)
                    HideWebUtils.showWeb("Home")
                    HomePageFragment.INSTANCE?.scrollToTop()
                }
            }
            2 -> {
                if (!isChecked(tvFeed)) {
                    TekoHelper.tagMallViewed()
                    viewPager.setCurrentItem(1, false)
                }
            }
            3 -> {
                if (!isChecked(tvHistory)) {
                    viewPager.setCurrentItem(2, false)
                }
            }
            4 -> {
                if (SessionManager.isUserLogged || SessionManager.isDeviceLogged) {
                    if (!isChecked(tvChat)) {
                        TrackingAllHelper.trackMessageViewed()
                        viewPager.setCurrentItem(3, false)
                        ListConversationFragment.isOpenConversation = true
                    }
                } else {
                    onRequireLogin(requestLoginChat)
                }
            }
            5 -> {
//                unCheckAll()
                isScan = true

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(arrayOf(Manifest.permission.CAMERA), ICK_REQUEST_CAMERA)
                    } else {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), ICK_REQUEST_CAMERA)
                    }
                } else {
//                    ICKScanActivity.create(this)
                    V6ScanditActivity.create(this)
                }
            }
        }
    }

    private fun setupTabListener() {
        WidgetUtils.setClickListener(this, tvHome, tvFeed, imgScanQr, tvHistory, tvChat, imgAvatar, tv_logout, tv_help, help_root, btn_rank_user, tv_hdsd, layoutSetting,
                tv_dksd, tv_chtg, tv_lhic, group_history_topup, btnCreateQr, tv_ttud, btn_icheck_xu, layoutOrder, btn_manage_page, group_favorite_products, btnApply, tv_username)
    }

    private fun checkPermission() {
        PermissionDialog.checkPermission(this, PermissionDialog.LOCATION, object : PermissionDialog.PermissionListener {
            override fun onPermissionAllowed() {
                startLocationUpdates()
            }

            override fun onRequestPermission() {
                PermissionHelper.checkPermission(this@HomeActivity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), requestLocationPermission)
            }

            override fun onPermissionNotAllow() {

            }
        })
    }

    private fun checkData() {
        intent?.getBooleanExtra(Constant.DATA_2, false)?.let { isLogin ->
            if (isLogin) {
                startActivity<IckLoginActivity>()
                return
            }
        }

        intent?.getStringExtra(Constant.DATA_3)?.let { appInitScheme ->
            if (appInitScheme.isNotEmpty()) {
                FirebaseDynamicLinksActivity.startDestinationUrl(this@HomeActivity, appInitScheme)
            }
        }

        intent?.getStringExtra(Constant.DATA_4)?.let { notificationPath ->
            if (notificationPath.isNotEmpty()) {
                FirebaseDynamicLinksActivity.startDestinationUrl(this@HomeActivity, notificationPath)
            }
        }
    }

    private fun setListenerDrawerLayout() {
        val drawerToggle = object : ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                onCloseDrawer()
            }

        }
        drawerLayout.addDrawerListener(drawerToggle)
    }

    fun openSlideMenu() {
        TrackingAllHelper.trackLeftMenuViewed()
        drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun isChecked(view: AppCompatCheckedTextView): Boolean {
        return if (view.isChecked) {
            true
        } else {
            unCheckAll()
            view.isChecked = true
            false
        }
    }

    private fun unCheckAll() {
        tvHome.isChecked = false
        tvFeed.isChecked = false
        tvHistory.isChecked = false
        tvChat.isChecked = false
        isScan = false
    }

    private fun startLocationUpdates() {
        if (PermissionHelper.isAllowPermission(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))) {
            if (fusedLocationClient == null) {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            }

            if (locationCallback == null) {
                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        super.onLocationResult(locationResult)

                        val lastLocation = locationResult?.lastLocation

                        if (lastLocation != null) {
                            APIConstants.LATITUDE = lastLocation.latitude
                            APIConstants.LONGITUDE = lastLocation.longitude
                        } else {
                            APIConstants.LATITUDE = 0.0
                            APIConstants.LONGITUDE = 0.0
                        }
                    }

                    override fun onLocationAvailability(locationAvailability: LocationAvailability?) {
                        super.onLocationAvailability(locationAvailability)

                        if (locationAvailability?.isLocationAvailable != true) {
                            APIConstants.LATITUDE = 0.0
                            APIConstants.LONGITUDE = 0.0
                        }
                    }
                }
            }

            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = 4000
            locationRequest.fastestInterval = 2000

            fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient?.removeLocationUpdates(locationCallback)
        APIConstants.LATITUDE = 0.0
        APIConstants.LONGITUDE = 0.0
    }

    override fun onUpdateUserInfo() {
        val user = SessionManager.session.user
//
        if (SessionManager.isUserLogged) {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_COIN_AND_RANK))
            WidgetUtils.loadImageUrl(imgAvatar, user?.avatar, R.drawable.ic_avatar_default_84px, R.drawable.ic_avatar_default_84px)

            tv_username.apply {
                text = user?.getName
                if (user?.kycStatus == 2) {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_user_24dp, 0)
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }

            }
            tv_logout.visibility = View.VISIBLE
            tv_user_rank.text = user?.getPhoneAndRank()
            tv_user_rank.setTextColor(Color.parseColor("#757575"))
            img_rank_user.beVisible()
            background.loadImageWithHolder(SessionManager.session.user?.background, R.drawable.left_menu_bg)
            when (user?.rank?.level) {
                Constant.USER_LEVEL_GOLD -> {
                    img_rank_user.setImageResource(R.drawable.ic_leftmenu_avatar_gold_36dp)
                }
                Constant.USER_LEVEL_DIAMOND -> {
                    img_rank_user.setImageResource(R.drawable.ic_leftmenu_avatar_diamond_36dp)
                }
                Constant.USER_LEVEL_STANDARD -> {
                    img_rank_user.setImageResource(R.drawable.ic_leftmenu_avatar_standard_36dp)
                }
                Constant.USER_LEVEL_SILVER -> {
                    img_rank_user.setImageResource(R.drawable.ic_leftmenu_avatar_silver_36dp)
                }
            }

        } else {
            background.setImageResource(R.drawable.left_menu_bg)
            img_rank_user.beGone()
            imgAvatar.setImageResource(R.drawable.ic_avatar_default_84px)

            tv_username.apply {
                text = Build.MODEL
                setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
            tv_user_rank.visibility = View.VISIBLE
            tv_user_rank.setTextColor(Color.parseColor("#212121"))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val spannableString = SpannableString(Html.fromHtml("Vui lòng <font color=#057DDA><u>Đăng kí</u></font> hoặc <font color=#057DDA><u>Đăng nhập</u></font>", Html.FROM_HTML_MODE_COMPACT))
                spannableString.setSpan(registerClickable, 8, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                spannableString.setSpan(loginClickable, 23, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                tv_user_rank.text = spannableString
                tv_user_rank.movementMethod = LinkMovementMethod.getInstance()
            } else {
                val spannableString = SpannableString(Html.fromHtml("Vui lòng <font color=#057DDA><u>Đăng kí</u></font> hoặc <font color=#057DDA><u>Đăng nhập</u></font>"))

                spannableString.setSpan(registerClickable, 8, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                spannableString.setSpan(loginClickable, 23, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                tv_user_rank.text = spannableString
                tv_user_rank.movementMethod = LinkMovementMethod.getInstance()
            }

            tv_logout.visibility = View.INVISIBLE
        }
    }

    private fun setupTheme() {
        val theme = SettingManager.themeSetting?.theme

        if (!theme?.bottomBarSelectedTextColor.isNullOrEmpty()) {
            ViewHelper.createColorStateList(ContextCompat.getColor(this@HomeActivity, R.color.colorDisableText), Color.parseColor(theme!!.bottomBarSelectedTextColor))
        } else {
            ContextCompat.getColorStateList(this@HomeActivity, R.color.text_color_home_tab)
        }.apply {
            tvHome.setTextColor(this)
            tvFeed.setTextColor(this)
            tvHistory.setTextColor(this)
            tvChat.setTextColor(this)
        }

        val path = FileHelper.getPath(this@HomeActivity)
        val homeBitmap = BitmapFactory.decodeFile(path + FileHelper.homeIcon)
        if (homeBitmap != null) {
            tvHome.setCompoundDrawablesWithIntrinsicBounds(null, ViewHelper.createDrawableStateList(
                    ContextCompat.getDrawable(this@HomeActivity, R.drawable.ic_bottombar_home_unchecked_27dp)!!,
                    BitmapDrawable(resources, Bitmap.createScaledBitmap(homeBitmap, SizeHelper.size27, SizeHelper.size27, false))),
                    null, null)
        } else {
            tvHome.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this@HomeActivity, R.drawable.ic_selected_home_page_27), null, null)
        }

        val feedBitmap = BitmapFactory.decodeFile(path + FileHelper.newsIcon)
        if (feedBitmap != null) {
            tvFeed.setCompoundDrawablesWithIntrinsicBounds(null, ViewHelper.createDrawableStateList(
                    ContextCompat.getDrawable(this@HomeActivity, R.drawable.ic_bottombar_feed_unchecked_27dp)!!,
                    BitmapDrawable(resources, Bitmap.createScaledBitmap(feedBitmap, SizeHelper.size27, SizeHelper.size27, false))),
                    null, null)
        } else {
            tvFeed.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this@HomeActivity, R.drawable.ic_selected_feed_27), null, null)
        }

        val historyBitmap = BitmapFactory.decodeFile(path + FileHelper.historyIcon)
        if (historyBitmap != null) {
            tvHistory.setCompoundDrawablesWithIntrinsicBounds(null, ViewHelper.createDrawableStateList(
                    ContextCompat.getDrawable(this@HomeActivity, R.drawable.ic_bottombar_history_unchecked_27dp)!!,
                    BitmapDrawable(resources, Bitmap.createScaledBitmap(historyBitmap, SizeHelper.size27, SizeHelper.size27, false))),
                    null, null)
        } else {
            tvHistory.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this@HomeActivity, R.drawable.ic_selected_history_27), null, null)
        }

        val chatBitmap = BitmapFactory.decodeFile(path + FileHelper.messageIcon)
        if (chatBitmap != null) {
            tvChat.setCompoundDrawablesWithIntrinsicBounds(null, ViewHelper.createDrawableStateList(
                    ContextCompat.getDrawable(this@HomeActivity, R.drawable.ic_bottombar_chat_unchecked_27dp)!!,
                    BitmapDrawable(resources, Bitmap.createScaledBitmap(chatBitmap, SizeHelper.size27, SizeHelper.size27, false))),
                    null, null)
        } else {
            tvChat.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this@HomeActivity, R.drawable.ic_selected_chat_27), null, null)
        }

        val scanBitmap = BitmapFactory.decodeFile(path + FileHelper.scanIcon)
        if (scanBitmap != null) {
            imgScanQr.setImageBitmap(Bitmap.createScaledBitmap(scanBitmap, SizeHelper.size66, SizeHelper.size66, false))
        } else {
            imgScanQr.setImageResource(R.drawable.ic_bottombar_scan_66dp)
        }
    }

    private fun checkkNewTheme() {
        lifecycleScope.async {
            val file = File(FileHelper.getPath(this@HomeActivity) + FileHelper.imageFolder)
            if (file.exists()) {
                FileHelper.deleteTheme(file)
            }
            SettingManager.themeSetting = null
            setupTheme()

            val themeSettingRes = try {
                withTimeoutOrNull(10000L) { CheckThemeViewModel().getThemeSetting() }
            } catch (e: Exception) {
                null
            }
            SettingManager.themeSetting = themeSettingRes?.data

            viewModel.downloadTheme()
        }
    }

    private fun clearFilter() {
        unCheckAllFilterHistory()
        btnApply.performClick()
    }

    override fun showDialogUpdate() {
        DialogHelper.showNotification(this@HomeActivity, R.string.cap_nhat, R.string.message_update_app, false, object : NotificationDialogListener {
            override fun onDone() {
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.data = Uri.parse("https://play.google.com/store/apps/details?id=vn.icheck.android")
                startActivity(intent)
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ICK_REQUEST_CAMERA) {
            if (PermissionHelper.checkResult(grantResults)) {
//                ICKScanActivity.create(this)
                V6ScanditActivity.create(this)
            }
        }
    }

    override fun onUpdateMessageCount(count: String?) {

    }

    override fun onShowLoading(isShow: Boolean) {
        vn.icheck.android.ichecklibs.DialogHelper.showLoading(this@HomeActivity, isShow)
    }

    override fun onLogoutFalse() {
        DialogHelper.showNotification(this@HomeActivity, null, R.string.dang_xuat_khong_thanh_cong_vui_long_thu_lai, R.string.thu_lai, false, object : NotificationDialogListener {
            override fun onDone() {
                presenter.loginAnonymous()
            }
        })
    }

    override fun showError(errorMessage: String) {
        showLongErrorToast(errorMessage)
    }

    override val mContext: Context
        get() = this@HomeActivity

    override fun onRequireLoginSuccess(requestCode: Int) {
        super.onRequireLoginSuccess(requestCode)

        when (requestCode) {
            requestLoginProfile -> {
                SessionManager.session.user?.id?.let { userID ->
                    IckUserWallActivity.create(userID, this)
                }
            }
            requestLoginOrderHistory -> {
                startActivity<OrderHistoryActivity>()
            }
            requestLoginOrderHistoryConfirm -> {
                startActivity<OrderHistoryActivity, Int>(Constant.DATA_1, OrderHistoryActivity.waitForConfirmation)
            }
            requestLoginOrderHistoryDelivery -> {
                startActivity<OrderHistoryActivity, Int>(Constant.DATA_1, OrderHistoryActivity.delivery)
            }
            requestLoginOrderHistoryReview -> {
                startActivity<OrderHistoryActivity, Int>(Constant.DATA_1, OrderHistoryActivity.delivered)
            }
            requestLoginHistoryCard -> {
                startActivity<HistoryCardActivity>()
            }
            requestLoginBookmark -> {
                startActivity<BookMarkV2Activity>()
            }
            requestLoginCoinHistory -> {
                startActivity<CoinHistoryActivity>()
            }
            requestLoginChat -> {
                tvChat.performClick()
            }
            requestLoginXu -> {
                startActivity<CoinHistoryActivity>()
            }
            requestLoginRank -> {
                ickLoginViewModel.getUserInfo().observe(this) {
                    it?.data?.let { userInfoRes ->
                        SessionManager.session = SessionManager.session.apply {
                            user = userInfoRes.createICUser()
                            startActivity<RankOfUserActivity>()
                        }
                    }
                }
            }
            requestManagerPage -> {
                IckUserWallActivity.openManagePage(SessionManager.session.user?.id, this)
            }
        }
    }

    override fun onClickQrType(item: String?) {

    }

    override fun onValidStamp(item: String?) {

    }

    override fun onClick(view: View?) {
        if (view?.id != R.id.tv_help) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        when (view?.id) {

            R.id.tvHome -> {
                ringtoneHelper.playAudio(R.raw.pull_out)
                selectTab(1)
            }
            R.id.tvFeed -> {
                ringtoneHelper.playAudio(R.raw.pull_out)
                selectTab(2)
            }
            R.id.imgScanQr -> {
                ringtoneHelper.playAudio(R.raw.pull_out)
                selectTab(5)
            }
            R.id.tvHistory -> {
                ringtoneHelper.playAudio(R.raw.pull_out)
                selectTab(3)
            }
            R.id.tvChat -> {
                ringtoneHelper.playAudio(R.raw.pull_out)
                selectTab(4)
            }
            R.id.imgAvatar, R.id.tv_username -> {
                if (!SessionManager.isUserLogged) {
                    onEndOfToken()
                } else {
                    IckUserWallActivity.create(SessionManager.session.user?.id, this)
                }
            }
            R.id.btn_icheck_xu -> {
                if (SessionManager.isUserLogged) {
                    onRequireLoginSuccess(requestLoginXu)
                } else {
                    onRequireLogin(requestLoginXu)
                }
            }
            R.id.btn_rank_user -> {
                if (SessionManager.isUserLogged) {
                    onRequireLoginSuccess(requestLoginRank)
                } else {
                    onRequireLogin(requestLoginRank)
                }
            }
            R.id.btnCreateQr -> {
                startActivity<CreateQrCodeHomeActivity>()
            }
            R.id.tvName -> {
                if (SessionManager.isUserLogged) {
                    onRequireLoginSuccess(requestLoginProfile)
                } else {
                    onRequireLogin(requestLoginProfile)
                }
            }
            R.id.txtStatus -> {
                if (SessionManager.isUserLogged) {
                    onRequireLoginSuccess(requestLoginProfile)
                } else {
                    onRequireLogin(requestLoginProfile)
                }
            }
            R.id.layoutOrder -> {
                if (SessionManager.isUserLogged) {
                    onRequireLoginSuccess(requestLoginOrderHistory)
                } else {
                    onRequireLogin(requestLoginOrderHistory)
                }
            }
            R.id.txtConfirmation -> {
                if (SessionManager.isUserLogged) {
                    onRequireLoginSuccess(requestLoginOrderHistoryConfirm)
                } else {
                    onRequireLogin(requestLoginOrderHistoryConfirm)
                }
            }
            R.id.tvDelivery -> {
                if (SessionManager.isUserLogged) {
                    onRequireLoginSuccess(requestLoginOrderHistoryDelivery)
                } else {
                    onRequireLogin(requestLoginOrderHistoryDelivery)
                }
            }
            R.id.txtReview -> {
                if (SessionManager.isUserLogged) {
                    onRequireLoginSuccess(requestLoginOrderHistoryReview)
                } else {
                    onRequireLogin(requestLoginOrderHistoryReview)
                }
            }
            R.id.txtCardLoadingHistory -> {
                if (SessionManager.isLoggedAnyType) {
                    simpleStartActivity(HistoryCardActivity::class.java)
                }
            }
            R.id.txtBookmark -> {
                if (SessionManager.isUserLogged) {
                    onRequireLoginSuccess(requestLoginBookmark)
                } else {
                    onRequireLogin(requestLoginBookmark)
                }
            }
            R.id.tvCoin -> {
                if (SessionManager.isUserLogged) {
                    onRequireLoginSuccess(requestLoginCoinHistory)
                } else {
                    onRequireLogin(requestLoginCoinHistory)
                }
            }
            R.id.tvWallet -> {
//                tvCoin.performClick()
            }
            R.id.txtCreateQr -> {
                startActivity<CreateQrCodeHomeActivity>()
            }
            R.id.txtSetup -> {
                startActivity<SettingsActivity>()
            }
            R.id.txtGuid -> {
                for (obj in SettingManager.clientSetting?.support_links ?: mutableListOf()) {
                    if (obj.key == "guide") {
                        WebViewActivity.start(this, obj.link)
                    }
                }
            }
            R.id.txtPolicy -> {
                for (obj in SettingManager.clientSetting?.support_links ?: mutableListOf()) {
                    if (obj.key == "policy") {
                        WebViewActivity.start(this, obj.link)
                    }
                }
            }
            R.id.txtQuestion -> {
                for (obj in SettingManager.clientSetting?.support_links ?: mutableListOf()) {
                    if (obj.key == "faqs") {
                        WebViewActivity.start(this, obj.link)
                        return
                    }
                }
            }
            R.id.btn_manage_page -> {
                if (SessionManager.isUserLogged) {
                    IckUserWallActivity.openManagePage(SessionManager.session.user?.id, this)
                } else {
                    onRequireLogin(requestManagerPage)
                }
            }
            R.id.tv_logout -> {
                object : ConfirmDialog(this@HomeActivity, "Đăng xuất", "Bạn muốn thoát tài khoản này?", "Để Sau", "Đồng ý", true) {
                    override fun onDisagree() {
                    }

                    override fun onAgree() {
                        logoutFromHome()
                    }

                    override fun onDismiss() {
                    }
                }.show()
            }
            R.id.tv_help -> {
                if (tv_ttud.visibility == View.VISIBLE) {
                    tv_help.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_help_left_menu, 0, R.drawable.ic_arrow_down_gray_24dp, 0)
                    for (item in help_root.children) {
                        item.visibility = View.GONE
                    }
                    tv_help.visibility = View.VISIBLE
                } else {
                    tv_help.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_help_left_menu, 0, R.drawable.ic_arrow_up_gray, 0)
                    for (item in help_root.children) {
                        item.visibility = View.VISIBLE
                    }
                }
            }
            R.id.tv_hdsd -> {
                SettingHelper.getSystemSetting("app-support.support-url", "app-support", object : ISettingListener {
                    override fun onRequestError(error: String) {
                        logDebug(error)
                    }

                    override fun onGetClientSuccess(list: MutableList<ICClientSetting>?) {
                        WebViewActivity.start(this@HomeActivity, list?.firstOrNull()?.value, null, "Hướng dẫn sử dụng")
                    }
                })
            }
            R.id.tv_dksd -> {
                SettingHelper.getSystemSetting("app-support.privacy-url", "app-support", object : ISettingListener {
                    override fun onRequestError(error: String) {
                        logDebug(error)
                    }

                    override fun onGetClientSuccess(list: MutableList<ICClientSetting>?) {
                        WebViewActivity.start(this@HomeActivity, list?.firstOrNull()?.value, null, "Điều khoản sử dụng")
                    }
                })
            }
            R.id.tv_chtg -> {
                SettingHelper.getSystemSetting("app-support.question-url", "app-support", object : ISettingListener {
                    override fun onRequestError(error: String) {
                        logDebug(error)
                    }

                    override fun onGetClientSuccess(list: MutableList<ICClientSetting>?) {
                        WebViewActivity.start(this@HomeActivity, list?.firstOrNull()?.value, null, "Câu hỏi thường gặp")
                    }
                })
            }
            R.id.tv_lhic -> {
//                SettingHelper.getClientSettingSocial("app-support.contact-url", "app-support", object : ISettingListener {
//                    override fun onRequestError(error: String) {
//                        logDebug(error)
//                    }
//
//                    override fun onGetClientSuccess(list: MutableList<ICClientSetting>?) {
//                        WebViewActivity.start(this@HomeActivity, list?.firstOrNull()?.value, null, "Liên hệ và hỗ trợ")
//                    }
//                })
                simpleStartActivity(ContactActivity::class.java)
            }
            R.id.group_history_topup -> {
                if (SessionManager.isLoggedAnyType) {
                    simpleStartActivity(HistoryCardActivity::class.java)
                }
            }
            R.id.layoutSetting -> {
                startActivity<SettingsActivity>()
            }
            R.id.tv_ttud -> {
                simpleStartActivity(AppInfoActivity::class.java)
            }
            R.id.group_favorite_products -> {
                delayAction({
                    if (SessionManager.isUserLogged) {
                        simpleStartActivity(BookmarkHistoryActivity::class.java)
                    } else {
                        onEndOfToken()
                    }
                })

            }
            R.id.btnApply -> {
                adapterMenu.applyCode.clear()
                adapterMenu.applyShop.clear()

                ScanHistoryFragment.listIdBigCorp.clear()
                ScanHistoryFragment.listType.clear()

                var isFiltered = false

                for (item in adapterMenu.listData) {
                    if (item.data != null) {
                        if (item.type == ICViewTypes.FILTER_TYPE_HISTORY) {
                            for (child in (item.data as MutableList<ICTypeHistory>)) {
                                if (child.select) {
                                    isFiltered = true
                                    ScanHistoryFragment.listType.add(child.type!!)
                                    adapterMenu.applyCode[child.type ?: ""] = true
                                }
                            }
                        } else if (item.type == ICViewTypes.FILTER_SHOP_HISTORY) {
                            (item.data as MutableList<ICTypeHistory>).apply {
                                for (child in this) {
                                    if (child.select) {
                                        isFiltered = true
                                        ScanHistoryFragment.listIdBigCorp.add(child.idShop!!)
                                        adapterMenu.applyShop[child.idShop ?: 0] = true
                                    }
                                }
                            }
                        }
                    }
                }

                if (isFiltered) {
                    ScanHistoryFragment.adapter?.hideBigCopAndSuggest()
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_TICK_HISTORY))
                    historyViewModel.getListScanHistory(ScanHistoryFragment.sort, ScanHistoryFragment.listIdBigCorp, ScanHistoryFragment.listType)
                } else {
                    ScanHistoryFragment.adapter?.addBigCopAndSuggest(ICScanHistory(ICViewTypes.LIST_BIG_CORP, historyViewModel.listCategory))
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_UNTICK_HISTORY))
                    historyViewModel.getListScanHistory(ScanHistoryFragment.sort, ScanHistoryFragment.listIdBigCorp, ScanHistoryFragment.listType)
                }

                drawerLayout.closeDrawer(GravityCompat.END)
            }
        }
    }

    fun logoutFromHome() {
        if (NetworkHelper.isNotConnected(this@HomeActivity)) {
            showLongError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                ickLoginViewModel.logoutDevice(it.result)
            } else {
                ickLoginViewModel.logoutDevice("unknown")
            }
            RelationshipManager.removeListener()

            ickLoginViewModel.logout()
            //                        ickLoginViewModel.loginAnonymous()
            presenter.onLogOut()
        }
    }

    override fun onLoadmore() {

    }

    override fun onClickBigCorp(item: ICBigCorp) {

    }

    override fun onMessageErrorMenu() {

    }

    override fun unCheckAllFilterHistory() {
        for (item in adapterMenu.listData) {
            if (item.type != ICViewTypes.TOOLBAR_MENU_HISTORY) {
                for (child in (item.data as MutableList<ICTypeHistory>)) {
                    child.select = false
                }
            }
        }
        adapterMenu.notifyDataSetChanged()
    }

    override fun onCloseDrawer() {
        drawerLayout.closeDrawer(GravityCompat.END)
        adapterMenu.refreshSelected()
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)

        when (event.type) {
            ICMessageEvent.Type.GO_TO_HOME -> {
                if (event.data != null && event.data is Int) {
                    selectTab(event.data)
                }
            }
            ICMessageEvent.Type.UPDATE_COIN_AND_RANK -> {
//                setCoinAndRank()
            }
            ICMessageEvent.Type.ON_LOG_IN_FIREBASE -> {
                presenter.registerNotificationCount()
                presenter.registerMessageCount()
                FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                    ickLoginViewModel.loginDevice(token).observe(this) { _ ->
                    }
                }

                ChatSdk.shareIntent(SessionManager.session.firebaseToken, SessionManager.session.user?.id, SessionManager.session.token, DeviceUtils.getUniqueDeviceId(), SessionManager.isUserLogged)
            }
            ICMessageEvent.Type.ON_LOG_OUT -> {
                ChatSdk.shareIntent(null, null, null, null, false)

                tvChatCount.visibility = View.GONE
                RelationshipManager.removeListener()
                checkkNewTheme()
                clearFilter()

                checkLoginOrLogoutChat(false)

                FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                    ickLoginViewModel.loginDevice(token).observe(this, Observer { })
                }
            }
            ICMessageEvent.Type.ON_LOG_IN -> {
                tv_username.text = SessionManager.session.user?.getName
                tv_user_rank.text = SessionManager.session.user?.phone
                Glide.with(this.applicationContext)
                        .load(SessionManager.session.user?.avatar)
                        .placeholder(R.drawable.ic_avatar_default_84px)
                        .error(R.drawable.ic_avatar_default_84px)
                        .into(imgAvatar)
                RelationshipManager.removeListener()
                RelationshipManager.refreshToken(true)
                ChatSdk.shareIntent(SessionManager.session.firebaseToken, SessionManager.session.user?.id, SessionManager.session.token, DeviceUtils.getUniqueDeviceId(), SessionManager.isUserLogged)
                checkkNewTheme()
                clearFilter()
                checkLoginOrLogoutChat(true)
            }
            ICMessageEvent.Type.INIT_MENU_HISTORY -> {
                recyclerViewMenu.layoutManager = LinearLayoutManager(this)
                recyclerViewMenu.adapter = adapterMenu
                drawerLayout.openDrawer(GravityCompat.END)
            }
            ICMessageEvent.Type.UNREAD_COUNT -> {
                if (event.data is Long) {
                    val unread = event.data as Long
                    var text = ""
                    text = if (unread > 9) {
                        "9+"
                    } else {
                        "$unread"
                    }
                    tvChatCount.visibility = if (unread <= 0L) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }

                    tvChatCount.text = text
                } else {
                    tvChatCount.beGone()
                }
            }
            ICMessageEvent.Type.ON_SET_THEME -> {
                setupTheme()
            }
            ICMessageEvent.Type.UPDATE_CONVERSATION -> {
                val unread = RelationshipManager.unreadNotify
                var text = ""
                text = if (unread > 9) {
                    "9+"
                } else {
                    "$unread"
                }
                tvChatCount.visibility = if (unread <= 0L) {
                    View.GONE
                } else {
                    View.VISIBLE
                }

                tvChatCount.text = text
            }
            ICMessageEvent.Type.ON_CHECK_UPDATE_LOCATION -> {
                startLocationUpdates()
            }
            else -> {
//                onRequireLogin()
            }
        }
    }

    private fun checkLoginOrLogoutChat(isLogin: Boolean) {
        (viewPager.adapter as ViewPagerAdapter).apply {
            for (item in listData) {
                if (item.fragment is ChatSocialFragment) {
                    item.fragment.checkLoginOrLogOut(isLogin)
                    return
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                requestProfile -> {
                    tvChat.performClick()
                }
                requestUpdateProfile -> {
                    presenter.updateUserInfo()
                }
//                ProductDetailActivity.DETAIL_PRODUCT -> {
//                    if (!isChecked(tvHome)) {
//                        InsiderHelper.tagHomePageViewed()
//                        TekoHelper.tagHomepageViewed()
//                        viewPager.setCurrentItem(0, false)
//                    }
//                }
            }
        }
    }

    override fun onBackPressed() {
        when {
            drawerLayout.isDrawerOpen(GravityCompat.START) -> {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            isChecked(tvHome) -> {
                super.onBackPressed()
            }
            else -> {
//                TrackingAllHelper.trackHomePageViewed()
                viewPager.setCurrentItem(0, false)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
            }
        }

        try {
            presenter.checkVersionApp()
            onUpdateUserInfo()
            startLocationUpdates()
        } catch (e: Exception) {
            logError(e)
        }
    }

    override fun onStart() {
        super.onStart()
        RelationshipManager.refreshToken()
//        FirebaseAuth.getInstance().signInWithCustomToken(SessionManager.session.firebaseToken.toString())
//                .addOnSuccessListener {
//                    FirebaseDatabase.getInstance().goOnline()
//                    RelationshipManager.observe()
//                }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
        isOpen = false
        RelationshipManager.removeListener()
        INSTANCE = null
    }
}