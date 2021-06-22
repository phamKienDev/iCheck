package vn.icheck.android.screen.user.detail_stamp_hoa_phat.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_detail_stamp_hoa_phat.*
import vn.icheck.android.R
import vn.icheck.android.WrapContentLinearLayoutManager
import vn.icheck.android.activities.product.product_questions_v1.ProductQuestionsV1Activity
import vn.icheck.android.activities.product.review_product_v1.ReviewProductV1Activity
import vn.icheck.android.activities.product.review_v1.EditReviewV1Activity
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.chat.icheckchat.screen.detail.ChatSocialDetailActivity
import vn.icheck.android.constant.Constant
import vn.icheck.android.fragments.ProductReviewsBottomDialog
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICCriteria
import vn.icheck.android.screen.account.icklogin.IckLoginActivity
import vn.icheck.android.screen.user.detail_media.DetailMediaActivity
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter.DetailStampHoaPhatAdapter
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.call_back.SlideHeaderStampHoaPhatListener
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.view_model.DetailStampHoaPhatViewModel
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.infor_product.InforProductHoaPhatActivity
import vn.icheck.android.screen.user.home.HomeActivity
import vn.icheck.android.screen.user.list_product_review.ListProductReviewActivity
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.screen.user.view_item_image_stamp.ViewItemImageActivity
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.ContactUtils

class DetailStampHoaPhatActivity : BaseActivityMVVM(), SlideHeaderStampHoaPhatListener {

    private val adapter = DetailStampHoaPhatAdapter(this)

    private lateinit var viewModel: DetailStampHoaPhatViewModel
//    private lateinit var qrScanViewModel: QrScanViewModel

    private var mFusedLocationClient: FusedLocationProviderClient? = null

    private val REQUEST_CODE_PERMISSION = 1
    private val REQUEST_BOOKMARK = 2
    private val requestRefreshData = 3
    private val requestPhone = 4
    private val requestAddToCart = 5

    private var bookmarked = false

    private var phoneNumber = ""

    companion object {
        var INSTANCE: DetailStampHoaPhatActivity? = null
    }

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_stamp_hoa_phat)
        INSTANCE = this
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        btnRequestPermission.background=ViewHelper.bgWhiteStrokeSecondary1Corners40(this)
        btnEnableLocation.background=ViewHelper.bgWhiteStrokeSecondary1Corners40(this)
        textFab.background=ViewHelper.bgSecondaryCorners45(this)

        setupRecyclerView()
        setupSwipeLayout()
        setupViewModel()

        setupListener()
        setupUpdateLocation()

        viewModel.getDataIntent(intent)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = WrapContentLinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.setItemViewCacheSize(20)
    }

    private fun setupSwipeLayout() {
        val swipeColor = vn.icheck.android.ichecklibs.ColorManager.getPrimaryColor(this)
        swipeLayout.setColorSchemeColors(swipeColor, swipeColor, swipeColor)

        swipeLayout.setOnRefreshListener {
            swipeLayout.isRefreshing = true
            viewModel.reloadData()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(DetailStampHoaPhatViewModel::class.java)
//        qrScanViewModel = ViewModelProvider(this).get(QrScanViewModel::class.java)

        viewModel.getIdSocial.observe(this, {
            PageDetailActivity.start(this, it, Constant.PAGE_ENTERPRISE_TYPE)
        })

        viewModel.successDataMessage.observe(this, {
            showShortSuccess(it)
        })

        viewModel.onAddData.observe(this, Observer {
            recyclerView.visibility = View.VISIBLE
            bottomLayout.visibility = View.VISIBLE
            adapter.addData(it)
        })

        viewModel.onDetailStamp.observe(this, Observer {
            tv_title.text = it.barcode
        })

        viewModel.statusCode.observe(this, Observer {
            when (it) {
                ICMessageEvent.Type.ON_NO_INTERNET -> {
                    swipeLayout.isRefreshing = false
                    DialogHelper.showConfirm(this, R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau, R.string.huy_bo, R.string.thu_lai, object : ConfirmDialogListener {
                        override fun onDisagree() {
                            onBackPressed()
                        }

                        override fun onAgree() {
                            viewModel.getDataIntent(intent)
                        }
                    })
                }
                ICMessageEvent.Type.ON_SHOW_LOADING -> {
                    if (!swipeLayout.isRefreshing) {
                        DialogHelper.showLoading(this)
                    }
                }
                ICMessageEvent.Type.ON_CLOSE_LOADING -> {
                    swipeLayout.isRefreshing = false
                    DialogHelper.closeLoading(this)
                }
                else -> {
                }
            }
        })

        viewModel.errorDataMessage.observe(this, Observer {
            showShortError(it)
        })

        viewModel.errorDataStatusCode.observe(this, Observer {
            if (it == 401) {
                if (SessionManager.isUserLogged) {
                    onRequireLoginSuccess(requestRefreshData)
                } else {
                    onRequireLogin(requestRefreshData)
                }
            }
        })

        viewModel.onSetbookmark.observe(this, Observer {
            if (it == true) setBookmark()
        })

        viewModel.onBarcodeProduct.observe(this, Observer {
            if (viewModel.barcodeProduct?.userBookmark != null) {
                bookmarked = true
                setBookmark()
            } else {
                bookmarked = false
                setBookmark()
            }
        })
    }

    private fun setupListener() {
        btn_back.setOnClickListener {
            onBackPressed()
        }

        btn_homepage.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finishAffinity()
        }

        btn_bookmark.setOnClickListener {
            if (SessionManager.isUserLogged || SessionManager.isDeviceLogged) {
                onRequireLoginSuccess(REQUEST_BOOKMARK)
            } else {
                onRequireLogin(REQUEST_BOOKMARK)
            }
        }

        btnRequestPermission.setOnClickListener {
            setupUpdateLocation()
        }

        btnChat.setOnClickListener {
            if (SessionManager.isUserLogged || SessionManager.isDeviceLogged) {
                viewModel.barcodeProduct?.let {
                    it.manager?.let { manager ->
//                        SocialChatActivity.createPageChat(this, manager.id)
                        ChatSocialDetailActivity.createRoomChat(this@DetailStampHoaPhatActivity, manager.id, "page")
                    } ?: run {
//                        SocialChatActivity.createPageChat(this,viewModel.barcodeProduct?.owner?.id, it.barcode)
                    }
                }
            } else {
                ActivityUtils.startActivity<IckLoginActivity>(this@DetailStampHoaPhatActivity)
            }
        }

        layoutBuyNow.setOnClickListener {
            if (SessionManager.isUserLogged) {
                onRequireLoginSuccess(requestAddToCart)
            } else {
                onRequireLogin(requestAddToCart)
            }
        }
    }

    private fun setupUpdateLocation() {
        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (PermissionHelper.checkPermission(this, permission, REQUEST_CODE_PERMISSION)) {
            if (PermissionHelper.isAllowPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) && PermissionHelper.isAllowPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                mFusedLocationClient?.lastLocation?.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        APIConstants.LATITUDE = location.latitude
                        APIConstants.LONGITUDE = location.longitude
                    }
                }?.addOnFailureListener {
                    APIConstants.LATITUDE = 0.0
                    APIConstants.LONGITUDE = 0.0
                }?.addOnCanceledListener {
                    APIConstants.LATITUDE = 0.0
                    APIConstants.LONGITUDE = 0.0
                }
            }
        }
    }

    private fun setBookmark() {
        if (bookmarked) {
            btn_bookmark.setImageResource(R.drawable.ic_bookmark_active_blue_24px)
        } else {
            btn_bookmark.setImageResource(R.drawable.ic_bookmark_unactive_blue_24px)
        }
        bookmarked = !bookmarked
    }

    fun showAllReviews() {
        if (!viewModel.barcodeProduct?.barcode.isNullOrEmpty() && viewModel.barcodeProduct?.id != null) {
            ListProductReviewActivity.startActivity(viewModel.barcodeProduct!!.id, this)
//            ReviewProductActivity.showReviews(viewModel.barcodeProduct!!.barcode, viewModel.barcodeProduct!!.id, this)
        }
    }

    override fun itemPagerClickToVideo(urlVideo: String?) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlVideo))
        startActivity(intent)
    }

    override fun itemPagerClick(list: ArrayList<String?>, position: Int) {
        DetailMediaActivity.start(this, list)
    }

    override fun itemPagerClickToImage(url: String, position: Int) {
        val intent = Intent(this, ViewItemImageActivity::class.java)
        intent.putExtra(Constant.DATA_1, url)
        startActivity(intent)
    }

    override fun showAllBottomReviews() {
        val reviewsBottomDialog = ProductReviewsBottomDialog()
        reviewsBottomDialog.arguments = Bundle().apply {
            this.putSerializable("criteria", viewModel.criteria)
        }
        reviewsBottomDialog.show(supportFragmentManager, null)
    }

    override fun showAllInformationProduct(title: String, content: String) {
        val intent = Intent(this, InforProductHoaPhatActivity::class.java)
        intent.putExtra(Constant.DATA_1, title)
        intent.putExtra(Constant.DATA_2, content)
        startActivity(intent)
    }

    override fun dial(phone: String?) {
        if (phone != null) {
            phoneNumber = phone
            if (PermissionHelper.checkPermission(this, Manifest.permission.CALL_PHONE, requestPhone)) {
                ContactUtils.callFast(this@DetailStampHoaPhatActivity, phone)
            }
        }
    }

    override fun email(email: String?) {
        if (email != null) {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:$email")
            startActivity(Intent.createChooser(intent, getString(R.string.send_to)))
        }
    }

    override fun web(website: String?) {
        if (website != null) {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse(website)
            startActivity(intent)
        }
    }

    override fun showPage(id: Long) {
        viewModel.getIdPageSocial(id)
    }

    override fun editReview(objCriteria: ICCriteria) {
        val edit = Intent(this@DetailStampHoaPhatActivity, EditReviewV1Activity::class.java)
        edit.putExtra("criteria", objCriteria)

        if (!viewModel.barcodeProduct?.attachments.isNullOrEmpty()) {
            edit.putExtra("img", viewModel.barcodeProduct?.attachments?.first()?.thumbnails?.original)
        }

        edit.putExtra("name", viewModel.barcodeProduct?.name)
        edit.putExtra("id", viewModel.barcodeProduct?.id)
        startActivityForResult(edit, ReviewProductV1Activity.EDIT)
    }

    override fun viewAllQa() {
        viewModel.barcodeProduct?.let {
            val intent = Intent(this, ProductQuestionsV1Activity::class.java)
            intent.putExtra(Constant.DATA_1, it.id)
            startActivity(intent)
        }
    }

    override fun onClickRalatedProduct(barcode: String) {
        IckProductDetailActivity.start(this, barcode)
    }

    override fun scrollWithViewType(type: Int) {
        for (i in 0 until adapter.listData.size) {
            if (type == adapter.listData[i].type) {
                recyclerView.smoothScrollToPosition(i)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ReviewProductV1Activity.EDIT -> {
                if (resultCode == Activity.RESULT_OK) {
                    viewModel.reloadData()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (PermissionHelper.checkResult(grantResults)) {
                if (NetworkHelper.isOpenedGPS(this)) {
                    llAcceptPermission.visibility = View.GONE
                    setupUpdateLocation()
                } else {
                    DialogHelper.showNotification(this@DetailStampHoaPhatActivity, R.string.vui_long_bat_gpa_de_ung_dung_tim_duoc_vi_tri_cua_ban, false, object : NotificationDialogListener {
                        override fun onDone() {
                            onBackPressed()
                        }
                    })
                }
            } else {
                llAcceptPermission.visibility = View.VISIBLE
            }
        }

        if (requestCode == requestPhone) {
            if (PermissionHelper.checkResult(grantResults)) {
                ContactUtils.callFast(this@DetailStampHoaPhatActivity, phoneNumber)
            } else {
                showShortError(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequireLoginSuccess(requestCode: Int) {
        super.onRequireLoginSuccess(requestCode)
        when (requestCode) {
            REQUEST_BOOKMARK -> {
                if (bookmarked) {
                    mFusedLocationClient?.lastLocation?.addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            viewModel.postBookmark(location.latitude.toString(), location.longitude.toString())
                        }
                    }
                } else {
                    viewModel.deleteBookmark()
                }
            }

            requestRefreshData -> {
                viewModel.getDataIntent(intent)
            }

            requestAddToCart -> {
//                viewModel.addToCard()
                viewModel.addToCart()
            }
        }
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)

        when (event.type) {
            ICMessageEvent.Type.ON_SHOW_LOGIN -> {
                onRequireLogin()
            }
        }
    }
}