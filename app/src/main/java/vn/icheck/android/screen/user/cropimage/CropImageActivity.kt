package vn.icheck.android.screen.user.cropimage

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_crop_image.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.ActivityUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by VuLCL on 7/22/2020.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 *
 * Mô tả:
 * - Đầu vào (intent):
 * + data_1 - String: path của file ảnh
 *
 * - Đầu ra (intent):
 * + data_1 - String: path của image sau khi đã cropped
 */
class CropImageActivity : BaseActivityMVVM() {

    private val requestPermission = 1

    companion object {

        var showBottom = false


        fun start(activity: Activity, filePath: String?, uri: Uri?, widthRatio: String?, heightRatio: String?, requestCode: Int? = null) {
            if (!widthRatio.isNullOrEmpty() && !heightRatio.isNullOrEmpty()) {
                start(activity, filePath, uri, "$widthRatio:$heightRatio", requestCode)
            } else {
                start(activity, filePath, uri, null, requestCode)
            }
        }

        fun start(activity: Activity, filePath: String?, uri: Uri?, ratio: String?, requestCode: Int? = null) {
            val intent = Intent(activity, CropImageActivity::class.java)

            if (!filePath.isNullOrEmpty()) {
                intent.putExtra(Constant.DATA_1, filePath)
            }
            if (uri != null) {
                intent.putExtra(Constant.DATA_2, uri)
            }
            if (!ratio.isNullOrEmpty()) {
                intent.putExtra(Constant.DATA_3, ratio)
            }

            if (requestCode != null) {
                ActivityUtils.startActivityForResult(activity, intent, requestCode)
            } else {
                ActivityUtils.startActivity(activity, intent)
            }
        }

        fun start(fragment: Fragment, filePath: String?, uri: Uri?, widthRatio: String?, heightRatio: String?, requestCode: Int? = null) {
            if (!widthRatio.isNullOrEmpty() && !heightRatio.isNullOrEmpty()) {
                start(fragment, filePath, uri, "$widthRatio:$heightRatio", requestCode)
            } else {
                start(fragment, filePath, uri, null, requestCode)
            }
        }

        fun start(fragment: Fragment, filePath: String?, uri: Uri?, ratio: String?, requestCode: Int? = null) {
            val intent = Intent(fragment.requireContext(), CropImageActivity::class.java)

            if (!filePath.isNullOrEmpty()) {
                intent.putExtra(Constant.DATA_1, filePath)
            }
            if (uri != null) {
                intent.putExtra(Constant.DATA_2, uri)
            }
            if (!ratio.isNullOrEmpty()) {
                intent.putExtra(Constant.DATA_3, ratio)
            }

            if (requestCode != null) {
                ActivityUtils.startActivityForResult(fragment, intent, requestCode)
            } else {
                ActivityUtils.startActivity(fragment, intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_image)

        setupCropper()
        setupListener()
        if (showBottom) {
            layoutBottom.beVisible()
        } else {
            layoutBottom.beGone()
        }
    }

    private fun setupCropper() {
        val file = File(intent?.getStringExtra(Constant.DATA_1) ?: "")

        if (file.exists()) {
            imageView.setImageUriAsync(file.toUri())
        } else {
            val uri = intent.getSerializableExtra(Constant.DATA_2) as Uri?

            if (uri != null) {
                imageView.setImageUriAsync(uri)
            } else {
                DialogHelper.showNotification(this@CropImageActivity, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                    override fun onDone() {
                        onBackPressed()
                    }
                })
            }
        }

        intent?.getStringExtra(Constant.DATA_3)?.let { ratio ->
            try {
                val ratio = ratio.split(":")
                imageView.setAspectRatio(ratio[0].toInt(), ratio[1].toInt())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupListener() {
        btnCancel.setOnClickListener {
            onBackPressed()
        }

        btnDone.setOnClickListener {
            if (PermissionHelper.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, requestPermission)) {
                cropImage()
            } else {
                showLongError(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
            }
        }
    }

    private fun cropImage() {
        lifecycleScope.launch {
            layoutLoading.visibility = View.VISIBLE
            var isSaveSuccess = false
            var path = ""
            val bm = imageView.croppedImage
            withContext(Dispatchers.IO) {
                try {
                    path = cacheDir.absolutePath + "/" + System.currentTimeMillis() + ".png"
                    FileOutputStream(path).use { out ->
                        isSaveSuccess = if (out != null) {
                            bm.compress(Bitmap.CompressFormat.PNG, 100, out)
                        } else {
                            false
                        }
                    }
                } catch (e: IOException) {
                }
            }

            layoutLoading.visibility = View.GONE

            if (isSaveSuccess && path.isNotEmpty()) {
                setResult(Activity.RESULT_OK, Intent().apply { putExtra(Constant.DATA_1, path) })
                onBackPressed()
            } else {
                showLongError(R.string.co_loi_xay_ra_vui_long_thu_lai)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == requestPermission) {
            if (PermissionHelper.checkResult(grantResults)) {
                cropImage()
            } else {
                showLongError(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
            }
        }
    }
}