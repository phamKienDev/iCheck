package vn.icheck.android.screen.user.updateaddress.presenter

import android.content.Intent
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.helper.ValidHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.user.UserInteractor
import vn.icheck.android.network.models.ICAddress
import vn.icheck.android.network.models.ICDistrict
import vn.icheck.android.network.models.ICProvince
import vn.icheck.android.network.models.ICWard
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.user.updateaddress.view.IUpdateUserAddressView

/**
 * Created by VuLCL on 12/28/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class UpdateUserAddressPresenter(val view: IUpdateUserAddressView) : BaseActivityPresenter(view) {
    private val userInteraction = UserInteractor()

    private var addressID : Long? = null

    private var province: ICProvince? = null
    private var district: ICDistrict? = null
    private var ward: ICWard? = null

    fun getID(intent: Intent?) {
        addressID = try {
            intent?.getLongExtra(Constant.DATA_1, -1L)
        } catch (e: Exception) {
            null
        }

        if (addressID == null || addressID == -1L) {
            getAddressDetail()
        }
    }

    fun getAddressDetail() {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetAddressError(getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        view.onShowLoading()

        userInteraction.getUserAddressDetail(addressID!!, object : ICApiListener<ICAddress> {
            override fun onSuccess(obj: ICAddress) {
                view.onCloseLoading()

                obj.city?.let { mProvince ->
                    province = ICProvince(mProvince.id, mProvince.name, mProvince.country_id, TextHelper.unicodeToKoDauLowerCase(mProvince.name))
                    view.onSetProvince(mProvince.name)
                }

                obj.district?.let {mDistrict ->
                    district = ICDistrict(mDistrict.id, mDistrict.city_id, mDistrict.name, TextHelper.unicodeToKoDauLowerCase(mDistrict.name))
                    view.onSetDistrict(mDistrict.name)
                }

                obj.ward?.let { mWard ->
                    ward = ICWard(mWard.id, mWard.name, mWard.district_id, TextHelper.unicodeToKoDauLowerCase(mWard.name))
                    view.onSetWard(mWard.name)
                }

                view.onGetAddressSuccess(obj)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                val message = error?.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                view.onGetAddressError(message)
            }
        })
    }

    fun selectProvince(intent: Intent?) {
        val obj = try {
            intent?.getSerializableExtra(Constant.DATA_1) as ICProvince?
        } catch (e: Exception) {
            null
        }

        if (obj != null) {
            if (obj.id != province?.id) {
                province = obj
                view.onSetProvince(province!!.name)
                view.onErrorProvince("")

                district = null
                view.onSetDistrict(getString(R.string.tuy_chon))

                ward = null
                view.onSetWard(getString(R.string.tuy_chon))
            }
        }
    }

    fun selectDistict(intent: Intent?) {
        val obj = try {
            intent?.getSerializableExtra(Constant.DATA_1) as ICDistrict?
        } catch (e: Exception) {
            null
        }

        if (obj != null) {
            if (obj.id != district?.id) {
                district = obj
                view.onSetDistrict(district!!.name)
                view.onErrorDistrict("")

                ward = null
                view.onSetWard(getString(R.string.tuy_chon))
            }
        }
    }

    fun selectWard(intent: Intent?) {
        val obj = try {
            intent?.getSerializableExtra(Constant.DATA_1) as ICWard?
        } catch (e: Exception) {
            null
        }

        if (obj != null) {
            ward = obj
            view.onSetWard(ward!!.name)
            view.onErrorWard("")
        }
    }

    val getProvince: ICProvince?
        get() {
            return province
        }

    val getDistrict: ICDistrict?
        get() {
            return district
        }

    fun createUserAddress(lastName: String, firstName: String, phone: String, address: String) {
        var isSuccess = true

        val validLastName = ValidHelper.validLastName(view.mContext, lastName)
        if (validLastName != null) {
            isSuccess = false
            view.onErrorLastName(validLastName)
        } else {
            view.onErrorLastName("")
        }

        val validFirstName = ValidHelper.validName(view.mContext, firstName)
        if (validFirstName != null) {
            isSuccess = false
            view.onErrorFirstName(validFirstName)
        } else {
            view.onErrorFirstName("")
        }

        val validPhone = ValidHelper.validPhoneNumber(view.mContext, phone)
        if (validPhone != null) {
            isSuccess = false
            view.onErrorPhone(validPhone)
        } else {
            view.onErrorPhone("")
        }

        if (province == null) {
            isSuccess = false
            view.onErrorProvince(getString(R.string.chon_tinh_thanh))
        } else {
            view.onErrorProvince("")
        }

        if (district == null) {
            isSuccess = false
            view.onErrorDistrict(getString(R.string.chon_quan_huyen))
        } else {
            view.onErrorDistrict("")
        }

        if (ward == null) {
            isSuccess = false
            view.onErrorWard(getString(R.string.vui_long_chon_phuong_xa))
        } else {
            view.onErrorWard("")
        }

        val validAddress = ValidHelper.validAddress(view.mContext, address)
        if (validAddress != null) {
            isSuccess = false
            view.onErrorAddress(validAddress)
        } else {
            view.onErrorAddress("")
        }

        if (isSuccess) {
            val reqAddress = ICAddress()
            reqAddress.first_name = firstName
            reqAddress.last_name = lastName
            reqAddress.user_id = SessionManager.session.user?.id
            reqAddress.phone = phone
            reqAddress.address = address
            reqAddress.country_id = province!!.country_id
            reqAddress.city_id = province!!.id.toInt()
            reqAddress.district_id = district!!.id
            reqAddress.ward_id = ward!!.id

            createUserAddress(reqAddress)
        }
    }

    private fun createUserAddress(reqAddress: ICAddress) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        view.onShowLoading()

        userInteraction.createUserAddress(reqAddress, object : ICApiListener<ICAddress> {
            override fun onSuccess(obj: ICAddress) {
                view.onCloseLoading()
                view.onCreateAddressSuccess(JsonHelper.toJson(obj))
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                val message = error?.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                showError(message)
            }
        })
    }
}