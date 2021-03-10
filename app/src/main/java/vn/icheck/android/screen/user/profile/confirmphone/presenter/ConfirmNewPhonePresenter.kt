package vn.icheck.android.screen.user.profile.confirmphone.presenter

import android.content.Intent
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.tracking.insider.InsiderHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.user.UserInteractor
import vn.icheck.android.network.models.ICReqUpdateUser
import vn.icheck.android.network.models.ICStatus
import vn.icheck.android.network.models.ICUser
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.user.profile.confirmphone.view.IConfirmNewPhoneView

/**
 * Created by VuLCL on 11/7/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class ConfirmNewPhonePresenter(val view: IConfirmNewPhoneView) : BaseActivityPresenter(view) {
    private val interaction = UserInteractor()

    private var reqUpdateUser: ICReqUpdateUser? = null
    private var phone: String? = null

    fun getData(bundle: Intent?) {
        phone = try {
            bundle?.getStringExtra(Constant.DATA_1)
        } catch (e: Exception) {
            null
        }

        reqUpdateUser = try {
            JsonHelper.parseJson(bundle?.getStringExtra(Constant.DATA_2), ICReqUpdateUser::class.java)
        } catch (e: Exception) {
            null
        }

        if (phone != null) {
            view.onCountDownOtp()
            view.onGetDataSuccess(phone!!)
        } else {
            view.onGetDataError()
        }
    }

    fun sendOtpConfirmPhone() {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        if (phone == null) {
            view.onGetDataError()
            return
        }

        UserInteractor().sendOtpConfirmPhone(phone!!, object : ICApiListener<ICStatus> {
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

        if (phone == null) {
            view.onGetDataError()
            return
        }

        view.onShowLoading()

        interaction.confirmNewPhone(phone!!, otp, object : ICApiListener<ICUser> {
            override fun onSuccess(obj: ICUser) {
                if (reqUpdateUser == null) {
                    view.onUpdateSuccess()
                } else {
                    updateAccount()
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

    fun updateAccount() {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        val userID = SessionManager.session.user?.id

        if (reqUpdateUser == null || userID == null) {
            view.onGetDataError()
            return
        }

        interaction.updateUser(userID, reqUpdateUser!!, object : ICApiListener<ICUser> {
            override fun onSuccess(obj: ICUser) {
                view.onCloseLoading()
                SessionManager.updateUser(obj)
                InsiderHelper.setUserAttributes()
                view.onUpdateSuccess()
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