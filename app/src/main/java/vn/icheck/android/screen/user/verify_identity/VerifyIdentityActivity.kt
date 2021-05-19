package vn.icheck.android.screen.user.verify_identity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_verify_identity.*
import kotlinx.android.synthetic.main.toolbar_light_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.screen.user.crop_camera.CropCameraActivity
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import java.io.File


class VerifyIdentityActivity : BaseActivityMVVM(), View.OnClickListener {
    lateinit var selectPassportDialog: SelectPassportDialog
    private var cameraAfter = true
    private val requestCrop = 1
    lateinit var viewModel: VerifyIdentityViewModel

    private val cameraPermission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
    private val cameraRequest = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_identity)
        viewModel = ViewModelProvider(this).get(VerifyIdentityViewModel::class.java)
        initView()
        listenerData()
    }

    private fun initView() {
        txtTitle.text = getString(R.string.xac_thuc_danh_tinh)
        txtTitle.typeface = ViewHelper.createTypeface(this, R.font.barlow_semi_bold)

        viewModel.typeCard = tvSelectPassport.text.toString()

        WidgetUtils.setClickListener(this, imgBack, tvSelectPassport, imgCameraFront, imgCameraAfter, tvComplete, tvGuide)
    }

    private fun listenerData() {
        viewModel.getData(intent)
        viewModel.onKycStatus.observe(this, {
            when (it) {
                3 -> {
                    tvErrorVerify.visibility = View.VISIBLE
                    tvErrorAfter.visibility = View.VISIBLE
                    tvErrorFront.visibility = View.VISIBLE
                }
                else -> {
                    tvErrorVerify.visibility = View.GONE
                    tvErrorAfter.visibility = View.GONE
                    tvErrorFront.visibility = View.GONE
                }
            }
        })

        viewModel.supportWebData.observe(this, {
            for (i in it) {
                if (i.key == "kyc-benefit") {
                    WebViewActivity.start(this, i.value, null, getString(R.string.xac_thuc_danh_tinh))
                }
            }
        })

        viewModel.onSuccess.observe(this, {
            DialogHelper.closeLoading(this)
            Intent().apply {
                setResult(Activity.RESULT_OK, this)
            }
            onBackPressed()
        })

        viewModel.statusCode.observe(this, {
            when (it.type) {
                ICMessageEvent.Type.ON_NO_INTERNET -> {
                    DialogHelper.closeLoading(this)
                    showShortError(getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
                }
                ICMessageEvent.Type.MESSAGE_ERROR -> {
                    DialogHelper.closeLoading(this)
                    if (it.data !=null && it.data is String) {
                        showShortError(it.data)
                    }
                }
                ICMessageEvent.Type.ON_SHOW_LOADING -> {
                    DialogHelper.showLoading(this)
                }
                ICMessageEvent.Type.ON_CLOSE_LOADING -> {
                    DialogHelper.closeLoading(this)
                }
            }
        })

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imgBack -> {
                onBackPressed()
            }
            R.id.tvSelectPassport -> {
                selectPassportDialog = SelectPassportDialog(tvSelectPassport.text.toString(), object : SelectPassportDialog.SelectedPassportCallback {
                    override fun getSelectPassport(passport: String) {
                        tvSelectPassport.text = passport
                        setImage(passport)
                    }
                }).apply {
                    show(supportFragmentManager, null)
                }
            }
            R.id.imgCameraFront -> {
                if (PermissionHelper.isAllowPermission(this, cameraPermission)) {
                    cameraAfter = false
                    startCamera()
                } else {
                    PermissionHelper.checkPermission(this, cameraPermission, cameraRequest)
                }
            }
            R.id.imgCameraAfter -> {
                if (PermissionHelper.isAllowPermission(this, cameraPermission)) {
                    cameraAfter = true
                    startCamera()
                } else {
                    PermissionHelper.checkPermission(this, cameraPermission, cameraRequest)
                }
            }
            R.id.tvGuide -> {
                viewModel.getSetting()
            }

            R.id.tvComplete -> {
                DialogHelper.showLoading(this)
                viewModel.listImage.clear()
                viewModel.postKyc()
            }
        }
    }

    private fun startCamera() {
        ActivityUtils.startActivityForResult<CropCameraActivity>(this, requestCrop)
    }

    fun setImage(type: String) {
        viewModel.typeCard = type
        if (type == getString(R.string.chung_minh_nhan_dan)) {
            textView46.setText(R.string.mat_truoc_cmnd)
            textView47.setText(R.string.mat_sau_cmnd)
        } else {
            textView46.setText(R.string.mat_truoc_cccd)
            textView47.setText(R.string.mat_sau_cccd)
        }

        if (viewModel.frontImage != null) {
            WidgetUtils.loadImageFile(imgFront, viewModel.frontImage, R.drawable.front_passport, SizeHelper.size4)
        } else {
            imgFront.setImageResource(R.drawable.front_passport)
        }

        if (viewModel.afterImage != null) {
            WidgetUtils.loadImageFile(imgAfter, viewModel.afterImage, R.drawable.after_passport, SizeHelper.size4)
        } else {
            imgAfter.setImageResource(R.drawable.after_passport)
        }
        checkImageCmnd()
    }

    private fun setAfterImage(file: File) {
        viewModel.afterImage = file
        checkImageCmnd()
    }

    private fun setFrontImage(file: File) {
        viewModel.frontImage = file
        checkImageCmnd()
    }

    private fun checkImageCmnd() {
        if (viewModel.afterImage != null && viewModel.frontImage != null) {
            tvComplete.background = vn.icheck.android.ichecklibs.ViewHelper.bgPrimaryCorners4(this@VerifyIdentityActivity)
            tvComplete.isEnabled = true
        } else {
            tvComplete.setBackgroundResource(R.drawable.bg_gray_b4_corners_4)
            tvComplete.isEnabled = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            requestCrop -> {
                if (resultCode == Activity.RESULT_OK) {
                    val path = data?.getStringExtra(Constant.DATA_1)
                    if (data != null) {
                        val file = File(path)
                        if (cameraAfter) {
                            setAfterImage(file)
                            WidgetUtils.loadImageFile(imgAfter, file, R.drawable.after_passport, SizeHelper.size4)
                        } else {
                            setFrontImage(file)
                            WidgetUtils.loadImageFile(imgFront, file, R.drawable.front_passport, SizeHelper.size4)
                        }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            requestCrop -> {
                if (!PermissionHelper.checkResult(grantResults)) {
                    showLongError(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
                    finish()
                }
            }
        }
    }
}