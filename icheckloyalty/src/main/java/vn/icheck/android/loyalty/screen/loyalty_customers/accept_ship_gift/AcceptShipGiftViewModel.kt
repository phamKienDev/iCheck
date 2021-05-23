package vn.icheck.android.loyalty.screen.loyalty_customers.accept_ship_gift

import android.content.Intent
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.BaseViewModel
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.helper.NetworkHelper
import vn.icheck.android.loyalty.helper.ValidHelper
import vn.icheck.android.loyalty.model.*
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.repository.AddressRepository
import vn.icheck.android.loyalty.repository.CampaignRepository
import vn.icheck.android.loyalty.repository.LoyaltyCustomersRepository

class AcceptShipGiftViewModel : BaseViewModel<Any>() {
    private val repository = LoyaltyCustomersRepository()

    val onSuccessRedemption = MutableLiveData<ICKRedemptionHistory?>()
    val onSuccessVoucher = MutableLiveData<Long>()
    val onSuccessReceiveGift = MutableLiveData<ICKWinner?>()
    val onSuccessUsedVoucher = MutableLiveData<String>()

    val showError = MutableLiveData<String>()

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

    /**
     * @param type
     * 1 -> Đổi quà tích điểm dài hạn
     * 2 -> Đổi quà rơi quà
     * 3 -> Đổi quà voucher
     * 4 -> Đánh dấu đã dùng
     */
    var type = 0

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

    fun postExchangeGift(name: String, phone: String, email: String?, address: String) {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            checkError(false)
            return
        }

        var isSuccess = true

        when {
            name.isNullOrEmpty() -> {
                isSuccess = false
                onErrorName.postValue("Vui lòng nhập tên")
            }
            name.length > 255 -> {
                isSuccess = false
                onErrorName.postValue("Họ và tên không nhập quá 255 kí tự")
            }
            else -> {
                onErrorName.postValue("")
            }
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
            when (type) {
                1 -> {
                    repository.exchangeGift(collectionID, name, phone, email, province?.id
                            ?: -1, district?.id ?: -1, address, province?.name ?: "", district?.name
                            ?: "", ward?.id ?: -1, ward?.name ?: "",
                            object : ICApiListener<ICKResponse<ICKRedemptionHistory>> {
                                override fun onSuccess(obj: ICKResponse<ICKRedemptionHistory>) {
                                    if (obj.status == "FAIL") {
                                        showError.postValue(obj.data?.message
                                                ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                                    } else {
                                        onSuccessRedemption.postValue(obj.data)
                                    }
                                }

                                override fun onError(error: ICKBaseResponse?) {
                                    checkError(true, error?.message)
                                }
                            })
                }
                3 -> {
                    repository.exchangeCardGiftVQMM(null, collectionID, phone, object : ICApiListener<ICKResponse<ICKRedemptionHistory>> {
                        override fun onSuccess(obj: ICKResponse<ICKRedemptionHistory>) {
                            if (obj.statusCode == 200) {
                                onSuccessVoucher.postValue(obj.data?.id)
                            } else {
                                showError.postValue(obj.data?.message
                                        ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                            }
                        }

                        override fun onError(error: ICKBaseResponse?) {
                            checkError(true, error?.message)
                        }
                    })
                }
                else -> {
                    AddressRepository().confirmGiftLoyalty(collectionID, name, phone, email, province?.id
                            ?: -1, district?.id ?: -1, address, province?.name ?: "", district?.name
                            ?: "", ward?.id ?: -1, ward?.name ?: "",
                            object : ICApiListener<ICKResponse<ICKWinner>> {
                                override fun onSuccess(obj: ICKResponse<ICKWinner>) {
                                    if (obj.status == "FAIL") {
                                        showError.postValue(obj.data?.message
                                                ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                                    } else {
                                        onSuccessReceiveGift.postValue(obj.data)
                                    }
                                }

                                override fun onError(error: ICKBaseResponse?) {
                                    checkError(true, error?.message)
                                }
                            })
                }
            }
        }
    }

    fun usedVoucher(voucher: String, note: String?, name: String, phone: String, email: String?, address: String) {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            checkError(false)
            return
        }

        if (name.length > 100) {
            onErrorName.postValue("Tên bạn nhập quá dài!")
            return
        }

        val validPhone = ValidHelper.validPhoneNumber(ApplicationHelper.getApplicationByReflect(), phone)
        if (validPhone != null) {
            onErrorPhone.postValue(validPhone!!)
            return
        }

        CampaignRepository().usedVoucher(voucher, note, phone, name, email, address, province?.id, district?.id, ward?.id, object : ICApiListener<ICKResponse<ICKNone>> {
            override fun onSuccess(obj: ICKResponse<ICKNone>) {
                if (obj.statusCode != 200) {
                    showError.postValue(obj.message
                            ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                } else {
                    onSuccessUsedVoucher.postValue("SUCCESS")
                }
            }

            override fun onError(error: ICKBaseResponse?) {
                checkError(true, error?.message)
            }
        })
    }
}