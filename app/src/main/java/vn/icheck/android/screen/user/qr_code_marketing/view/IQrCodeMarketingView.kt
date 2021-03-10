package vn.icheck.android.screen.user.qr_code_marketing.view

import android.graphics.Bitmap
import vn.icheck.android.base.activity.BaseActivityView

interface IQrCodeMarketingView : BaseActivityView {
    fun onGetDataError()
    fun onShowQrCode(qrCodeBitmap: Bitmap)
}