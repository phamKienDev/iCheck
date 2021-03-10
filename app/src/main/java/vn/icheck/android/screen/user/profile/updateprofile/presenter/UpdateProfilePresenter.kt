package vn.icheck.android.screen.user.profile.updateprofile.presenter

import android.content.Intent
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.*
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.facebook.FacebookInteractor
import vn.icheck.android.network.feature.user.UserInteractor
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.upload.UploadResponse
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.user.profile.updateprofile.view.IUpdateProfileView
import vn.icheck.android.tracking.insider.InsiderHelper
import java.io.File

/**
 * Created by VuLCL on 9/15/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class UpdateProfilePresenter(val view: IUpdateProfileView) : BaseActivityPresenter(view) {
    private val userInteraction = UserInteractor()

    private lateinit var user: ICUser

    private lateinit var addressHelper: AddressHelper

    private var imageAvatar: String? = null
    private var imageCover: String? = null

    private var province: ICProvince? = null
    private var district: ICDistrict? = null
    private var ward: ICWard? = null

    private var isGoToProfile = false

    fun getData(intent: Intent?) {
        isGoToProfile = try {
            intent?.getBooleanExtra(Constant.DATA_1, false) ?: false
        } catch (e: Exception) {
            false
        }
    }

    val getIsGoToProfile: Boolean
        get() {
            return isGoToProfile
        }

    fun setupAddressHelper() {
        addressHelper = AddressHelper(view.mContext)
    }

    fun getUserDetail() {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetUserDetailError(getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        view.onShowLoading()

        userInteraction.getUserMeDelay(object : ICApiListener<ICUser> {
            override fun onSuccess(obj: ICUser) {
                view.onCloseLoading()
                user = obj
                province = user.city
                district = user.district
                ward = user.ward
                view.onGetDataSuccess(user)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                val errorMessage = error?.message
                        ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                view.onGetUserDetailError(errorMessage)
            }
        })
    }

    val birthday: String
        get() {
            return if (user.birth_day != null && user.birth_month != null && user.birth_year != null) {
                TimeHelper.getNumberWithTwoCharacter(user.birth_day!!) + "/" +
                        TimeHelper.getNumberWithTwoCharacter(user.birth_month!!) + "/" +
                        user.birth_year
            } else {
                ""
            }
        }

    fun uploadImage(file: File, type: Int) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        view.onShowLoading()

        ImageHelper.uploadMedia(file, object : ICApiListener<UploadResponse> {
            override fun onSuccess(obj: UploadResponse) {
                view.onCloseLoading()

                if (type == 1) {
                    imageAvatar = obj.fileId
                } else if (type == 2) {
                    imageCover = obj.fileId
                }

                view.onUploadImageSuccess(file, type)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                val errorMessage = error?.message
                        ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                showError(errorMessage)
            }
        })
    }

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
            intent?.getSerializableExtra(Constant.DATA_1) as ICProvince?
        } catch (e: Exception) {
            null
        }

        if (obj != null) {
            if (obj.id != province?.id) {
                province = obj
                district = null
                ward = null
                view.onSetProvinceName(obj.name)
                view.onSetDistrictName(getString(R.string.tuy_chon))
                view.onSetWardName(getString(R.string.tuy_chon))
            }
        }
    }

    fun selectDistrict(intent: Intent?) {
        val obj = try {
            intent?.getSerializableExtra(Constant.DATA_1) as ICDistrict?
        } catch (e: Exception) {
            null
        }

        if (obj != null) {
            if (obj.id != district?.id) {
                district = obj
                ward = null
                view.onSetDistrictName(obj.name)
                view.onSetWardName(getString(R.string.tuy_chon))
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
            if (obj.id != ward?.id) {
                ward = obj
                view.onSetWardName(obj.name)
            }
        }
    }

    fun mappingFacebook(facebookToken: String) {
        view.onShowLoading()

        FacebookInteractor().mappingFacebook(user.id, facebookToken, object : ICApiListener<ICRespMappingFacebook> {
            override fun onSuccess(obj: ICRespMappingFacebook) {
                view.onCloseLoading()
                view.onMappingFacebookSuccess()
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                val errorMessage = error?.message
                        ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                showError(errorMessage)
            }
        })
    }

    fun updateProfile(isMale: Boolean, lastName: String, firstName: String,
                      birthday: String, phone: String, email: String,
                      address: String) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        if (lastName.isEmpty()) {
            showError(R.string.ho_khong_duoc_de_trong)
            return
        }

        if (firstName.isEmpty()) {
            showError(R.string.ten_khong_duoc_de_trong)
            return
        }

        if (birthday.isEmpty()) {
            showError(R.string.ngay_sinh_khong_duoc_de_trong)
            return
        }

        val birthdayMillisecond = TimeHelper.convertDateVnToMillisecond(birthday)

        if (birthdayMillisecond != null && birthdayMillisecond > System.currentTimeMillis()) {
            showError(R.string.ngay_sinh_khong_duoc_lon_hon_thoi_gian_hien_tai)
            return
        }

        val validPhone = ValidHelper.validPhoneNumber(view.mContext, phone)

        if (!validPhone.isNullOrEmpty()) {
            showError(validPhone)
            return
        }

        val validEmail = ValidHelper.validEmail(view.mContext, email)

        if (!validEmail.isNullOrEmpty()) {
            showError(validEmail)
            return
        }

        if (province == null) {
            showError(R.string.vui_long_chon_tinh_thanh)
            return
        }

        if (district == null) {
            showError(R.string.vui_long_chon_quan_huyen)
            return
        }

        if (ward == null) {
            showError(R.string.vui_long_chon_phuong_xa)
            return
        }

        if (address.isEmpty()) {
            showError(R.string.dia_chi_khong_duoc_de_trong)
            return
        }

        val reqUpdateUser = ICReqUpdateUser()
        reqUpdateUser.gender = if (isMale) "male" else "female"
        reqUpdateUser.last_name = lastName
        reqUpdateUser.first_name = firstName

        if (imageAvatar != null)
            reqUpdateUser.avatar = imageAvatar

        if (imageCover != null)
            reqUpdateUser.cover = imageCover

        val splitBirthday = birthday.split("/")
        if (splitBirthday.size == 3) {
            reqUpdateUser.birth_day = splitBirthday[0].toInt()
            reqUpdateUser.birth_month = splitBirthday[1].toInt()
            reqUpdateUser.birth_year = splitBirthday[2].toInt()
        }

        reqUpdateUser.phone = phone
        reqUpdateUser.email = email
        reqUpdateUser.country_id = province!!.country_id
        reqUpdateUser.city_id = province!!.id.toInt()
        reqUpdateUser.district_id = district!!.id
        reqUpdateUser.ward_id = ward!!.id
        reqUpdateUser.address = address

        if (phone != user.phone || !user.phone_verified) {
            sendOtpConfirmPhone(reqUpdateUser.phone!!, reqUpdateUser)
        } else {
            updateUser(user.id, reqUpdateUser)
        }
    }

    fun confirmPhone(phone: String) {
        val validPhone = ValidHelper.validPhoneNumber(view.mContext, phone)

        if (!validPhone.isNullOrEmpty()) {
            showError(validPhone)
            return
        }

        sendOtpConfirmPhone(phone, null)
    }

    private fun sendOtpConfirmPhone(phone: String, reqUpdateUser: ICReqUpdateUser?) {
        view.onShowLoading()

        userInteraction.sendOtpConfirmPhone(phone, object : ICApiListener<ICStatus> {
            override fun onSuccess(obj: ICStatus) {
                view.onCloseLoading()

                if (obj.status == true) {
                    view.onConfirmNewPhone(phone, if (reqUpdateUser != null) JsonHelper.toJson(reqUpdateUser) else null)
                } else {
                    showError(R.string.co_loi_xay_ra_vui_long_thu_lai)
                }
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                val message = error?.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                showError(message)
            }
        })
    }

    private fun updateUser(userID: Long, reqUpdateUser: ICReqUpdateUser) {
        view.onShowLoading()

        userInteraction.updateUser(userID, reqUpdateUser, object : ICApiListener<ICUser> {
            override fun onSuccess(obj: ICUser) {
                view.onCloseLoading()
                view.onUpdateUserSuccess()
                SessionManager.updateUser(reqUpdateUser)
                InsiderHelper.setUserAttributes()
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()

                val errorMessage = error?.message
                        ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                showError(errorMessage)
            }
        })
    }

    fun updateCurrentPhone(phone: String) {
        user.phone = phone
    }

    fun clearAllCacheData() {
        addressHelper.clearAllData()
    }
}