package vn.icheck.android.loyalty.screen.voucher

import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.activity_voucher_loyalty.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.loyalty.helper.TimeHelper

class VoucherLoyaltyActivity : BaseActivityGame() {

    override val getLayoutID: Int
        get() = R.layout.activity_voucher_loyalty

    override fun onInitView() {
        val code = intent.getStringExtra(ConstantsLoyalty.DATA_1)
        val date = intent.getStringExtra(ConstantsLoyalty.DATA_2)

        val qrCode = try {
            val writer = QRCodeWriter()

            val hints = mutableMapOf<EncodeHintType, Any>()
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            hints[EncodeHintType.MARGIN] = 2

            val bitMatrix = writer.encode(code, BarcodeFormat.QR_CODE, 512, 512, hints)
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

        imgQrCode.setImageBitmap(qrCode)

        tvCode.apply {
            text = code

            setOnClickListener {
                isChecked = !isChecked
            }
        }

        tvDate.text = TimeHelper.convertDateTimeSvToDateVn(date)
    }
}