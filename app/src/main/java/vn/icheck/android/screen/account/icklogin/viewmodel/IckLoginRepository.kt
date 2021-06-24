package vn.icheck.android.screen.account.icklogin.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.constant.ICK_TOKEN
import vn.icheck.android.constant.ICK_URI
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.model.firebase.LoginDeviceResponse
import vn.icheck.android.network.model.icklogin.ConfirmOtpResponse
import vn.icheck.android.network.model.icklogin.RequestOtpResponse
import vn.icheck.android.network.api.ICKApi
import vn.icheck.android.network.model.icklogin.IckLoginFacebookResponse
import vn.icheck.android.network.model.icklogin.IckLoginResponse
import vn.icheck.android.network.model.icklogin.IckUserInfoResponse
import vn.icheck.android.network.util.DeviceUtils
import vn.icheck.android.screen.user.contribute_product.UPLOAD_LIST_IMAGE
import vn.icheck.android.util.ick.logError
import vn.icheck.android.worker.UploadImageWorker
import java.io.File
import javax.inject.Inject

class IckLoginRepository @Inject constructor(private val ickApi: ICKApi, private val workManager: WorkManager, private val sharedPreferences: SharedPreferences) {
    val error = MutableLiveData<Exception>()
    var phone: String = ""
    var sentOtp = false

    suspend fun login(username: String, password: String): IckLoginResponse? {
        return try {
            val body = hashMapOf<String, Any>()
            body["userName"] = username
            body["password"] = password
            val response = ickApi.loginICKAcc(body)
            if (!response.data?.token.isNullOrEmpty()) {
                sharedPreferences.edit().putString(ICK_TOKEN, response.data?.token).apply()
            }
            response
        } catch (e: Exception) {
            if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
                error.postValue(Exception( getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            } else {
                postErr(e)
            }
            null
        }
    }

    suspend fun getUserInfo(): IckUserInfoResponse? {
        return try {
            ickApi.getUserInfo()
        } catch (e: Exception) {
            postErr(e)
            null
        }
    }

    suspend fun loginFacebook(facebookToken: String): IckLoginFacebookResponse? {
        return try {
            val response = ickApi.loginFacebook(facebookToken)
            if (!response.data?.token.isNullOrEmpty()) {
                sharedPreferences.edit().putString(ICK_TOKEN, response.data?.token).apply()
            }
            response
        } catch (e: Exception) {
            postErr(e)
            null
        }
    }


    suspend fun loginFacebookNoToken(facebookToken: String): IckLoginFacebookResponse? {
        return try {
            val response = ickApi.loginFacebook(facebookToken)
            response
        } catch (e: Exception) {
            postErr(e)
            null
        }
    }

    suspend fun requestOtp(): RequestOtpResponse? {
        return try {
            val response = ickApi.requestLoginOtp(if (phone.length == 9) "0$phone" else phone)
            if (!response.data?.token.isNullOrEmpty()) {
                sharedPreferences.edit().putString(ICK_TOKEN, response.data?.token).apply()
            }
            sentOtp = true
            response
        } catch (e: Exception) {
            postErr(e)
            null
        }
    }

    suspend fun loginOtp(otp: String): ConfirmOtpResponse? {
        return try {
            val requestBody = hashMapOf<String, Any?>()
            requestBody["token"] = sharedPreferences.getString(ICK_TOKEN, "")
            requestBody["otp"] = otp
            val response = ickApi.loginOtp(requestBody)
            if (!response.data?.token.isNullOrEmpty()) {
                sharedPreferences.edit().putString(ICK_TOKEN, response.data?.token).apply()
            }
            sentOtp = false
            response
        } catch (e: Exception) {
            postErr(e)
            null
        }
    }

    suspend fun requestForgotPw(): RequestOtpResponse? {
        return try {
            val response = ickApi.requestForgotPw(if (phone.length == 9) "0$phone" else phone)
            if (!response.data?.token.isNullOrEmpty()) {
                sharedPreferences.edit().putString(ICK_TOKEN, response.data?.token).apply()
            }
            sentOtp = true
            response
        } catch (e: Exception) {
            postErr(e)
            null
        }
    }

    suspend fun requestRegisterOtp(password: String, confirmPassword: String): RequestOtpResponse? {
        return try {
            val requestBody = hashMapOf<String, Any>()

            requestBody["phoneNumber"] = if (phone.length == 9) "0$phone" else phone
            requestBody["password"] = password
            requestBody["confirmPassword"] = confirmPassword
            val response = ickApi.requestRegisterOtp(requestBody)
            if (!response.data?.token.isNullOrEmpty()) {
                sharedPreferences.edit().putString(ICK_TOKEN, response.data?.token).apply()
            }
            sentOtp = true
            response
        } catch (e: Exception) {
            postErr(e)
            null
        }
    }

    suspend fun forgotPwOtp(otp: String): ConfirmOtpResponse? {
        return try {
            val requestBody = hashMapOf<String, Any?>()
            requestBody["token"] = sharedPreferences.getString(ICK_TOKEN, "")
            requestBody["otp"] = otp
            val res = ickApi.confirmOtpForgotPw(requestBody)
            sentOtp = false
            res
        } catch (e: Exception) {
            postErr(e)
            null
        }
    }

    suspend fun conFirmRegister(otp: String): ConfirmOtpResponse? {
        return try {
            val requestBody = hashMapOf<String, Any?>()
            requestBody["token"] = sharedPreferences.getString(ICK_TOKEN, "")
            requestBody["otp"] = otp
            val response = ickApi.confirmOtpRegister(requestBody)
            if (!response.data?.token.isNullOrEmpty()) {
                sharedPreferences.edit().putString(ICK_TOKEN, response.data?.token).apply()
            }
            sentOtp = false
            response
        } catch (e: Exception) {
            postErr(e)
            null
        }
    }

    suspend fun resendRegisterOtp(): RequestOtpResponse? {
        return try {
            val response = ickApi.resentOtpRegister(sharedPreferences.getString(ICK_TOKEN, ""))
            if (!response.data?.token.isNullOrEmpty()) {
                sharedPreferences.edit().putString(ICK_TOKEN, response.data?.token).apply()
            }
            sentOtp = true
            response
        } catch (e: Exception) {
            postErr(e)
            null
        }
    }

    suspend fun resetPassword(token: String?, password: String): RequestOtpResponse? {
        return try {
            val requestBody = hashMapOf<String, Any?>()
            requestBody["token"] = token ?: sharedPreferences.getString(ICK_TOKEN, "")
            requestBody["password"] = password
            requestBody["confirmPassword"] = password

            val response = ickApi.changePassword(requestBody)
            if (!response.data?.token.isNullOrEmpty()) {
                sharedPreferences.edit().putString(ICK_TOKEN, response.data?.token).apply()
            }
            sentOtp = true
            response
        } catch (e: Exception) {
            postErr(e)
            null
        }
    }

    fun uploadImage(file: File?): LiveData<List<WorkInfo>> {
        val listWork = arrayListOf<OneTimeWorkRequest>()
        val worker = OneTimeWorkRequestBuilder<UploadImageWorker>()
        worker.setInputData(workDataOf(ICK_URI to file?.absolutePath))
        listWork.add(worker.build())
        workManager.beginUniqueWork(UPLOAD_LIST_IMAGE, ExistingWorkPolicy.REPLACE, listWork)
                .enqueue()
        return workManager.getWorkInfosForUniqueWorkLiveData(UPLOAD_LIST_IMAGE)
    }

    suspend fun updateUserInfo(requestBody: HashMap<String, Any?>): RequestOtpResponse? {
        return try {
            ickApi.updateUser(requestBody)
        } catch (e: Exception) {
            postErr(e)
            null
        }
    }

    suspend fun logout() {
        try {
            ickApi.logout()
        } catch (e: Exception) {
            postErr(e)
        }
    }

    suspend fun loginAnonymous() {
        try {
            ickApi.loginAnonymous(hashMapOf("os" to 1, "deviceId" to DeviceUtils.getUniqueDeviceId()))
        } catch (e: Exception) {
            postErr(e)
        }
    }

    suspend fun loginDevice(token: String?): LoginDeviceResponse? {
        return try {
            val body = HashMap<String, Any?>()
            body["platform"] = "android"
            body["deviceId"] = DeviceUtils.getUniqueDeviceId()
            body["deviceToken"] =  token
//                val requestBody = body.toString().toRequestBody("application/json".toMediaTypeOrNull())
            ickApi.loginDevice(body)
        } catch (e: Exception) {
            logError(e)
            null
        }

    }

    suspend fun logoutDevice(token:String?) {
        try {
            val requestBody = hashMapOf<String, Any?>()
            requestBody["deviceId"] = DeviceUtils.getUniqueDeviceId()
            requestBody["platform"] = "android"
            requestBody["deviceToken"] = token
            ickApi.logoutDevice(requestBody)
        } catch (e: Exception) {
            postErr(e)
        }
    }

    suspend fun requestRegisterFacebook(phoneNumber: String, facebookToken: String): RequestOtpResponse? {
        return try {
            val requestBody = hashMapOf<String, Any?>()
            requestBody.put("phoneNumber", if (phoneNumber.length == 9) "0$phoneNumber" else phoneNumber)
            requestBody.put("token", facebookToken)
            val response = ickApi.requestRegisterFacebook(requestBody)
            if (!response.data?.token.isNullOrEmpty()) {
                sharedPreferences.edit().putString(ICK_TOKEN, response.data?.token).apply()
            }
            response
        } catch (e: Exception) {
            postErr(e)
            null
        }
    }

    private fun postErr(e: Exception) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            error.postValue(Exception( getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
        } else {
            error.postValue(e)
        }
    }

}