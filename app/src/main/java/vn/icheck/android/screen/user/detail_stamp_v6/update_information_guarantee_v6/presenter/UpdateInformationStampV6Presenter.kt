package vn.icheck.android.screen.user.detail_stamp_v6.update_information_guarantee_v6.presenter

import android.content.Intent
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.ValidHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.feature.detail_stamp_v6.DetailStampV6Interactor
import vn.icheck.android.network.models.detail_stamp_v6.IC_RESP_UpdateCustomerGuaranteeV6
import vn.icheck.android.network.models.detail_stamp_v6_1.ICNameCity
import vn.icheck.android.network.models.detail_stamp_v6_1.ICNameDistricts
import vn.icheck.android.network.models.detail_stamp_v6_1.ICObjectCustomerHistoryGurantee
import vn.icheck.android.network.util.DeviceUtils
import vn.icheck.android.room.entity.ICDistrict
import vn.icheck.android.room.entity.ICProvince
import vn.icheck.android.screen.user.detail_stamp_v6.update_information_guarantee_v6.view.IUpdateInformationStampV6View

/**
 * Created by PhongLH on 1/4/2020.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
class UpdateInformationStampV6Presenter(val view: IUpdateInformationStampV6View) : BaseActivityPresenter(view) {

    private val interactor = DetailStampV6Interactor()

    private var mIdStamp :String ? = null

    private var city: ICProvince? = null
    private var district: ICDistrict? = null
    var cityId: Int? = null
    var districtId: Int? = null
    var nameCity: String? = null
    var nameDistrict: String? = null

    fun getDataIntent(intent: Intent?) {
        mIdStamp = intent?.getStringExtra(Constant.DATA_2)
        val objCustomer = intent?.getSerializableExtra(Constant.DATA_3) as? ICObjectCustomerHistoryGurantee

        if (!mIdStamp.isNullOrEmpty() && objCustomer != null ){
            view.onGetDataIntentSuccess(mIdStamp, objCustomer)
        }else{
            view.onGetDataFail(Constant.ERROR_EMPTY)
        }
    }

    fun validUpdateInformationGuarantee(name: String, phone: String, email: String, address: String, shop: String, midStore: Int?) {
        var isValidSuccess = true

        if (name.isEmpty()) {
            isValidSuccess = false
            view.onErrorName(getString(R.string.ten_khong_duoc_de_trong))
            return
        }

        val validPhone = ValidHelper.validPhoneNumber(view.mContext, phone)
        if (validPhone != null) {
            isValidSuccess = false
            view.onErrorPhone(validPhone)
            return
        }

        val validEmail = ValidHelper.validEmail(view.mContext, email)
        if (validEmail != null) {
            isValidSuccess = false
            view.onErrorEmail(validEmail)
            return
        }

        if (cityId == null) {
            showError(R.string.vui_long_chon_tinh_thanh)
            return
        }

        if (districtId == null) {
            showError(R.string.vui_long_chon_quan_huyen)
            return
        }

        if (address.isEmpty()) {
            isValidSuccess = false
            view.onErrorAddress(getString(R.string.dia_chi_khong_duoc_de_trong))
            return
        }

        if (shop.isEmpty()) {
            isValidSuccess = false
            view.onErrorProductCode(getString(R.string.diem_ban_khong_duoc_de_trong))
            return
        }

        if (isValidSuccess) {
            updateInformationGuarantee(name, phone, email, nameCity!!, nameDistrict!!, address, midStore!!)
//            updateInformationVerifiedPhone(name, phone, email, cityId!!, districtId!!,address,productCode)
        }
    }

    private fun updateInformationGuarantee(name: String, phone: String, email: String, city: String, district: String, address: String,midStore:Int) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDataFail(Constant.ERROR_INTERNET)
            return
        }

        if (mIdStamp.isNullOrEmpty()){
            view.onGetDataFail(Constant.ERROR_EMPTY)
            return
        }

        if (midStore == null){
            view.onGetDataFail(Constant.ERROR_EMPTY)
            return
        }

        val deviceId = DeviceUtils.getUniqueDeviceId()

        interactor.onUpdateInformationStampV6(district,city,name,email,mIdStamp!!,midStore,address,phone,deviceId,object :ICApiListener<IC_RESP_UpdateCustomerGuaranteeV6>{
            override fun onSuccess(obj: IC_RESP_UpdateCustomerGuaranteeV6) {
                view.onUpdateInformationSuccess(obj)
            }

            override fun onError(error: ICBaseResponse?) {
                error?.message?.let {
                    showError(it)
                }
            }
        })

    }

    fun onGetNameCity(city: Int?) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDataFail(Constant.ERROR_INTERNET)
            return
        }

        interactor.getNameCity(city, object : ICApiListener<ICNameCity> {
            override fun onSuccess(obj: ICNameCity) {
                if (obj.data != null) {
                    view.onGetNameCitySuccess(obj)
                } else {
                    view.onGetNameCityFail()
                }
            }

            override fun onError(error: ICBaseResponse?) {
                error?.message?.let {
                    showError(it)
                }
            }
        })
    }

    fun onGetNameDistricts(district: Int?) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDataFail(Constant.ERROR_INTERNET)
            return
        }

        interactor.getNameDistricts(district, object : ICApiListener<ICNameDistricts> {
            override fun onSuccess(obj: ICNameDistricts) {
                if (obj.data != null) {
                    view.onGetNameDistrictSuccess(obj)
                } else {
                    view.onGetNameDistrictFail()
                }
            }

            override fun onError(error: ICBaseResponse?) {
                error?.message?.let {
                    showError(it)
                }
            }
        })
    }

    fun selectCity(obj: ICProvince) {
        if (obj.id != city?.id) {
            city = obj
            view.onSetCityName(obj.name, obj.id)
        }
    }

    fun selectDistrict(obj: ICDistrict) {
        if (obj.id != district?.id) {
            district = obj
            view.onSetDistrictName(obj.name, obj.id)
        }
    }
}