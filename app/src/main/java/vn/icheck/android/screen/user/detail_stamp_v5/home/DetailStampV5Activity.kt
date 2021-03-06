package vn.icheck.android.screen.user.detail_stamp_v5.home

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.Spannable
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import kotlinx.android.synthetic.main.activity_detail_stamp_v5.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.adapter.RecyclerViewAdapter
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.holder.StampECommerceHolder
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.loyalty.helper.ActivityHelper
import vn.icheck.android.network.models.ICProductLink
import vn.icheck.android.network.models.detail_stamp_v6.ICDetailStampV6
import vn.icheck.android.network.models.detail_stamp_v6.ICObjectBusinessV6
import vn.icheck.android.network.models.detail_stamp_v6.ICObjectImageProductV6
import vn.icheck.android.network.models.detail_stamp_v6.ICObjectOtherProductV6
import vn.icheck.android.network.models.detail_stamp_v6_1.ICObjectCustomerHistoryGurantee
import vn.icheck.android.network.models.detail_stamp_v6_1.IC_Config_Error
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.user.detail_stamp_v5.history_guarantee_v5.HistoryGuaranteeV5Activity
import vn.icheck.android.screen.user.detail_stamp_v5.home.adapter.BannerV5Adapter
import vn.icheck.android.screen.user.detail_stamp_v5.home.presenter.DetailStampV5Presenter
import vn.icheck.android.screen.user.detail_stamp_v5.home.view.IDetailStampV5View
import vn.icheck.android.screen.user.detail_stamp_v5.more_business_v5.MoreBusinessV5Activity
import vn.icheck.android.screen.user.detail_stamp_v5.update_information_guarantee_v5.UpdateInformationStampV5Activity
import vn.icheck.android.screen.user.detail_stamp_v6_1.contact_support.ContactSupportActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.holder.ICStampContactAdapter
import vn.icheck.android.screen.user.listproductecommerce.ListProductsECommerceActivity
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.screen.user.viewimage.ViewImageActivity
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.util.ick.visibleOrInvisible
import vn.icheck.android.util.kotlin.ContactUtils
import vn.icheck.android.util.kotlin.GlideImageGetter
import vn.icheck.android.util.kotlin.WidgetUtils
import java.util.*

class DetailStampV5Activity : BaseActivityMVVM(), IDetailStampV5View {

    private val presenter = DetailStampV5Presenter(this@DetailStampV5Activity)

    private var showVendor: Int? = null
    private var showDistributor: Int? = null

    private var objVendor : ICBarcodeProductV1.VendorPage? = null
    private var objUpdateCustomer: ICObjectCustomerHistoryGurantee? = null

    private var isShow = true
    private var currentPage = 0
    private var numberPage = 0
    private var idStamp: String? = null

    private var url: String? = null
    private var nameProduct: String? = null
    private var productId: Long? = null

    private var otherProuct = mutableListOf<ICObjectOtherProductV6>()
    private var itemDistributor: ICObjectBusinessV6? = null

    private var mLocationRequest: LocationRequest? = null

    private val REQUEST_CODE_PERMISSION = 39
    private val requestPhone = 1
    private val requestUpdateCustomer = 2

    private var qrm: String? = null

    private var bannerAdapter: BannerV5Adapter? = null
    private lateinit var adapterConfigError: ICStampContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_stamp_v5)
        onInitView()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun onInitView() {
        setupView()
        initBanner()
        initUpdateLocation()
        listener()

    }

    private fun setupView() {
        constraintLayoutShop.background = ViewHelper.bgSecondaryCornersTop10(this).apply {
            constraintLayoutShop.background = this
            constraintLayout1.background = this
            layoutMoreDistributor.background = this
            layoutMoreDistributor.background = this
        }

        constraintLayout.background = ViewHelper.bgWhiteCornersBottom10(this)
        textFab.background = ViewHelper.bgSecondaryCorners45(this)
        tvMessageGuarantee.background = ViewHelper.bgOutlineSecondary1Corners8(this)
        linearLayout.background = ViewHelper.bgAccentGreenCornersLeft45(this)
    }

    private fun initGps() {
        mLocationRequest = LocationRequest()
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest?.interval = (10 * 1000)
        mLocationRequest?.fastestInterval = 2000

        mLocationRequest?.let {
            val builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(it)
            val locationSettingsRequest = builder.build()

            val settingsClient = LocationServices.getSettingsClient(this)
            settingsClient.checkLocationSettings(locationSettingsRequest)
        }
    }

    private fun initUpdateLocation() {
        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (PermissionHelper.checkPermission(this, permission, REQUEST_CODE_PERMISSION)) {
            presenter.onGetDataIntent(intent)
            initGps()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }

        layoutInformaion.setOnClickListener {
            if (expandTextView.maxLines == 5) {
                expandTextView.maxLines = 10000
                toggle_expand.visibility = View.GONE
            } else {
                expandTextView.maxLines = 5
                toggle_expand.visibility = View.VISIBLE
            }
        }

        imgMore.setOnClickListener {
            imgMore.isClickable = false
            val cx: Int = layoutChatAdmin.measuredWidth * 2
            val cy: Int = layoutChatAdmin.measuredHeight / 2
            val finalRadius = Math.hypot(layoutChatAdmin.width * 2.toDouble(), layoutChatAdmin.height.toDouble()).toFloat()

            if (layoutChatAdmin.visibility == View.VISIBLE) {
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
            }
        }

        layoutChatAdmin.setOnClickListener {
            startActivity<ContactSupportActivity>()
            layoutChatAdmin.visibility = View.INVISIBLE
        }

        tvMoreHistoryGuarantee.setOnClickListener {
            val intent = Intent(this, HistoryGuaranteeV5Activity::class.java)
            intent.putExtra(Constant.DATA_1, qrm)
            startActivity(intent)
        }

        layoutMoreVendor.setOnClickListener {
            PageDetailActivity.start(this,objVendor?.id!!,Constant.PAGE_ENTERPRISE_TYPE)
        }

        layoutMoreDistributor.setOnClickListener {
            val intent = Intent(this, MoreBusinessV5Activity::class.java)
            intent.putExtra(Constant.DATA_1, 2)
            intent.putExtra(Constant.DATA_2, itemDistributor)
            intent.putExtra(Constant.DATA_3, JsonHelper.toJson(otherProuct))
            startActivity(intent)
        }

        textFab.setOnClickListener {
            val intent = Intent(this, UpdateInformationStampV5Activity::class.java)
            intent.putExtra(Constant.DATA_2, idStamp)
            intent.putExtra(Constant.DATA_3, objUpdateCustomer)
            startActivityForResult(intent,requestUpdateCustomer)
        }

        tvWebsiteDistributor.setOnClickListener {
            if (tvWebsiteDistributor.text.toString() != getString(R.string.dang_cap_nhat)) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(tvWebsiteDistributor.text.toString()))
                startActivity(intent)
            }
        }

        tvPhoneDistributor.setOnClickListener {
            if (tvPhoneDistributor.text.toString() != getString(R.string.dang_cap_nhat)) {
                if (PermissionHelper.checkPermission(this, Manifest.permission.CALL_PHONE, requestPhone)) {
                    ContactUtils.callFast(this@DetailStampV5Activity, tvPhoneDistributor.text.toString())
                }
            }
        }

        tvMailDistributor.setOnClickListener {
            if (tvMailDistributor.text.toString() != getString(R.string.dang_cap_nhat)) {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, tvMailDistributor.text.toString())
                startActivity(Intent.createChooser(intent, getString(R.string.send_to)))
            }
        }

        btnAgainError.setOnClickListener {
            initUpdateLocation()
        }
    }

    private fun initAdapterConfigError() {
        adapterConfigError = ICStampContactAdapter()
        rcvConfigError.layoutManager = LinearLayoutManager(this)
        rcvConfigError.adapter = adapterConfigError
    }

    override fun onItemHotlineClick(hotline: String?) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, hotline)
        startActivity(Intent.createChooser(intent, getString(R.string.send_to)))
    }

    override fun onItemEmailClick(email: String?) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, email)
        startActivity(Intent.createChooser(intent, getString(R.string.send_to)))
    }

    @SuppressLint("SetTextI18n")
    override fun onGetDetailStampQRMSuccess(obj: ICDetailStampV6) {
//Check Error Client
        if (obj == null) {
            layoutErrorClient.visibility = View.VISIBLE
            scrollViewError.visibility = View.GONE
            scrollView.visibility = View.GONE
        } else {
            layoutErrorClient.visibility = View.GONE
//check Error detail Stamp
            if (obj.data?.type == "error") {
                if (!obj.data?.message.isNullOrEmpty()) {
                    presenter.getConfigError()
                    obj.data?.message?.let {
                        tvMessageStampError.setText(R.string.canh_bao_s, it)
                    }
                } else {
                    scrollView.visibility = View.VISIBLE
                }
            }else{
                scrollView.visibility = View.VISIBLE
            }
        }

        idStamp = obj.data?.stamp?._id
        qrm = obj.data?.stamp?.qrm

// check show hide floating action button
        if (obj.data?.stamp?.can_update_info == 1) {
            textFab.visibility = View.VISIBLE
            initScrollFab()
        }

        if (obj.data?.stamp?.customer != null){
            objUpdateCustomer = obj.data?.stamp?.customer
        }

//check force update thong tin ca nhan
        if (obj.data?.stamp?.force_update == 1) {
            val intent = Intent(this, UpdateInformationStampV5Activity::class.java)
            intent.putExtra(Constant.DATA_2, idStamp)
            startActivity(intent)
        }

//setdata slide image product
        if (obj.data?.product?.images != null) {
            if (obj.data?.product?.images!!.isEmpty()) {
                obj.data?.product?.images!!.add(ICObjectImageProductV6())
            }

            bannerAdapter?.setListImageData(obj.data?.product?.images!!)

            if (obj.data?.product?.images!!.size > 0) {
                numberPage = obj.data?.product?.images!!.size - 1
            }
        } else {
            obj.data?.product?.images = mutableListOf()
            obj.data?.product?.images?.add(ICObjectImageProductV6())
            bannerAdapter?.setListImageData(obj.data?.product?.images!!)
        }

//price
        if (obj.data?.product?.price == null || obj.data?.product?.price!! <= 0L) {
            tvPriceProduct.text = getString(R.string.dang_cap_nhat_gia)
            tvPriceProduct.setTextColor(Color.parseColor("#828282"))
            tvPriceProduct.textSize = 16F
            tvPriceProduct.setTypeface(null, Typeface.ITALIC)
        } else {
            tvPriceProduct.setText(R.string.s_space_d, TextHelper.formatMoneyComma(obj.data?.product?.price!!))
        }

//namePrice
        if (!obj.data?.product?.name.isNullOrEmpty()) {
            nameProduct = obj.data?.product?.name
            obj.data?.product?.name?.let {
                tvNameProduct.text = if (!it.isNullOrEmpty()) it else getString(R.string.dang_cap_nhat)
            }
        }

//barcode
        if (!obj.data?.product?.sku.isNullOrEmpty()) {
            obj.data?.product?.sku?.let {
                tvBarcodeProduct.text = if (!it.isNullOrEmpty()) it else getString(R.string.dang_cap_nhat)
            }
        }

// serial
        tvSerial.text = if (!obj.data?.stamp?.serial.isNullOrEmpty()){
            "Serial: " + obj.data?.stamp?.serial
        }else{
            getString(R.string.dang_cap_nhat)
        }

//check gurantee
        if (obj.data?.stamp?.guarantee != null) {
            layoutGurantee.visibility = View.VISIBLE
//          Thong tin bao hanh

            obj.data?.stamp?.guarantee?.let {
                tvGuaranteeDay.text = if (it.days != null) {
                    Html.fromHtml("<font color=#434343>${getString(R.string.thoi_gian_bao_hanh)}: </font>" + "<b>" + it.days + " ${getString(R.string.ngay)}" + "</b>")
                } else {
                    Html.fromHtml("<font color=#434343>${getString(R.string.thoi_gian_bao_hanh)} </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
                }

                tvExpiredDay.text = if (it.expired_time != null) {
                    Html.fromHtml("<font color=#434343>${getString(R.string.han_bao_hanh)}: </font>" + "<b>" + TimeHelper.convertMillisecondToDateVn(it.expired_time!! * 1000) + "</b>")
                } else {
                    Html.fromHtml("<font color=#434343>${getString(R.string.han_bao_hanh)}: </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
                }

                //lay expired_time - thoi gian hien tai
                val currrentTime = System.currentTimeMillis()
                val timeServer = it.expired_time!! * 1000
                val timeRemaining = ((timeServer - currrentTime) / AlarmManager.INTERVAL_DAY) + 1

                tvRemainingDay.text = if (timeRemaining <= 0) {
                    Html.fromHtml("<font color=#434343>${getString(R.string.so_ngay_bao_hanh_con_lai)}: </font><b>0 ${getString(R.string.ngay)}</b>")
                } else {
                    Html.fromHtml("<font color=#434343>${getString(R.string.so_ngay_bao_hanh_con_lai)}: </font><b>$timeRemaining ${getString(R.string.ngay)}</b>")
                }

                tvActiveDay.text = if (it.return_time != null) {
                    Html.fromHtml("<font color=#434343>${getString(R.string.ngay_hen_tra_bao_hanh)}: </font>" + "<b>" + TimeHelper.convertMillisecondToDateVn(it.return_time!! * 1000) + "</b>")
                } else {
                    Html.fromHtml("<font color=#434343>${getString(R.string.ngay_hen_tra_bao_hanh)}: </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
                }
            }
        } else {
            layoutGurantee.visibility = View.GONE
        }

        //Check verified
        if (!obj.data?.messages.isNullOrEmpty()) {
            for (i in obj.data?.messages!!) {
                when (i.service?.id) {
                    //chong gia
                    1 -> {
                        when (i.type) {
                            "success" -> {
                                layoutVerified.visibility = View.VISIBLE
                                tvMessageVerified.text = i.service?.message_success
                                obj.data?.stamp?.serial?.let {
                                    tvSerialVerified.setText(R.string.serial_s, it)
                                }
                            }
                            "warning" -> {
                                layoutFake.visibility = View.VISIBLE
                                tvMessageVerifiedFake.text = i.service?.message_warning
                                obj.data?.stamp?.serial?.let {
                                    tvSerialFake.setText(R.string.serial_s, it)
                                }
                            }
                            else -> {
                                layoutFake.visibility = View.VISIBLE
                                tvMessageVerifiedFake.text = i.service?.message_error
                                obj.data?.stamp?.serial?.let {
                                    tvSerialFake.setText(R.string.serial_s, it)
                                }
                            }
                        }
                    }
                    2 -> {
                        when (i.type) {
                            "success" -> {
                                layoutVerified.visibility = View.VISIBLE
                                tvMessageVerified.text = i.service?.message_success
                                obj.data?.stamp?.serial?.let {
                                    tvSerialVerified.setText(R.string.serial_s, it)
                                }
                            }
                            "warning" -> {
                                layoutFake.visibility = View.VISIBLE
                                tvMessageVerifiedFake.text = i.service?.message_warning
                                obj.data?.stamp?.serial?.let {
                                    tvSerialFake.setText(R.string.serial_s, it)
                                }
                            }
                            else -> {
                                layoutFake.visibility = View.VISIBLE
                                tvMessageVerifiedFake.text = i.service?.message_error
                                obj.data?.stamp?.serial?.let {
                                    tvSerialFake.setText(R.string.serial_s, it)
                                }
                            }
                        }
                    }

                    //bao hanh
                    3 -> {
                        viewGuarantee.visibility = View.VISIBLE
                        tvMessageGuarantee.visibility = View.VISIBLE
                        when (i.type) {
                            "success" -> {
                                tvMessageGuarantee.text = i.service?.message_success
                            }
                            "warning" -> {
                                tvMessageGuarantee.text = i.service?.message_warning
                            }
                            else -> {
                                tvMessageGuarantee.text = i.service?.message_error
                            }
                        }
                    }

                    //tran hang
                    4 -> {
                    }
                }
            }
        }

        obj.data?.product?.sku?.let {
            presenter.getProductBySku(it)
        }

        if (!obj.data?.product_link.isNullOrEmpty()) {
            layoutProductLink.beVisible()
            val adapter = object : RecyclerViewAdapter<ICProductLink>() {
                override fun getItemCount() = if (listData.size > 3) 3 else listData.size

                override fun viewHolder(parent: ViewGroup) = StampECommerceHolder(parent)

                override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                    if (holder is StampECommerceHolder) {
                        holder.bind(listData[position])
                    } else {
                        super.onBindViewHolder(holder, position)
                    }
                }
            }
            adapter.disableLoading()
            adapter.disableLoadMore()
            recyclerView.adapter = adapter
            adapter.setListData(obj.data!!.product_link!!)

            tvViewMore.visibleOrInvisible(adapter.getListData.size > 3)
            tvViewMore.setOnClickListener {
                ActivityHelper.startActivity<ListProductsECommerceActivity>(this@DetailStampV5Activity, Constant.DATA_1, JsonHelper.toJson(obj.data?.product_link ?: mutableListOf()))
            }
        }

        //check nha san xuat
        showVendor = obj.data?.stamp?.show_vendor

        //check nha phan phoi
        showDistributor = obj.data?.stamp?.show_business
        if (showDistributor == 1) {
            if (obj.data?.business != null) {
                tvSubDistributor.visibility = View.VISIBLE
                layoutDistributor.visibility = View.VISIBLE

                obj.data?.business?.let {
                    itemDistributor = it
//                    idDistributor = it.id
                    tvNameDistributor.text = if (!it.company_name.isNullOrEmpty()) {
                        it.company_name
                    } else {
                        getString(R.string.dang_cap_nhat)
                    }

                    tvAddressDistributor.text = if (!it.address.isNullOrEmpty()) {
                        it.address
                    } else {
                        getString(R.string.dang_cap_nhat)
                    }

                    tvWebsiteDistributor.text = if (!it.company_website.isNullOrEmpty()) {
                        it.company_website
                    } else {
                        getString(R.string.dang_cap_nhat)
                    }

                    tvPhoneDistributor.text = if (!it.phone.isNullOrEmpty()) {
                        it.phone
                    } else {
                        getString(R.string.dang_cap_nhat)
                    }

                    tvMailDistributor.text = if (!it.company_email.isNullOrEmpty()) {
                        it.company_email
                    } else {
                        getString(R.string.dang_cap_nhat)
                    }
                }
            } else {
                tvSubDistributor.visibility = View.GONE
                layoutDistributor.visibility = View.GONE
            }
        } else {
            tvSubDistributor.visibility = View.GONE
            layoutDistributor.visibility = View.GONE
        }

        //bind Mo ta san pham
        if (!obj.data?.product?.description.isNullOrEmpty()) {
            tvSubInfomation.visibility = View.VISIBLE
            layoutInformaion.visibility = View.VISIBLE
            obj.data?.product?.description?.let {
                val imageGetter = GlideImageGetter(expandTextView)

                val html = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(it, Html.FROM_HTML_MODE_LEGACY, imageGetter, null) as Spannable
                } else {
                    Html.fromHtml(it, imageGetter, null)
                }
                expandTextView.text = html
            }
        } else {
            tvSubInfomation.visibility = View.GONE
            layoutInformaion.visibility = View.GONE
        }

        obj.data?.other_product?.let {
            otherProuct.addAll(it)
        }

        url = obj.data?.product?.image
        productId = obj.data?.product?.id

//        qrScanViewModel.update(ICQrScan(presenter.mCode!!
//                , null
//                , null
//                , 1
//                , productId
//                , url
//                , nameProduct
//                , null
//                , tvPriceProduct.text.toString()
//                , tvPriceNoSale.text.toString()
//                , null
//                , null
//                , false
//                , null
//                ,"v5"))
//
//        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.REFRESH_DATA_HISTORY_QR))
//        qrScanViewModel.search(presenter.code!!).observe(this,
//                androidx.lifecycle.Observer {
//                    it?.let {
//                        Log.d("codeV6", it.toString())
//                    }
//                })
    }

    override fun onGetProductBySkuSuccess(obj: ICBarcodeProductV1) {
//region
        if (!obj.owner?.vendorPage?.country?.name.isNullOrEmpty() && !obj.owner?.vendorPage?.country?.code.isNullOrEmpty()) {
            obj.owner?.vendorPage?.country?.name?.let {
                tvRegion.text = if (!it.isNullOrEmpty()) it else getString(R.string.dang_cap_nhat)
            }
            obj.owner?.vendorPage?.country?.code?.let {
                WidgetUtils.loadImageUrl(imgRegion, "http://ucontent.icheck.vn/ensign/$it.png")
            }
        }

//bind data nha san xuat
        if (showVendor == 1) {
            if (obj.owner?.vendorPage != null) {
                objVendor = obj.owner?.vendorPage
                tvSubVendor.visibility = View.VISIBLE
                layoutVendor.visibility = View.VISIBLE
                obj.owner?.vendorPage?.let {
                    //objVendor = it

                    tvNameVendor.text = if (!it.name.isNullOrEmpty()) {
                        it.name
                    } else {
                        getString(R.string.dang_cap_nhat)
                    }

                    tvAddressVendor.text = if (!it.address.isNullOrEmpty()) {
                        it.address
                    } else {
                        getString(R.string.dang_cap_nhat)
                    }

                    tvPhoneVendor.text = if (!it.phone.isNullOrEmpty()) {
                        it.phone
                    } else {
                        getString(R.string.dang_cap_nhat)
                    }
                }
            } else {
                tvSubVendor.visibility = View.GONE
                layoutVendor.visibility = View.GONE
            }
        } else {
            tvSubVendor.visibility = View.GONE
            layoutVendor.visibility = View.GONE
        }
    }

    override fun onGetConfigSuccess(obj: IC_Config_Error) {
        scrollViewError.visibility = View.VISIBLE
        if (!obj.data?.contacts.isNullOrEmpty()) {
            initAdapterConfigError()
            adapterConfigError.setListData(obj.data?.contacts!!)
        }
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

    private fun initBanner() {
        bannerAdapter = BannerV5Adapter(this, this)
        viewPagerImgProduct.adapter = bannerAdapter

        val handler = Handler()
        val update = Runnable {
            currentPage++

            if (currentPage > numberPage) {
                currentPage = 0
            }

            try {
                viewPagerImgProduct.setCurrentItem(currentPage, true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val swipeTimer = Timer()
        swipeTimer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, 3000, 3000)

        viewPagerImgProduct.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {

            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

            }

            override fun onPageSelected(p0: Int) {
                currentPage = p0
            }
        })
    }

    override fun itemPagerClick(list: String, position: Int) {
        val intent = Intent(this, ViewImageActivity::class.java)
        intent.putExtra(Constant.DATA_1, list)
        intent.putExtra(Constant.DATA_2, position)
        startActivity(intent)
    }

    override fun onGetDataIntentError(errorType: Int) {
        layoutErrorClient.visibility = View.VISIBLE
        scrollViewError.visibility = View.GONE
        scrollView.visibility = View.GONE
        when (errorType) {
            Constant.ERROR_INTERNET -> {
                imgError.setImageResource(R.drawable.ic_error_network)
                tvMessageError.setText(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            }
            Constant.ERROR_UNKNOW -> {
                imgError.setImageResource(R.drawable.ic_error_request)
                tvMessageError.setText(R.string.khong_the_truy_cap_vui_long_thu_lai_sau)
            }
            Constant.ERROR_EMPTY -> {
                imgError.setImageResource(R.drawable.ic_error_request)
                tvMessageError.setText(R.string.khong_the_truy_cap_vui_long_thu_lai_sau)
            }
        }
    }

    override fun showError(errorMessage: String) {
        showShortError(errorMessage)
    }

    override val mContext: Context
        get() = this@DetailStampV5Activity

    override fun onShowLoading(isShow: Boolean) {
        vn.icheck.android.ichecklibs.DialogHelper.showLoading(this@DetailStampV5Activity, isShow)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (PermissionHelper.checkResult(grantResults)) {
                presenter.onGetDataIntent(intent)
                initGps()
            } else {
                DialogHelper.showNotification(this@DetailStampV5Activity, R.string.vui_long_vao_phan_cai_dat_va_cho_phep_ung_dung_su_dung_vi_tri_cua_thiet_bi, false, object : NotificationDialogListener {
                    override fun onDone() {
                        onBackPressed()
                    }
                })
            }
        }

        if (requestCode == requestPhone) {
            if (PermissionHelper.checkResult(grantResults)) {
                ContactUtils.callFast(this@DetailStampV5Activity, tvPhoneDistributor.text.toString())
            } else {
                showShortError(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestUpdateCustomer) {
            if (resultCode == Activity.RESULT_OK) {
                val dataIntent = data?.getSerializableExtra(Constant.DATA_1) as ICObjectCustomerHistoryGurantee
                objUpdateCustomer = dataIntent
            }
        }
    }
}
