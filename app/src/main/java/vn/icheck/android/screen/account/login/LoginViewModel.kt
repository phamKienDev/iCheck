package vn.icheck.android.screen.account.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.ValidHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.auth.AuthInteractor
import vn.icheck.android.network.models.ICSessionData

class LoginViewModel : ViewModel() {
    private val interaction = AuthInteractor()

    val errorPhone = MutableLiveData<String?>()
    val errorPassword = MutableLiveData<String?>()

    val statusCode = MutableLiveData<ICMessageEvent.Type>()
    val errorMessage = MutableLiveData<String>()

    var facebookToken = ""
    var name: String? = null
    var avatar: String? = null

    fun loginUser(phoneNumber: String, password: String) {
        var isValidSuccess = true

        val validPhone = ValidHelper.validPhoneNumber(ICheckApplication.getInstance(), phoneNumber)

        if (validPhone != null) {
            isValidSuccess = false
        }
        errorPhone.postValue(validPhone)

        val validPassword = ValidHelper.validPassword(ICheckApplication.getInstance(), password)

        if (validPassword != null) {
            isValidSuccess = false
        }
        errorPassword.postValue(validPassword)

        if (!isValidSuccess) {
            return
        }

        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        interaction.loginUser(phoneNumber, password, object : ICApiListener<ICSessionData> {
            override fun onSuccess(obj: ICSessionData) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)

                if (obj.token.isNullOrEmpty() || obj.tokenType.isNullOrEmpty()) {
                    errorMessage.postValue(ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                } else {
                    SessionManager.session = obj
                    statusCode.postValue(ICMessageEvent.Type.ON_LOGIN_SUCCESS)
                }
            }

            override fun onError(error: ICBaseResponse?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                val message = error?.message ?: ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                errorMessage.postValue(message)
            }
        })
    }

    fun loginFacebook(facebookToken: String, name: String?, avatar: String?) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            errorMessage.postValue(ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        interaction.loginFacebook(facebookToken, null, null, null, object : ICApiListener<ICSessionData> {
            override fun onSuccess(obj: ICSessionData) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)

                if (obj.token.isNullOrEmpty() || obj.tokenType.isNullOrEmpty()) {
                    errorMessage.postValue(ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                } else {
                    SessionManager.session = obj
                    statusCode.postValue(ICMessageEvent.Type.ON_LOGIN_SUCCESS)
                }
            }

            override fun onError(error: ICBaseResponse?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)

                if (error?.statusCode == 404) {
                    this@LoginViewModel.facebookToken = facebookToken
                    this@LoginViewModel.name = name
                    this@LoginViewModel.avatar = avatar
                    statusCode.postValue(ICMessageEvent.Type.ON_REGISTER_FACEBOOK_PHONE)
                } else {
                    errorMessage.postValue(ICheckApplication.getInstance().getString(R.string.dang_nhap_loi_vui_long_thu_lai))
                }
            }
        })
    }

    fun onLoginSuccess() {
        statusCode.postValue(ICMessageEvent.Type.ON_LOGIN_SUCCESS)
    }
}