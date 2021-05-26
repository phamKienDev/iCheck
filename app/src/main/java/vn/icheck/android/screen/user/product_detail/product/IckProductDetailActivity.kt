package vn.icheck.android.screen.user.product_detail.product

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_ick_product_detail.*
import kotlinx.android.synthetic.main.activity_ick_product_detail.imgBack
import kotlinx.android.synthetic.main.activity_ick_product_detail.layoutBottom
import kotlinx.android.synthetic.main.activity_ick_product_detail.recyclerView
import kotlinx.android.synthetic.main.activity_ick_product_detail.txtTitle
import kotlinx.android.synthetic.main.activity_ick_product_detail.view
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.commentpost.ICCommentPostMore
import vn.icheck.android.component.product.ProductDetailListener
import vn.icheck.android.component.product_list_review.ProductListReviewHolder
import vn.icheck.android.component.product_list_review.ProductListReviewModel
import vn.icheck.android.component.product_review.list_review.ItemListReviewModel
import vn.icheck.android.component.product_review.my_review.IMyReviewListener
import vn.icheck.android.component.product_review.my_review.MyReviewModel
import vn.icheck.android.component.product_review.submit_review.ISubmitReviewListener
import vn.icheck.android.component.product_review.submit_review.SubmitReviewHolder
import vn.icheck.android.component.product_review.submit_review.SubmitReviewModel
import vn.icheck.android.constant.*
import vn.icheck.android.fragments.ReviewTributeDialog
import vn.icheck.android.helper.*
import vn.icheck.android.ichecklibs.take_media.TakeMediaDialog
import vn.icheck.android.ichecklibs.take_media.TakeMediaListener
import vn.icheck.android.loyalty.base.listener.IClickListener
import vn.icheck.android.loyalty.helper.CampaignLoyaltyHelper
import vn.icheck.android.loyalty.model.ICKLoyalty
import vn.icheck.android.loyalty.sdk.LoyaltySdk
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.*
import vn.icheck.android.screen.account.icklogin.IckLoginActivity
import vn.icheck.android.screen.dialog.DialogNotificationFirebaseAds
import vn.icheck.android.screen.user.contact.ContactActivity
import vn.icheck.android.screen.user.contribute_product.IckContributeProductActivity
import vn.icheck.android.screen.user.detail_media.DetailMediaActivity
import vn.icheck.android.screen.user.detail_post.DetailPostActivity
import vn.icheck.android.screen.user.list_contribution.ContributionAttributesActivity
import vn.icheck.android.screen.user.list_product_question.ListProductQuestionActivity
import vn.icheck.android.screen.user.list_product_review.ListProductReviewActivity
import vn.icheck.android.screen.user.listcontribute.ListContributeActivity
import vn.icheck.android.screen.user.media_in_post.MediaInPostActivity
import vn.icheck.android.screen.user.product_detail.product.dialog.ContactBusinessDialog
import vn.icheck.android.screen.user.reason_not_buy_product.BottomSheetWebView
import vn.icheck.android.screen.user.report_product.ReportProductActivity
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.ick.beInvisible
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.ick.showSimpleSuccessToast
import vn.icheck.android.util.ick.simpleStartActivity
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.StatusBarUtils
import java.io.File

/**
 * Happy new year
 * 00:00 ngày 01/01/2021
 * Chúc mừng năm mới anh Phong nha. Năm mới kiếm nhiều tiền nha anh :v
 */
class IckProductDetailActivity : BaseActivityMVVM(), IRecyclerViewCallback, ISubmitReviewListener,
    ProductDetailListener, CampaignLoyaltyHelper.IRemoveHolderInputLoyaltyListener,
    CampaignLoyaltyHelper.ILoginListener, IMyReviewListener {
    private lateinit var viewModel: IckProductDetailViewModel

    private val adapter = IckProductDetailAdapter(this, this, this, this, this, this)

    private val permissionCamera = 98
    private var positionSubmit = -1

    private val requestQuestion = 2
    private val requestReview = 3
    private val requestPostReview = 4
    private val requestVoteContribution = 5 //request vote đúng -sai
    private val requestListContribution = 6 //request chuyển màn ListContributeActivity
    private val requestReportProduct = 7
    private val requestMediaInPost = 8

    private var isActivityVisible = true
    private var productViewedInsider = true
    private var reviewStartInsider = true

    private var obj: ICKLoyalty? = null
    private val takeMediaListener = object : TakeMediaListener {
        override fun onPickMediaSucess(file: File) {
            val holder = recyclerView.findViewHolderForAdapterPosition(positionSubmit)
            if (holder != null && holder is SubmitReviewHolder) {
                holder.setImage(file)
            }
        }

        override fun onPickMuliMediaSucess(file: MutableList<File>) {
            val holder = recyclerView.findViewHolderForAdapterPosition(positionSubmit)
            if (holder != null && holder is SubmitReviewHolder) {
                holder.setImage(file)
            }
        }

        override fun onStartCrop(filePath: String?, uri: Uri?, ratio: String?, requestCode: Int?) {

        }

        override fun onDismiss() {
        }

        override fun onTakeMediaSuccess(file: File?) {
            val holder = recyclerView.findViewHolderForAdapterPosition(positionSubmit)
            if (holder != null && holder is SubmitReviewHolder) {
                file?.let { holder.setImage(it) }
            }
        }
    }

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_PRODUCT_DETAIL) {
                if (intent.getIntExtra(ACTION_PRODUCT_DETAIL, 0) == SHOW_ATTRIBUTES) {
                    if (job == null || job?.isActive == false) {
                        job = lifecycleScope.launch {
                            delay(200)
                            val i = Intent(
                                this@IckProductDetailActivity,
                                ContributionAttributesActivity::class.java
                            )
                            i.putExtra("productId", viewModel.productID)
                            i.putExtra("image", viewModel.listMedia.firstOrNull()?.content)
                            i.putExtra("listInfo", viewModel.listInfo)
                            startActivity(i)
                        }
                    }

                }
                if (intent.getIntExtra(ACTION_PRODUCT_DETAIL, 0) == SHOW_ATTACHMENTS) {
                    if (job == null || job?.isActive == false) {
                        job = lifecycleScope.launch {
                            delay(200)
                            val arr = arrayListOf<String?>()
                            for (item in viewModel.listMedia) {
                                if (item.type == "image") {
                                    arr.add(item.content)
                                }
                            }
                            DetailMediaActivity.start(
                                this@IckProductDetailActivity,
                                arr,
                                intent.getIntExtra(POSITION, 0)
                            )
                        }
                    }

                }
            }
        }
    }

    companion object {
        var INSTANCE: IckProductDetailActivity? = null
        var postVote = false
        var barcode = ""
        var urlDistributor = ""

        const val REQUEST_MMB = 1

        fun start(activity: Activity, barcode: String, isScan: Boolean? = null) {
            val intent = Intent(activity, IckProductDetailActivity::class.java)

            intent.putExtra(Constant.DATA_1, barcode)
            if (isScan != null)
                intent.putExtra(Constant.DATA_2, isScan)

            ActivityUtils.startActivity(activity, intent)
        }

        fun start(activity: Activity, productID: Long) {
            ActivityUtils.startActivity<IckProductDetailActivity, Long>(
                activity,
                Constant.DATA_3,
                productID
            )
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ick_product_detail)
        StatusBarUtils.setOverStatusBarDark(this)

        setupView()
        setupRecyclerView()
        setupViewModel()
        setupSwipeLayout()
        setupListener()

        viewModel.getData(intent)
        barcode = intent.getStringExtra("barcode") ?: ""
        registerReceiver(mBroadcastReceiver, IntentFilter(ACTION_PRODUCT_DETAIL))
    }

    private fun setupView() {
        INSTANCE = this
//        txtTitle.text = intent.getStringExtra("barcode")
        layoutToolbarAlpha.setPadding(0, getStatusBarHeight + SizeHelper.size16, 0, 0)
        layoutAction.setPadding(0, getStatusBarHeight + SizeHelper.size16, 0, 0)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
//        recyclerView.setItemViewCacheSize(50)
        adapter.enableLoadMore(false)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val scrollPosition = recyclerView.findViewHolderForAdapterPosition(0)?.itemView?.y
                if (scrollPosition != null && scrollPosition > -layoutToolbarAlpha.height) {
                    val alpha = (1f / -layoutToolbarAlpha.height) * scrollPosition
                    layoutToolbarAlpha.alpha = alpha
                } else {
                    layoutToolbarAlpha.alpha = 1f
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    ExoPlayerManager.checkPlayVideoBase(recyclerView, layoutToolbarAlpha.height)
                }
            }
        })
    }

    private fun showLayoutStatus() {
        layoutStatus.visibility = View.VISIBLE
        layoutToolbarAlpha.alpha = 1f
        imgLike.beInvisible()
        imgAction.setImageResource(R.drawable.ic_home_blue_v2_24px)
    }

    private fun hideLayoutStatus() {
        layoutStatus.visibility = View.GONE
        layoutToolbarAlpha.alpha = 0f
        imgLike.beVisible()
        imgAction.setImageResource(R.drawable.ic_more_light_blue_24dp)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(IckProductDetailViewModel::class.java)

        viewModel.onAddHolderInput.observe(this, Observer {
            adapter.addHolderInput(it)
        })

        viewModel.alertProduct.observe(this) {
            DialogHelper.showConfirm(
                this,
                it.title,
                it.alertDescription,
                "Đóng",
                "Chi tiết",
                false,
                object : ConfirmDialogListener {
                    override fun onDisagree() {
                    }

                    override fun onAgree() {
                        WebViewActivity.start(
                            this@IckProductDetailActivity,
                            it.content,
                            null,
                            it.title
                        )
                    }
                })
        }

        viewModel.onSetTitle.observe(this) {
            barcode = it
            txtTitle.text = it
        }

        viewModel.onDataProduct.observe(this, Observer {
            hideLayoutStatus()
            swipeLayout.beVisible()
            CampaignLoyaltyHelper.getCampaign(
                this@IckProductDetailActivity, barcode,
                object : IClickListener {
                    override fun onClick(obj: Any) {
                        if (obj is ICKLoyalty) {
                            viewModel.onAddHolderInput.postValue(ICLayout().apply {
                                data = obj
                                viewType = ICViewTypes.LOYALTY_HOLDER_TYPE
                            })
                        }
                    }
                }, this@IckProductDetailActivity
            )
            if (it.verified == true) {
                tvBuy.setText(R.string.dang_ky_mua_hang_chinh_hang)
            } else {
                tvBuy.setText(R.string.mua_tai_nha_san_xuat)
            }
        })

        viewModel.statusCode.observe(this) {
            when (it) {
                ICMessageEvent.Type.BACK -> {
                    removeLoading()

                    DialogHelper.showNotification(
                        this@IckProductDetailActivity,
                        R.string.khong_tim_thay_du_lieu_vui_long_thu_lai_sau,
                        false,
                        object : NotificationDialogListener {
                            override fun onDone() {
                                onBackPressed()
                            }
                        })
                }
                ICMessageEvent.Type.ON_NO_INTERNET -> {
                    removeLoading()

                    DialogHelper.showConfirm(
                        this,
                        R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau,
                        R.string.huy_bo,
                        R.string.thu_lai,
                        object : ConfirmDialogListener {
                            override fun onDisagree() {
                                onBackPressed()
                            }

                            override fun onAgree() {
                                getLayoutData()
                            }
                        })
                }
                ICMessageEvent.Type.ON_SHOW_LOADING -> {
                    DialogHelper.showLoading(this)
                }
                ICMessageEvent.Type.ON_CLOSE_LOADING -> {
                    DialogHelper.closeLoading(this)
                }
                else -> {
                }
            }
        }

        viewModel.errorRequest.observe(this, Observer {
            imgActionGray.beInvisible()
            removeLoading()
            adapter.setError(it)
        })

        viewModel.errorMessage.observe(this) {
            showShortError(it)
        }

        //Sản phẩm chưa có trên hệ thống
        viewModel.onProductNotFound.observe(this) {
            removeLoading()
            swipeLayout.visibility = View.GONE
            layoutBottom.visibility = View.GONE
            view.visibility = View.GONE

            showLayoutStatus()
            layoutNotFound.visibility = View.VISIBLE
            btn_contact_not_found.setOnClickListener {
                simpleStartActivity(ContactActivity::class.java)
            }

            imgError.setImageResource(R.drawable.ic_holder_product_notfound)
            tvTitleError.text = getString(R.string.default_product_not_found)
            tvDescError.text = getString(R.string.desc_product_not_found)

            layoutToolbarAlpha.alpha = 1f
        }

        //Sản phẩm bị ẩn bởi iCheck
        viewModel.onProductAdminDeactivate.observe(this) {
            removeLoading()
            swipeLayout.visibility = View.GONE
            layoutBottom.visibility = View.GONE
            view.visibility = View.GONE

            showLayoutStatus()

            btn_lien_he.visibility = View.VISIBLE

            imgError.setImageResource(R.drawable.ic_holder_product_notfound)
            tvTitleError.text = getString(R.string.default_product_not_found)
            tvDescError.text = getString(R.string.desc_product_not_found)

            layoutToolbarAlpha.alpha = 1f
        }

        //Sản phẩm bị gỡ khỏi bởi doanh nghiệp
        viewModel.onProductBusinessDeactivate.observe(this) {
            removeLoading()
            swipeLayout.visibility = View.GONE
            layoutBottom.visibility = View.GONE
            view.visibility = View.GONE

            showLayoutStatus()

            layoutHideFromBusiness.visibility = View.VISIBLE

            btn_lien_he.setOnClickListener {
                simpleStartActivity(ContactActivity::class.java)
            }
            imgError.setImageResource(R.drawable.ic_holder_product_removed)

            tvTitleError.text = getString(R.string.default_product_deleted)
            tvDescError.text = getString(R.string.desc_product_not_found)

            layoutToolbarAlpha.alpha = 1f
        }

        //Sản phẩm bị gỡ khỏi bởi doanh nghiệp và không có doanh nghiệp quản lý
        viewModel.onProductBusinessDeactivateNotManager.observe(this, {
            removeLoading()
            swipeLayout.visibility = View.GONE
            layoutBottom.visibility = View.GONE
            view.visibility = View.GONE

            showLayoutStatus()

            layoutHideFromBusiness.visibility = View.VISIBLE
            btn_contact_dn.visibility = View.GONE

            btn_lien_he.setOnClickListener {
                simpleStartActivity(ContactActivity::class.java)
            }
            imgError.setImageResource(R.drawable.ic_holder_product_removed)

            tvTitleError.text = getString(R.string.default_product_deleted)
            tvDescError.text = getString(R.string.desc_product_not_found)

            layoutToolbarAlpha.alpha = 1f
        })

        viewModel.onClearData.observe(this) {
            adapter.resetData(true)
        }

        viewModel.onAddLayout.observe(this) {
            if (it.viewType == ICViewTypes.HEADER_TYPE) {
                viewModel.productDetail?.let { productDetail ->
                    if (productViewedInsider) {
                        TrackingAllHelper.trackProductViewed(productDetail)
                        if (intent.getBooleanExtra(Constant.DATA_2, false)) {
                            TrackingAllHelper.trackScanSuccessful(productDetail)
                            intent.putExtra(Constant.DATA_2, false)
                        }
                        productViewedInsider = false
                    }
                    TrackingAllHelper.trackScanBarcodeViewedSuccess(productDetail)
                }
            }
            imgActionGray.beVisible()
            layoutBottom.beVisible()
            removeLoading()
            adapter.addLayout(it)
            swipeLayout.descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
        }

        viewModel.onUpdateLayout.observe(this) {
            adapter.updateLayout(it)

            //tự động thực hiện action vote trước đó
//            if (it.viewType == ICViewTypes.CONTRIBUTE_USER) {
//                isVoteContribution?.let { value ->
//                    Handler().postDelayed({
//                        for (i in adapter.getListData.size - 1 downTo 0) {
//                            recyclerView.findViewHolderForAdapterPosition(i)?.let { viewHolder ->
//                                if (viewHolder is ContributionHolder) {
//                                    viewHolder.vote(value)
//                                }
//                            }
//                        }
//                        isVoteContribution = null
//                    }, 400)
//                }
//            }
        }

        viewModel.onUpdateListLayout.observe(this) {
            adapter.updateLayout(it)
        }

        viewModel.onUpdateAds.observe(this) {
            if (it == true) {
                adapter.updateAds()
            }
        }

        viewModel.onUpdateQuestion.observe(this) {
            adapter.updateQuestion(it)
        }

        viewModel.onUpdateReview.observe(this) {
            adapter.updateReview(it)
        }

        viewModel.onPostTransparency.observe(this) {
            adapter.updateTransparency(it)
        }

        viewModel.onDetailPost.observe(this, Observer {
            for (i in 0 until adapter.getListData.size) {
                if (adapter.getListData[i].viewType == ICViewTypes.LIST_REVIEWS_TYPE) {
                    if (recyclerView.findViewHolderForAdapterPosition(i) is ProductListReviewHolder) {
                        (recyclerView.findViewHolderForAdapterPosition(i) as ProductListReviewHolder).updateReview(
                            ItemListReviewModel(it)
                        )
                    }
                }
            }
        })

        viewModel.onMyReviewData.observe(this, Observer {
            var positionSubmit = -1
            var positionMyReview = -1

            for (i in 0 until adapter.getListData.size) {
                if (adapter.getListData[i].viewType == ICViewTypes.MY_REVIEW_TYPE) {
                    positionMyReview = i
                } else if (adapter.getListData[i].viewType == ICViewTypes.SUBMIT_REVIEW_TYPE) {
                    positionSubmit = i
                }

            }
            if (it.myReview != null) {
                val myReviewModel = ICLayout().also { layout ->
                    layout.data = MyReviewModel(it)
                    layout.viewType = ICViewTypes.MY_REVIEW_TYPE
                }

                if (positionSubmit != -1) {
                    adapter.getListData.removeAt(positionSubmit)
                    adapter.getListData.add(positionSubmit, myReviewModel)
                } else if (positionMyReview != -1) {
                    adapter.getListData[positionMyReview] = myReviewModel
                }
            } else {
                if (!it.criteria.isNullOrEmpty()) {
                    val submitReviewModel = ICLayout().also { layout ->
                        layout.data = SubmitReviewModel(it.criteria!!, viewModel.productID)
                        layout.viewType = ICViewTypes.SUBMIT_REVIEW_TYPE
                    }

                    if (positionMyReview != -1) {
                        adapter.getListData.removeAt(positionMyReview)
                        adapter.getListData.add(positionMyReview, submitReviewModel)
                    } else if (positionSubmit != -1) {
                        adapter.getListData[positionSubmit] = submitReviewModel
                    }
                }
            }
            adapter.notifyDataSetChanged()
        })

        viewModel.onShareLinkProduct.observe(this, Observer {
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_SUBJECT, viewModel.infoProduct?.name)
                putExtra(Intent.EXTRA_TEXT, it)
                type = "text/plain"
                val title = "Chia sẻ " + (viewModel.infoProduct?.name ?: "")
                startActivity(Intent.createChooser(this, title))
            }
        })

        viewModel.onShareLink.observe(this) {
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_SUBJECT, viewModel.infoProduct?.name)
                it?.let {
                    if (it is ICPost) {
                        putExtra(
                            Intent.EXTRA_TEXT,
                            resources.getString(
                                R.string.chia_se_danh_gia,
                                it.avgPoint,
                                it.content,
                                it.link
                            )
                        )
                    } else {
                        putExtra(Intent.EXTRA_TEXT, it as String)
                    }
                }
                type = "text/plain"
                val title = "Chia sẻ " + (viewModel.infoProduct?.name ?: "")
                startActivity(Intent.createChooser(this, title))
            }
        }

        viewModel.onUrlDistributor.observe(this@IckProductDetailActivity, Observer {
            urlDistributor = it
        })
        viewModel.onRegisterBuyProduct.observe(this@IckProductDetailActivity, Observer {
            showSimpleSuccessToast("Cảm ơn bạn, chúng tôi sẽ liên hệ lại trong thời gian sớm nhất.")
        })
        viewModel.onPopupAds.observe(this@IckProductDetailActivity,Observer{
            object : DialogNotificationFirebaseAds(this@IckProductDetailActivity,image = it.image, htmlText = it.document,link=it.url,schema = it.deeplink) {
                override fun onDismiss() {

                }

            }.show()
        })
    }

    private fun setupSwipeLayout() {
        swipeLayout.setColorSchemeColors(
            ContextCompat.getColor(this, R.color.colorPrimary),
            ContextCompat.getColor(this, R.color.colorPrimary),
            ContextCompat.getColor(this, R.color.colorPrimary)
        )

        swipeLayout.setOnRefreshListener {
            getLayoutData()
            adapter.setRefeshTextReview(true)
        }
    }

    private fun setupListener() {
        btn_contact_dn.setOnClickListener {
            viewModel.productDetail?.let { productDetail ->
                if (productDetail.owner?.verified == true) {
                    ContactBusinessDialog(this).show(
                        productDetail.owner?.id,
                        productDetail.manager?.phone,
                        productDetail.manager?.email
                    )
                } else {
                    ContactBusinessDialog(this).show(
                        productDetail.manager?.id,
                        productDetail.manager?.phone,
                        productDetail.manager?.email
                    )
                }
            }
        }

        btn_contact_not_found.setOnClickListener {
            startActivity<ContactActivity>()
        }

        btn_contribute.setOnClickListener {
            if (SessionManager.isUserLogged) {
                IckContributeProductActivity.start(this, viewModel.barcode, viewModel.productID)
            } else {
                onRequireLogin()
            }
        }

        btn_lien_he.setOnClickListener {
            startActivity<ContactActivity>()
        }

        btn_contact_icheck.setOnClickListener {
            startActivity<ContactActivity>()
        }

        imgLike.setOnClickListener {
            delayAction({
                if (SessionManager.isUserLogged) {
                    if (imgLike.tag as Boolean? != true) {
                        viewModel.addBookMark()
                    } else {
                        viewModel.deleteBookMark()
                    }
                } else {
                    onRequireLogin()
                }
            })

        }

        imgAction.setOnClickListener {
            if (imgAction.drawable.constantState == ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_more_light_blue_24dp
                )?.constantState
            ) {
                if (viewModel.productID != 0L) {
                    if (layoutAction.visibility == View.VISIBLE) {
                        layoutAction.visibility = View.GONE
                    } else {
                        layoutAction.visibility = View.VISIBLE
                    }
                }
            } else {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.GO_TO_HOME))
            }
        }

//        imgHome.setOnClickListener {
//            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.GO_TO_HOME))
//        }

        tvGoToHome.setOnClickListener {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.GO_TO_HOME, 1))
            EventBus.getDefault().post(hashMapOf("goHome" to true))
        }

        tvShare.setOnClickListener {
            layoutAction.visibility = View.GONE
            viewModel.getProductShareLink()
        }

        tvReport.setOnClickListener {
            if (SessionManager.isUserLogged) {
                startActivity<ReportProductActivity, Long>(Constant.DATA_1, viewModel.productID)
                layoutAction.visibility = View.GONE
            } else {
                onRequireLogin(requestReportProduct)
            }
        }

        layoutAction.setOnClickListener {
            layoutAction.visibility = View.GONE
        }

        btnRate.setOnClickListener {
            if (viewModel.productID != 0L)
                startActivityForResult<ListProductReviewActivity, Long>(
                    Constant.DATA_1,
                    viewModel.productID,
                    requestReview
                )
        }

        btnBuy.setOnClickListener {
            if (!viewModel.verifyProduct) {
                if (!viewModel.urlBuy.isNullOrEmpty()) {
                    val bottomSheetWebView = BottomSheetWebView(this)
                    bottomSheetWebView.showWithUrl(viewModel.urlBuy!!)
                }
            } else {

                DialogHelper.showConfirm(
                    this,
                    "Thông báo",
                    "Bạn muốn Đăng ký mua hàng chính hãng? Hãy gửi yêu cầu cho chúng tôi, chúng tôi sẽ liên hệ lại khi nhận được thông tin.",
                    "Hủy",
                    "Gửi",
                    true,
                    object : ConfirmDialogListener {
                        override fun onDisagree() {

                        }

                        override fun onAgree() {
                            if (SessionManager.isUserLogged) {
                                viewModel.registerBuyProduct()
                            } else {
                                onRequireLogin()
                            }
                        }
                    })

            }
        }

        imgBack.setOnClickListener {
            finish()
        }
    }

    private fun getLayoutData() {
        swipeLayout.isRefreshing = true
        viewModel.getProductLayout()
    }


    private fun removeLoading() {
        swipeLayout.isRefreshing = false

        layoutLoading?.let { loading ->
            layoutContainer.removeView(loading)
        }
    }

    fun vote(yesOrNo: Boolean, idProduct: Long) {
        viewModel.postTransparency(yesOrNo, idProduct)
    }

    fun scrollWithViewType(type: Int) {
        for (i in 0 until adapter.getListData.size) {
            if (type == adapter.getListData[i].viewType) {
                recyclerView.smoothScrollToPosition(i)
            }
        }
    }

    override fun onLoadMore() {

    }

    override fun onMessageClicked() {
        getLayoutData()
    }

    override fun shareProduct(id: Long) {
        viewModel.getProductShareLink(id)
    }

    override fun clickGoQa(idProduct: ICCommentPostMore?) {
        ListProductQuestionActivity.start(
            this,
            viewModel.productID,
            viewModel.barcode,
            false,
            requestQuestion
        )
    }

    override fun clickToContributionInfo(barcode: String) {
        if (SessionManager.isUserLogged) {
            IckContributeProductActivity.start(this, barcode, viewModel.productID)
        } else {
            onRequireLogin()
        }
    }

    override fun clickCancelContribution(position: Int) {
        adapter.removeContributionEnterprise()
    }

    override fun clickBottomContact(item: ICClientSetting, position: Int) {
        item.value?.let { Constant.openUrl(it) }
    }

    override fun onTakeImage(positionHolder: Int) {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (!PermissionHelper.isAllowPermission(this, permissions)) {
            PermissionHelper.checkPermission(this, permissions, permissionCamera)
            return
        }

        positionSubmit = positionHolder
        TakeMediaDialog.show(
            supportFragmentManager,
            this,
            takeMediaListener,
            selectMulti = true,
            isVideo = true
        )
    }

    override fun onPostReviewSuccess(obj: ICPost) {
        DialogHelper.closeLoading(this)
        viewModel.productDetail?.let {
            TrackingAllHelper.trackProductReviewSuccess(it)
        }
        ReviewTributeDialog(this).apply {
            setAction(object : ReviewTributeDialog.ReviewTributeAction {
                override fun onDismiss() {
                }

                override fun onShare() {
                    viewModel.getProductShareLink(obj)
                }

            })
        }.show()

        val myReview = ICLayout(
            "",
            "",
            ICRequest(),
            null,
            null,
            ICViewTypes.MY_REVIEW_TYPE,
            MyReviewModel(ICProductMyReview(obj, null, null))
        )
        for (i in 0 until adapter.getListData.size) {
            recyclerView.findViewHolderForAdapterPosition(i)?.let { holder ->
                if (holder is SubmitReviewHolder) {
                    adapter.getListData[i] = myReview
                    adapter.notifyItemChanged(i)
                }
            }
        }

        viewModel.updateData()
    }

    override fun clickAnswersInQuestion(obj: ICProductQuestion) {
        ListProductQuestionActivity.start(
            this,
            viewModel.productID,
            viewModel.barcode,
            false,
            obj,
            requestQuestion
        )
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)

        when (event.type) {
            ICMessageEvent.Type.OPEN_LIST_QUESTIONS -> {
                if (isActivityVisible) {
                    ListProductQuestionActivity.start(
                        this,
                        viewModel.productID,
                        viewModel.barcode,
                        event.data as Boolean?,
                        requestQuestion
                    )
                }
            }
            ICMessageEvent.Type.ON_REQUIRE_LOGIN -> {
                if (isActivityVisible) {
                    onRequireLogin(requestVoteContribution)
                }
            }
            ICMessageEvent.Type.OPEN_LIST_REVEWS -> {
                if (isActivityVisible) {
                    startActivityForResult<ListProductReviewActivity, Long>(
                        Constant.DATA_1,
                        viewModel.productID,
                        requestReview
                    )
                }
            }
            ICMessageEvent.Type.OPEN_LIST_CONTRIBUTION -> {
                if (isActivityVisible) {
                    if (event.data != null && event.data is String) {
                        val intent = Intent(this, ListContributeActivity::class.java)
                        intent.putExtra(Constant.DATA_1, event.data)
                        if (!viewModel.listMedia.isNullOrEmpty()) {
                            intent.putExtra(Constant.DATA_2, viewModel.listMedia[0].content)
                        }
                        this.startActivityForResult(intent, requestListContribution)
                    }
                }
            }
            ICMessageEvent.Type.REQUEST_VOTE_CONTRIBUTION -> {
                if (isActivityVisible) {
//                    isVoteContribution = event.data as Boolean?
                    onRequireLogin(requestVoteContribution)
                }
            }
            ICMessageEvent.Type.ON_UPDATE_AUTO_PLAY_VIDEO -> {
                if (isActivityVisible) {
                    ExoPlayerManager.checkPlayVideoBase(recyclerView, layoutToolbarAlpha.height)
                }
            }
            ICMessageEvent.Type.REQUEST_POST_REVIEW -> {
                if (isActivityVisible) {
                    positionSubmit = event.data as Int
                    onRequireLogin(requestPostReview)
                }
            }
            ICMessageEvent.Type.ON_UPDATE_BOOKMARK -> {
                if (isActivityVisible) {
                    imgLike.tag = event.data as Boolean
                    if (event.data) {
                        imgLike.setImageResource(R.drawable.ic_like_on_24dp)
                    } else {
                        imgLike.setImageResource(R.drawable.ic_like_off_24dp)
                    }
                    adapter.updateBookMark(event.data)
                }
            }
            ICMessageEvent.Type.ADD_OR_REMOVE_BOOKMARK -> {
                if (isActivityVisible) {
                    imgLike.performClick()
                }
            }
            ICMessageEvent.Type.OPEN_DETAIL_POST -> {
                if (isActivityVisible) {
                    if (event.data is Long) {
                        DetailPostActivity.start(this, event.data, false)
                    } else {
                        DetailPostActivity.start(this, (event.data as ICPost).id, true)
                    }
                }
            }
            ICMessageEvent.Type.OPEN_MEDIA_IN_POST -> {
                if (isActivityVisible) {
                    if (event.data != null && event.data is ICPost) {
                        MediaInPostActivity.start(event.data, this, null, requestMediaInPost)
                    }
                }
            }
            ICMessageEvent.Type.RESULT_DETAIL_POST_ACTIVITY -> {
                if (event.data != null && event.data is ICPost) {
                    adapter.getListData.forEachIndexed { index, icLayout ->
                        if (adapter.getListData[index].viewType == ICViewTypes.MY_REVIEW_TYPE) {
                            if ((adapter.getListData[index].data as MyReviewModel).data.myReview?.id == event.data.id) {
                                Handler().postDelayed({
                                    viewModel.reloadMyReview()
                                }, 500)
                            }
                        } else if (adapter.getListData[index].viewType == ICViewTypes.LIST_REVIEWS_TYPE) {
                            (adapter.getListData[index].data as ProductListReviewModel).data.forEachIndexed { indexReview, icPost ->
                                if ((adapter.getListData[index].data as ProductListReviewModel).data[indexReview].id == event.data.id) {
                                    (adapter.getListData[index].data as ProductListReviewModel).data[indexReview] =
                                        event.data
                                    adapter.notifyItemChanged(index)
                                }
                            }
                        }
                    }
                }
            }
            ICMessageEvent.Type.CLICK_START_REVIEW -> {
                if (reviewStartInsider) {
                    viewModel.productDetail?.let {
                        TrackingAllHelper.trackProductReviewStart(it)
                    }
                    reviewStartInsider = false
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                requestReportProduct -> {
                    startActivity<ReportProductActivity, Long>(Constant.DATA_1, viewModel.productID)
                    layoutAction.visibility = View.GONE
                }
                REQUEST_MMB -> {
                    if (resultCode == RESULT_OK) {
                        viewModel.postTransparency(postVote, viewModel.productID)
                    }
                }
                requestQuestion -> {
                    viewModel.updateData()
                }
                requestReview -> {
                    viewModel.updateData()
                    viewModel.reloadMyReview()
                }
                requestVoteContribution -> {
                    viewModel.getProductLayout(true)
                }
                requestListContribution -> {
                    viewModel.getProductLayout(true)
                }
                requestMediaInPost -> {
                    val post = data?.getSerializableExtra(Constant.DATA_1)
                    if (post != null && post is ICPost) {
                        for (i in 0 until adapter.getListData.size) {
                            if (adapter.getListData[i].viewType == ICViewTypes.LIST_REVIEWS_TYPE) {
                                if (recyclerView.findViewHolderForAdapterPosition(i) is ProductListReviewHolder) {
                                    (recyclerView.findViewHolderForAdapterPosition(i) as ProductListReviewHolder).updateReview(
                                        ItemListReviewModel(post)
                                    )
                                }
                            }
                        }
                    }
                }
                CONTRIBUTION_PRODUCT -> {
                    val productID = data?.getLongExtra(Constant.DATA_1, -1)
                    val myContribute = data?.getIntExtra(Constant.DATA_2, 0)
                    if (myContribute == 0) {
                        showSimpleSuccessToast("Bạn đã đóng góp thông tin thành công")
                    } else {
                        showSimpleSuccessToast("Bạn đã chỉnh sửa đóng góp thành công")
                    }
                    if (productID != -1L) {
                        viewModel.barcode = ""
                        viewModel.productID = productID!!
                        viewModel.getProductLayout()
                        productViewedInsider = true
                    }
                }
                CampaignLoyaltyHelper.REQUEST_GET_GIFT -> {
                    CampaignLoyaltyHelper.getReceiveGift(
                        this@IckProductDetailActivity, viewModel.barcode, viewModel.code, obj?.name
                            ?: "", null
                    )
                }
                CampaignLoyaltyHelper.REQUEST_CHECK_CODE -> {
                    obj?.let {
                        CampaignLoyaltyHelper.checkCodeLoyalty(
                            this@IckProductDetailActivity,
                            it,
                            viewModel.code,
                            viewModel.barcode,
                            this@IckProductDetailActivity,
                            this@IckProductDetailActivity
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isActivityVisible = true
        viewModel.getOrUpdateAds()
    }

    override fun onPause() {
        super.onPause()
        isActivityVisible = false
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.dispose()
    }

    override fun onRemoveHolderInput() {
        adapter.removeHolderInput()
    }

    override fun showDialogLogin(data: ICKLoyalty, code: String?) {
        this@IckProductDetailActivity.obj = data
        if (code.isNullOrEmpty()) {
            LoyaltySdk.showDialogLogin<IckLoginActivity, Int>(
                this@IckProductDetailActivity,
                "requestCode",
                1,
                CampaignLoyaltyHelper.REQUEST_GET_GIFT,
                data
            )
        } else {
            viewModel.code = code
            LoyaltySdk.showDialogLogin<IckLoginActivity, Int>(
                this@IckProductDetailActivity,
                "requestCode",
                1,
                CampaignLoyaltyHelper.REQUEST_CHECK_CODE,
                data,
                viewModel.code
            )
        }
    }

    override fun onClickReviewPermission() {
        viewModel.reloadMyReview(viewModel.urlMyReview)
    }
}