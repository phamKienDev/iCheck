package vn.icheck.android.screen.account.registeruser.register.presetner

import android.content.Intent
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.ValidHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.feature.user.UserInteractor
import vn.icheck.android.network.models.ICReqRegisterUser
import vn.icheck.android.network.models.ICStatus
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.account.registeruser.register.view.IRegisterUserView

class RegisterUserPresenter(val view: IRegisterUserView) : BaseActivityPresenter(view) {
    private val interaction = UserInteractor()

    private var isGoToLogin = false

    fun getData(bundle: Intent?) {
        isGoToLogin = try {
            bundle?.getBooleanExtra(Constant.DATA_1, false) ?: false
        } catch (e: Exception) {
            false
        }
    }

    val getIsGoToLogin: Boolean
        get() {
            return isGoToLogin
        }

    fun registerAccount(phone: String, lastName: String, firstName: String, password: String, rePassword: String) {
        var isValidSuccess = true

        val validPhone = ValidHelper.validPhoneNumber(view.mContext, phone)

        if (validPhone != null) {
            isValidSuccess = false
            view.onErrorPhone(validPhone)
        } else {
            view.onErrorPhone("")
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
                view.onNoInternet()
                return
            }

            val mFirstName = if (firstName.isEmpty()) {
                phone
            } else {
                firstName
            }

            val mLastName = if (lastName.isEmpty()) {
                getString(R.string.nguoi_dung)
            } else {
                lastName
            }

            confirmPhone(phone, mLastName, mFirstName, password)
        }
    }

    private fun confirmPhone(phone: String, lastName: String, firstName: String, password: String) {
        view.showLoading()

        interaction.checkCredentials(phone, object : ICApiListener<ICStatus> {
            override fun onSuccess(obj: ICStatus) {
                if (obj.status == true) {
                    val reqRegisterUser = ICReqRegisterUser(phone, phone, password, lastName, firstName)
                    sendOtpConfirmPhone(phone, reqRegisterUser)
                } else {
                    view.closeLoading()
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

    fun sendOtpConfirmPhone(phone: String, reqRegisterUser: ICReqRegisterUser) {
        interaction.sendOtpConfirmPhone(phone, object : ICApiListener<ICStatus> {
            override fun onSuccess(obj: ICStatus) {
                view.closeLoading()

                if (obj.status == true) {
                    view.onCheckPhoneSuccess(JsonHelper.toJson(reqRegisterUser))
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
}