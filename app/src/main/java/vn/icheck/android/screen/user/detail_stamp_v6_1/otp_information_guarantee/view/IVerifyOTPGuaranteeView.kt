package vn.icheck.android.screen.user.detail_stamp_v6_1.otp_information_guarantee.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.detail_stamp_v6_1.ICUpdateCustomerGuarantee

interface IVerifyOTPGuaranteeView : BaseActivityView {
    fun onGetDataIntentSuccess(obj: ICUpdateCustomerGuarantee)
    fun onCountDownOtp()
    fun onGetDataError()
    fun onErrorOtp(errorMessage: String)
    fun updateInformationCusomterGuaranteeSuccess(user: ICUpdateCustomerGuarantee)
    fun updateInformationCusomterGuaranteeFail()
}