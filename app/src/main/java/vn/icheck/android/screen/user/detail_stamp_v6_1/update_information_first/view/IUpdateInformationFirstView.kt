package vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.detail_stamp_v6_1.*

interface IUpdateInformationFirstView : BaseActivityView {

    fun onGetNameCitySuccess(obj: ICNameCity)
    fun onGetNameCityFail()
    fun onGetNameDistrictSuccess(obj: ICNameDistricts)
    fun onGetNameDistrictFail()
    fun onErrorPhone(message: String)
    fun onErrorName(message: String)
    fun onErrorEmail(message: String)
    fun onErrorAddress(message: String)
    fun onErrorProductCode(message: String)
    fun onSetCityName(name: String, id: Int)
    fun onSetDistrictName(name: String, id: Int)
    fun onSendOtpGuaranteeSuccess(name: String, phone: String, email: String, cityId: Int?, districtId: Int?, address: String, productCode: String, variant: Long?, customerData: HashMap<String, Any>, guaranteeData: HashMap<String, Any>)
    fun updateInformationCusomterGuaranteeSuccess(user: ICUpdateCustomerGuarantee)
    fun updateInformationCusomterGuaranteeFail()
    fun onGetDetailStampSuccess(obj: ICDetailStampV6_1)
}