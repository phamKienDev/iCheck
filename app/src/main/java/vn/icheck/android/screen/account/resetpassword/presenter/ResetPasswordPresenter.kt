package vn.icheck.android.screen.account.resetpassword.presenter

import android.content.Intent
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.ValidHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.ICID
import vn.icheck.android.network.feature.user.UserInteractor
import vn.icheck.android.network.models.ICStatus
import vn.icheck.android.screen.account.resetpassword.view.IResetPasswordView

/**
 * Created by VuLCL on 9/18/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class ResetPasswordPresenter(val view: IResetPasswordView) : BaseActivityPresenter(view) {
    private val userInteraction = UserInteractor()
    private var phone: String = ""

    fun getData(data: Intent?) {
        phone = try {
            data?.getStringExtra(Constant.DATA_1) ?: ""
        } catch (e: Exception) {
            ""
        }

        if (phone.isEmpty()) {
            view.onGetDataError()
        } else {
            view.onGetDataSuccess()
        }
    }

    fun sendOtp() {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onNoInternet(1)
            return
        }

        view.showLoading()

        userInteraction.sendOtpResetPassword(phone, object : ICApiListener<ICStatus> {
            override fun onSuccess(obj: ICStatus) {
                view.closeLoading()

                if (obj.status == true) {
                    view.onCountDownOtp()
                } else {
                    showError(R.string.co_loi_xay_ra_vui_long_thu_lai)
                }
            }

            override fun onError(error: ICBaseResponse?) {
                view.closeLoading()

                error?.message?.let {
                    showError(it)
                }
            }
        })
    }

    fun resetPassword(otp: String, password: String, rePassword: String) {
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
            changePassword(otp, password)
        }
    }

    private fun changePassword(otp: String, password: String) {
        if (phone.isEmpty()) {
            view.onGetDataError()
            return
        }

        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onNoInternet(2)
            return
        }

        view.showLoading()

        userInteraction.resetPassword(phone, otp, null, password, object : ICApiListener<ICID> {
            override fun onSuccess(obj: ICID) {
                view.closeLoading()
                view.onResetPasswordSuccess()
            }

            override fun onError(error: ICBaseResponse?) {
                view.closeLoading()

                error?.message?.let {
                    showError(it)
                }
            }
        })
    }
}