package vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.presenter

import android.content.Intent
import com.google.gson.annotations.Expose
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.ValidHelper
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.detail_stamp_v6_1.DetailStampRepository
import vn.icheck.android.network.feature.user.UserInteractor
import vn.icheck.android.network.models.ICStatus
import vn.icheck.android.network.models.detail_stamp_v6_1.*
import vn.icheck.android.network.util.DeviceUtils
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.StampDetailActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.view.IUpdateInformationFirstView

/**
 * Created by PhongLH on 12/13/2019.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
class UpdateInformationFirstPresenter(val view: IUpdateInformationFirstView) : BaseActivityPresenter(view) {

    private val interactor = DetailStampRepository()

    private var city: CitiesItem? = null
    private var district: DistrictsItem? = null

    //    private var mSerial: String? = null
    private var mId: String? = null
    var cityId: Int? = null
    var districtId: Int? = null

    private var totalRequest = 0

//    fun getDataByIntent(intent: Intent) {
//        val productId = intent.getLongExtra(Constant.DATA_6, -1)
//        getVariantProduct(productId)
//    }

//    private fun getVariantProduct(productId: Long) {
//        if (NetworkHelper.isNotConnected(view.mContext)) {
//            view.onGetDataError(Constant.ERROR_INTERNET)
//            return
//        }
//
//        interactor.getVariantProduct(productId, null, object : ICApiListener<ICVariantProductStampV6_1> {
//            override fun onSuccess(obj: ICVariantProductStampV6_1) {
//                if (obj.data != null) {
//                    if (!obj.data?.products.isNullOrEmpty()) {
//                        view.onGetProductVariantSuccess(obj.data?.products!!, productId)
//                    } else {
//                        view.onGetProductVariantError()
//                    }
//                } else {
//                    view.onGetProductVariantError()
//                }
//            }
//
//            override fun onError(error: ICBaseResponse?) {
//                error?.message?.let {
//                    showError(it)
//                    view.onGetProductVariantError()
//                }
//            }
//        })
//    }

//    fun getDataByIntentSecond(intent: Intent) {
//        val typeShow = intent.getIntExtra(Constant.DATA_1, 0)
//        val distributorID = intent.getLongExtra(Constant.DATA_2, 0)
//        val phoneNumber = intent.getStringExtra(Constant.DATA_3)
//        val productCode = intent.getStringExtra(Constant.DATA_4)
//        val serial:String? = intent.getStringExtra(Constant.DATA_5)
//        val objVariant = try {
//            intent.getSerializableExtra(Constant.DATA_7) as ICVariantProductStampV6_1.ICVariant.ICObjectVariant
//        } catch (e: Exception) {
//            null
//        }
//        val barcode = intent.getStringExtra(Constant.DATA_8) ?: ""
//
//        if (typeShow == 1 || typeShow == 2) {
//            if (!serial.isNullOrEmpty())
//                mSerial = serial.replace("Serial: ", "").replace(" ", "")
//            if (NetworkHelper.isNotConnected(view.mContext)) {
//                view.onGetDataError(Constant.ERROR_INTERNET)
//                return
//            }
//
//            view.onShowLoading(false)
//
//            totalRequest = 1
//            getFieldListGuarantee(barcode)
//        }
//
//        view.onGetDataIntentSuccess(objVariant)
//    }

//    private fun getFieldListGuarantee(barcode: String) {
//        interactor.getFieldListGuarantee(barcode, object : ICApiListener<ICResponse<MutableList<ICFieldGuarantee>>> {
//            override fun onSuccess(obj: ICResponse<MutableList<ICFieldGuarantee>>) {
//                finishRequest()
//                if (!obj.data.isNullOrEmpty()) {
//                    view.onGetFieldListGuareanteeSuccess(obj.data!!)
//                } else {
//                    view.onGetFieldListGuareanteeFail()
//                }
//            }
//
//            override fun onError(error: ICBaseResponse?) {
//                finishRequest()
//                view.onGetFieldListGuareanteeFail()
//            }
//        })
//    }

//    fun getInforCustomer(id: Long, phoneNumber: String) {
//        interactor.getDetailCustomerGurantee(id, phoneNumber, object : ICApiListener<ICDetailCustomerGuranteeVerified> {
//            override fun onSuccess(obj: ICDetailCustomerGuranteeVerified) {
//                finishRequest()
//                if (obj.data != null) {
//                    if (obj.data?.customer != null) {
//                        view.onGetDataDetailCustomeSuccess(obj.data?.customer!!)
//                    } else {
//                        view.onGetDataDetailCustomeFail()
//                    }
//                } else {
//                    view.onGetDataDetailCustomeFail()
//                }
//            }
//
//            override fun onError(error: ICBaseResponse?) {
//                finishRequest()
//                error?.message?.let {
//                    showError(it)
//                    view.onGetDataDetailCustomeFail()
//                }
//            }
//        })
//    }

//    private fun finishRequest() {
//        totalRequest--
//        if (totalRequest == 0) {
//            view.onShowLoading(false)
//        }
//    }

//    fun searchInforCustomer(id: Long, phoneNumber: String) {
//        if (NetworkHelper.isNotConnected(view.mContext)) {
//            view.onGetDataError(Constant.ERROR_INTERNET)
//            return
//        }
//
//        view.onShowLoading(true)
//
//        interactor.getDetailCustomerGurantee(id, phoneNumber, object : ICApiListener<ICDetailCustomerGuranteeVerified> {
//            override fun onSuccess(obj: ICDetailCustomerGuranteeVerified) {
//                view.onShowLoading(false)
//                if (obj.data != null) {
//                    if (obj.data?.customer != null) {
//                        view.onGetDataDetailCustomeSuccess(obj.data?.customer!!)
//                    } else {
//                        view.onGetDataDetailCustomeFail()
//                    }
//                } else {
//                    view.onGetDataDetailCustomeFail()
//                }
//            }
//
//            override fun onError(error: ICBaseResponse?) {
//                view.onShowLoading(false)
//                error?.message?.let {
//                    view.onSearchCustomerFail()
//                }
//            }
//        })
//    }

    fun onGetNameCity(city: Int?) {
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

    fun validUpdateInformationGuarantee(name: String, phone: String, email: String, address: String,
                                        productCode: String, variant: Long?,
                                        customerData: HashMap<String, Any>,
                                        guaranteeData: HashMap<String, Any>,
                                        barcode: String, updateType: Int?, serial: String) {
        val validPhone = ValidHelper.validPhoneNumber(view.mContext, phone)
        if (validPhone != null) {
            view.onErrorPhone(validPhone)
            return
        }

        when (updateType) {
            1 -> {
                updateInformationVerifiedPhone(name, phone, email, address, districtId, cityId, productCode, variant, customerData, guaranteeData, serial)
            }
            2 -> {
                sendOtpGuarantee(name, phone, email, cityId, districtId, address, productCode, variant, customerData, guaranteeData)
            }
            else -> {
                val obj = ICUpdateCustomerGuarantee()
                obj.name = name
                obj.phone = phone
                obj.email = email
                obj.address = address
                obj.city = cityId
                obj.district = districtId
                scanQrStamp(obj, barcode)
            }
        }
    }

    private fun scanQrStamp(obj: ICUpdateCustomerGuarantee, barcode: String) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        val id = SessionManager.session.user?.id
        if (id != null) {
            mId = "i-$id"
        }

        view.onShowLoading(true)

        interactor.getDetailStampWhenUpdate(obj, null, mId, barcode, APIConstants.LATITUDE.toString(), APIConstants.LONGITUDE.toString(), object : ICApiListener<ICDetailStampV6_1> {
            override fun onSuccess(obj: ICDetailStampV6_1) {
                view.onShowLoading(false)
                view.onGetDetailStampSuccess(obj)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onShowLoading(false)
                error?.message?.let {
                    showError(it)
                }
            }
        })
    }

    private fun updateInformationVerifiedPhone(name: String, phone: String, email: String, address: String,
                                               district: Int?, city: Int?, productCode: String,
                                               variant: Long?, customerData: HashMap<String, Any>,
                                               guaranteeData: HashMap<String, Any>, serial: String) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        val id = SessionManager.session.user?.id
        mId = if (id != null) {
            "i-$id"
        } else {
            null
        }

        val deviceId = DeviceUtils.getUniqueDeviceId()

        interactor.updateInformationGuarantee(name, phone, email, address, district, city, deviceId, mId, productCode, variant, customerData, guaranteeData, serial, object : ICApiListener<IC_RESP_UpdateCustomerGuarantee> {
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

    private fun sendOtpGuarantee(name: String, phone: String, email: String, cityId: Int?,
                                 districtId: Int?, address: String, productCode: String,
                                 variant: Long?, customerData: HashMap<String, Any>,
                                 guaranteeData: HashMap<String, Any>) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        UserInteractor().sendOtpConfirmPhoneStamp(phone, object : ICApiListener<ICStatus> {
            override fun onSuccess(obj: ICStatus) {
                view.onSendOtpGuaranteeSuccess(name, phone, email, cityId, districtId, address, productCode, variant, customerData, guaranteeData)
            }

            override fun onError(error: ICBaseResponse?) {
                error?.message?.let {
                    showError(it)
                }
            }
        })
    }

    fun selectCity(intent: Intent?) {
        val obj = try {
            intent?.getSerializableExtra(Constant.DATA_1) as CitiesItem?
        } catch (e: Exception) {
            null
        }

        if (obj != null) {
            city = obj
            view.onSetCityName(obj.name ?: "", obj.id)
        }
    }

    fun selectDistrict(intent: Intent?) {
        val obj = try {
            intent?.getSerializableExtra(Constant.DATA_1) as DistrictsItem?
        } catch (e: Exception) {
            null
        }

        if (obj != null) {
            district = obj
            view.onSetDistrictName(obj.name ?: "", obj.id)
        }
    }
}