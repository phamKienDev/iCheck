package vn.icheck.android.screen.user.detail_stamp_v6_1.otp_information_guarantee.presenter

import android.content.Intent
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.detail_stamp_v6_1.DetailStampInteractor
import vn.icheck.android.network.feature.user.UserInteractor
import vn.icheck.android.network.models.ICStatus
import vn.icheck.android.network.models.detail_stamp_v6_1.ICUpdateCustomerGuarantee
import vn.icheck.android.network.models.detail_stamp_v6_1.IC_RESP_UpdateCustomerGuarantee
import vn.icheck.android.network.util.DeviceUtils
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.DetailStampActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.otp_information_guarantee.view.IVerifyOTPGuaranteeView

/**
 * Created by PhongLH on 12/17/2019.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
class VerifyOTPGuaranteePresenter(val view: IVerifyOTPGuaranteeView) : BaseActivityPresenter(view) {

    private val interaction = UserInteractor()
    private val interactor = DetailStampInteractor()

    private var mSerial: String? = null
    private var obj: ICUpdateCustomerGuarantee? = null
    private var mProductCode: String? = null
    private var mId: String? = null
    private var mProductVariant: Long? = null

    private var body = hashMapOf<String, Any>()

    fun getDataByIntent(intent: Intent) {
        obj = try {
            intent.getSerializableExtra(Constant.DATA_1) as ICUpdateCustomerGuarantee
        } catch (e: Exception) {
            null
        }

        mSerial = intent.getStringExtra(Constant.DATA_2)

        mProductCode = try {
            intent.getStringExtra(Constant.DATA_3)
        } catch (e: Exception) {
            null
        }

        mProductVariant = try {
            intent.getLongExtra(Constant.DATA_4, 0L)
        } catch (e: Exception) {
            0L
        }

        body = intent.getSerializableExtra(Constant.DATA_5) as HashMap<String, Any>

        if (obj != null) {
            view.onCountDownOtp()
            view.onGetDataIntentSuccess(obj!!)
        } else {
            view.onGetDataError()
        }
    }

    fun sendOtpConfirmPhone() {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        if (obj == null) {
            view.onGetDataError()
            return
        }

        view.onShowLoading(true)

        UserInteractor().sendOtpConfirmPhoneStamp(obj?.phone!!, object : ICApiListener<ICStatus> {
            override fun onSuccess(obj: ICStatus) {
                view.onShowLoading(false)
                view.onCountDownOtp()
            }

            override fun onError(error: ICBaseResponse?) {
                view.onShowLoading(false)
                error?.message?.let {
                    showError(it)
                }
            }
        })
    }

    fun confirmOtp(otp: String) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        if (otp.isEmpty()) {
            view.onErrorOtp(getString(R.string.ma_xac_nhan_khong_duoc_de_trong))
            return
        }

        if (otp.contains(" ") || otp.length != 6) {
            view.onErrorOtp(getString(R.string.ma_xac_nhan_khong_hop_le))
            return
        }

        view.onErrorOtp("")

        if (obj == null) {
            view.onGetDataError()
            return
        }

        view.onShowLoading(true)

        interaction.confirmOtpStamp(obj?.phone!!, otp, object : ICApiListener<ICStatus> {
            override fun onSuccess(obj: ICStatus) {
                view.onShowLoading(false)
                if (obj.status == true) {
                    updateInformationVerifiedPhone()
                } else {
                    showError(R.string.co_loi_xay_ra_vui_long_thu_lai)
                }
            }

            override fun onError(error: ICBaseResponse?) {
                view.onShowLoading(false)
                error?.message?.let {
                    showError(it)
                }
            }
        })
    }

    private fun updateInformationVerifiedPhone() {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        val id = SessionManager.session.user?.id
        if (id != null) {
            mId = "i-$id"
        } else {
            mId = null
        }

        val deviceId = DeviceUtils.getUniqueDeviceId()

        interactor.updateInfomationGuarantee(obj!!, deviceId, mId, mProductCode, DetailStampActivity.mSerial, mProductVariant, body, object : ICApiListener<IC_RESP_UpdateCustomerGuarantee> {
            override fun onSuccess(obj: IC_RESP_UpdateCustomerGuarantee) {
                if (obj.status == 200) {
                    view.updateInformationCusomterGuaranteeSuccess()
                } else {
                    view.updateInformationCusomterGuaranteeFail()
                }
            }

            override fun onError(error: ICBaseResponse?) {
                error?.message?.let {
                    showError(view.mContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            }
        })
    }

}