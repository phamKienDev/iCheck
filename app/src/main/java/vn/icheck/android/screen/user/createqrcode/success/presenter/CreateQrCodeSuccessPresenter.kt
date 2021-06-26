package vn.icheck.android.screen.user.createqrcode.success.presenter

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.AsyncTask
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.screen.user.createqrcode.success.view.ICreateQrCodeSuccessView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by VuLCL on 10/5/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CreateQrCodeSuccessPresenter(val view: ICreateQrCodeSuccessView) : BaseActivityPresenter(view) {
    private var qrCodeBitmap: Bitmap? = null

    private val iCheckName = getString(R.string.i_check)

    fun getData(intent: Intent?) {
        val code = try {
            intent?.getStringExtra(Constant.DATA_1)
        } catch (e: Exception) {
            null
        }

        qrCodeBitmap = generateQrCode(code)

        if (qrCodeBitmap == null) {
            view.onGetDataError()
        } else {
            view.onShowQrCode(qrCodeBitmap!!)
        }
    }

    private fun generateQrCode(text: String?): Bitmap? {
        if (text.isNullOrEmpty()) {
            return null
        }

        return try {
            val writer = QRCodeWriter()

            val hints = mutableMapOf<EncodeHintType, Any>()
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            hints[EncodeHintType.MARGIN] = 2

            val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512, hints)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                }
            }
            bmp
        } catch (e: Exception) {
            null
        }
    }

    fun saveQrCode(bitmap: Bitmap? = null) {
        if (qrCodeBitmap == null) {
            return
        }

//        if (getUriFromImageUrl(savedImageURL)) {
//            listener.onSaveQrCodeSuccess()
//            return
//        }

        val fileName = "${iCheckName}_${System.currentTimeMillis()}"

        val savedImageURL = MediaStore.Images.Media.insertImage(
                view.mContext.contentResolver,
                bitmap ?: qrCodeBitmap,
                fileName,
                "Generate QR code from iCheck Application"
        )

        if (!savedImageURL.isNullOrEmpty()) {
            view.onSaveQrCodeSuccess()
        } else {
            showError(R.string.co_loi_xay_ra_vui_long_thu_lai)
        }
    }

    fun shareQrCode() {
        if (qrCodeBitmap == null) {
            return
        }

        ShareImage(this).execute()
    }

    companion object {
        class ShareImage(private val createQrCodeSuccessPresenter: CreateQrCodeSuccessPresenter) : AsyncTask<Bitmap, String, File>() {
            override fun doInBackground(vararg params: Bitmap?): File? {
                val fileFolder = (Environment.getExternalStorageDirectory().toString()
                        + "/Android/data/"
                        + createQrCodeSuccessPresenter.view.mContext.applicationContext.packageName
                        + "/Files" +
                        "/Share")

                val dir = File(fileFolder)

                if (!dir.exists())
                    dir.mkdirs()

                val filename = dir.absolutePath + "/" + System.currentTimeMillis() + ".png"

                try {
                    FileOutputStream(filename).use { out ->
                        createQrCodeSuccessPresenter.qrCodeBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }

                    val file = File(filename)

                    if (file.exists()) {
                        return file
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                return null
            }

            override fun onPostExecute(result: File?) {
                super.onPostExecute(result)

                if (createQrCodeSuccessPresenter.view.mContext == null) {
                    return
                }

                if (result != null && result.exists()) {
                    val fileprovider = createQrCodeSuccessPresenter.view.mContext.getString(R.string.xxx_fileprovider, createQrCodeSuccessPresenter.view.mContext.applicationContext.packageName)
                    val contentUri = FileProvider.getUriForFile(createQrCodeSuccessPresenter.view.mContext, fileprovider, result)

                    if (contentUri != null) {
                        createQrCodeSuccessPresenter.view.onShareQrCode(contentUri)
                    } else {
                        createQrCodeSuccessPresenter.showError(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    }
                } else {
                    createQrCodeSuccessPresenter.showError(R.string.co_loi_xay_ra_vui_long_thu_lai)
                }
            }
        }
    }
}