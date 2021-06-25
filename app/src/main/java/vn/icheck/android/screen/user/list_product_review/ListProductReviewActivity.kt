package vn.icheck.android.screen.user.list_product_review

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_list_product_review.*
import kotlinx.android.synthetic.main.activity_list_product_review.imgBack
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.product_review.my_review.IMyReviewListener
import vn.icheck.android.component.product_review.my_review.MyReviewModel
import vn.icheck.android.component.product_review.submit_review.ISubmitReviewListener
import vn.icheck.android.component.product_review.submit_review.SubmitReviewHolder
import vn.icheck.android.constant.Constant
import vn.icheck.android.fragments.ReviewTributeDialog
import vn.icheck.android.helper.*
import vn.icheck.android.ichecklibs.take_media.TakeMediaDialog
import vn.icheck.android.ichecklibs.take_media.TakeMediaListener
import vn.icheck.android.network.models.ICPost
import vn.icheck.android.network.models.ICProductMyReview
import vn.icheck.android.screen.user.detail_post.DetailPostActivity
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.ick.logError
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.ToastUtils.showShortError
import vn.icheck.android.util.kotlin.WidgetUtils
import java.io.File

class ListProductReviewActivity : BaseActivityMVVM(), ISubmitReviewListener, IRecyclerViewCallback, IMyReviewListener {

    lateinit var adapter: ListProductReviewAdapter
    lateinit var viewModel: ListProductReviewViewModel

    lateinit var shareReviewDialog: ReviewTributeDialog
    private val requestCameraPermission = 3
    private val requestPostReview = 4
    private val requestEditReview = 5
    private var positionSubmit = -1
    private var isActivityVisible = true

    private var reviewStartInsider = true

    private val takeMediaListener = object : TakeMediaListener {
        override fun onPickMediaSucess(file: File) {
            val holder = rcvListReview.findViewHolderForAdapterPosition(positionSubmit)
            if (holder != null && holder is SubmitReviewHolder) {
                holder.setImage(file)
            }
        }

        override fun onPickMuliMediaSucess(file: MutableList<File>) {
            val holder = rcvListReview.findViewHolderForAdapterPosition(positionSubmit)
            if (holder != null && holder is SubmitReviewHolder) {
                holder.setImage(file)
            }
        }

        override fun onStartCrop(filePath: String?, uri: Uri?, ratio: String?, requestCode: Int?) {

        }

        override fun onDismiss() {
        }

        override fun onTakeMediaSuccess(file: File?) {
            val holder = rcvListReview.findViewHolderForAdapterPosition(positionSubmit)
            if (holder != null && holder is SubmitReviewHolder) {
                file?.let { holder.setImage(it) }
            }
        }

    }

    companion object {
        fun startActivity(productId: Long, activity: Activity) {
            ActivityUtils.startActivity<ListProductReviewActivity, Long>(activity, Constant.DATA_1, productId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_product_review)

        viewModel = ViewModelProvider(this)[ListProductReviewViewModel::class.java]
        isActivityVisible = true
        initView()
        initRecyclerView()
        listenerData()
    }

    private fun initView() {
        imgBack.setOnClickListener {
            onBackPressed()
        }

        val swipeColor = vn.icheck.android.ichecklibs.ColorManager.getPrimaryColor(this)
        swipe_container.setColorSchemeColors(swipeColor, swipeColor, swipeColor)

        swipe_container.setOnRefreshListener {
            getData()
            adapter.setRefeshTextReview(true)
        }
    }

    private fun initRecyclerView() {
        adapter = ListProductReviewAdapter(this, this, this)
        rcvListReview.adapter = adapter
    }


    private fun listenerData() {
        viewModel.getData(intent)

        viewModel.onStatusMessage.observe(this, {
            when (it.type) {
                ICMessageEvent.Type.ON_SHOW_LOADING -> {
                    DialogHelper.showLoading(this)
                }
                ICMessageEvent.Type.ON_CLOSE_LOADING -> {
                    swipe_container.isRefreshing = false
                    DialogHelper.closeLoading(this)
                }
                else -> {
                    swipe_container.isRefreshing = false
                    DialogHelper.closeLoading(this)
                    if (it.data != null) {
                        if (adapter.itemCount > 0) {
                            showShortError(this, (it.data as ICError).message)
                        } else {
                            adapter.setError(it.data as ICError)
                        }
                    }
                }
            }
        })

        viewModel.onReviewSummary.observe(this, {
            adapter.addReviewInfo(it)
        })

        viewModel.onUpdateReviewSummary.observe(this, {
            if (adapter.getListData[0].getViewType() == ICViewTypes.HEADER_REVIEW_TYPE) {
                adapter.getListData[0] = it
                adapter.notifyItemChanged(0)
            }
        })

        viewModel.onMyReview.observe(this, {
            adapter.addReviewInfo(it)
        })

        viewModel.onUpdateMyReview.observe(this, {
            adapter.updateMyReview(it)
        })


        viewModel.onListReview.observe(this, {
            adapter.addListData(it)
        })

        viewModel.onDataProduct.observe(this, {
            tvNameProduct.text = it.basicInfo?.name
            if (!it.media.isNullOrEmpty()) {
                WidgetUtils.loadImageUrlRounded(imgProduct, it.media!![0].content, R.drawable.product_images_new_placeholder, SizeHelper.size4)
            } else {
                WidgetUtils.loadImageUrlRounded(imgProduct, null, R.drawable.product_images_new_placeholder, SizeHelper.size4)
            }
        })

        viewModel.onShareLinkData.observe(this, {
            shareReviewDialog.dismiss()
            if (it == null) {
                showShortError(R.string.co_loi_xay_ra_vui_long_thu_lai)
            } else {
                if (!it.link.isNullOrEmpty()) {
                    val share = Intent()
                    share.action = Intent.ACTION_SEND
                    share.putExtra(Intent.EXTRA_SUBJECT, viewModel.currentProduct?.basicInfo?.name)
                    share.putExtra(Intent.EXTRA_TEXT, getString(R.string.chia_se_danh_gia, it.avgPoint, it.content, it.link))
                    share.type = "text/plain"
                    startActivity(Intent.createChooser(share, getString(R.string.chia_se_s,viewModel.currentProduct?.basicInfo?.name)))
                }
            }
        })
    }

    private fun getData() {
        swipe_container.isRefreshing = true
        adapter.resetData(true)
        adapter.isReviewSummary = 0
        Handler().postDelayed({
            viewModel.getData(intent)
        }, 150)
    }

    override fun onTakeImage(positionHolder: Int) {
        positionSubmit = positionHolder

        val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (PermissionHelper.checkPermission(this, permission, requestCameraPermission)) {
            selectPicture()
        }
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        when (event.type) {
            ICMessageEvent.Type.REQUEST_POST_REVIEW -> {
                positionSubmit = event.data as Int
                onRequireLogin(requestPostReview)
            }
            ICMessageEvent.Type.OPEN_DETAIL_POST -> {
                if (isActivityVisible) {
                    if (isActivityVisible) {
                        if (event.data is Long) {
                            DetailPostActivity.start(this, event.data, false, requestEditReview)
                        } else {
                            DetailPostActivity.start(this, (event.data as ICPost).id, true, requestEditReview)
                        }
                    }
                }
            }
            ICMessageEvent.Type.RESULT_DETAIL_POST_ACTIVITY -> {
                viewModel.getMyReview(true)
            }
            ICMessageEvent.Type.CLICK_START_REVIEW -> {
                if (reviewStartInsider) {
                    viewModel.currentProduct?.let {
                        TrackingAllHelper.trackProductReviewStart(it)
                    }
                    reviewStartInsider = false
                }
            }
            else -> {
            }
        }
    }

    override fun onPostReviewSuccess(obj: ICPost) {
        if (adapter.getListData[1].getViewType() == ICViewTypes.SUBMIT_REVIEW_TYPE) {
            adapter.getListData.removeAt(1)
            adapter.notifyItemRemoved(1)
        }

        val myReview = MyReviewModel(ICProductMyReview(obj, null, null))
        adapter.addReviewInfo(myReview)

        Handler().postDelayed({
            viewModel.getReviewSummary(true)
        }, 1500)

        viewModel.currentProduct?.let {
            TrackingAllHelper.trackProductReviewSuccess(it)
        }
        shareReviewDialog = ReviewTributeDialog(this).apply {
            setAction(object : ReviewTributeDialog.ReviewTributeAction {
                override fun onDismiss() {
                    dismiss()
                }

                override fun onShare() {
                    dismiss()
                    viewModel.getShareLink(obj)
                }
            })
        }
        shareReviewDialog.show()
    }

    override fun onClickReviewPermission() {
        viewModel.getMyReview(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == requestEditReview) {
                val login = data?.getBooleanExtra(Constant.DATA_2, false) ?: false
                if (login) {
                    getData()
                } else {
                    val post = data?.getSerializableExtra(Constant.DATA_1)
                    if (post != null && post is ICPost) {
                        adapter.updateItemReview(post)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            requestCameraPermission -> {
                if (PermissionHelper.checkResult(grantResults)) {
                    try {
                        selectPicture()
                    } catch (e: Exception) {
                        logError(e)
                    }
                } else {
                    ToastUtils.showLongError(this, getString(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen))
                }
            }
        }
    }

    private fun selectPicture() {
        TakeMediaDialog.show(supportFragmentManager, this, takeMediaListener, selectMulti = true, isVideo = true)
    }

    override fun onMessageClicked() {
        getData()
    }

    override fun onLoadMore() {
        viewModel.getListReview(true)
    }

    override fun onBackPressed() {
        Intent().apply {
            setResult(RESULT_OK, this)
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        isActivityVisible = false
    }

    override fun onResume() {
        super.onResume()
        isActivityVisible = true
    }
}
