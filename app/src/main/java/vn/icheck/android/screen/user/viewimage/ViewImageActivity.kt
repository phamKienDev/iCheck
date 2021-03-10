package vn.icheck.android.screen.user.viewimage

import android.Manifest
import kotlinx.android.synthetic.main.activity_view_image.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.ImageHelper
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.network.models.ICThumbnail
import vn.icheck.android.screen.user.viewimage.adapter.ViewImageAdapter
import vn.icheck.android.screen.user.viewimage.presenter.ViewImagePresenter
import vn.icheck.android.screen.user.viewimage.view.IViewImageView

/**
 * Created by VuLCL on 11/14/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class ViewImageActivity : BaseActivity<ViewImagePresenter>(), IViewImageView {
    private val adapter = ViewImageAdapter()

    private val requestStorage = 1

    override val getLayoutID: Int
        get() = R.layout.activity_view_image

    override val getPresenter: ViewImagePresenter
        get() = ViewImagePresenter(this)

    override fun onInitView() {
        setupViewPager()
        initListener()

        presenter.getData(intent)
    }

    private fun setupViewPager() {
        viewPager.adapter = adapter
    }

    private fun initListener() {
        tvClose.setOnClickListener {
            onBackPressed()
        }

        txtDownload.setOnClickListener {
            if (PermissionHelper.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, requestStorage)) {
                downloadImage()
            }
        }
    }

    private fun downloadImage() {
        adapter.getImageFromPosition(viewPager.currentItem)?.let { url ->
            ImageHelper.downloadFileByDownloadManager(this, url)
        }
    }

    override fun onGetDataError() {
        DialogHelper.showNotification(this@ViewImageActivity,
                R.string.co_loi_xay_ra_vui_long_thu_lai,
                false, object : NotificationDialogListener {
            override fun onDone() {
                onBackPressed()
            }
        })
    }

    override fun onGetDataSuccess(list: MutableList<ICThumbnail>, position: Int) {
        adapter.setData(list)
        viewPager.currentItem = position
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == requestStorage) {
            if (PermissionHelper.checkResult(grantResults)) {
                downloadImage()
            } else {
                showShortError(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
            }
        }
    }
}