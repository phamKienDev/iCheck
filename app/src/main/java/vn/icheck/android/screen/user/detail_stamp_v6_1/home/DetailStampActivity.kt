package vn.icheck.android.screen.user.detail_stamp_v6_1.home

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.location.*
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_detail_stamp.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.banner.ListBannerAdapter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.*
import vn.icheck.android.ichecklibs.DialogHelper
import vn.icheck.android.ichecklibs.beGone
import vn.icheck.android.ichecklibs.beVisible
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.listener.IClickListener
import vn.icheck.android.loyalty.helper.CampaignLoyaltyHelper
import vn.icheck.android.loyalty.model.ICKLoyalty
import vn.icheck.android.loyalty.screen.url_gift_detail.UrlGiftDetailActivity
import vn.icheck.android.loyalty.sdk.LoyaltySdk
import vn.icheck.android.network.base.*
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.detail_stamp_v6_1.*
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.account.home.AccountActivity
import vn.icheck.android.screen.account.icklogin.IckLoginActivity
import vn.icheck.android.screen.user.cart.CartActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.contact_support.ContactSupportActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.history_guarantee.HistoryGuaranteeActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.adapter.*
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.presenter.DetailStampPresenter
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.view.IDetailStampView
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.viewmodel.ICDetailStampViewModel
import vn.icheck.android.screen.user.detail_stamp_v6_1.more_business.MoreBusinessActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.more_information_product.MoreInformationProductActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.more_product_verified_by_distributor.MoreProductVerifiedByDistributorActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.UpdateInformationFirstActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.verified_phone.VerifiedPhoneActivity
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.screen.user.shipping.ship.ShipActivity
import vn.icheck.android.screen.user.view_item_image_stamp.ViewItemImageActivity
import vn.icheck.android.screen.user.viewimage.ViewImageActivity
import vn.icheck.android.util.AdsUtils
import vn.icheck.android.util.kotlin.ContactUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.math.hypot

class DetailStampActivity : BaseActivityMVVM(), IDetailStampView, CampaignLoyaltyHelper.ILoginListener, IClickListener, CampaignLoyaltyHelper.IRemoveHolderInputLoyaltyListener {
    private lateinit var viewModel: ICDetailStampViewModel
    private val presenter = DetailStampPresenter(this)

    private val adapter = ICDetailStampAdapter()

    companion object {
        val listActivities = mutableListOf<AppCompatActivity>()
        var isVietNamLanguage: Boolean? = null
        var mSerial: String? = null
    }

    private var disposable: Disposable? = null

    var id = -1L

    private var isShow = true
    private var numberPage = 0
    private var isVerified = -1
    private var idDistributor: Long? = null
    private var productCode: String? = null
    private var productId: Long? = null
    private var objVariant: ICVariantProductStampV6_1.ICVariant.ICObjectVariant? = null
    private var phoneGuarantee: String? = null
    private var verfiedSerial: String? = null

    private var idShopVariant: Long? = null
    private var idVariant: Long? = null
    private var seller_id: Long? = null
    private var barcode: String? = null
    private var objProductShopVariant: ICShopVariant? = null
    private var objShopVariantStamp: ICShopVariantStamp? = null

    private var objVendor: ICObjectVendor? = null
    private var itemDistributor: ICObjectDistributor? = null
    private var objGuarantee: ICObjectGuarantee? = null
    private var objCustomerLastGuarantee: ICObjectCustomer? = null

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var lat: String? = null
    private var lng: String? = null

    private val requestGpsPermission = 39
    private val requestPhone = 1
    private val requestGps = 2
    private val requestGoToCart = 4
    private val requestRefreshData = 5
    private var requestRequireLogin = 0
    private val requestGift = 12

    var type = ""
    var banner = ""
    var description = ""
    var targetType = ""

//    private var nameProduct: String? = null
    private var url: String? = null
    private var km: String? = null

//    private var bannerAdapter: BannerAdapter? = null
    private lateinit var adapterSuggestion: MoreProductVerifiedAdapter
    private lateinit var adapterService: ServiceShopVariantAdapter
    private lateinit var adapterConfigError: ConfigErrorAdapter
    private lateinit var adapterInformationProduct: InformationProductStampAdapter

    private var bannerViewPager: ViewPager? = null

    private val requestBannerSurvey = 1

    var codeInput = ""
    var obj: ICKLoyalty? = null

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_stamp)

        setupCountry()
        setupRecyclerView()
        setupViewModel()
        getData()
        setupListener()
    }

    private fun setupCountry() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        isVietNamLanguage = Locale.getDefault().displayLanguage.toLowerCase(Locale.getDefault()) == "vi"

        runOnUiThread {
            if (isVietNamLanguage == false) {
                txtTitle.text = "Verified product"
                tvChatWithAdmin.text = "Contact to Admin Icheck"
                btnAgainError.text = "Try Again"
                btnRequestPermission.text = "Turn on GPS"
            } else {
                txtTitle.text = "Xác thực sản phẩm"
                tvChatWithAdmin.text = "Liên hệ Admin iCheck"
                btnAgainError.text = "Thử Lại"
                btnRequestPermission.text = "Bật GPS"
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerView.adapter = adapter
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(ICDetailStampViewModel::class.java)

        viewModel.getData(intent)
    }

    private var isGetLocationSuccess = false
    private fun getData() {
        if (!checkGpsPermission) {
            return
        }

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 500
        locationRequest.fastestInterval = 200

        mFusedLocationClient?.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                val lastLocation = locationResult?.lastLocation

                if (lastLocation != null && !isGetLocationSuccess) {
                    llAcceptPermission.beGone()
                    isGetLocationSuccess = true
                    lat = lastLocation.latitude.toString()
                    lng = lastLocation.longitude.toString()
                }

                presenter.onGetDataIntent(intent, lat, lng)
                getStampDetailV61()
                mFusedLocationClient?.removeLocationUpdates(this)
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability?) {
                super.onLocationAvailability(locationAvailability)
                if (locationAvailability?.isLocationAvailable != true) {
                    presenter.onGetDataIntent(intent, lat, lng)
                }
            }
        }, Looper.getMainLooper())
    }

    private fun setupListener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }

        imgMore.setOnClickListener {
            imgMore.isClickable = false
            val cx: Int = layoutChatAdmin.measuredWidth * 2
            val cy: Int = layoutChatAdmin.measuredHeight / 2
            val finalRadius = hypot(layoutChatAdmin.width * 2.toDouble(), layoutChatAdmin.height.toDouble()).toFloat()

            if (layoutChatAdmin.visibility == View.VISIBLE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val anim = ViewAnimationUtils.createCircularReveal(layoutChatAdmin, cx, cy, finalRadius, 0f)
                    anim.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            layoutChatAdmin.visibility = View.INVISIBLE
                            imgMore.isClickable = true
                        }
                    })
                    anim.duration = 1000
                    anim.start()
                } else {
                    layoutChatAdmin.visibility = View.INVISIBLE
                    imgMore.isClickable = true
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val anim = ViewAnimationUtils.createCircularReveal(layoutChatAdmin, cx, cy, 0f, finalRadius)
                    layoutChatAdmin.visibility = View.VISIBLE
                    anim.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            imgMore.isClickable = true
                        }
                    })
                    anim.duration = 1000
                    anim.start()
                } else {
                    layoutChatAdmin.visibility = View.VISIBLE
                    imgMore.isClickable = true
                }
            }
        }

        layoutChatAdmin.setOnClickListener {
            startActivity<ContactSupportActivity>()
            layoutChatAdmin.visibility = View.INVISIBLE
        }

        tvMoreHistoryGuarantee.setOnClickListener {
            // todo
//            val intent = Intent(this, HistoryGuaranteeActivity::class.java)
//            when {
//                tvSerialFake.text.toString().isNotEmpty() -> {
//                    intent.putExtra(Constant.DATA_1, tvSerialFake.text.toString())
//                }
//                tvSerialVerified.text.toString().isNotEmpty() -> {
//                    intent.putExtra(Constant.DATA_1, tvSerialVerified.text.toString())
//                }
//                tvSerialVerifiedChongGia.text.toString().isNotEmpty() -> {
//                    intent.putExtra(Constant.DATA_1, tvSerialVerifiedChongGia.text.toString())
//                }
//                else -> {
//                    intent.putExtra(Constant.DATA_1, tvSerialVerifiedBaoHanh.text.toString())
//                }
//            }
//            startActivity(intent)
        }

        layoutMoreVendor.setOnClickListener {
            val intent = Intent(this, MoreBusinessActivity::class.java)
            intent.putExtra(Constant.DATA_1, 1)
            intent.putExtra(Constant.DATA_2, objVendor)
            startActivity(intent)
        }

        layoutMoreDistributor.setOnClickListener {
            val intent = Intent(this, MoreBusinessActivity::class.java)
            intent.putExtra(Constant.DATA_1, 2)
            intent.putExtra(Constant.DATA_2, itemDistributor)
            startActivity(intent)
        }

        textFab.setOnClickListener {
            // todo
//            // 0 - la chong gia , 1 - tran hang , 2 - bao hanh
//            if (objGuarantee != null) {
//                if (objCustomerLastGuarantee != null) {
//                    val intent = Intent(this, VerifiedPhoneActivity::class.java)
//                    when {
//                        tvSerialFake.text.toString().isNotEmpty() -> {
//                            intent.putExtra(Constant.DATA_1, tvSerialFake.text.toString())
//                        }
//                        tvSerialVerified.text.toString().isNotEmpty() -> {
//                            intent.putExtra(Constant.DATA_1, tvSerialVerified.text.toString())
//                        }
//                        tvSerialVerifiedChongGia.text.toString().isNotEmpty() -> {
//                            intent.putExtra(Constant.DATA_1, tvSerialVerifiedChongGia.text.toString())
//                        }
//                        else -> {
//                            intent.putExtra(Constant.DATA_1, tvSerialVerifiedBaoHanh.text.toString())
//                        }
//                    }
//                    intent.putExtra(Constant.DATA_2, idDistributor)
//                    intent.putExtra(Constant.DATA_3, productCode)
//                    intent.putExtra(Constant.DATA_4, productId)
//                    intent.putExtra(Constant.DATA_5, objVariant)
//                    intent.putExtra(Constant.DATA_8, presenter.code)
//                    startActivity(intent)
//                } else {
//                    val intent = Intent(this, UpdateInformationFirstActivity::class.java)
//                    intent.putExtra(Constant.DATA_1, 2)
//                    intent.putExtra(Constant.DATA_2, idDistributor)
//                    intent.putExtra(Constant.DATA_4, productCode)
//                    intent.putExtra(Constant.DATA_5, verfiedSerial)
//                    intent.putExtra(Constant.DATA_6, productId)
//                    intent.putExtra(Constant.DATA_7, objVariant)
//                    intent.putExtra(Constant.DATA_8, presenter.code)
//                    startActivity(intent)
//                }
//            } else {
//                val intent = Intent(this, UpdateInformationFirstActivity::class.java)
//                intent.putExtra(Constant.DATA_1, 2)
//                intent.putExtra(Constant.DATA_2, idDistributor)
//                intent.putExtra(Constant.DATA_4, productCode)
//                when {
//                    tvSerialFake.text.toString().isNotEmpty() -> {
//                        intent.putExtra(Constant.DATA_5, tvSerialFake.text.toString())
//                    }
//                    tvSerialVerified.text.toString().isNotEmpty() -> {
//                        intent.putExtra(Constant.DATA_5, tvSerialVerified.text.toString())
//                    }
//                    tvSerialVerifiedChongGia.text.toString().isNotEmpty() -> {
//                        intent.putExtra(Constant.DATA_5, tvSerialVerifiedChongGia.text.toString())
//                    }
//                    else -> {
//                        intent.putExtra(Constant.DATA_5, tvSerialVerifiedBaoHanh.text.toString())
//                    }
//                }
//                intent.putExtra(Constant.DATA_6, productId)
//                intent.putExtra(Constant.DATA_7, objVariant)
//                startActivity(intent)
//            }
        }

        tvWebsiteVendor.setOnClickListener {
            var webpage = Uri.parse(tvWebsiteVendor.text.toString())

            if (!tvWebsiteVendor.text.toString().startsWith("http://") && !tvWebsiteVendor.text.toString().startsWith("https://")) {
                webpage = Uri.parse("http://${tvWebsiteVendor.text}")
            }

            val intent = Intent(Intent.ACTION_VIEW, webpage)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }

        tvPhoneVendor.setOnClickListener {
            if (tvPhoneVendor.text.toString() != getString(R.string.dang_cap_nhat)) {
                if (PermissionHelper.checkPermission(this, Manifest.permission.CALL_PHONE, requestPhone)) {
                    ContactUtils.callFast(this@DetailStampActivity, tvPhoneVendor.text.toString())
                }
            }
        }

        tvMailVendor.setOnClickListener {
            if (tvMailVendor.text.toString() != getString(R.string.dang_cap_nhat)) {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:" + tvMailVendor.text.toString())
                startActivity(Intent.createChooser(intent, "Send To"))
            }
        }

        tvWebsiteDistributor.setOnClickListener {
            var webpage = Uri.parse(tvWebsiteDistributor.text.toString())

            if (!tvWebsiteDistributor.text.toString().startsWith("http://") && !tvWebsiteDistributor.text.toString().startsWith("https://")) {
                webpage = Uri.parse("http://${tvWebsiteVendor.text}")
            }

            val intent = Intent(Intent.ACTION_VIEW, webpage)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }

        tvHotlineBussiness.setOnClickListener {
            if (!itemDistributor?.phone.isNullOrEmpty()) {
                if (PermissionHelper.checkPermission(this, Manifest.permission.CALL_PHONE, requestPhone)) {
                    ContactUtils.callFast(this@DetailStampActivity, itemDistributor?.phone!!)
                }
            }
        }

        tvPhoneDistributor.setOnClickListener {
            if (tvPhoneDistributor.text.toString() != getString(R.string.dang_cap_nhat)) {
                if (PermissionHelper.checkPermission(this, Manifest.permission.CALL_PHONE, requestPhone)) {
                    ContactUtils.callFast(this@DetailStampActivity, tvPhoneDistributor.text.toString())
                }
            }
        }

        tvEmailBussiness.setOnClickListener {
            if (!itemDistributor?.email.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:" + itemDistributor?.email)
                startActivity(Intent.createChooser(intent, "Send To"))
            }
        }

        tvMailDistributor.setOnClickListener {
            if (tvMailDistributor.text.toString() != getString(R.string.dang_cap_nhat)) {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:" + tvMailDistributor.text.toString())
                startActivity(Intent.createChooser(intent, "Send To"))
            }
        }

        btnAgainError.setOnClickListener {
            getData()
        }

        btnChat.setOnClickListener {
            if (SessionManager.isUserLogged || SessionManager.isUserLogged) {
//                val intent = Intent(this, ChatV2Activity::class.java)
//                intent.putExtra(Constant.DATA_1, idShopVariant)
//                intent.putExtra(Constant.DATA_2, objProductShopVariant)
//                startActivity(intent)
            } else {
                startActivity<AccountActivity>()
            }
        }

//        btnAddToCart.setOnClickListener {
//            if (SessionManager.isDeviceLogged || SessionManager.isDeviceLogged) {
//                onRequireLoginSuccess(requestAddToCart)
//            } else {
//                onRequireLogin(requestAddToCart)
//            }
//        }

        imgGotoCart.setOnClickListener {
            if (SessionManager.isUserLogged || SessionManager.isUserLogged) {
                onRequireLoginSuccess(requestGoToCart)
            } else {
                onRequireLogin(requestGoToCart)
            }
        }

        layoutBuyNow.setOnClickListener {
//            if (SessionManager.isUserLogged || SessionManager.isUserLogged) {
//                onRequireLoginSuccess(requestAddToCartShortToast)
//            } else {
//                onRequireLogin(requestAddToCartShortToast)
//            }
            showShortSuccess("Tính năng đang phát triển")
        }

        tvAddToCartInDiemBan.setOnClickListener {
//            if (SessionManager.isUserLogged || SessionManager.isUserLogged) {
//                onRequireLoginSuccess(requestAddToCart)
//            } else {
//                onRequireLogin(requestAddToCart)
//            }
        }

        tvMoreProductVerified.setOnClickListener {
            idDistributor?.let { id ->
                startActivity<MoreProductVerifiedByDistributorActivity, Long>(Constant.DATA_1, id)
            }
        }

        btnRequestPermission.setOnClickListener {
            if (checkGpsPermission) {
                getData()
            }
        }
    }

    private val checkGpsPermission: Boolean
        get() {
            val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            if (!PermissionHelper.checkPermission(this, permission, requestGpsPermission)) {
                llAcceptPermission.beVisible()
                return false
            }

            if (!NetworkHelper.checkGPS(this@DetailStampActivity, getString(R.string.vui_long_bat_gpa_de_ung_dung_tim_duoc_vi_tri_cua_ban), requestGps)) {
                llAcceptPermission.beVisible()
                return false
            }

            return true
        }

    private fun getStampDetailV61() {
        viewModel.getStampDetailV61(lat, lng).observe(this, {
            when (it.status) {
                Status.LOADING -> {
                    if (it.message.isNullOrEmpty()) {
                        DialogHelper.showLoading(this@DetailStampActivity)
                    } else {
                        DialogHelper.closeLoading(this@DetailStampActivity)
                    }
                }
                Status.ERROR_NETWORK -> {
                    adapter.setError(R.drawable.ic_error_network, ICheckApplication.getError(it.message), null)
                }
                Status.ERROR_REQUEST -> {
                    adapter.setError(R.drawable.ic_error_request, ICheckApplication.getError(it.message), null)
                }
                Status.SUCCESS -> {
                    if (it.data?.data?.widgets.isNullOrEmpty()) {
                        adapter.setError(R.drawable.ic_empty_product_110dp, ICheckApplication.getError(it.message), null)
                    } else {
                        val listData = mutableListOf<ICLayout>()

                        for (widget in it.data!!.data!!.widgets!!) {
                            when (widget.name) {
                                "IMAGE_PRODUCT" -> {
                                    if (!widget.data?.atts.isNullOrEmpty()) {
                                        listData.add(ICLayout().apply {
                                            viewType = ICViewTypes.PRODUCT_IMAGE
                                            data = widget.data!!.atts!!
                                        })
                                    }
                                }
                                "PRODUCT" -> {
                                    if (widget.data != null) {
                                        listData.add(ICLayout().apply {
                                            viewType = ICViewTypes.PRODUCT_INFO
                                            data = widget.data!!
                                        })
                                    }
                                }
                                "MESSAGE_RESULT" -> {
                                    if (widget.data != null) {
                                        listData.add(ICLayout().apply {
                                            viewType = ICViewTypes.MESSAGE_TYPE
                                            data = widget.data!!
                                        })
                                    }
                                }
                                "MESSAGE_RESULT" -> {
                                    if (widget.data != null) {
                                        isVerified = widget.data!!.success ?: 0
                                        listData.add(ICLayout().apply {
                                            viewType = ICViewTypes.MESSAGE_TYPE
                                            data = widget.data!!
                                        })
                                    }
                                }
                            }
                        }

                        adapter.setListData(listData)
                    }
                }
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun getShopVariant(seller_id: Long, barcode: String?) {
        mFusedLocationClient?.lastLocation?.addOnSuccessListener {
            if (it != null) {
                lat = it.latitude.toString()
                lng = it.longitude.toString()
                presenter.getShopVariant(it.latitude.toString(), it.longitude.toString(), seller_id, barcode)
            } else {
                presenter.getShopVariant(lat, lng, seller_id, barcode)
            }
        }?.addOnFailureListener {
            presenter.getShopVariant(lat, lng, seller_id, barcode)
        }?.addOnCanceledListener {
            presenter.getShopVariant(lat, lng, seller_id, barcode)
        }
    }

    override fun itemPagerClick(list: String, position: Int) {
        val intent = Intent(this, ViewImageActivity::class.java)
        intent.putExtra(Constant.DATA_1, list)
        intent.putExtra(Constant.DATA_2, position)
        startActivity(intent)
    }

    override fun itemPagerClickToVideo(urlVideo: String?) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlVideo))
        startActivity(intent)
    }

    private fun initRecyclerViewMoreProduct() {
        adapterSuggestion = MoreProductVerifiedAdapter(this)
        rcvMoreProductVerified.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rcvMoreProductVerified.adapter = adapterSuggestion
    }

    override fun onItemClick(item: ICObjectListMoreProductVerified) {
        if (!item.sku.isNullOrEmpty()) {
            IckProductDetailActivity.start(this, item.sku!!)
        }
    }

    private fun initAdapterService() {
        adapterService = ServiceShopVariantAdapter(this)
        rcvServiceShopVariant.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rcvServiceShopVariant.adapter = adapterService
    }

    private fun initAdapterConfigError() {
        adapterConfigError = ConfigErrorAdapter(this)
        rcvConfigError.layoutManager = LinearLayoutManager(this)
        rcvConfigError.adapter = adapterConfigError
    }

    private fun initAdapterInformationProduct() {
        adapterInformationProduct = InformationProductStampAdapter(this)
        rcvInformation.layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean = false
        }
        rcvInformation.adapter = adapterInformationProduct
    }

    override fun onClickInforProduct(item: ICObjectInfo) {
        val intent = Intent(this@DetailStampActivity, MoreInformationProductActivity::class.java)
        intent.putExtra(Constant.DATA_1, item.id)
        startActivity(intent)
    }

    override fun onItemHotlineClick(hotline: String?) {
        if (hotline != null) {
            if (hotline != getString(R.string.dang_cap_nhat)) {
                if (PermissionHelper.checkPermission(this, Manifest.permission.CALL_PHONE, requestPhone)) {
                    ContactUtils.callFast(this@DetailStampActivity, hotline)
                }
            }
        }
    }

    override fun onItemEmailClick(email: String?) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:$email")
        startActivity(Intent.createChooser(intent, "Send To"))
    }

    override fun onGetDataRequireLogin() {
        if (SessionManager.isUserLogged) {
            onRequireLoginSuccess(requestRefreshData)
        } else {
            onRequireLogin(requestRefreshData)
        }
    }

    fun getYoutubeVideoId(youtubeUrl: String?): String? {
        var videoId: String? = ""
        if (youtubeUrl != null && youtubeUrl.trim { it <= ' ' }.isNotEmpty() && youtubeUrl.startsWith("http")) {
            val expression = "^.*((youtu.be" + "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*"
            val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(youtubeUrl)
            if (matcher.matches()) {
                val groupIndex1: String = matcher.group(7)
                if (groupIndex1 != null && groupIndex1.length == 11) videoId = groupIndex1
            }
        }
        return videoId
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onGetDetailStampSuccess(obj: ICDetailStampV6_1) {
        if (viewModel.code.isNotEmpty()) {
            CampaignLoyaltyHelper.getCampaign(this@DetailStampActivity, viewModel.code, this@DetailStampActivity, this@DetailStampActivity)
        }
        //Check Error Client
        if (obj == null) {
            layoutErrorClient.visibility = View.VISIBLE
            scrollViewError.visibility = View.GONE
            scrollView.visibility = View.GONE
            bottomLayout.visibility = View.GONE
            return
        } else {
            layoutErrorClient.visibility = View.GONE

            //check Error detail Stamp
            if (obj.data?.scan_message?.redirect_warning == true) {
                itemDistributor = obj.data?.distributor
                scrollView.visibility = View.GONE
                layoutExceededScan.visibility = View.VISIBLE
                tvMessageApollo.text = obj.data?.scan_message?.text
                tvBussinessName.text = obj.data?.distributor?.name
                tvAddressBussiness.text = "Địa chỉ: " + obj.data?.distributor?.address + ", " + obj.data?.distributor?.district + ", " + obj.data?.distributor?.city
                tvHotlineBussiness.text = "Tổng đài: " + obj.data?.distributor?.phone
                tvEmailBussiness.text = " - Email: " + obj.data?.distributor?.email
                return
            }

            if (obj.data?.active_require_profile == 1) {
                val intent = Intent(this, UpdateInformationFirstActivity::class.java)
                intent.putExtra(Constant.DATA_1, 3)
                intent.putExtra(Constant.DATA_8, presenter.code)
                startActivity(intent)
            }

            if (obj.data?.error_code == "REQUIRE_LOCATION") {
                llAcceptPermission.visibility = View.VISIBLE
                tvMessageLocation.text = obj.data?.message?.message
                return
            }

            if (!obj.data?.message?.message.isNullOrEmpty()) {
                presenter.getConfigError()
                tvMessageStampError.text = "CẢNH BÁO!" + "\n" + obj.data?.message?.message
            } else {
                scrollView.visibility = View.VISIBLE
            }

            idDistributor = obj.data?.distributor?.id

//      set image local cho tab history qrCode
            url = obj.data?.product?.image
            productId = obj.data?.product?.id
            objVariant = obj.data?.guarantee?.last_guarantee?.variant

            if (!obj.data?.barcode.isNullOrEmpty() && obj.data?.seller_id != null) {
                seller_id = obj.data?.seller_id!!
                barcode = obj.data?.barcode
                getShopVariant(obj.data?.seller_id!!, obj.data?.barcode)
            }

//      check verified stamp
//            if (obj.data?.scan_message != null) {
//                obj.data?.scan_message?.is_success?.let {
//                    isVerified = it
//                }
//                when (obj.data?.scan_message?.is_success) {
//                    //fake
//                    0 -> {
//                        if (isVietNamLanguage == false) {
//                            appCompatTextView12.text = "Number of scan"
//                            appCompatTextView13.text = "Number of scaner"
//                        } else {
//                            appCompatTextView12.text = "Số lần quét"
//                            appCompatTextView13.text = "Số người quét"
//                        }
//
//                        layoutFake.visibility = View.VISIBLE
//                        tvMessageVerifiedFake.text = obj.data?.scan_message?.text
//                        obj.data?.count?.let {
////                            var textNumberSerial = "000000"
////                            textNumberSerial = textNumberSerial!!.removeRange(6 - it.number.toString().length, 6)
////                            val mNumber = textNumberSerial + it.number
//                            mSerial = getSerialNumber(it.prefix, it.number)
//                            verfiedSerial = getSerialNumber(it.prefix, it.number)
//                            tvSerialFake.text = "Serial: $verfiedSerial"
//
//                            tvCountScanFake.text = if (it.scan_count.toString().isNotEmpty()) {
//                                it.scan_count.toString()
//                            } else {
//                                getString(R.string.dang_cap_nhat)
//                            }
//
//                            tvCountUserScanFake.text = if (it.people_count.toString().isNotEmpty()) {
//                                it.people_count.toString()
//                            } else {
//                                getString(R.string.dang_cap_nhat)
//                            }
//
//                        }
//                    }
//                    //verified
//                    1 -> {
//                        if (isVietNamLanguage == false) {
//                            appCompatTextView15.text = "Number of scan"
//                            appCompatTextView16.text = "Number of scaner"
//                        } else {
//                            appCompatTextView15.text = "Số lần quét"
//                            appCompatTextView16.text = "Số người quét"
//                        }
//
//                        layoutVerified.visibility = View.VISIBLE
//                        tvMessageVerified.text = obj.data?.scan_message?.text
//                        obj.data?.count?.let {
//                            mSerial = getSerialNumber(it.prefix, it.number)
//                            verfiedSerial = getSerialNumber(it.prefix, it.number)
//                            tvSerialVerified.text = "Serial: $verfiedSerial"
//
//                            tvCountScanVerified.text = if (it.scan_count.toString().isNotEmpty()) {
//                                it.scan_count.toString()
//                            } else {
//                                getString(R.string.dang_cap_nhat)
//                            }
//
//                            tvCountUserScanVerified.text = if (it.people_count.toString().isNotEmpty()) {
//                                it.people_count.toString()
//                            } else {
//                                getString(R.string.dang_cap_nhat)
//                            }
//                        }
//                    }
//                    //Guarantee
//                    2 -> {
//                        if (isVietNamLanguage == false) {
//                            appCompatTextView19.text = "Number of scan"
//                            appCompatTextView20.text = "Number of scaner"
//                        } else {
//                            appCompatTextView19.text = "Số lần quét"
//                            appCompatTextView20.text = "Số người quét"
//                        }
//                        layoutVerifiedBaoHanh.visibility = View.VISIBLE
//                        tvMessageVerifiedBaoHanh.text = obj.data?.scan_message?.text
//                        obj.data?.count?.let {
//                            mSerial = getSerialNumber(it.prefix, it.number)
//                            verfiedSerial = getSerialNumber(it.prefix, it.number)
//                            tvSerialVerifiedBaoHanh.text = "Serial: $verfiedSerial"
//
//                            tvCountScanVerifiedBaoHanh.text = if (it.scan_count.toString().isNotEmpty()) {
//                                it.scan_count.toString()
//                            } else {
//                                getString(R.string.dang_cap_nhat)
//                            }
//
//                            tvCountUserScanVerifiedBaoHanh.text = if (it.people_count.toString().isNotEmpty()) {
//                                it.people_count.toString()
//                            } else {
//                                getString(R.string.dang_cap_nhat)
//                            }
//                        }
//                    }
//                }
//            } else {
//                layoutVerifiedChongGia.visibility = View.VISIBLE
//                layoutheaderChongGia.visibility = View.GONE
//                obj.data?.count?.let {
//                    mSerial = getSerialNumber(it.prefix, it.number)
//                    verfiedSerial = getSerialNumber(it.prefix, it.number)
//                    tvSerialVerifiedChongGia.text = "Serial: $verfiedSerial"
//                }
//            }

//            if (!obj.data?.product_link.isNullOrEmpty()) {
//                layoutProductLink.beVisible()
//                val adapter = object : RecyclerViewAdapter<ICProductLink>() {
//                    override fun getItemCount() = if (listData.size > 3) 3 else listData.size
//
//                    override fun viewHolder(parent: ViewGroup) = StampECommerceHolder(parent)
//
//                    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//                        if (holder is StampECommerceHolder) {
//                            holder.bind(listData[position])
//                        } else {
//                            super.onBindViewHolder(holder, position)
//                        }
//                    }
//                }
//                adapter.disableLoading()
//                adapter.disableLoadMore()
//                recyclerView.adapter = adapter
//                adapter.setListData(obj.data!!.product_link!!)
//
//                tvViewMore.visibleOrInvisible(adapter.getListData.size > 3)
//                tvViewMore.setOnClickListener {
//                    ActivityHelper.startActivity<ListProductsECommerceActivity>(this@DetailStampActivity, Constant.DATA_1, JsonHelper.toJson(obj.data?.product_link
//                            ?: mutableListOf()))
//                }
//            }

//      check show hide floating action button
            if (obj.data?.can_update == true) {

                if (isVietNamLanguage == false) {
                    textFab.text = "Update customer information"
                } else {
                    textFab.text = "Cập nhật thông tin khách hàng"
                }

                textFab.visibility = View.VISIBLE
                initScrollFab()
            }

//      check force update thong tin ca nhan
            if (obj.data?.force_update == true) {
                if (obj.data?.guarantee != null) {
                    val intent = Intent(this, UpdateInformationFirstActivity::class.java)
                    intent.putExtra(Constant.DATA_1, 1)
                    intent.putExtra(Constant.DATA_2, idDistributor)
                    intent.putExtra(Constant.DATA_4, obj.data?.guarantee?.last_guarantee?.product_code)
                    intent.putExtra(Constant.DATA_5, verfiedSerial)
                    intent.putExtra(Constant.DATA_6, obj.data?.product?.id)
                    intent.putExtra(Constant.DATA_7, obj.data?.guarantee?.last_guarantee?.variant)
                    intent.putExtra(Constant.DATA_8, presenter.code)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, UpdateInformationFirstActivity::class.java)
                    intent.putExtra(Constant.DATA_1, 2)
                    intent.putExtra(Constant.DATA_2, idDistributor)
                    intent.putExtra(Constant.DATA_4, obj.data?.guarantee?.last_guarantee?.product_code)
                    intent.putExtra(Constant.DATA_5, verfiedSerial)
                    intent.putExtra(Constant.DATA_6, obj.data?.product?.id)
                    intent.putExtra(Constant.DATA_7, obj.data?.guarantee?.last_guarantee?.variant)
                    intent.putExtra(Constant.DATA_8, presenter.code)
                    startActivity(intent)
                }
            }

//      setdata slide image product
            if (obj.data?.product?.images != null) {
                val param = if (!obj.data?.product?.video.isNullOrEmpty()) {
                    getYoutubeVideoId(obj.data?.product?.video)
                } else {
                    null
                }

                if (obj.data?.product?.images == null) {
                    obj.data?.product?.images = mutableListOf()
                }

                if (!param.isNullOrEmpty()) {
                    val videoImage = "https://img.youtube.com/vi/$param/hqdefault.jpg"
                    if (obj.data?.product?.images!!.isNotEmpty())
                        obj.data?.product?.images!!.add(0, videoImage)
                    else
                        obj.data?.product?.images!!.add(videoImage)
//                    bannerAdapter?.setListData(obj.data?.product?.images!!, obj.data?.product?.video, videoImage)
                } else {
//                    bannerAdapter?.setListData(obj.data?.product?.images!!, null, null)
                }

                if (obj.data?.product?.images!!.size > 0) {
                    numberPage = obj.data?.product?.images!!.size - 1
                }
            } else {
                if (obj.data?.product == null) {
                    obj.data?.product = ICObjectProduct()
                }
                obj.data?.product?.images = mutableListOf()
                obj.data?.product?.images?.add("")

//                bannerAdapter?.setListData(obj.data?.product?.images ?: mutableListOf(), null, null)
            }

//      Thong tin bao hanh
            objGuarantee = obj.data?.guarantee
            if (obj.data?.guarantee?.time != null) {
                if (isVietNamLanguage == false) {
                    tvWarrantyInformation.text = "Warranty information"
                    tvDetailsWarranty.text = "Details of Warranty information"
                } else {
                    tvWarrantyInformation.text = "Thông tin bảo hành"
                    tvDetailsWarranty.text = "Chi tiết bảo hành"
                }

                layoutGurantee.visibility = View.VISIBLE
                obj.data?.guarantee?.time?.let {
                    if (isVietNamLanguage == false) {
                        tvGuaranteeDay.text = if (it.guarantee_days_update != null && !it.type_guarantee_day.isNullOrEmpty()) {
                            if (it.type_guarantee_day!!.trim() == "years") {
                                Html.fromHtml("<font color=#434343>Warranty period: </font>" + "<b>" + it.guarantee_days_update + " " + "year" + "</b>")
                            } else if (it.type_guarantee_day!!.trim() == "months") {
                                Html.fromHtml("<font color=#434343>Warranty period: </font>" + "<b>" + it.guarantee_days_update + " " + "month" + "</b>")
                            } else {
                                Html.fromHtml("<font color=#434343>Warranty period: </font>" + "<b>" + it.guarantee_days_update + " " + "day" + "</b>")
                            }
                        } else {
                            Html.fromHtml("<font color=#434343>Warranty period: </font>" + "<b>" + "updating" + "</b>")
                        }

                        tvExpiredDay.text = if (!it.expired_date.isNullOrEmpty()) {
                            Html.fromHtml("<font color=#434343>Expire date: </font>" + "<b>" + TimeHelper.convertDateTimeSvToDateVn(it.expired_date) + "</b>")
                        } else {
                            Html.fromHtml("<font color=#434343>Expire date: </font>" + "<b>" + "updating" + "</b>")
                        }

                        tvRemainingDay.text = if (it.days_remaining_str != null) {
                            Html.fromHtml("<font color=#434343>Expiry date of warranty: </font>" + "<b>" + it.days_remaining_str + "</b>")
                        } else {
                            Html.fromHtml("<font color=#434343>Expiry date of warranty: </font>" + "<b>" + "updating" + "</b>")
                        }

                        tvActiveDay.text = if (!it.active.isNullOrEmpty()) {
                            Html.fromHtml("<font color=#434343>Warranty activation date: </font>" + "<b>" + TimeHelper.convertDateTimeSvToDateVn(it.active) + "</b>")
                        } else {
                            Html.fromHtml("<font color=#434343>Warranty activation date: </font>" + "<b>" + "updating" + "</b>")
                        }
                    } else {
                        tvGuaranteeDay.text = if (it.guarantee_days_update != null && !it.type_guarantee_day.isNullOrEmpty()) {
                            if (it.type_guarantee_day!!.trim() == "years") {
                                Html.fromHtml("<font color=#434343>Thời gian bảo hành: </font>" + "<b>" + it.guarantee_days_update + " " + "năm" + "</b>")
                            } else if (it.type_guarantee_day!!.trim() == "months") {
                                Html.fromHtml("<font color=#434343>Thời gian bảo hành: </font>" + "<b>" + it.guarantee_days_update + " " + "tháng" + "</b>")
                            } else {
                                Html.fromHtml("<font color=#434343>Thời gian bảo hành: </font>" + "<b>" + it.guarantee_days_update + " " + "ngày" + "</b>")
                            }
                        } else {
                            Html.fromHtml("<font color=#434343>Thời gian bảo hành: </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
                        }

                        tvExpiredDay.text = if (!it.expired_date.isNullOrEmpty()) {
                            Html.fromHtml("<font color=#434343>Hạn bảo hành: </font>" + "<b>" + TimeHelper.convertDateTimeSvToDateVn(it.expired_date) + "</b>")
                        } else {
                            Html.fromHtml("<font color=#434343>Hạn bảo hành: </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
                        }

                        tvRemainingDay.text = if (it.days_remaining_str != null) {
                            Html.fromHtml("<font color=#434343>Số ngày bảo hành còn lại: </font>" + "<b>" + it.days_remaining_str + "</b>")
                        } else {
                            Html.fromHtml("<font color=#434343>Số ngày bảo hành còn lại: </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
                        }

                        tvActiveDay.text = if (!it.active.isNullOrEmpty()) {
                            Html.fromHtml("<font color=#434343>Ngày kích hoạt bảo hành: </font>" + "<b>" + TimeHelper.convertDateTimeSvToDateVn(it.active) + "</b>")
                        } else {
                            Html.fromHtml("<font color=#434343>Ngày kích hoạt bảo hành: </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
                        }
                    }
                }
            } else {
                layoutGurantee.visibility = View.GONE
            }

//          Lich su bao hanh gan nhat
            if (obj.data?.guarantee?.last_guarantee != null) {
                layoutContentHistory.removeAllViews()
                if (isVietNamLanguage == false) {
                    appCompatTextView6.text = "Lastest Warranty log"
                    tvMoreHistoryGuarantee.text = "View all"
                } else {
                    appCompatTextView6.text = "Lịch sử bảo hành gần nhất"
                    tvMoreHistoryGuarantee.text = "Xem tất cả"
                }
                layoutHistoryGuarantee.visibility = View.VISIBLE
                obj.data?.guarantee?.last_guarantee?.let {
                    productCode = it.product_code
                    objCustomerLastGuarantee = it.customer
                    phoneGuarantee = it.customer?.phone

                    if (!it.images.isNullOrEmpty()) {
                        layoutContentHistory.addView(LinearLayout(this).also { layoutImage ->
                            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                            params.setMargins(SizeHelper.size8, SizeHelper.size8, SizeHelper.size8, 0)
                            layoutImage.layoutParams = params
                            layoutImage.orientation = LinearLayout.HORIZONTAL

                            if (it.images!!.size <= 5) {
                                for (i in it.images ?: mutableListOf()) {
                                    layoutImage.addView(AppCompatImageView(this).also { itemImage ->
                                        val paramsImage = LinearLayout.LayoutParams(SizeHelper.size40, SizeHelper.size40)
                                        paramsImage.setMargins(0, 0, SizeHelper.size8, 0)
                                        itemImage.layoutParams = paramsImage
                                        itemImage.scaleType = ImageView.ScaleType.FIT_CENTER
                                        WidgetUtils.loadImageUrlRounded(itemImage, i, SizeHelper.size4)

                                        itemImage.setOnClickListener {
                                            startActivity<ViewItemImageActivity, String>(Constant.DATA_1, i)
                                        }
                                    })
                                }
                            }
                        })
                    }

                    if (!it.created_time.isNullOrEmpty()) {
                        layoutContentHistory.addView(AppCompatTextView(this).also { tvCreatedTime ->
                            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                            layoutParams.setMargins(SizeHelper.size8, SizeHelper.size8, SizeHelper.size8, 0)
                            tvCreatedTime.layoutParams = layoutParams
                            tvCreatedTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                            tvCreatedTime.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
                            tvCreatedTime.gravity = Gravity.CENTER or Gravity.START
                            tvCreatedTime.setTextColor(ContextCompat.getColor(this, R.color.black))
                            tvCreatedTime.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_time_start_blue_18_px, 0, 0, 0)
                            tvCreatedTime.compoundDrawablePadding = SizeHelper.size8
                            if (isVietNamLanguage == false) {
                                tvCreatedTime.text = Html.fromHtml("<font color=#434343>Time: </font>" + "<b>" + TimeHelper.convertDateTimeSvToDateVn(it.created_time!!) + "</b>")
                            } else {
                                tvCreatedTime.text = Html.fromHtml("<font color=#434343>Thời gian: </font>" + "<b>" + TimeHelper.convertDateTimeSvToDateVn(it.created_time!!) + "</b>")
                            }
                        })
                    }

                    if (!it.store?.name.isNullOrEmpty()) {
                        layoutContentHistory.addView(AppCompatTextView(this).also { tvNameStoreGuarantee ->
                            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                            layoutParams.setMargins(SizeHelper.size8, SizeHelper.size8, SizeHelper.size8, 0)
                            tvNameStoreGuarantee.layoutParams = layoutParams
                            tvNameStoreGuarantee.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                            tvNameStoreGuarantee.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
                            tvNameStoreGuarantee.gravity = Gravity.CENTER or Gravity.START
                            tvNameStoreGuarantee.setTextColor(ContextCompat.getColor(this, R.color.black))
                            tvNameStoreGuarantee.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_store_blue_18px, 0, 0, 0)
                            tvNameStoreGuarantee.compoundDrawablePadding = SizeHelper.size8
                            if (isVietNamLanguage == false) {
                                tvNameStoreGuarantee.text = Html.fromHtml("<font color=#434343>Warranty Center: </font>" + "<b>" + it.store?.name + "</b>")
                            } else {
                                tvNameStoreGuarantee.text = Html.fromHtml("<font color=#434343>Điểm bảo hành: </font>" + "<b>" + it.store?.name + "</b>")
                            }
                        })
                    }

                    if (!it.status?.name.isNullOrEmpty()) {
                        layoutContentHistory.addView(AppCompatTextView(this).also { tvStatusLastGuarantee ->
                            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                            layoutParams.setMargins(SizeHelper.size8, SizeHelper.size8, SizeHelper.size8, 0)
                            tvStatusLastGuarantee.layoutParams = layoutParams
                            tvStatusLastGuarantee.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                            tvStatusLastGuarantee.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
                            tvStatusLastGuarantee.gravity = Gravity.CENTER or Gravity.START
                            tvStatusLastGuarantee.setTextColor(ContextCompat.getColor(this, R.color.black))
                            tvStatusLastGuarantee.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_blue_18px, 0, 0, 0)
                            tvStatusLastGuarantee.compoundDrawablePadding = SizeHelper.size8
                            if (isVietNamLanguage == false) {
                                tvStatusLastGuarantee.text = Html.fromHtml("<font color=#434343>State: </font>" + "<b>" + it.status?.name + "</b>")
                            } else {
                                tvStatusLastGuarantee.text = Html.fromHtml("<font color=#434343>Trạng thái: </font>" + "<b>" + it.status?.name + "</b>")
                            }
                        })
                    }

                    if (!it.state?.name.isNullOrEmpty()) {
                        layoutContentHistory.addView(AppCompatTextView(this).also { tvStateLastGuarantee ->
                            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                            layoutParams.setMargins(SizeHelper.size8, SizeHelper.size8, SizeHelper.size8, 0)
                            tvStateLastGuarantee.layoutParams = layoutParams
                            tvStateLastGuarantee.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                            tvStateLastGuarantee.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
                            tvStateLastGuarantee.gravity = Gravity.CENTER or Gravity.START
                            tvStateLastGuarantee.setTextColor(ContextCompat.getColor(this, R.color.black))
                            tvStateLastGuarantee.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_warranty_blue_18px, 0, 0, 0)
                            tvStateLastGuarantee.compoundDrawablePadding = SizeHelper.size8
                            if (isVietNamLanguage == false) {
                                tvStateLastGuarantee.text = Html.fromHtml("<font color=#434343>Warranty Status: </font>" + "<b>" + it.state?.name + "</b>")
                            } else {
                                tvStateLastGuarantee.text = Html.fromHtml("<font color=#434343>Tình trạng: </font>" + "<b>" + it.state?.name + "</b>")
                            }
                        })
                    }

                    if (!it.return_time.isNullOrEmpty()) {
                        layoutContentHistory.addView(AppCompatTextView(this).also { tvReturnTime ->
                            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                            layoutParams.setMargins(SizeHelper.size8, SizeHelper.size8, SizeHelper.size8, 0)
                            tvReturnTime.layoutParams = layoutParams
                            tvReturnTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                            tvReturnTime.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
                            tvReturnTime.gravity = Gravity.CENTER or Gravity.START
                            tvReturnTime.setTextColor(ContextCompat.getColor(this, R.color.black))
                            tvReturnTime.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_time_start_blue_18_px, 0, 0, 0)
                            tvReturnTime.compoundDrawablePadding = SizeHelper.size8
                            if (isVietNamLanguage == false) {
                                tvReturnTime.text = Html.fromHtml("<font color=#434343>Date of return: </font>" + "<b>" + TimeHelper.convertDateTimeSvToDateVn(it.return_time) + "</b>")
                            } else {
                                tvReturnTime.text = Html.fromHtml("<font color=#434343>Ngày hẹn trả: </font>" + "<b>" + TimeHelper.convertDateTimeSvToDateVn(it.return_time) + "</b>")
                            }
                        })
                    }

                    if (!it.note.isNullOrEmpty()) {
                        layoutContentHistory.addView(AppCompatTextView(this).also { textNote ->
                            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                            layoutParams.setMargins(SizeHelper.size8, SizeHelper.size8, SizeHelper.size8, 0)
                            textNote.layoutParams = layoutParams
                            textNote.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                            textNote.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
                            textNote.gravity = Gravity.CENTER or Gravity.START
                            textNote.setTextColor(ContextCompat.getColor(this, R.color.black))
                            textNote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_blue_18px, 0, 0, 0)
                            textNote.compoundDrawablePadding = SizeHelper.size8
                            if (isVietNamLanguage == false) {
                                textNote.text = Html.fromHtml("<font color=#434343>Notes of Warranty reuturn: </font>" + "<b>" + it.note + "</b>")
                            } else {
                                textNote.text = Html.fromHtml("<font color=#434343>Ghi chú trả BH: </font>" + "<b>" + it.note + "</b>")
                            }
                        })
                    }

                    for (i in it.fields ?: mutableListOf()) {
                        if (!i.value.isNullOrEmpty() && !i.name.isNullOrEmpty()) {
                            layoutContentHistory.addView(AppCompatTextView(this).also { text ->
                                val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                                layoutParams.setMargins(SizeHelper.size8, SizeHelper.size8, SizeHelper.size8, 0)
                                text.layoutParams = layoutParams
                                text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                                text.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
                                text.gravity = Gravity.CENTER or Gravity.START
                                text.setTextColor(ContextCompat.getColor(this, R.color.black))
                                text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_blue_18px, 0, 0, 0)
                                text.compoundDrawablePadding = SizeHelper.size8

                                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                try {
                                    val value = sdf.parse(i.value!!)
                                    if (value != null) {
                                        text.text = Html.fromHtml("<font color=#434343>${i.name}: </font>" + "<b>" + TimeHelper.convertDateTimeSvToDateVn(i.value) + "</b>")
                                    } else {
                                        text.text = Html.fromHtml("<font color=#434343>${i.name}: </font>" + "<b>" + i.value + "</b>")
                                    }
                                } catch (e: Exception) {
                                    text.text = Html.fromHtml("<font color=#434343>${i.name}: </font>" + "<b>" + i.value + "</b>")
                                }
                            })
                        }
                    }

                    if (!it.variant?.extra.isNullOrEmpty()) {
                        layoutContentHistory.addView(AppCompatTextView(this).also { textNote ->
                            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                            layoutParams.setMargins(SizeHelper.size8, SizeHelper.size8, SizeHelper.size8, 0)
                            textNote.layoutParams = layoutParams
                            textNote.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                            textNote.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
                            textNote.gravity = Gravity.CENTER or Gravity.START
                            textNote.setTextColor(ContextCompat.getColor(this, R.color.black))
                            textNote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_blue_18px, 0, 0, 0)
                            textNote.compoundDrawablePadding = SizeHelper.size8
                            if (isVietNamLanguage == false) {
                                textNote.text = Html.fromHtml("<font color=#434343>Variation: </font>" + "<b>" + it.variant?.extra + "</b>")
                            } else {
                                textNote.text = Html.fromHtml("<font color=#434343>Biến thể: </font>" + "<b>" + it.variant?.extra + "</b>")
                            }
                        })
                    }
                }
            } else {
                layoutHistoryGuarantee.visibility = View.GONE
            }

//      check nha san xuat
            if (obj.data?.show_vendor == 1) {
                if (obj.data?.product?.vendor != null) {
                    if (isVietNamLanguage == false) {
                        tvSubVendor.text = "Manufacturer"
                    } else {
                        tvSubVendor.text = "Nhà sản xuất"
                    }

                    tvSubVendor.visibility = View.VISIBLE
                    layoutVendor.visibility = View.VISIBLE

                    objVendor = obj.data?.product?.vendor

                    tvNameVendor.text = if (!obj.data?.product?.vendor?.name.isNullOrEmpty()) {
                        obj.data?.product?.vendor?.name
                    } else {
                        getString(R.string.dang_cap_nhat)
                    }

                    tvAddressVendor.text = if (!obj.data?.product?.vendor?.address.isNullOrEmpty()) {
                        if (!obj.data?.product?.vendor?.city.isNullOrEmpty()) {
                            if (!obj.data?.product?.vendor?.district.isNullOrEmpty()) {
                                obj.data?.product?.vendor?.address + ", " + obj.data?.product?.vendor?.city + ", " + obj.data?.product?.vendor?.district
                            } else {
                                obj.data?.product?.vendor?.address + ", " + obj.data?.product?.vendor?.city
                            }
                        } else {
                            obj.data?.product?.vendor?.address
                        }
                    } else {
                        getString(R.string.dang_cap_nhat)
                    }

                    tvWebsiteVendor.text = if (!obj.data?.product?.vendor?.website.isNullOrEmpty()) {
                        obj.data?.product?.vendor?.website
                    } else {
                        getString(R.string.dang_cap_nhat)
                    }

                    tvPhoneVendor.text = if (!obj.data?.product?.vendor?.phone.isNullOrEmpty()) {
                        obj.data?.product?.vendor?.phone
                    } else {
                        getString(R.string.dang_cap_nhat)
                    }

                    tvMailVendor.text = if (!obj.data?.product?.vendor?.email.isNullOrEmpty()) {
                        obj.data?.product?.vendor?.email
                    } else {
                        getString(R.string.dang_cap_nhat)
                    }
                } else {
                    tvSubVendor.visibility = View.GONE
                    layoutVendor.visibility = View.GONE
                }
            } else {
                tvSubVendor.visibility = View.GONE
                layoutVendor.visibility = View.GONE
            }

//      check nha phan phoi
            if (obj.data?.show_distributor == 1) {
                if (obj.data?.show_distributor != null) {
                    if (isVietNamLanguage == false) {
                        tvSubDistributor.text = "Distributor"
                    } else {
                        tvSubDistributor.text = "Nhà phân phối"
                    }

                    tvSubDistributor.visibility = View.VISIBLE
                    layoutDistributor.visibility = View.VISIBLE

                    itemDistributor = obj.data?.distributor
                    idDistributor = obj.data?.distributor?.id

                    tvNameDistributor.text = if (!obj.data?.distributor?.name.isNullOrEmpty()) {
                        obj.data?.distributor?.name
                    } else {
                        getString(R.string.dang_cap_nhat)
                    }

                    tvAddressDistributor.text = if (!obj.data?.distributor?.address.isNullOrEmpty()) {
                        if (!obj.data?.distributor?.district.isNullOrEmpty()) {
                            if (!obj.data?.distributor?.city.isNullOrEmpty()) {
                                obj.data?.distributor?.address + ", " + obj.data?.distributor?.district + ", " + obj.data?.distributor?.city
                            } else {
                                obj.data?.distributor?.address + ", " + obj.data?.distributor?.district
                            }
                        } else {
                            obj.data?.distributor?.address
                        }
                    } else {
                        getString(R.string.dang_cap_nhat)
                    }

                    tvWebsiteDistributor.text = if (!obj.data?.distributor?.website.isNullOrEmpty()) {
                        obj.data?.distributor?.website
                    } else {
                        getString(R.string.dang_cap_nhat)
                    }

                    tvPhoneDistributor.text = if (!obj.data?.distributor?.phone.isNullOrEmpty()) {
                        obj.data?.distributor?.phone
                    } else {
                        getString(R.string.dang_cap_nhat)
                    }

                    tvMailDistributor.text = if (!obj.data?.distributor?.email.isNullOrEmpty()) {
                        obj.data?.distributor?.email
                    } else {
                        getString(R.string.dang_cap_nhat)
                    }
                } else {
                    tvSubDistributor.visibility = View.GONE
                    layoutDistributor.visibility = View.GONE
                }
            } else {
                tvSubDistributor.visibility = View.GONE
                layoutDistributor.visibility = View.GONE
            }

//      Mo ta san pham
            if (!obj.data?.product?.infos.isNullOrEmpty()) {
                rcvInformation.visibility = View.VISIBLE
                initAdapterInformationProduct()
                adapterInformationProduct.setListData(obj.data?.product?.infos)
            } else {
                rcvInformation.visibility = View.GONE
            }

//      check MoreProductVerified
            if (obj.data?.product?.sku.isNullOrEmpty()) {
                presenter.onGetDataMoreProductVerified(obj.data?.distributor?.id)
                initRecyclerViewMoreProduct()

                if (isVietNamLanguage == false) {
                    appCompatTextView9.text = "Related Product"
                    tvMoreProductVerified.text = "View all"
                } else {
                    appCompatTextView9.text = "Sản phẩm liên quan"
                    tvMoreProductVerified.text = "Xem tất cả"
                }

                layoutSubProductVerified.visibility = View.VISIBLE
                rcvMoreProductVerified.visibility = View.VISIBLE
            } else {
                layoutSubProductVerified.visibility = View.GONE
                rcvMoreProductVerified.visibility = View.GONE
            }

//            qrScanViewModel.update(ICQrScan(presenter.code!!, null, null, 1, productId, "http://icheckcdn.net/images/480x480/$url.jpg", nameProduct, null, tvPriceProduct.text.toString(), tvPriceNoSale.text.toString(), seller_id, barcode, false, null, "6.1"))

//            qrScanViewModel.search(presenter.code!!).observe(this,
//                    androidx.lifecycle.Observer {
//                        Log.d("found", it.toString())
//                    })

            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.REFRESH_DATA_HISTORY_QR))
        }
    }

    private fun getSerialNumber(prefix: String?, number: Long? = 0): String {
        val length = number.toString().length // 2

        var numberSerial = ""

        if (length <= 6) {
            for (i in 0 until (6 - length)) {
                numberSerial += "0"
            }
        }

        return "$prefix-$numberSerial$number"
    }

    @SuppressLint("SetTextI18n")
    override fun onGetShopVariantSuccess(obj: ICListResponse<ICShopVariantStamp>) {
        if (!obj.rows.isNullOrEmpty()) {
            layoutShopVariant.visibility = View.VISIBLE

            idShopVariant = obj.rows[0].shop_id
            idVariant = obj.rows[0].id
            objProductShopVariant = ICShopVariant()
            objProductShopVariant = obj.rows[0].product
            objShopVariantStamp = obj.rows[0]

            if (obj.rows[0].shop?.is_online == true) {
                if (obj.rows[0].is_active == true) {
                    if (isVietNamLanguage == false) {
                        btnChat.text = "Chat now"
//                        btnAddToCart.text = "+ Cart"
                        tvBuyNow.text = "Buy now"
                    } else {
                        btnChat.text = "Chat ngay"
//                        btnAddToCart.text = "+ Giỏ hàng"
                        tvBuyNow.text = "Mua ngay"
                    }

                    layoutShopVariant.visibility = View.VISIBLE
                    bottomLayout.visibility = View.VISIBLE
                    tvAddToCartInDiemBan.visibility = View.GONE
                } else {
                    layoutShopVariant.visibility = View.GONE
                    bottomLayout.visibility = View.GONE
                    tvAddToCartInDiemBan.visibility = View.GONE
                }
            }

            val listService = mutableListOf<ICServiceShopVariant>()
//            if (obj.rows[0].shop?.is_online == true) {
//                listService.add(ICServiceShopVariant(0, R.drawable.ic_online_shop_18px, "Bán Online", "#2d9cdb", R.drawable.bg_corner_online_shop_variant))
//            }

            if (obj.rows[0].verified == "verified") {
                listService.add(ICServiceShopVariant(1, R.drawable.ic_verified_18px, "Đại lý chính hãng", "#4dbba6", R.drawable.bg_corner_offline_shop_variant))
            }

            if (obj.rows[0].shop?.is_offline == true) {
                listService.add(ICServiceShopVariant(2, R.drawable.ic_offline_shop_18px, "Mua tại cửa hàng", "#49aa2d", R.drawable.bg_corner_verified_shop_variant))
            }

            if (!listService.isNullOrEmpty()) {
                initAdapterService()
                adapterService.setListData(listService)
            }

            tvNameShopVariant.text = if (!obj.rows[0].shop?.name.isNullOrEmpty()) {
                obj.rows[0].shop?.name
            } else {
                getString(R.string.dang_cap_nhat)
            }

            if (obj.rows[0].shop?.distance != null) {
                if (obj.rows[0].shop?.distance?.value.toString().isNotEmpty()) {
                    km = TextHelper.getTextAfterDot(obj.rows[0].shop?.distance?.value)
                }
            }

            tvKmShopVariant.text = if (obj.rows[0].shop?.distance != null) {
                km + " " + obj.rows[0].shop?.distance?.unit
            } else {
                getString(R.string.dang_cap_nhat)
            }
            0
            tvRatingShopVariant.text = if (obj.rows[0].shop?.rating != null) {
                obj.rows[0].shop?.rating.toString() + "/" + "5.0"
            } else {
                getString(R.string.dang_cap_nhat)
            }

            tvAddressShopVariant.text = if (!obj.rows[0].shop?.address.isNullOrEmpty()) {
                obj.rows[0].shop?.address + ", " + obj.rows[0].shop?.ward?.name + ", " + obj.rows[0].shop?.district?.name + ", " + obj.rows[0].shop?.city?.name
            } else {
                getString(R.string.dang_cap_nhat)
            }

            if (obj.rows[0].sale_off == true) {
                if (obj.rows[0].special_price != null) {
                    if (obj.rows[0].special_price!! > 0L) {
                        tvPriceNoSale.text = TextHelper.formatMoneyPhay(obj.rows[0].price)
                        tvPriceNoSale.paintFlags = tvPriceNoSale.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    } else {
                        tvPriceNoSale.visibility = View.INVISIBLE
                    }
                } else {
                    tvPriceNoSale.visibility = View.INVISIBLE
                }
            } else {
                tvPriceNoSale.visibility = View.INVISIBLE
            }

            tvPriceSale.text = TextHelper.formatMoneyPhay(obj.rows[0].special_price) + "đ"

            if (tvPriceNoSale.text.toString().isNotEmpty()) {
//                qrScanViewModel.update(ICQrScan(presenter.code!!, null, null, 1, productId, "http://icheckcdn.net/images/480x480/$url.jpg", nameProduct, presenter.data, tvPriceProduct.text.toString(), tvPriceNoSale.text.toString(), seller_id, barcode, false, null, "v6.1"))
//                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.REFRESH_DATA_HISTORY_QR))
            }
        } else {
            layoutShopVariant.visibility = View.GONE
        }
    }

    override fun onGetBannerSuccess(list: MutableList<ICAds>) {
//        val itemView = ViewHelper.createListBannerHolder(layoutContent.context)
//        itemView.id = R.id.layoutContainer
//        layoutContent.addView(itemView, 2)
//
//        bannerViewPager = (itemView as ViewGroup).getChildAt(0) as HeightWrappingViewPager
//        bannerViewPager?.adapter = ListBannerAdapter(list, object : IBannerListener {
//            override fun onBannerClicked(ads: ICAds) {
//                AdsUtils.bannerClicked(this@DetailStampActivity, ads)
//            }
//
//            override fun onBannerSurveyClicked(ads: ICAds) {
//                AdsUtils.bannerSurveyClicked(this@DetailStampActivity, requestBannerSurvey, ads)
//            }
//        })
//
//        bannerViewPager?.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    }

    override fun onGetPopupSuccess(obj: ICAds) {
        AdsUtils.showAdsPopup(this@DetailStampActivity, obj)
    }

    override fun onGetShopVariantFail() {
        layoutShopVariant.visibility = View.GONE
    }

    override fun onGetConfigSuccess(obj: IC_Config_Error) {
        scrollViewError.visibility = View.VISIBLE
        bottomLayout.visibility = View.GONE
        if (!obj.data?.contacts.isNullOrEmpty()) {
            initAdapterConfigError()
            adapterConfigError.setListData(obj.data?.contacts)
        }
    }

    override fun onGetDataMoreProductVerifiedSuccess(products: MutableList<ICObjectListMoreProductVerified>) {
        adapterSuggestion.setListData(products)
    }

    private fun initScrollFab() {
        scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            when {
                scrollY < oldScrollY -> {
                    if (isShow) {
                        return@OnScrollChangeListener
                    }
                    isShow = true
                    textFab.animate()
                            .translationY(0f)
                            .setDuration(300)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator?) {
                                    super.onAnimationEnd(animation)
                                    textFab.visibility = View.VISIBLE
                                }
                            })
                }
                scrollY > oldScrollY -> {
                    if (!isShow) {
                        return@OnScrollChangeListener
                    }
                    isShow = false
                    textFab.animate().translationY(500f).duration = 300
                }
            }
        })
    }

    override fun onGetDataMoreProductVerifiedError(errorType: Int) {
        layoutSubProductVerified.visibility = View.GONE
        rcvMoreProductVerified.visibility = View.GONE
    }

    override fun onTryAgain() {
        presenter.onGetDataMoreProductVerified(idDistributor)
    }

    override fun onGetDataIntentError(errorType: Int) {
        layoutErrorClient.visibility = View.VISIBLE
        scrollViewError.visibility = View.GONE
        scrollView.visibility = View.GONE
        when (errorType) {
            Constant.ERROR_INTERNET -> {
                imgError.setImageResource(R.drawable.ic_error_network)
                tvMessageError.text = "Kết nối mạng của bạn có vấn đề. Vui lòng thử lại"
            }
            Constant.ERROR_UNKNOW -> {
                imgError.setImageResource(R.drawable.ic_error_request)
                tvMessageError.text = "Không thể truy cập. Vui lòng thử lại sau"
            }
            Constant.ERROR_EMPTY -> {
                imgError.setImageResource(R.drawable.ic_error_request)
                tvMessageError.text = "Không thể truy cập. Vui lòng thử lại sau"
            }
        }
    }

    override fun onAddToCartSuccess(type: Int) {
        if (type == 1) showShortSuccess(getString(R.string.them_vao_gio_hang_thanh_cong)) else startActivity<CartActivity>()
    }

    override fun showError(errorMessage: String) {
        showShortError(errorMessage)
    }

    override val mContext: Context
        get() = this

    override fun onShowLoading(isShow: Boolean) {

    }

    override fun onRequireLoginSuccess(requestCode: Int) {
        super.onRequireLoginSuccess(requestCode)

        when (requestCode) {
//            requestAddToCart -> {
//                if (idVariant != null) {
//                    presenter.addToCart(idVariant!!, objShopVariantStamp!!, 1, 1)
//                }
//            }

//            requestAddToCartShortToast -> {
//                if (idVariant != null) {
//                    presenter.addToCart(idVariant!!, objShopVariantStamp!!, 1, 2)
//                }
//            }

            requestGoToCart -> {
                startActivity<ShipActivity, Boolean>(Constant.CART, true)
            }

            requestRefreshData -> {
                getData()
            }
        }
    }

    override fun onRequireLoginCancel() {
        super.onRequireLoginCancel()
        onBackPressed()
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)

        when (event.type) {
            ICMessageEvent.Type.REFRESH_DATA -> {
                presenter.onGetDataDetailStampSecond()
            }
//            ICMessageEvent.Type.ON_LOG_IN -> {
//                onRequireLoginSuccess(requestRequireLogin)
//            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestGpsPermission) {
            if (PermissionHelper.checkResult(grantResults)) {
                if (NetworkHelper.isOpenedGPS(this)) {
                    llAcceptPermission.visibility = View.GONE
                    getData()
                } else {
                    if (NetworkHelper.checkGPS(this@DetailStampActivity, getString(R.string.vui_long_bat_gpa_de_ung_dung_tim_duoc_vi_tri_cua_ban), requestGps)) {
                        getData()
                        return
                    }
                }
            } else {
                llAcceptPermission.visibility = View.VISIBLE
            }
        }

        if (requestCode == requestPhone) {
            if (PermissionHelper.checkResult(grantResults)) {
                ContactUtils.callFast(this@DetailStampActivity, tvPhoneDistributor.text.toString())
            } else {
                showShortError(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
        listActivities.clear()
        isVietNamLanguage = null
        mSerial = null
    }

    private var requestLogin = 101
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == requestLogin) {
            if (resultCode == Activity.RESULT_OK) {
                onRequireLoginSuccess(requestRequireLogin)
            } else {
                onRequireLoginCancel()
            }
        }

        when (requestCode) {
            requestGift -> {
//                getGiftLoyalty()
            }
            requestGps -> {
                if (NetworkHelper.isOpenedGPS(this)) {
                    getData()
                }
            }
            requestBannerSurvey -> {
                if (bannerViewPager != null) {
                    if (resultCode == Activity.RESULT_CANCELED) {
                        JsonHelper.parseJson(data?.getStringExtra(Constant.DATA_1), ICAds::class.java)?.let {
                            val adapter = bannerViewPager!!.adapter as ListBannerAdapter
                            adapter.updateBannerSurvey(it)
                        }
                    } else if (resultCode == Activity.RESULT_OK) {
                        val surveyID = data?.getLongExtra(Constant.DATA_1, -1)

                        if (surveyID != null && surveyID != -1L) {
                            val adapter = bannerViewPager!!.adapter as ListBannerAdapter
                            adapter.removeBannerSurvey(surveyID)

                            if (adapter.getListData.isEmpty()) {
                                layoutContent.findViewById<View>(R.id.layoutContainer)?.let {
                                    layoutContent.removeView(it)
                                }
                            }
                        }
                    }
                }
            }
            CampaignLoyaltyHelper.REQUEST_GET_GIFT -> {
                CampaignLoyaltyHelper.getCampaign(this@DetailStampActivity, viewModel.code, this@DetailStampActivity, this@DetailStampActivity)
            }
            CampaignLoyaltyHelper.REQUEST_CHECK_CODE -> {
                obj?.let {
                    CampaignLoyaltyHelper.checkCodeLoyalty(
                            this@DetailStampActivity, it, codeInput, viewModel.code,
                            this@DetailStampActivity, this@DetailStampActivity)
                }
            }
        }
    }

    override fun showDialogLogin(data: ICKLoyalty, code: String?) {
        this@DetailStampActivity.obj = data
        if (code.isNullOrEmpty()) {
            LoyaltySdk.showDialogLogin<IckLoginActivity, Int>(this@DetailStampActivity, "requestCode", 1, CampaignLoyaltyHelper.REQUEST_GET_GIFT, data)
        } else {
            codeInput = code
            LoyaltySdk.showDialogLogin<IckLoginActivity, Int>(this@DetailStampActivity, "requestCode", 1, CampaignLoyaltyHelper.REQUEST_CHECK_CODE, data, codeInput)
        }
    }

    override fun onClick(obj: Any) {
        this@DetailStampActivity.layoutLoyalty.beVisible()
        if (obj is ICKLoyalty) {
            loyaltyDescription.text = obj.name
            WidgetUtils.loadImageUrl(bannerLoyalty, obj.image?.original)
            btnCheckCode.setOnClickListener {
                btnCheckCode.isEnabled = false
                CampaignLoyaltyHelper.checkCodeLoyalty(this@DetailStampActivity, obj, edittextCode.text?.trim().toString(), viewModel.code, this@DetailStampActivity, this@DetailStampActivity)
                Handler().postDelayed({
                    btnCheckCode.isEnabled = true
                }, 3000)
            }
            bannerLoyalty.setOnClickListener {
                startActivity(Intent(this@DetailStampActivity, UrlGiftDetailActivity::class.java).apply {
                    putExtra(ConstantsLoyalty.DATA_1, obj.id)
                    putExtra(ConstantsLoyalty.DATA_2, viewModel.code)
                    putExtra(ConstantsLoyalty.DATA_3, obj)
                })
            }
        }
    }

    override fun onRemoveHolderInput() {
        this@DetailStampActivity.layoutLoyalty.beGone()
    }


}