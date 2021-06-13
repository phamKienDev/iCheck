package vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.presenter

import android.content.Intent
import vn.icheck.android.ICheckApplication
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

    fun validUpdateInformationGuarantee(user: ICUpdateCustomerGuarantee,
                                        productCode: String, variant: Long?,
                                        customerData: HashMap<String, Any>,
                                        guaranteeData: HashMap<String, Any>,
                                        barcode: String, updateType: Int?, serial: String) {

        when (updateType) {
            1 -> {
                updateInformationVerifiedPhone(user, productCode, variant, customerData, guaranteeData, serial)
            }
            2 -> {
                sendOtpGuarantee(user, productCode, variant, customerData, guaranteeData)
            }
            else -> {
                scanQrStamp(user, barcode)
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

    private fun updateInformationVerifiedPhone(user: ICUpdateCustomerGuarantee, productCode: String,
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

        interactor.updateInformationGuarantee(user, deviceId, mId, productCode, variant, customerData, guaranteeData, serial, object : ICApiListener<IC_RESP_UpdateCustomerGuarantee> {
            override fun onSuccess(obj: IC_RESP_UpdateCustomerGuarantee) {
                if (obj.status == 200) {
                    view.updateInformationCusomterGuaranteeSuccess(user)
                } else {
                    view.updateInformationCusomterGuaranteeFail()
                }
            }

            override fun onError(error: ICBaseResponse?) {
                error?.message?.let {
                    showError(ICheckApplication.getError(error.message))
                }
            }
        })
    }

    private fun sendOtpGuarantee(user: ICUpdateCustomerGuarantee, productCode: String,
                                 variant: Long?, customerData: HashMap<String, Any>,
                                 guaranteeData: HashMap<String, Any>) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        UserInteractor().sendOtpConfirmPhoneStamp(user.phone!!, object : ICApiListener<ICStatus> {
            override fun onSuccess(obj: ICStatus) {
                view.onSendOtpGuaranteeSuccess(user, productCode, variant, customerData, guaranteeData)
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