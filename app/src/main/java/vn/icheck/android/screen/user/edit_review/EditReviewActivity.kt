package vn.icheck.android.screen.user.edit_review

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_edit_review.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.list_image_send.ListImageSendAdapter
import vn.icheck.android.component.review.ReviewBottomSheetAdapter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.take_media.TakeMediaDialog
import vn.icheck.android.ichecklibs.take_media.TakeMediaListener
import vn.icheck.android.network.models.ICCriteriaReview
import vn.icheck.android.network.models.ICPost
import vn.icheck.android.network.models.ICReqCriteriaReview
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.ichecklibs.util.showShortErrorToast
import vn.icheck.android.util.ick.rText
import vn.icheck.android.util.ick.simpleText
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import java.io.File

class EditReviewActivity : BaseActivityMVVM() {
    lateinit var ratingAdapter: ReviewBottomSheetAdapter
    lateinit var listImageAdapter: ListImageSendAdapter
    lateinit var viewModel: EditReviewViewModel

    private val listDataRating = mutableListOf<ICCriteriaReview>()
    private val requestCameraPermission = 3
    private var reviewStartInsider = true
    var create = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_review)

        viewModel = ViewModelProvider(this).get(EditReviewViewModel::class.java)
        initView()
        initRcvImage()
        listenerData()
    }

    private fun initView() {
        btnBack.setOnClickListener {
            onBackPressed()
        }

        imgCamera.setOnClickListener {
            val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            if (PermissionHelper.checkPermission(this, permission, requestCameraPermission)) {
                takeImage()
            }
        }

        btnSend.setOnClickListener {
            postProductReview()
        }

    }

    private fun initRcvImage() {
        listImageAdapter = ListImageSendAdapter(object : ListImageSendAdapter.IListImageSendListener {
            override fun onClickDeleteImageSend(position: Int) {
                listImageAdapter.deleteItem(position)
            }
        }, true)

        rcvImage.adapter = listImageAdapter
        rcvImage.layoutManager = GridLayoutManager(this, 4)
    }

    private fun takeImage() {
        TakeMediaDialog.show(supportFragmentManager,this, object : TakeMediaListener {
            override fun onPickMediaSucess(file: File) {
                listImageAdapter.addItem(file)
            }

            override fun onPickMuliMediaSucess(file: MutableList<File>) {
                listImageAdapter.addData(file)
            }

            override fun onStartCrop(filePath: String?, uri: Uri?, ratio: String?, requestCode: Int?) {
            }

            override fun onDismiss() {
            }

            override fun onTakeMediaSuccess(file: File?) {
                file?.let { listImageAdapter.addItem(it) }
            }
        }, true, isVideo = true)
    }


    private fun listenerData() {
        viewModel.getData(intent)

        viewModel.onDataProduct.observe(this, Observer {
            if (!it.media.isNullOrEmpty()) {
                WidgetUtils.loadImageUrlRounded(imgProduct, it.media!![0].content, R.drawable.product_images_new_placeholder, SizeHelper.size4)
            } else {
                WidgetUtils.loadImageUrlRounded(imgProduct, null, R.drawable.product_images_new_placeholder, SizeHelper.size4)
            }
        })

        viewModel.onReviewData.observe(this, Observer {

            if (it.myReview != null) {
                viewModel.createReview = false
                it.myReview!!.customerCriteria?.let { criteria ->
                    listDataRating.addAll(criteria)
                }
                it.myReview!!.media?.let { myMedia ->
                    for (image in myMedia) {
                        image.content?.let { content ->
                            listImageAdapter.addItem(content)
                        }
                    }
                }
                edtContent.setText(it.myReview!!.content)
            } else {
                viewModel.createReview = true
                tv_title rText R.string.danh_gia_san_pham
                it.criteria?.let { criteria ->
                    listDataRating.addAll(criteria)
                }
            }

            ratingAdapter = ReviewBottomSheetAdapter(listDataRating.toList(), true)
            rcvRating.adapter = ratingAdapter
            rcvRating.layoutManager = LinearLayoutManager(this)
        })

        viewModel.onStatusMessage.observe(this, Observer {
            when (it.type) {
                ICMessageEvent.Type.ON_SHOW_LOADING -> {
                    DialogHelper.showLoading(this)
                }
                ICMessageEvent.Type.ON_CLOSE_LOADING -> {
                    DialogHelper.closeLoading(this)
                }
                ICMessageEvent.Type.BACK -> {
                    DialogHelper.closeLoading(this)
                    DialogHelper.showNotification(this@EditReviewActivity, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                        override fun onDone() {
                            onBackPressed()
                        }
                    })
                }
                else -> {
                    DialogHelper.closeLoading(this)
                    showShortErrorToast((it.data as ICError).message ?: "")
                }
            }
        })

        viewModel.onPostReviewSuccess.observe(this, Observer { myReview ->
            viewModel.currentProduct?.let {
                TrackingAllHelper.trackProductReviewSuccess(it)
            }
            backData(myReview)
        })
    }

    private fun backData(review: ICPost) {
        val intent = Intent()

        if (!this.intent.getBooleanExtra("wall", false)) {
            intent.putExtra(Constant.DATA_1, viewModel.getProductId)
        } else {
            intent.putExtra(Constant.DATA_1, review)
        }
        if (viewModel.getFromHome != null) {
            intent.putExtra(Constant.DATA_2, viewModel.getProductBarcode)
            intent.putExtra(Constant.DATA_3, viewModel.getFromHome)
            intent.putExtra(Constant.DATA_4, viewModel.getPosition)
        }

        setResult(RESULT_OK, intent)
        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.RESULT_EDIT_POST, review))
        if (viewModel.createReview) {
            DialogHelper.showDialogSuccessBlack(this, rText(R.string.ban_da_tao_danh_gia_thanh_cong), null, 1000)
        } else {
            DialogHelper.showDialogSuccessBlack(this, rText(R.string.ban_da_chinh_sua_danh_gia_thanh_cong), null, 1000)
        }
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            requestCameraPermission -> {
                if (PermissionHelper.checkResult(grantResults)) {
                    takeImage()
                } else {
                    ToastUtils.showLongError(this, getString(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen))
                }
            }
        }
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        when (event.type) {
            ICMessageEvent.Type.CLICK_START_REVIEW -> {
                if (reviewStartInsider) {
                    viewModel.currentProduct?.let {
                        TrackingAllHelper.trackProductReviewStart(it)
                    }
                    reviewStartInsider = false
                }
            }
        }
    }

    private fun postProductReview() {
        val criterias = mutableListOf<ICReqCriteriaReview>()
        var validate = true

        for (i in listDataRating) {
            if (i.point != 0F && i.criteriaId != null && i.criteriaSetId != null) {
                criterias.add(ICReqCriteriaReview(i.criteriaId!!, i.point, i.criteriaSetId!!))
                validate = true
            } else {
                validate = false
                break
            }
        }

        if (validate) {
            if (listImageAdapter.getlistData.isNullOrEmpty()) {
                viewModel.postReview(edtContent.text.toString(), criterias)
            } else {
                viewModel.uploadImage(edtContent.text.toString(), criterias, listImageAdapter.getlistData)
            }
        } else {
            ToastUtils.showShortError(this, getString(R.string.vui_long_dien_day_du_tieu_chi_danh_gia))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(RESULT_CANCELED)
    }
}

