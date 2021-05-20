package vn.icheck.android.screen.user.detail_stamp_v6_1.home

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.view.ViewAnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_detail_stamp.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.*
import vn.icheck.android.ichecklibs.util.visibleOrInvisible
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.listener.IClickListener
import vn.icheck.android.loyalty.helper.CampaignLoyaltyHelper
import vn.icheck.android.loyalty.model.ICKLoyalty
import vn.icheck.android.loyalty.screen.url_gift_detail.UrlGiftDetailActivity
import vn.icheck.android.loyalty.sdk.LoyaltySdk
import vn.icheck.android.network.base.*
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.detail_stamp_v6_1.*
import vn.icheck.android.screen.account.icklogin.IckLoginActivity
import vn.icheck.android.screen.user.cart.CartActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.contact_support.ContactSupportActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.adapter.*
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.presenter.DetailStampPresenter
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.view.IDetailStampView
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.viewmodel.ICDetailStampViewModel
import vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.UpdateInformationFirstActivity
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.screen.user.shipping.ship.ShipActivity
import java.util.*
import java.util.regex.Pattern
import kotlin.math.hypot

@AndroidEntryPoint
class DetailStampActivity : BaseActivityMVVM(), IDetailStampView, IRecyclerViewCallback, IClickListener, CampaignLoyaltyHelper.ILoginListener, CampaignLoyaltyHelper.IRemoveHolderInputLoyaltyListener {
    private val viewModel: ICDetailStampViewModel by viewModels()
    private val presenter = DetailStampPresenter(this)

    private val adapter = ICDetailStampAdapter(this)

    companion object {
        val listActivities = mutableListOf<AppCompatActivity>()
        var isVietNamLanguage: Boolean? = null
        var mSerial: String? = null
    }

    private var disposable: Disposable? = null
    var id = -1L

    private var isShow = true
    private var numberPage = 0
    private var idDistributor: Long? = null
    private var productId: Long? = null
    private var objVariant: ICVariantProductStampV6_1.ICVariant.ICObjectVariant? = null
    private var verfiedSerial: String? = null

    private var seller_id: Long? = null
    private var barcode: String? = null

    private var itemDistributor: ICObjectDistributor? = null
    private var objGuarantee: ICObjectGuarantee? = null

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var lat: String? = null
    private var lng: String? = null

    private val requestGpsPermission = 39
    private val requestGps = 2
    private val requestGoToCart = 4
    private val requestRefreshData = 5
    private var requestRequireLogin = 0

    var type = ""
    var banner = ""
    var description = ""
    var targetType = ""

    //    private var nameProduct: String? = null
    private var url: String? = null

    //    private var bannerAdapter: BannerAdapter? = null
//    private lateinit var adapterConfigError: ICStampContactAdapter

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_stamp)

        setupToolbar()
        setupView()
        setupRecyclerView()
        setupListener()
//        setupViewModel()
        getIntentData()
    }

    private fun setupToolbar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }

        imgAction.apply {
            setImageResource(R.drawable.ic_more_horiz_24px)

            setOnClickListener {
                imgAction.isClickable = false
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
                                imgAction.isClickable = true
                            }
                        })
                        anim.duration = 1000
                        anim.start()
                    } else {
                        layoutChatAdmin.visibility = View.INVISIBLE
                        imgAction.isClickable = true
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val anim = ViewAnimationUtils.createCircularReveal(layoutChatAdmin, cx, cy, 0f, finalRadius)
                        layoutChatAdmin.visibility = View.VISIBLE
                        anim.addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                super.onAnimationEnd(animation)
                                imgAction.isClickable = true
                            }
                        })
                        anim.duration = 1000
                        anim.start()
                    } else {
                        layoutChatAdmin.visibility = View.VISIBLE
                        imgAction.isClickable = true
                    }
                }
            }
        }

        txtTitle.setText(R.string.xac_thuc_san_pham)
    }

    private fun setupView() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        isVietNamLanguage = Locale.getDefault().displayLanguage.toLowerCase(Locale.getDefault()) == "vi"

        runOnUiThread {
            if (isVietNamLanguage == false) {
                txtTitle.text = "Verified product"
                tvChatWithAdmin.text = "Contact to Admin Icheck"
//                btnAgainError.text = "Try Again"
//                btnRequestPermission.text = "Turn on GPS"
                textFab.text = "Update customer information"
            } else {
                txtTitle.text = "Xác thực sản phẩm"
                tvChatWithAdmin.text = "Liên hệ Admin iCheck"
//                btnAgainError.text = "Thử Lại"
//                btnRequestPermission.text = "Bật GPS"
                textFab.setText(R.string.cap_nhat_thong_tin_khach_hang)
            }
        }
    }

    private fun setupRecyclerView() {
        val mLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.apply {
            layoutManager = mLayoutManager

            adapter = this@DetailStampActivity.adapter.apply {
                enableLoadMore(false)
                setCampaignListener({
                    this@DetailStampActivity.apply {
                        startActivity(Intent(this, UrlGiftDetailActivity::class.java).apply {
                            putExtra(ConstantsLoyalty.DATA_1, it.id)
                            putExtra(ConstantsLoyalty.DATA_2, viewModel.barcode)
                            putExtra(ConstantsLoyalty.DATA_3, it)
                        })
                    }
                }, {
                    this@DetailStampActivity.apply {
                        CampaignLoyaltyHelper.checkCodeLoyalty(this, it, it.content
                                ?: "", viewModel.barcode, this@DetailStampActivity, this@DetailStampActivity)
                    }
                })
            }

            var firstVisibleInListview = 0
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (textFab.isVisible) {
                        val currentFirstVisible = mLayoutManager.findFirstVisibleItemPosition()

                        if (currentFirstVisible > firstVisibleInListview) { // scroll up
                            if (!isShow) {
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
                        } else { // scroll down
                            if (isShow) {
                                isShow = false
                                textFab.animate().translationY(500f).duration = 300
                            }
                        }

                        firstVisibleInListview = currentFirstVisible
                    }
                }
            })
        }
    }

    private fun setupListener() {
        layoutChatAdmin.setOnClickListener {
            startActivity<ContactSupportActivity>()
            layoutChatAdmin.visibility = View.INVISIBLE
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

        tvHotlineBussiness.setOnClickListener {
            Constant.callPhone(itemDistributor?.phone)
        }

        tvEmailBussiness.setOnClickListener {
            if (!itemDistributor?.email.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:" + itemDistributor?.email)
                startActivity(Intent.createChooser(intent, "Send To"))
            }
        }
    }

//    private fun setupViewModel() {
//        viewModel = ViewModelProviders.of(this).get(ICDetailStampViewModel::class.java)
//    }

    private fun getIntentData() {
        viewModel.getData(intent)

        if (viewModel.barcode.isEmpty()) {
            DialogHelper.showNotification(this@DetailStampActivity, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                override fun onDone() {
                    onBackPressed()
                }
            })
        } else {
            getLocation()
        }
    }

    private var isGetLocationSuccess = false
    private fun getLocation() {
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
                    isGetLocationSuccess = true
                    lat = lastLocation.latitude.toString()
                    lng = lastLocation.longitude.toString()
                }

                mFusedLocationClient?.removeLocationUpdates(this)
                getStampDetailV61()
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability?) {
                super.onLocationAvailability(locationAvailability)
                if (locationAvailability?.isLocationAvailable != true) {
                    getStampDetailV61()
                }
            }
        }, Looper.getMainLooper())
    }

    private val checkGpsPermission: Boolean
        get() {
            val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            if (!PermissionHelper.checkPermission(this, permission, requestGpsPermission)) {
                adapter.setError(R.drawable.ic_location_permission_history, getString(R.string.de_hien_thi_du_lieu_cua_hang_vui_long_bat_gps), R.string.bat_gps)
                return false
            }

            if (!NetworkHelper.checkGPS(this@DetailStampActivity, getString(R.string.vui_long_bat_gpa_de_ung_dung_tim_duoc_vi_tri_cua_ban), requestGps)) {
                adapter.setError(R.drawable.ic_location_permission_history, getString(R.string.de_hien_thi_du_lieu_cua_hang_vui_long_bat_gps), R.string.bat_vi_tri)
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
                    val mData = it.data?.data
                    if (mData != null && !mData.widgets.isNullOrEmpty()) {
                        val listData = mutableListOf<ICLayout>()

                        for (widget in it.data!!.data!!.widgets!!) {
                            when (widget.name) {
                                "IMAGE_PRODUCT" -> {
                                    if (!widget.data?.atts.isNullOrEmpty()) {
                                        listData.add(ICLayout().apply {
                                            viewType = ICViewTypes.PRODUCT_IMAGE_TYPE
                                            data = widget.data!!.atts!!
                                        })
                                    }
                                }
                                "PRODUCT" -> {
                                    if (widget.data != null) {
                                        listData.add(ICLayout().apply {
                                            viewType = ICViewTypes.PRODUCT_TYPE
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
                                "STAMP_INFO" -> {
                                    if (!widget.data?.serial.isNullOrEmpty()) {
                                        listData.add(ICLayout().apply {
                                            viewType = ICViewTypes.STAMP_INFO_TYPE
                                            data = widget.data!!.serial
                                        })
                                    }
                                }
                                "SCAN_INFO" -> {
                                    if (widget.data != null) {
                                        listData.add(ICLayout().apply {
                                            viewType = ICViewTypes.SCAN_INFO_TYPE
                                            data = widget.data
                                        })
                                    }
                                }
                                "GUARANTEE" -> {
                                    if (widget.data != null) {
                                        listData.add(ICLayout().apply {
                                            viewType = ICViewTypes.GUARANTEE_INFO_TYPE
                                            data = widget.data
                                        })
                                    }
                                }
                                "LAST_GUARANTEE" -> {
                                    if (widget.data != null) {
                                        listData.add(ICLayout().apply {
                                            viewType = ICViewTypes.LAST_GUARANTEE_INFO_TYPE
                                            data = widget.data!!.apply {
                                                if (serial.isNullOrEmpty()) {
                                                    serial = it.data!!.data!!.serial
                                                }
                                            }
                                        })
                                    }
                                }
                                "VENDOR" -> {
                                    if (widget.data != null) {
                                        listData.add(ICLayout().apply {
                                            viewType = ICViewTypes.VENDOR_TYPE
                                            data = widget.data!!.apply {
                                                title = getString(R.string.nha_san_xuat)
                                                icon = R.drawable.ic_verified_24px
                                                background = R.color.colorPrimary
                                            }
                                        })
                                    }
                                }
                                "DISTRIBUTOR" -> {
                                    if (widget.data != null) {
                                        listData.add(ICLayout().apply {
                                            viewType = ICViewTypes.VENDOR_TYPE
                                            data = widget.data!!.apply {
                                                title = getString(R.string.nha_phan_phoi)
                                                icon = R.drawable.ic_verified_24px
                                                background = R.color.colorPrimary
                                            }
                                        })
                                    }
                                }
                                "PRODUCT_INFO" -> {
                                    if (!widget.data?.infors.isNullOrEmpty()) {
                                        for (info in widget.data!!.infors!!) {
                                            listData.add(ICLayout().apply {
                                                viewType = ICViewTypes.PRODUCT_INFO_TYPE
                                                data = info
                                            })
                                        }
                                    }
                                }
                                "PRODUCT_LINK" -> {
                                    if (!widget.data?.productLinks.isNullOrEmpty()) {
                                        listData.add(ICLayout().apply {
                                            viewType = ICViewTypes.PRODUCT_ECCOMMERCE_TYPE
                                            data = widget.data!!.productLinks
                                        })
                                    }
                                }
                            }
                        }

                        adapter.setListData(listData)

                        getCampaign()

                        textFab.visibleOrInvisible(mData.canUpdate == true)
                        if (mData.forceUpdate == true) {
                            // Todo
                        }
                    } else {
                        textFab.visibleOrInvisible(false)

                        if (it.data?.message.isNullOrEmpty()) {
                            adapter.setError(R.drawable.ic_error_request, ICheckApplication.getError(it.data?.message), null)
                        } else {
                            getStampConfig(it.data!!.message!!)
                        }
                    }
                }
            }
        })
    }

    private fun getCampaign() {
        CampaignLoyaltyHelper.getCampaign(this, viewModel.barcode, this, this)
    }

    private fun getStampConfig(message: String) {
        viewModel.getStampConfig().observe(this, {
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
                    if (it.data?.data != null) {
                        adapter.setListData(mutableListOf(ICLayout().apply {
                            viewType = ICViewTypes.ERROR_STAMP_TYPE
                            data = it.data!!.data!!.apply {
                                errorMessage = message
                            }
                        }))
                    } else {
                        adapter.setError(R.drawable.ic_error_request, ICheckApplication.getError(it.message), null)
                    }
                }
            }
        })
    }

    override fun itemPagerClickToVideo(urlVideo: String?) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlVideo))
        startActivity(intent)
    }

    override fun onItemClick(item: ICObjectListMoreProductVerified) {
        if (!item.sku.isNullOrEmpty()) {
            IckProductDetailActivity.start(this, item.sku!!)
        }
    }

    private fun initAdapterConfigError() {
//        adapterConfigError = ICStampContactAdapter()
//        rcvConfigError.layoutManager = LinearLayoutManager(this)
//        rcvConfigError.adapter = adapterConfigError
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
//            layoutErrorClient.visibility = View.GONE

        //check Error detail Stamp
        if (obj.data?.scan_message?.redirect_warning == true) {
            itemDistributor = obj.data?.distributor
//                scrollView.visibility = View.GONE
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

//            if (!obj.data?.message?.message.isNullOrEmpty()) {
//                presenter.getConfigError()
//                tvMessageStampError.text = "CẢNH BÁO!" + "\n" + obj.data?.message?.message
//            } else {
////                scrollView.visibility = View.VISIBLE
//            }

        idDistributor = obj.data?.distributor?.id

//      set image local cho tab history qrCode
        url = obj.data?.product?.image
        productId = obj.data?.product?.id
        objVariant = obj.data?.guarantee?.last_guarantee?.variant

        if (!obj.data?.barcode.isNullOrEmpty() && obj.data?.seller_id != null) {
            seller_id = obj.data?.seller_id!!
            barcode = obj.data?.barcode
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
    }

//    override fun onGetConfigSuccess(obj: IC_Config_Error) {
//        scrollViewError.visibility = View.VISIBLE
//        if (!obj.data?.contacts.isNullOrEmpty()) {
//            initAdapterConfigError()
//            adapterConfigError.setListData(obj.data?.contacts!!)
//        }
//    }

//    override fun onGetDataIntentError(errorType: Int) {
//        layoutErrorClient.visibility = View.VISIBLE
//        scrollViewError.visibility = View.GONE
////        scrollView.visibility = View.GONE
//        when (errorType) {
//            Constant.ERROR_INTERNET -> {
//                imgError.setImageResource(R.drawable.ic_error_network)
//                tvMessageError.text = "Kết nối mạng của bạn có vấn đề. Vui lòng thử lại"
//            }
//            Constant.ERROR_UNKNOW -> {
//                imgError.setImageResource(R.drawable.ic_error_request)
//                tvMessageError.text = "Không thể truy cập. Vui lòng thử lại sau"
//            }
//            Constant.ERROR_EMPTY -> {
//                imgError.setImageResource(R.drawable.ic_error_request)
//                tvMessageError.text = "Không thể truy cập. Vui lòng thử lại sau"
//            }
//        }
//    }

    override fun onAddToCartSuccess(type: Int) {
        if (type == 1) showShortSuccess(getString(R.string.them_vao_gio_hang_thanh_cong)) else startActivity<CartActivity>()
    }

    override fun onLoadMore() {

    }

    override fun onMessageClicked() {
        getLocation()
    }

    override fun onClick(obj: Any) {
        if (obj is ICKLoyalty) {
            adapter.addCampaign(ICLayout().apply {
                viewType = ICViewTypes.CAMPAIGN_TYPE
                data = obj
            })
        }
    }

    override fun showDialogLogin(data: ICKLoyalty, code: String?) {
        this@DetailStampActivity.viewModel.loyaltyObj = data

        if (code.isNullOrEmpty()) {
            LoyaltySdk.showDialogLogin<IckLoginActivity, Int>(this@DetailStampActivity, "requestCode", 1, CampaignLoyaltyHelper.REQUEST_GET_GIFT, data)
        } else {
            viewModel.codeInput = code
            LoyaltySdk.showDialogLogin<IckLoginActivity, Int>(this@DetailStampActivity, "requestCode", 1, CampaignLoyaltyHelper.REQUEST_CHECK_CODE, data, viewModel.codeInput)
        }
    }

    override fun onRemoveHolderInput() {
        adapter.removeCampaign()
    }

    override fun onRequireLoginSuccess(requestCode: Int) {
        super.onRequireLoginSuccess(requestCode)

        when (requestCode) {
            requestGoToCart -> {
                startActivity<ShipActivity, Boolean>(Constant.CART, true)
            }
            requestRefreshData -> {
                getLocation()
            }
        }
    }

    override fun onRequireLoginCancel() {
        super.onRequireLoginCancel()
        onBackPressed()
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)

        if (event.type == ICMessageEvent.Type.REFRESH_DATA) {
            getStampDetailV61()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestGpsPermission) {
            if (PermissionHelper.checkResult(grantResults)) {
                if (NetworkHelper.isOpenedGPS(this)) {
                    getLocation()
                } else {
                    if (NetworkHelper.checkGPS(this@DetailStampActivity, getString(R.string.vui_long_bat_gpa_de_ung_dung_tim_duoc_vi_tri_cua_ban), requestGps)) {
                        getLocation()
                        return
                    }
                }
            } else {
                adapter.setError(R.drawable.ic_location_permission_history, getString(R.string.de_hien_thi_du_lieu_cua_hang_vui_long_bat_gps), R.string.bat_gps)
            }
        }
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
            requestGps -> {
                if (NetworkHelper.isOpenedGPS(this)) {
                    getLocation()
                }
            }
            CampaignLoyaltyHelper.REQUEST_GET_GIFT -> {
                getCampaign()
            }
            CampaignLoyaltyHelper.REQUEST_CHECK_CODE -> {
                viewModel.loyaltyObj?.let {
                    CampaignLoyaltyHelper.checkCodeLoyalty(this@DetailStampActivity, it, viewModel.codeInput, viewModel.barcode, this@DetailStampActivity, this@DetailStampActivity)
                }
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
}