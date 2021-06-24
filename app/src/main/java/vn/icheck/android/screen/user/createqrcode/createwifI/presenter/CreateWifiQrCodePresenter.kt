package vn.icheck.android.screen.user.createqrcode.createwifI.presenter

import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentPresenter
import vn.icheck.android.helper.ValidHelper
import vn.icheck.android.screen.user.createqrcode.createwifI.view.ICreateWifiQrCodeView

/**
 * Created by VuLCL on 10/8/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CreateWifiQrCodePresenter(val view: ICreateWifiQrCodeView) : BaseFragmentPresenter(view) {

    fun validData(name: String, password: String, securityType: String?) {
        var isSuccess = true

        if (password.isEmpty()) {
            isSuccess = false
            view.onInValidPassword(getString(R.string.vui_long_nhap_du_lieu))
        } else {
            if (password.length < 8) {
                isSuccess = false
                view.onInValidPassword(getString(R.string.mat_khau_toi_thieu_la_8_ky_tu))
            } else {
                if (password.contains(" ")) {
                    isSuccess = false
                    view.onInValidPassword(getString(R.string.mat_khau_khong_hop_le))
                } else {
                    view.onInValidPasswordSuccess()
                }
            }
        }

        val validName = ValidHelper.validNameNetWork(view.mContext,name)

        if (validName != null) {
            isSuccess = false
            view.onInValidName(validName)
        } else {
            view.onInValidNameSuccess()
        }

        if (!isSuccess) {
            return
        }

        val security = securityType ?: "WPA"

        val code = view.mContext?.getString(R.string.qr_code_wifi_format, name, password, security)

        view.onValidSuccess(code?:"")
    }
}