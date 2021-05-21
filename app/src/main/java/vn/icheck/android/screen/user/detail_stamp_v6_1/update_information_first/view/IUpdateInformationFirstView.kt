package vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.detail_stamp_v6_1.*

interface IUpdateInformationFirstView : BaseActivityView {

    fun onGetDataError(errorType: Int)
    fun onGetProductVariantSuccess(products: MutableList<ICVariantProductStampV6_1.ICVariant.ICObjectVariant>, productId: Long)
    fun onGetProductVariantError()
    fun onGetDataIntentSuccess(type: Int, id: Long, phoneNumber: String?, productCode: String?, objVariant: ICVariantProductStampV6_1.ICVariant.ICObjectVariant?)

    fun onGetDataDetailCustomeFail()
    fun onGetDataDetailCustomeSuccess(customer: ICDetailCustomerGuranteeVerified.ICDetailCustomerGurantee.ICObjectCustomerGurantee)
    fun onSearchCustomerFail()
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
    fun onSendOtpGuaranteeSuccess(name: String, phone: String, email: String, cityId: Int?, districtId: Int?, address: String, productCode: String, mSerial: String?, variant: Long?, body: HashMap<String, Any>)
    fun updateInformationCusomterGuaranteeSuccess()
    fun updateInformationCusomterGuaranteeFail()
    fun onGetDetailStampSuccess(obj: ICDetailStampV6_1)
    fun onGetFieldListGuareanteeSuccess(data: MutableList<ICFieldGuarantee>)
    fun onGetFieldListGuareanteeFail()
}