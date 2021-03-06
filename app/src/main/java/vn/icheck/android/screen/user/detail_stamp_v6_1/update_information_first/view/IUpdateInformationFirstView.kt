package vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.detail_stamp_v6_1.*

interface IUpdateInformationFirstView : BaseActivityView {

    fun onGetNameCitySuccess(obj: ICNameCity)
    fun onGetNameCityFail()
    fun onGetNameDistrictSuccess(obj: ICNameDistricts)
    fun onGetNameDistrictFail()
    fun onShowError(message: String)
    fun onErrorProductCode(message: String)
    fun onSetCityName(name: String, id: Int)
    fun onSetDistrictName(name: String, id: Int)
    fun onSendOtpGuaranteeSuccess(user: ICUpdateCustomerGuarantee, productCode: String, variant: Long?, customerData: HashMap<String, Any>, guaranteeData: HashMap<String, Any>)
    fun updateInformationCusomterGuaranteeSuccess(user: ICUpdateCustomerGuarantee)
    fun updateInformationCusomterGuaranteeFail()
    fun onGetDetailStampSuccess(obj: ICDetailStampV6_1)
}