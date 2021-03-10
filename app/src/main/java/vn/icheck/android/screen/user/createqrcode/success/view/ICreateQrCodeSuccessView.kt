package vn.icheck.android.screen.user.createqrcode.success.view

import android.graphics.Bitmap
import android.net.Uri
import vn.icheck.android.base.activity.BaseActivityView

/**
 * Created by VuLCL on 10/5/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
interface ICreateQrCodeSuccessView : BaseActivityView {

    fun onGetDataError()
    fun onShowQrCode(bitmap: Bitmap)
    fun onSaveQrCodeSuccess()
    fun onShareQrCode(contentUri: Uri)
}

