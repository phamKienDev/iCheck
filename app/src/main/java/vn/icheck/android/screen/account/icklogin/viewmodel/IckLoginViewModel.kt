package vn.icheck.android.screen.account.icklogin.viewmodel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.work.WorkInfo
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import vn.icheck.android.constant.*
import vn.icheck.android.model.country.Nation
import vn.icheck.android.model.firebase.LoginDeviceResponse
import vn.icheck.android.model.icklogin.*
import vn.icheck.android.model.location.CityItem
import vn.icheck.android.screen.account.icklogin.REGISTER
import vn.icheck.android.util.ick.logDebug
import java.io.File
import java.util.*
import kotlin.collections.HashMap

const val HIDE_LOGIN_REGISTER = 3
const val SHOW_LOGIN_REGISTER = 2
const val CHOOSE_TOPIC = 4

class IckLoginViewModel @ViewModelInject constructor(
        private val ickLoginRepository: IckLoginRepository,
        @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val mErr = ickLoginRepository.error
    var cPhone: String = ""
    var cPw: String = ""
    var cRPw: String = ""
    var registerType = REGISTER
    val stateRegister = MutableLiveData<Int>()
    var onAction = false
    var state = 1

    fun sentOtp() = ickLoginRepository.sentOtp
    var facebookAvatar:String? = null
    var facebookUsername:String? = null
    var facebookToken:String? = null
    var facebookPhone:String? = null

    var password:String = ""
    var waitResponse = false

    fun hasData(): Boolean {
        return cPhone.isNotEmpty() || cPw.isNotEmpty() || cRPw.isNotEmpty()
    }

    fun resetData() {
        cPhone = ""
        cPw = ""
        cRPw = ""
    }

    var nation: Nation? = null
        set(value) {
            savedStateHandle.set("nation", value)
            field = value
        }
        get() = savedStateHandle.get("nation")
    val nationLiveData = MutableLiveData<Nation>()

    val mState = MutableLiveData<Int>()
    var file: File? = null
        set(value) {
            savedStateHandle.set("file", value)
            field = value
        }
        get() = savedStateHandle.get("file")
    private val updateUser = hashMapOf<String, Any?>()
    var calendar = Calendar.getInstance()

    fun setAddress(address: String?) {
        updateUser[ADDRESS] = address
    }

    fun removeAddress() {
        updateUser.remove(ADDRESS)
    }

    fun hasAddress():Boolean {
        return updateUser[ADDRESS] != null
    }

    fun setLastName(lastName: String?) {
        updateUser[LAST_NAME] = lastName
    }

    fun setFirstName(s: String?) {
        updateUser[FIRST_NAME] = s
    }

    fun setEmail(email: String?) {
        updateUser[EMAIL] = email
    }

    fun setMgt(mgt: String?) {
        updateUser["invitationCode"] = mgt
    }

    fun setBirthDay(birthDay: String) {
        updateUser[BIRTH_DAY] = birthDay
    }

    fun setGender(gender: Int) {
        updateUser[GENDER] = gender
    }

    fun setDistrict(districtId: Int?) {
        updateUser[DISTRICT_ID] = districtId
    }

    fun hasDistrict() :Boolean{
        return updateUser[DISTRICT_ID] != null
    }

    fun removeDistrict() {
        updateUser.remove(DISTRICT_ID)
    }

    fun getDistrict(): Int? = updateUser[DISTRICT_ID] as Int?

    fun setCity(cityId: Int?) {
        updateUser[CITY_ID] = cityId
    }


    fun getCity(): Int? = updateUser[CITY_ID] as Int?

    fun setWard(wardId: Int?) {
        updateUser[WARD_ID] = wardId
    }

    fun hasWard() :Boolean{
        return updateUser[WARD_ID] != null
    }

    fun removeWard() {
        updateUser.remove(WARD_ID)
    }


    fun hideLoginRegister() {
        mState.postValue(HIDE_LOGIN_REGISTER)
    }

    fun showLoginRegister() {
        mState.postValue(SHOW_LOGIN_REGISTER)
    }

    // Set phone number
    fun setPhone(phone: String) {
        ickLoginRepository.phone = phone
    }

    // Login by account
    fun login(username: String, password: String): LiveData<IckLoginResponse?> {
        return liveData {
            emit(ickLoginRepository.login(username, password))
        }
    }

    // Get ick user information to set session
    fun getUserInfo(): LiveData<IckUserInfoResponse?> {
        return liveData {
            emit(ickLoginRepository.getUserInfo())
        }
    }

    /**
     * Method call when request server send otp sms back to client
     */
    fun requestLoginOtp(): LiveData<RequestOtpResponse?> {
        return liveData {
            emit(ickLoginRepository.requestOtp())
        }
    }

    fun requestRegisterFacebook(phoneNumber: String, facebookToken: String): LiveData<RequestOtpResponse?> {
        return liveData {
            emit(ickLoginRepository.requestRegisterFacebook(phoneNumber, facebookToken))
        }
    }

    // Confirm otp, use token from requestLoginOtp
    fun loginOtp(otp: String): LiveData<ConfirmOtpResponse?> {
        return liveData {
            emit(ickLoginRepository.loginOtp(otp))
        }
    }

    // Request otp from login facebook
    fun loginFacebook(facebookToken: String): LiveData<IckLoginFacebookResponse?> {
        return liveData {
            emit(ickLoginRepository.loginFacebook(facebookToken))
        }
    }

    // Request otp forgot password
    fun requestForgotPw(): LiveData<RequestOtpResponse?> {
        return liveData {
            emit(ickLoginRepository.requestForgotPw())
        }
    }

    // Request otp register account
    fun requestRegisterOtp(password: String, confirmPassword: String): LiveData<RequestOtpResponse?> {
        return liveData {
            emit(ickLoginRepository.requestRegisterOtp(password, confirmPassword))
        }
    }

    // Confirm otp forgot password
    fun forgotPwOtp(otp: String): LiveData<ConfirmOtpResponse?> {
        return liveData {
            emit(ickLoginRepository.forgotPwOtp(otp))
        }
    }

    fun registerOtp(otp: String): LiveData<ConfirmOtpResponse?> {
        return liveData {
            emit(ickLoginRepository.conFirmRegister(otp))
        }
    }

    fun updatePassword(token: String?, password: String): LiveData<RequestOtpResponse?> {
        return liveData {
            emit(ickLoginRepository.resetPassword(token, password))
        }
    }

    fun uploadImage(): LiveData<List<WorkInfo>> {
        return ickLoginRepository.uploadImage(file)
    }

    fun resendRegisterOtp(): LiveData<RequestOtpResponse?> {
        return liveData {
            emit(ickLoginRepository.resendRegisterOtp())
        }
    }

    fun updateUserInfo(src: String?): LiveData<RequestOtpResponse?> {

        when{
            !src.isNullOrEmpty() -> {
                updateUser["avatar"] = src
            }
            !facebookAvatar.isNullOrEmpty() -> {
                updateUser["avatar"] = facebookAvatar
            }
        }
        if (updateUser[GENDER] == null) {
            updateUser[GENDER] = 1
        }
//        for (item in updateUser.entries) {
//            if (item.value == null || item.value.toString().isEmpty()) {
//                updateUser.remove(item.key)
//            }
//        }
        return liveData {
            emit(ickLoginRepository.updateUserInfo(HashMap(updateUser.filterNot {
                it.value == null || (it.value is String? && (it.value as String?).isNullOrEmpty())
            })))
        }
    }

    fun logout() {
        viewModelScope.launch {
            ickLoginRepository.logout()
        }
    }

    fun loginAnonymous() {
        viewModelScope.launch {
            val res = ickLoginRepository.loginAnonymous()
            logDebug(res.toString())
        }
    }

    fun loginDevice(token: String?) :LiveData<LoginDeviceResponse?>{
        return liveData{
            emit(ickLoginRepository.loginDevice(token))
        }
    }

    fun logoutDevice(token:String?) {
        viewModelScope.launch {
            ickLoginRepository.logoutDevice(token)
        }
    }

    override fun onCleared() {
        super.onCleared()
        file = null
    }
}