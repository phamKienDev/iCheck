package vn.icheck.android.screen.user.shopreview

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.TableRow
import androidx.appcompat.widget.AppCompatTextView
import com.willy.ratingbar.ScaleRatingBar
import kotlinx.android.synthetic.main.activity_shop_review.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.helper.TakePhotoHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.ICCriteriaShop
import vn.icheck.android.screen.user.shopreview.adapter.ShopReviewAdapter
import vn.icheck.android.screen.user.shopreview.entity.ShopReviewImage
import vn.icheck.android.screen.user.shopreview.presenter.OrderReviewPresenter
import vn.icheck.android.screen.user.shopreview.view.IOrderReviewView
import java.io.File

/**
 * Created by VuLCL on 2/2/2020.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class OrderReviewActivity : BaseActivity<OrderReviewPresenter>(), IOrderReviewView, TakePhotoHelper.TakePhotoListener {
    private val adapter = ShopReviewAdapter(1, this)

    private val takePhotoHelper = TakePhotoHelper(this)
    private val requestCameraPermission = 1

    override val getLayoutID: Int
        get() = R.layout.activity_shop_review

    override val getPresenter: OrderReviewPresenter
        get() = OrderReviewPresenter(this)

    override fun onInitView() {
        setupToolbar()
        setupRecyclerView()
        setupListener()
        edtNote.background=ViewHelper.bgTransparentRadius10StrokeLineColor1(this)
        presenter.getShopID(intent)
    }

    private fun setupToolbar() {
        txtTitle.setText(R.string.danh_gia_don_hang)

        imgBack.setImageResource(R.drawable.ic_close_blue_24)

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        recyclerView.adapter = adapter
    }

    private fun setupListener() {
        tvDone.setOnClickListener {
            val list = mutableListOf<ICCriteriaShop.Criteria>()

            for (i in 0 until tableLayout.childCount) {
                val tableRow = tableLayout.getChildAt(i) as TableRow
                val criteria = tableRow.tag as ICCriteriaShop.Criteria
                criteria.rating = (tableRow.getChildAt(1) as ScaleRatingBar).rating
                list.add(criteria)
            }

            presenter.reviewShop(list, edtNote.text.toString().trim(), adapter.getListData)
        }
    }

    override fun onGetShopIDError() {
        DialogHelper.showNotification(this@OrderReviewActivity, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
            override fun onDone() {
                onBackPressed()
            }
        })
    }

    override fun onGetCriteriaError(error: String) {
        DialogHelper.showConfirm(this@OrderReviewActivity, error, false, object : ConfirmDialogListener {
            override fun onDisagree() {
                onBackPressed()
            }

            override fun onAgree() {
                presenter.getListCriteria()
            }
        })
    }

    override fun onGetCriteriaSuccess(list: MutableList<ICCriteriaShop>) {
        list.sortBy { it.position }

        for (it in list) {
            val tableRow = LayoutInflater.from(this).inflate(R.layout.item_shop_review_criteria, tableLayout, false) as TableRow

            (tableRow.getChildAt(0) as AppCompatTextView).text = it.criteria.name
            (tableRow.getChildAt(1) as ScaleRatingBar).run {
                rating = 4F
                setOnRatingChangeListener { ratingBar, rating, fromUser ->
                    var isOk = true

                    for (i in 0 until tableLayout.childCount) {
                        val tbr = tableLayout.getChildAt(i) as TableRow

                        if ((tbr.getChildAt(1) as ScaleRatingBar).rating == 0f) {
                            isOk = false
                        }
                    }

                    tvDone.isEnabled = isOk
                }
            }
            tableRow.tag = it.criteria

            tableLayout.addView(tableRow)
        }
    }

    override fun onShowLoading() {
        DialogHelper.showLoading(this)
    }

    override fun onCloseLoading() {
        DialogHelper.closeLoading(this)
    }

    override fun onAddImage() {
        val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (PermissionHelper.checkPermission(this@OrderReviewActivity, permission, requestCameraPermission)) {
            takePhotoHelper.takePhoto(this@OrderReviewActivity)
        }
    }

    override fun onAddImageSuccess(obj: ShopReviewImage) {
        adapter.addImage(obj)
    }

    override fun onDeleteImage(position: Int) {
        DialogHelper.showConfirm(this@OrderReviewActivity, R.string.ban_muon_xoa_anh_nay, true, object : ConfirmDialogListener {
            override fun onDisagree() {

            }

            override fun onAgree() {
                adapter.deleteImage(position)
            }
        })
    }

    override fun onReviewSuccess() {
        showLongSuccess(R.string.danh_gia_don_hang_thanh_cong)
        onBackPressed()
    }

    override fun onTakePhotoSuccess(file: File?) {
        if (file != null) {
            presenter.uploadImage(file)
        }
    }

    override fun onPickMultiImageSuccess(file: MutableList<File>) {

    }

    override fun showError(errorMessage: String) {
        super.showError(errorMessage)

        showShortError(errorMessage)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == requestCameraPermission) {
            if (PermissionHelper.checkResult(grantResults)) {
                takePhotoHelper.takePhoto(this@OrderReviewActivity)
            } else {
                showLongWarning(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        takePhotoHelper.onActivityResult(this, requestCode, resultCode, data)
    }
}