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
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewAnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import io.reactivex.disposables.Disposable
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.ActivityDetailStampBinding
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
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.view.IDetailStampView
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.viewmodel.ICDetailStampViewModel
import vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.UpdateInformationFirstActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.verified_phone.VerifiedPhoneActivity
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.screen.user.shipping.ship.ShipActivity
import vn.icheck.android.util.kotlin.ActivityUtils
import java.util.*
import java.util.regex.Pattern
import kotlin.math.hypot

class StampDetailActivity : BaseActivityMVVM(), IDetailStampView, IRecyclerViewCallback, IClickListener, CampaignLoyaltyHelper.ILoginListener, CampaignLoyaltyHelper.IRemoveHolderInputLoyaltyListener {
    private lateinit var binding: ActivityDetailStampBinding
    private val viewModel by viewModels<ICDetailStampViewModel>()

    private val adapter = ICStampDetailAdapter(this)

    companion object {
        val listActivities = mutableListOf<AppCompatActivity>()
        var isVietNamLanguage: Boolean? = true
    }

    private var disposable: Disposable? = null

    private var isShow = true
    private var numberPage = 0
    private var distributorId: Long? = null
    private var productId: Long? = null
    private var serial: String? = null

    private var itemDistributor: ICObjectDistributor? = null

    private var mFusedLocationClient: FusedLocationProviderClient? = null

    private val requestGpsPermission = 39
    private val requestGoToCart = 4
    private val requestRefreshData = 5

    private var requestRequireLogin = 0
    private val requestGps = 2
    private val requestUpdateOrDestroy = 1

    private var guarantee: ICWidgetData? = null
    private var isExistLastGuarantee = false

    //    private var nameProduct: String? = null
//    private var url: String? = null

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStampBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupToolbar()
        setupRecyclerView()
        setupListener()
        getIntentData()
    }

    private fun setupView() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

//        isVietNamLanguage = Locale.getDefault().displayLanguage.toLowerCase(Locale.getDefault()) == "vi"

        runOnUiThread {
            if (isVietNamLanguage == false) {
                binding.layoutToolbar.txtTitle.text = "Verified product"
                binding.tvChatWithAdmin.text = "Contact to Admin Icheck"
//                btnAgainError.text = "Try Again"
//                btnRequestPermission.text = "Turn on GPS"
                binding.textFab.text = "Update customer information"
            } else {
                binding.layoutToolbar.txtTitle.text = "Xác thực sản phẩm"
                binding.tvChatWithAdmin.text = "Liên hệ Admin iCheck"
//                btnAgainError.text = "Thử Lại"
//                btnRequestPermission.text = "Bật GPS"
                binding.textFab.setText(R.string.cap_nhat_thong_tin_khach_hang)
            }
        }
    }

    private fun setupToolbar() {
        binding.layoutToolbar.imgBack.setOnClickListener {
            onBackPressed()
        }

        binding.layoutToolbar.imgAction.apply {
            setImageResource(R.drawable.ic_more_horiz_24px)

            setOnClickListener {
                binding.layoutToolbar.imgAction.isClickable = false
                val cx: Int = binding.layoutChatAdmin.measuredWidth * 2
                val cy: Int = binding.layoutChatAdmin.measuredHeight / 2
                val finalRadius = hypot(binding.layoutChatAdmin.width * 2.toDouble(), binding.layoutChatAdmin.height.toDouble()).toFloat()

                if (binding.layoutChatAdmin.visibility == View.VISIBLE) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val anim = ViewAnimationUtils.createCircularReveal(binding.layoutChatAdmin, cx, cy, finalRadius, 0f)
                        anim.addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                super.onAnimationEnd(animation)
                                binding.layoutChatAdmin.visibility = View.INVISIBLE
                                binding.layoutToolbar.imgAction.isClickable = true
                            }
                        })
                        anim.duration = 1000
                        anim.start()
                    } else {
                        binding.layoutChatAdmin.visibility = View.INVISIBLE
                        binding.layoutToolbar.imgAction.isClickable = true
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val anim = ViewAnimationUtils.createCircularReveal(binding.layoutChatAdmin, cx, cy, 0f, finalRadius)
                        binding.layoutChatAdmin.visibility = View.VISIBLE
                        anim.addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                super.onAnimationEnd(animation)
                                binding.layoutToolbar.imgAction.isClickable = true
                            }
                        })
                        anim.duration = 1000
                        anim.start()
                    } else {
                        binding.layoutChatAdmin.visibility = View.VISIBLE
                        binding.layoutToolbar.imgAction.isClickable = true
                    }
                }
            }
        }

        binding.layoutToolbar.txtTitle.setText(R.string.xac_thuc_san_pham)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@StampDetailActivity, LinearLayoutManager.VERTICAL, false)

            adapter = this@StampDetailActivity.adapter.apply {
                enableLoadMore(false)
                setCampaignListener({
                    this@StampDetailActivity.apply {
                        startActivity(Intent(this, UrlGiftDetailActivity::class.java).apply {
                            putExtra(ConstantsLoyalty.DATA_1, it.id)
                            putExtra(ConstantsLoyalty.DATA_2, viewModel.barcode)
                            putExtra(ConstantsLoyalty.DATA_3, it)
                        })
                    }
                }, {
                    this@StampDetailActivity.apply {
                        CampaignLoyaltyHelper.checkCodeLoyalty(this, it, it.content
                                ?: "", viewModel.barcode, this@StampDetailActivity, this@StampDetailActivity)
                    }
                })
            }

            var firstVisibleInListview = 0
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (binding.textFab.isVisible) {
                        val currentFirstVisible = computeVerticalScrollOffset()

                        if (currentFirstVisible >= firstVisibleInListview) { // scroll up
                            if (!isShow) {
                                isShow = true
                                binding.textFab.animate()
                                        .translationY(0f)
                                        .setDuration(300)
                                        .setListener(object : AnimatorListenerAdapter() {
                                            override fun onAnimationEnd(animation: Animator?) {
                                                super.onAnimationEnd(animation)
                                                binding.textFab.visibility = View.VISIBLE
                                            }
                                        })
                            }
                        } else { // scroll down
                            if (isShow) {
                                isShow = false
                                binding.textFab.animate().translationY(500f).duration = 300
                            }
                        }

                        firstVisibleInListview = currentFirstVisible
                    }
                }
            })
        }
    }

    private fun setupListener() {
        binding.layoutChatAdmin.setOnClickListener {
            startActivity<ContactSupportActivity>()
            binding.layoutChatAdmin.visibility = View.INVISIBLE
        }

        binding.textFab.setOnClickListener {
            if (guarantee != null) {
                if (guarantee?.customerId != null) {
                    val intent = Intent(this, VerifiedPhoneActivity::class.java)
                    intent.putExtra(Constant.DATA_1, serial)
                    intent.putExtra(Constant.DATA_2, distributorId)
//                    intent.putExtra(Constant.DATA_3, productCode)
                    intent.putExtra(Constant.DATA_4, productId)
//                    intent.putExtra(Constant.DATA_5, objVariant)
                    intent.putExtra(Constant.DATA_8, viewModel.barcode)
                    ActivityUtils.startActivity(this, intent)
                } else {
                    val intent = Intent(this, UpdateInformationFirstActivity::class.java)
                    intent.putExtra(Constant.DATA_1, 2)
                    intent.putExtra(Constant.DATA_2, distributorId)
//                intent.putExtra(Constant.DATA_4, productCode)
                    intent.putExtra(Constant.DATA_5, serial)
                    intent.putExtra(Constant.DATA_6, productId)
//                    intent.putExtra(Constant.DATA_7, objVariant)
                    intent.putExtra(Constant.DATA_8, viewModel.barcode)
                    ActivityUtils.startActivity(this, intent)
                }
            } else {
                val intent = Intent(this, UpdateInformationFirstActivity::class.java)
                intent.putExtra(Constant.DATA_1, 2)
                intent.putExtra(Constant.DATA_2, distributorId)
//                intent.putExtra(Constant.DATA_4, productCode)
                intent.putExtra(Constant.DATA_5, serial)
                intent.putExtra(Constant.DATA_6, productId)
//                intent.putExtra(Constant.DATA_7, objVariant)
                intent.putExtra(Constant.DATA_8, viewModel.barcode)
                ActivityUtils.startActivity(this, intent)
            }
        }

        binding.tvHotlineBussiness.setOnClickListener {
            Constant.callPhone(itemDistributor?.phone)
        }

        binding.tvEmailBussiness.setOnClickListener {
            if (!itemDistributor?.email.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:" + itemDistributor?.email)
                startActivity(Intent.createChooser(intent, "Send To"))
            }
        }
    }

    private fun getIntentData() {
        viewModel.getData(intent)

        if (viewModel.barcode.isEmpty()) {
            DialogHelper.showNotification(this@StampDetailActivity, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
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

        DialogHelper.showLoading(this)

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
                    viewModel.lat = lastLocation.latitude
                    viewModel.lng = lastLocation.longitude
                }

                mFusedLocationClient?.removeLocationUpdates(this)
                getStampDetail()
//                presenter.onGetDataDetailStamp(viewModel.barcode, viewModel.lat, viewModel.lng)
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability?) {
                super.onLocationAvailability(locationAvailability)
                if (locationAvailability?.isLocationAvailable != true) {
                    getStampDetail()
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

            if (!NetworkHelper.checkGPS(this@StampDetailActivity, getString(R.string.vui_long_bat_gpa_de_ung_dung_tim_duoc_vi_tri_cua_ban), requestGps)) {
                adapter.setError(R.drawable.ic_location_permission_history, getString(R.string.de_hien_thi_du_lieu_cua_hang_vui_long_bat_gps), R.string.bat_vi_tri)
                return false
            }

            return true
        }

    private fun getStampDetail() {
        viewModel.getStampDetailV61().observe(this, {
            when (it.status) {
                Status.LOADING -> {
                    if (it.message.isNullOrEmpty()) {
                        DialogHelper.showLoading(this@StampDetailActivity)
                    } else {
                        DialogHelper.closeLoading(this@StampDetailActivity)
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

                    if (mData != null) {
                        if (!mData.widgets.isNullOrEmpty()) {
                            val listData = mutableListOf<ICLayout>()

                            for (widget in it.data!!.data!!.widgets!!) {
                                distributorId = mData.distributorId
                                serial = mData.serial

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
                                            productId = widget.data?.id
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
                                            guarantee = widget.data
                                            if (widget.data?.guaranteeDays != null ||
                                                    widget.data?.expireDate != null ||
                                                    widget.data?.dayRemaining != null ||
                                                    widget.data?.activeDate != null) {
                                                listData.add(ICLayout().apply {
                                                    viewType = ICViewTypes.GUARANTEE_INFO_TYPE
                                                    data = widget.data
                                                })
                                            }
                                        }
                                    }
                                    "LAST_GUARANTEE" -> {
                                        if (widget.data != null) {
                                            isExistLastGuarantee = true
                                            if (widget.data?.createTime != null ||
                                                    widget.data?.storeName != null ||
                                                    widget.data?.status != null ||
                                                    widget.data?.state != null) {
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
                                    }
                                    "VENDOR" -> {
                                        if (widget.data != null) {
                                            listData.add(ICLayout().apply {
                                                viewType = ICViewTypes.VENDOR_TYPE
                                                data = widget.data!!.apply {
                                                    this.category = getString(R.string.nha_san_xuat)
                                                    this.icon = R.drawable.ic_verified_24px
                                                    this.background = R.color.colorPrimary
                                                }
                                            })
                                        }
                                    }
                                    "DISTRIBUTOR" -> {
                                        if (widget.data != null) {
                                            listData.add(ICLayout().apply {
                                                viewType = ICViewTypes.VENDOR_TYPE
                                                data = widget.data!!.apply {
                                                    this.category = getString(R.string.nha_phan_phoi)
                                                    this.icon = R.drawable.ic_verified_24px
                                                    this.background = R.color.colorPrimary
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

                            binding.textFab.visibleOrInvisible(mData.canUpdate == true)

                            if (mData.forceUpdate == true) {
                                Handler(Looper.getMainLooper()).postDelayed({
                                    if (guarantee != null) {
                                        val intent = Intent(this, UpdateInformationFirstActivity::class.java)
                                        intent.putExtra(Constant.DATA_1, 1)
                                        intent.putExtra(Constant.DATA_2, distributorId)
//                                intent.putExtra(Constant.DATA_4, product_code)
                                        intent.putExtra(Constant.DATA_5, serial)
                                        intent.putExtra(Constant.DATA_6, productId)
//                                intent.putExtra(Constant.DATA_7, objVariant)
                                        intent.putExtra(Constant.DATA_8, viewModel.barcode)
                                        startActivity(this, intent)
                                    } else {
                                        val intent = Intent(this, UpdateInformationFirstActivity::class.java)
                                        intent.putExtra(Constant.DATA_1, 2)
                                        intent.putExtra(Constant.DATA_2, distributorId)
//                                intent.putExtra(Constant.DATA_4, product_code)
                                        intent.putExtra(Constant.DATA_5, serial)
                                        intent.putExtra(Constant.DATA_6, productId)
//                                intent.putExtra(Constant.DATA_7, objVariant)
                                        intent.putExtra(Constant.DATA_8, viewModel.barcode)
                                        startActivity(this, intent)
                                    }
                                }, 500)
                            }
                        } else {
                            if (it.data?.data?.errorCode == 4) {
                                val intent = Intent(this, UpdateInformationFirstActivity::class.java)
                                intent.putExtra(Constant.DATA_1, 3)
                                intent.putExtra(Constant.DATA_5, serial)
                                intent.putExtra(Constant.DATA_8, viewModel.barcode)
                                startActivityForResult(this, intent, requestUpdateOrDestroy)
                            } else {
                                binding.textFab.visibleOrInvisible(false)

                                if (mData.errorMessage.isNullOrEmpty()) {
                                    adapter.setError(R.drawable.ic_error_request, ICheckApplication.getError(mData.errorMessage), null)
                                } else {
                                    getStampConfig(mData.errorMessage!!)
                                }
                            }
                        }
                    } else {
                        adapter.setError(R.drawable.ic_error_request, ICheckApplication.getError(it.message), null)
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
                        DialogHelper.showLoading(this@StampDetailActivity)
                    } else {
                        DialogHelper.closeLoading(this@StampDetailActivity)
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
            binding.layoutExceededScan.visibility = View.VISIBLE
            binding.tvMessageApollo.text = obj.data?.scan_message?.text
            binding.tvBussinessName.text = obj.data?.distributor?.name
            binding.tvAddressBussiness.text = "Địa chỉ: " + obj.data?.distributor?.address + ", " + obj.data?.distributor?.district + ", " + obj.data?.distributor?.city
            binding.tvHotlineBussiness.text = "Tổng đài: " + obj.data?.distributor?.phone
            binding.tvEmailBussiness.text = " - Email: " + obj.data?.distributor?.email
            return
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
    }

    override fun onAddToCartSuccess(type: Int) {
        if (type == 1) showShortSuccess(getString(R.string.them_vao_gio_hang_thanh_cong)) else startActivity<CartActivity>()
    }

    override fun onLoadMore() {

    }

    override fun onMessageClicked() {
        getLocation()
    }

    override fun onClick(obj: Any) {
        val position = binding.recyclerView.scrollY

        if (obj is ICKLoyalty) {
            adapter.addCampaign(ICLayout().apply {
                viewType = ICViewTypes.CAMPAIGN_TYPE
                data = obj
            })
        }

        if (position == 0) {
            (binding.recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(0, 0)
        }
    }

    override fun showDialogLogin(data: ICKLoyalty, code: String?) {
        this@StampDetailActivity.viewModel.loyaltyObj = data

        if (code.isNullOrEmpty()) {
            LoyaltySdk.showDialogLogin<IckLoginActivity, Int>(this@StampDetailActivity, "requestCode", 1, CampaignLoyaltyHelper.REQUEST_GET_GIFT, data)
        } else {
            viewModel.codeInput = code
            LoyaltySdk.showDialogLogin<IckLoginActivity, Int>(this@StampDetailActivity, "requestCode", 1, CampaignLoyaltyHelper.REQUEST_CHECK_CODE, data, viewModel.codeInput)
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
            getStampDetail()
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
                    if (NetworkHelper.checkGPS(this@StampDetailActivity, getString(R.string.vui_long_bat_gpa_de_ung_dung_tim_duoc_vi_tri_cua_ban), requestGps)) {
                        getLocation()
                        return
                    }
                }
            } else {
                adapter.setError(R.drawable.ic_location_permission_history, getString(R.string.de_hien_thi_du_lieu_cua_hang_vui_long_bat_gps), R.string.bat_gps)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            requestUpdateOrDestroy -> {
                if (resultCode == RESULT_OK) {
                    viewModel.user = try {
                        data?.getSerializableExtra(Constant.DATA_1) as ICUpdateCustomerGuarantee?
                    } catch (e: Exception) {
                        null
                    }

                    if (viewModel.user != null) {
                        adapter.setListData(mutableListOf())
                        getStampDetail()
                    } else {
                        onBackPressed()
                    }
                } else {
                    onBackPressed()
                }
            }
            requestLogin -> {
                if (resultCode == Activity.RESULT_OK) {
                    onRequireLoginSuccess(requestRequireLogin)
                } else {
                    onRequireLoginCancel()
                }
            }
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
                    CampaignLoyaltyHelper.checkCodeLoyalty(this@StampDetailActivity, it, viewModel.codeInput, viewModel.barcode, this@StampDetailActivity, this@StampDetailActivity)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
        listActivities.clear()
        isVietNamLanguage = null
    }
}