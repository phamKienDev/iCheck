package vn.icheck.android.screen.user.crop_camera

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.hardware.Camera.PictureCallback
import android.os.Bundle
import android.os.Environment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_crop_camera.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.TimeHelper
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt


class CropCameraActivity : BaseActivityMVVM() {
    lateinit var viewModel: CropCameraViewModel
    private var mCamera: Camera? = null
    private var mPreview: CameraPreview? = null
    private var mPicture: PictureCallback? = null

    private var clickTakePicture = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_camera)
        viewModel = ViewModelProvider(this).get(CropCameraViewModel::class.java)

        initView()
        initCamera()
    }

    private fun initView() {
        btn_clear.setOnClickListener {
            onBackPressed()
        }

        img_flash.setOnClickListener {
            setFlashLight()
        }

        btnCapture.setOnClickListener {
            if (clickTakePicture) {
                mCamera?.takePicture(null, null, getPictureCallback())
                clickTakePicture = false
            }
        }

    }

    private fun initCamera() {
        mCamera = Camera.open()
        mCamera?.setDisplayOrientation(90)
        mPreview = mCamera?.let {
            CameraPreview(this, it)
        }.also {
            camera_preview.addView(it)
        }
        mPicture = getPictureCallback()
        mCamera?.startPreview()
    }

    override fun onResume() {
        super.onResume()
        if (mCamera == null) {
            initCamera()
        }
    }

    private fun releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera!!.stopPreview()
            mCamera!!.setPreviewCallback(null)
            mCamera!!.release()
            mCamera = null
        }
    }

    private fun setFlashLight() {
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            viewModel.setIsFlash(!viewModel.getIsFlash)
            val p = mCamera?.parameters
            if (viewModel.getIsFlash) {
                p?.flashMode = Camera.Parameters.FLASH_MODE_TORCH
            } else {
                p?.flashMode = Camera.Parameters.FLASH_MODE_OFF
            }
            mCamera?.parameters = p
            mCamera?.startPreview()

            img_flash.setImageResource(if (viewModel.getIsFlash) {
                R.drawable.ic_flash_on_24px
            } else {
                R.drawable.ic_flash_off_24px
            })
        } else {
            showShortError("Không có đèn flash trên thiết bị của bạn")
        }
    }

    private fun getPictureCallback(): PictureCallback {
        return PictureCallback { data, _ ->
            mCamera?.stopPreview()
            DialogHelper.showLoading(this)
            val bitmapPicture = BitmapFactory.decodeByteArray(data, 0, data.size)
            //rotate bitmap, because camera sensor usually in landscape mode
            val matrix = Matrix()
            matrix.postRotate(90F)
            val rotatedBitmap = Bitmap.createBitmap(bitmapPicture, 0, 0,
                    bitmapPicture.width, bitmapPicture.height, matrix, true)

            //calculate aspect ratio
            val koefX = rotatedBitmap.width.toFloat() / camera_preview.width.toFloat()
            val koefY: Float = rotatedBitmap.height.toFloat() / camera_preview.height

            //get viewfinder border size and position on the screen

            //get viewfinder border size and position on the screen
            val x1: Int = img_scan_focus.left
            val y1: Int = img_scan_focus.top

            val x2: Int = img_scan_focus.width
            val y2: Int = img_scan_focus.height


            //calculate position and size for cropping
            val cropStartX = (x1 * koefX).roundToInt()
            val cropStartY = (y1 * koefY).roundToInt()

            val cropWidthX = (x2 * koefX).roundToInt()
            val cropHeightY = (y2 * koefY).roundToInt()

            //check limits and make crop
            val croppedBitmap = if (cropStartX + cropWidthX <= rotatedBitmap.width &&
                    cropStartY + cropHeightY <= rotatedBitmap.height) {
                Bitmap.createBitmap(rotatedBitmap, cropStartX,
                        cropStartY, cropWidthX, cropHeightY)
            } else {
                null
            }

            //save result
            if (croppedBitmap != null) {
                createImageFile(croppedBitmap)
            }
        }
    }

    private fun createImageFile(bitmap: Bitmap) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                // Create an image file name
                try {
                    val timeStamp: String = TimeHelper.getCreteTimeDate()
                    val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    val file = File.createTempFile(
                            "JPEG_${timeStamp}_", /* prefix */
                            ".jpg", /* suffix */
                            storageDir /* directory */
                    )
                    val os = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
                    os.flush()
                    os.close()
                    clickTakePicture = true
                    withContext(Dispatchers.Main) {
                        DialogHelper.closeLoading(this@CropCameraActivity)
                        setResult(Activity.RESULT_OK, Intent().apply { putExtra(Constant.DATA_1, file.absolutePath) })
                        onBackPressed()
                    }

                } catch (e: Exception) {

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseCamera()
    }

}