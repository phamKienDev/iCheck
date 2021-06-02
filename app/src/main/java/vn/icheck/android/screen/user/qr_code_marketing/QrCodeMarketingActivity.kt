package vn.icheck.android.screen.user.qr_code_marketing

import android.graphics.Bitmap
import kotlinx.android.synthetic.main.activity_qr_code_marketing.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.screen.user.qr_code_marketing.presenter.QrCodeMarketingPresenter
import vn.icheck.android.screen.user.qr_code_marketing.view.IQrCodeMarketingView

class QrCodeMarketingActivity : BaseActivity<QrCodeMarketingPresenter>(), IQrCodeMarketingView {
    override val getLayoutID: Int
        get() = R.layout.activity_qr_code_marketing
    override val getPresenter: QrCodeMarketingPresenter
        get() = QrCodeMarketingPresenter(this)

    override fun onInitView() {
        imgLogo.background=ViewHelper.bgWhiteStrokeSecondary1Corners10(this)
        imgBanner.background=ViewHelper.bgWhiteCornersTop10(this)

        presenter.createQrCodeMarketing(SessionManager.session.user, SettingManager.clientSetting, imgBanner, tvMess, btnJoin, this@QrCodeMarketingActivity, imgLogo)
        initListener()
    }

    private fun initListener() {
        tvClose.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onGetDataError() {
        DialogHelper.showNotification(this@QrCodeMarketingActivity, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
            override fun onDone() {
                onBackPressed()
            }
        })
    }

    override fun onShowQrCode(qrCodeBitmap: Bitmap) {
        imgQrCode.setImageBitmap(qrCodeBitmap)
    }
}
