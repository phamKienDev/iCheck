package vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.accept_ship_gift

import android.content.Intent
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.BaseViewModel
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.helper.NetworkHelper
import vn.icheck.android.loyalty.helper.ToastHelper
import vn.icheck.android.loyalty.helper.ValidHelper
import vn.icheck.android.loyalty.model.*
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.repository.RedeemPointRepository

class AcceptShipGiftLoyaltyViewModel : BaseViewModel<Any>() {
    private val repository = RedeemPointRepository()

    val onSuccess = MutableLiveData<ICKGift>()

    val onSetProvince = MutableLiveData<String>()
    val onSetDistrict = MutableLiveData<String>()
    val onSetWard = MutableLiveData<String>()

    val onErrorProvince = MutableLiveData<String>()
    val onErrorDistrict = MutableLiveData<String>()
    val onErrorWard = MutableLiveData<String>()

    val onErrorName = MutableLiveData<String>()
    val onErrorPhone = MutableLiveData<String>()
    val onErrorEmail = MutableLiveData<String>()
    val onErrorAddress = MutableLiveData<String>()

    var province: ICProvince? = null
    var district: ICDistrict? = null
    var ward: ICWard? = null

    val getProvince: ICProvince?
        get() {
            return province
        }

    val getDistrict: ICDistrict?
        get() {
            return district
        }

    fun selectProvince(intent: Intent?) {
        val obj = try {
            intent?.getSerializableExtra(ConstantsLoyalty.DATA_1) as ICProvince?
        } catch (e: Exception) {
            null
        }

        if (obj != null) {
            if (obj.id != province?.id) {
                province = obj
                onSetProvince.postValue(province!!.name)
                onErrorProvince.postValue("")

                district = null
                onSetDistrict.postValue("Tùy chọn")

                ward = null
                onSetWard.postValue("Tùy chọn")
            }
        }
    }

    fun selectDistict(intent: Intent?) {
        val obj = try {
            intent?.getSerializableExtra(ConstantsLoyalty.DATA_1) as ICDistrict?
        } catch (e: Exception) {
            null
        }

        if (obj != null) {
            if (obj.id != district?.id) {
                district = obj
                onSetDistrict.postValue(district!!.name)
                onErrorDistrict.postValue("")

                ward = null
                onSetWard.postValue("Tùy chọn")
            }
        }
    }

    fun selectWard(intent: Intent?) {
        val obj = try {
            intent?.getSerializableExtra(ConstantsLoyalty.DATA_1) as ICWard?
        } catch (e: Exception) {
            null
        }

        if (obj != null) {
            ward = obj
            onSetWard.postValue(ward!!.name)
            onErrorWard.postValue("")
        }
    }

    fun postExchangeGift(giftID: Long, name: String, phone: String, email: String?, address: String) {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            checkError(false)
            return
        }

        var isSuccess = true

        if (name.isNullOrEmpty()) {
            isSuccess = false
            onErrorName.postValue("Vui lòng nhập tên")
        } else {
            onErrorName.postValue("")
        }

        val validPhone = ValidHelper.validPhoneNumber(ApplicationHelper.getApplicationByReflect(), phone)
        if (validPhone != null) {
            isSuccess = false
            onErrorPhone.postValue(validPhone!!)
        } else {
            onErrorPhone.postValue("")
        }

        if (!email.isNullOrEmpty()) {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                isSuccess = false
                onErrorEmail.postValue("Email không đúng định dạng")
            }
        }

        if (province?.name.isNullOrEmpty()) {
            isSuccess = false
            onErrorProvince.postValue("Chọn Tỉnh thành")
        } else {
            onErrorProvince.postValue("")
        }

        if (district?.name.isNullOrEmpty()) {
            isSuccess = false
            onErrorDistrict.postValue("Chọn Quận/Huyện")
        } else {
            onErrorDistrict.postValue("")
        }

        if (ward?.name.isNullOrEmpty()) {
            isSuccess = false
            onErrorWard.postValue("Chọn Phường/Xã")
        } else {
            onErrorWard.postValue("")
        }

        if (address.isNullOrEmpty()) {
            isSuccess = false
            onErrorAddress.postValue("Vui lòng nhập địa chỉ")
        } else {
            onErrorAddress.postValue("")
        }

        if (isSuccess) {
            repository.postExchangeGift(collectionID, giftID, name, phone, email, province?.id
                    ?: -1, district?.id ?: -1, address, province?.name ?: "", district?.name
                    ?: "", ward?.id ?: -1, ward?.name ?: "",
                    object : ICApiListener<ICKResponse<ICKBoxGifts>> {
                        override fun onSuccess(obj: ICKResponse<ICKBoxGifts>) {
                            if (obj.statusCode != 200) {
                                ToastHelper.showLongError(ApplicationHelper.getApplicationByReflect(), obj.data?.message ?: ApplicationHelper.getApplicationByReflect().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                            } else {
                                onSuccess.postValue(obj.data?.gift)
                            }
                        }

                        override fun onError(error: ICKBaseResponse?) {
                            checkError(true, error?.message)
                        }
                    })
        }
    }
}