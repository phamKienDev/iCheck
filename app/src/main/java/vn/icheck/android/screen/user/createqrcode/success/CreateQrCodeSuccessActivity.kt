package vn.icheck.android.screen.user.createqrcode.success

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import kotlinx.android.synthetic.main.fragment_create_qr_code_success.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.screen.dialog.PermissionDialog
import vn.icheck.android.screen.user.createqrcode.success.presenter.CreateQrCodeSuccessPresenter
import vn.icheck.android.screen.user.createqrcode.success.view.ICreateQrCodeSuccessView
import vn.icheck.android.util.ick.showSimpleSuccessToast

/**
 * Created by VuLCL on 10/5/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CreateQrCodeSuccessActivity : BaseActivity<CreateQrCodeSuccessPresenter>(), ICreateQrCodeSuccessView {
    private val requestSave = 1
    private val requestShare = 2

    override val getLayoutID: Int
        get() = R.layout.fragment_create_qr_code_success

    override val getPresenter: CreateQrCodeSuccessPresenter
        get() = CreateQrCodeSuccessPresenter(this)

    override fun onInitView() {
        presenter.getData(intent)
        initListener()
    }

    private fun initListener() {
        val listPermission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

        tvClose.setOnClickListener {
            onBackPressed()
        }

        imgCreate.setOnClickListener {
            setResult(Activity.RESULT_OK)
            onBackPressed()
        }

        imgDownload.setOnClickListener {
            PermissionDialog.checkPermission(this@CreateQrCodeSuccessActivity, PermissionDialog.STORAGE, object : PermissionDialog.PermissionListener {
                override fun onPermissionAllowed() {
                    presenter.saveQrCode()
                }

                override fun onRequestPermission() {
                    PermissionHelper.checkPermission(this@CreateQrCodeSuccessActivity, listPermission, requestSave)
                }

                override fun onPermissionNotAllow() {
                    showShortError(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
                }
            })
        }

        imgSave.setOnClickListener {
            PermissionDialog.checkPermission(this@CreateQrCodeSuccessActivity, PermissionDialog.STORAGE, object : PermissionDialog.PermissionListener {
                override fun onPermissionAllowed() {
                    presenter.shareQrCode()
                }

                override fun onRequestPermission() {
                    PermissionHelper.checkPermission(this@CreateQrCodeSuccessActivity, listPermission, requestShare)
                }

                override fun onPermissionNotAllow() {
                    showShortError(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
                }
            })
        }
    }

    override fun onGetDataError() {
        DialogHelper.showNotification(this@CreateQrCodeSuccessActivity, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
            override fun onDone() {
                onBackPressed()
            }
        })
    }

    override fun onShowQrCode(bitmap: Bitmap) {
        imgQrCode.setImageBitmap(bitmap)
    }

    override fun onSaveQrCodeSuccess() {
        showSimpleSuccessToast("Tải xuống thành công")
    }

    override fun onShareQrCode(contentUri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(contentUri, contentResolver.getType(contentUri))
        intent.putExtra(Intent.EXTRA_STREAM, contentUri)
        startActivity(Intent.createChooser(intent, "iCheck Share"))
    }

    override fun showError(errorMessage: String) {
        super.showError(errorMessage)

        showShortError(errorMessage)
    }

    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == requestSave) {
            if (PermissionHelper.checkResult(grantResults)) {
                presenter.saveQrCode()
            } else {
                showLongError(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
            }
        } else if (requestCode == requestShare) {
            if (PermissionHelper.checkResult(grantResults)) {
                presenter.shareQrCode()
            } else {
                showLongError(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
            }
        }
    }
}