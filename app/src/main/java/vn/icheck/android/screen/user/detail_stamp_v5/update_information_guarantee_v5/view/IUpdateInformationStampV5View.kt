package vn.icheck.android.screen.user.detail_stamp_v5.update_information_guarantee_v5.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.detail_stamp_v6.IC_RESP_UpdateCustomerGuaranteeV6
import vn.icheck.android.network.models.detail_stamp_v6_1.ICNameCity
import vn.icheck.android.network.models.detail_stamp_v6_1.ICNameDistricts
import vn.icheck.android.network.models.detail_stamp_v6_1.ICObjectCustomerHistoryGurantee
import vn.icheck.android.network.models.detail_stamp_v6_1.IC_RESP_UpdateCustomerGuarantee

/**
 * Created by PhongLH on 1/4/2020.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
interface IUpdateInformationStampV5View : BaseActivityView {
    fun onErrorPhone(message: String)
    fun onErrorName(message: String)
    fun onErrorEmail(message: String)
    fun onErrorAddress(message: String)
    fun onErrorProductCode(message: String)
    fun onSetCityName(name: String, id: Int)
    fun onSetDistrictName(name: String, id: Int)
    fun onGetNameCitySuccess(obj: ICNameCity)
    fun onGetNameCityFail()
    fun onGetNameDistrictSuccess(obj: ICNameDistricts)
    fun onGetNameDistrictFail()
    fun onGetDataIntentSuccess(mIdStamp: String?, objCustomer: ICObjectCustomerHistoryGurantee)
    fun onGetDataFail(type: Int)
    fun onUpdateInformationSuccess(obj: IC_RESP_UpdateCustomerGuaranteeV6)
}