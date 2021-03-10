package vn.icheck.android.screen.account.registeruser.inputotp.presenter

import android.content.Intent
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.feature.user.UserInteractor
import vn.icheck.android.network.models.ICNone
import vn.icheck.android.network.models.ICReqRegisterUser
import vn.icheck.android.network.models.ICStatus
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.account.registeruser.inputotp.view.IRegisterUserOtpView

class RegisterUserOtpPresenter(val view: IRegisterUserOtpView) : BaseActivityPresenter(view) {
    private val interaction = UserInteractor()

    private var reqRegisterUser: ICReqRegisterUser? = null

    private var isGoToLogin = false

    fun getData(bundle: Intent?) {
        reqRegisterUser = try {
            JsonHelper.parseJson(bundle?.getStringExtra(Constant.DATA_1), ICReqRegisterUser::class.java)
        } catch (e: Exception) {
            null
        }

        isGoToLogin = try {
            bundle?.getBooleanExtra(Constant.DATA_2, false) ?: false
        } catch (e: Exception) {
            false
        }

        if (reqRegisterUser != null) {
            view.onCountDownOtp()
            view.onGetDataSuccess(reqRegisterUser!!.username)
        } else {
            view.onGetDataError()
        }
    }

    val getIsGoToLogin: Boolean
        get() {
            return isGoToLogin
        }

    fun sendOtpConfirmPhone() {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onNoInternet(1)
            return
        }

        if (reqRegisterUser == null) {
            view.onGetDataError()
            return
        }


        UserInteractor().sendOtpConfirmPhone(reqRegisterUser!!.username, object : ICApiListener<ICStatus> {
            override fun onSuccess(obj: ICStatus) {
                view.onCountDownOtp()
            }

            override fun onError(error: ICBaseResponse?) {
                error?.message?.let {
                    showError(it)
                }
            }
        })
    }

    fun confirmOtp(otp: String) {
        if (otp.isEmpty()) {
            view.onErrorOtp(getString(R.string.ma_xac_nhan_khong_duoc_de_trong))
            return
        }

        if (otp.contains(" ") || otp.length != 6) {
            view.onErrorOtp(getString(R.string.ma_xac_nhan_khong_hop_le))
            return
        }

        view.onErrorOtp("")

        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onNoInternet(2)
            return
        }

        if (reqRegisterUser == null) {
            view.onGetDataError()
            return
        }

        view.onShowLoading()

        interaction.confirmOtp(reqRegisterUser!!.username, otp, object : ICApiListener<ICStatus> {
            override fun onSuccess(obj: ICStatus) {
                if (obj.status == true) {
                    registerAccount(obj.timestamp)
                } else {
                    showError(R.string.co_loi_xay_ra_vui_long_thu_lai)
                }
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()

                error?.message?.let {
                    showError(it)
                }
            }
        })
    }

    fun registerAccount(timestamp: String?) {
        if (reqRegisterUser == null) {
            view.onGetDataError()
            return
        }

        reqRegisterUser!!.timestamp = timestamp

        val json = JsonHelper.toJson(reqRegisterUser!!)

        interaction.registerUser(reqRegisterUser!!, object : ICApiListener<ICNone> {
            override fun onSuccess(obj: ICNone) {
                view.onCloseLoading()
                view.onRegisterSuccess(reqRegisterUser!!.username)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()

                error?.message?.let {
                    showError(it)
                }
            }
        })
    }
}