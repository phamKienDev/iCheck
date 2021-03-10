package vn.icheck.android.screen.account.registerfacebookphone.presenter

import android.content.Intent
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.ValidHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.auth.AuthInteractor
import vn.icheck.android.network.feature.user.UserInteractor
import vn.icheck.android.network.models.ICSessionData
import vn.icheck.android.network.models.ICStatus
import vn.icheck.android.screen.account.registerfacebookphone.view.RegisterFacebookPhoneView

/**
 * Created by VuLCL on 9/17/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class RegisterFacebookPhonePresenter(val view: RegisterFacebookPhoneView) : BaseActivityPresenter(view) {
    private val userInteraction = UserInteractor()
    private val authInteraction = AuthInteractor()

    private var phone = ""
    private var facebookToken = ""

    fun getData(bundle: Intent?): Boolean {
        phone = try {
            bundle?.getStringExtra(Constant.DATA_1) ?: ""
        } catch (e: Exception) {
            ""
        }

        facebookToken = try {
            bundle?.getStringExtra(Constant.DATA_2) ?: ""
        } catch (e: Exception) {
            ""
        }

        return if (phone.isEmpty() && facebookToken.isEmpty()) {
            view.onGetPhoneError()
            false
        } else {
            true
        }
    }

    fun sendOtp() {
        if (phone.isEmpty()) {
            view.onGetPhoneError()
            return
        }

        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        view.onShowLoading()

        userInteraction.sendOtpConfirmPhone(phone, object : ICApiListener<ICStatus> {
            override fun onSuccess(obj: ICStatus) {
                view.onCloseLoading()

                if (obj.status == true) {
                    view.onCountDownOtp()
                } else {
                    showError(R.string.co_loi_xay_ra_vui_long_thu_lai)
                }
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                val message = error?.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                showError(message)
            }
        })
    }

    fun registerFacebookPhone(otp: String, password: String, rePassword: String) {
        var isValidSuccess = true

        val validOtp = ValidHelper.validOtp(view.mContext, otp)

        if (validOtp != null) {
            isValidSuccess = false
            view.onErrorOtp(validOtp)
        } else {
            view.onErrorOtp("")
        }

        val validPassword = ValidHelper.validPassword(view.mContext, password)

        if (validPassword != null) {
            isValidSuccess = false
            view.onErrorPassword(validPassword)
        } else {
            view.onErrorPassword("")
        }

        val validRePassword = ValidHelper.validPassword(view.mContext, rePassword)

        if (validRePassword != null) {
            isValidSuccess = false
            view.onErrorRePassword(validRePassword)
        } else {
            if (password != rePassword) {
                isValidSuccess = false
                view.onErrorRePassword(getString(R.string.xac_nhan_mat_khau_khong_khop))
            } else {
                view.onErrorRePassword("")
            }
        }

        if (isValidSuccess) {
            if (NetworkHelper.isNotConnected(view.mContext)) {
                showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                return
            }

            if (phone.isEmpty()) {
                view.onGetPhoneError()
                return
            }

            view.onShowLoading()

            authInteraction.loginFacebook(facebookToken, phone, otp, password, object : ICApiListener<ICSessionData> {
                override fun onSuccess(obj: ICSessionData) {
                    view.onCloseLoading()

                    if (obj.token.isNullOrEmpty() || obj.tokenType.isNullOrEmpty()) {
                        showError(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    } else {
                        SessionManager.session = obj
                        view.onLoginSuccess()
                    }
                }

                override fun onError(error: ICBaseResponse?) {
                    view.onCloseLoading()
                    val message = error?.message
                            ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    showError(message)
                }
            })
        }
    }
}