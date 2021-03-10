package vn.icheck.android.screen.user.qr_code_marketing.presenter

import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.models.ICClientSetting
import vn.icheck.android.network.models.ICUser
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.screen.user.qr_code_marketing.view.IQrCodeMarketingView
import vn.icheck.android.util.kotlin.WidgetUtils
import java.sql.Timestamp
import java.util.*

class QrCodeMarketingPresenter(val view: IQrCodeMarketingView) : BaseActivityPresenter(view) {
    private var qrCodeBitmap: Bitmap? = null

    fun createQrCodeMarketing(user: ICUser?, setting: ICClientSetting?, imgBanner: AppCompatImageView, tvMess: AppCompatTextView, btnJoin: AppCompatTextView, activity: FragmentActivity, imgLogo: AppCompatImageView) {
        val date = Date()
        val time = Timestamp(date.time)
        val timestampMillisecond = TimeHelper.convertDateTimeStampVnToMillisecond99(time.toString()).toString()

        val phone = user?.phone

        WidgetUtils.loadImageUrl(imgLogo, user?.avatar_thumbnails?.medium, R.drawable.ic_logo_icheck_marketing)

        val qrCode = "${timestampMillisecond.substring(0, 4)}-${phone?.substring(0, 4)}-${phone?.substring(4, 8)}-${phone?.substring(8, 10) + timestampMillisecond.substring(7, 9)}-${timestampMillisecond.substring(9, 13)}"

        getData(qrCode)

        if (setting?.my_card != null) {

            if (!setting?.my_card?.banner.isNullOrEmpty()) {
                imgBanner.visibility = View.VISIBLE
                WidgetUtils.loadImageUrl(imgBanner, setting?.my_card?.banner)
            } else {
                imgBanner.visibility = View.INVISIBLE
            }

            if (!setting?.my_card?.event_scheme.isNullOrEmpty()) {
                btnJoin.visibility = View.VISIBLE
                btnJoin.setOnClickListener {
                    FirebaseDynamicLinksActivity.startDestinationUrl(activity, setting?.my_card?.event_scheme!!)
                }
            } else {
                btnJoin.visibility = View.INVISIBLE
            }

            if (!setting?.my_card?.event_desc.isNullOrEmpty()) {
                tvMess.visibility = View.VISIBLE
                tvMess.text = setting?.my_card?.event_desc
            } else {
                tvMess.visibility = View.INVISIBLE
            }
        } else {
            tvMess.visibility = View.INVISIBLE
            imgBanner.visibility = View.INVISIBLE
            btnJoin.visibility = View.INVISIBLE
        }
    }

    private fun getData(code: String) {
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
}