package vn.icheck.android.network.feature.auth

import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICSessionData
import vn.icheck.android.network.models.ICUser
import vn.icheck.android.network.util.DeviceUtils
import java.util.*

/**
 * Created by tamdv on 6/2/18.
 */
class AuthInteractor : BaseInteractor() {

    fun loginUser(username: String, password: String, listener: ICApiListener<ICSessionData>) {
        val fields = hashMapOf<String, String>()
        fields["username"] = username
        fields["password"] = password

        requestApi(ICNetworkClient.getApiClient().loginUser(fields), listener)
    }

    fun loginFacebook(facebookToken: String?, phone: String?, otp: String?, password: String?, listener: ICApiListener<ICSessionData>) {
        val body = hashMapOf<String, String>()

        if (facebookToken != null) {
            body["facebook_token"] = facebookToken
        }
        if (phone != null) {
            body["phone"] = phone
        }
        if (otp != null) {
            body["otp"] = otp
        }
        if (password != null) {
            body["password"] = password
        }

        requestApi(ICNetworkClient.getApiClient().loginFacebook(body), listener)
    }

    fun loginAnonymous(listener: ICApiListener<ICResponse<ICSessionData>>) {
        val url = APIConstants.socialHost + APIConstants.Auth.LOGIN_DEVICE

        val body = hashMapOf<String, Any>()
        body["deviceId"] = DeviceUtils.getUniqueDeviceId()
        body["os"] = 1

        requestApi(ICNetworkClient.getApiClient().loginDevice(url, body), listener)
    }

    suspend fun loginAnonymousV2(): ICResponse<ICSessionData> {
        val url = APIConstants.socialHost + APIConstants.Auth.LOGIN_DEVICE

        val body = hashMapOf<String, Any>()
        body["deviceId"] = DeviceUtils.getUniqueDeviceId()
        body["os"] = 1

        return ICNetworkClient.getApiClient().loginAnonymous(url, body)
    }

    fun changePassword(oldPassword: String, password: String, listener: ICApiListener<ICUser>) {
        val hashMap = hashMapOf<String, String>()
        hashMap["old_password"] = oldPassword
        hashMap["password"] = password

        requestApi(ICNetworkClient.getApiClient().changePassword(hashMap), listener)
    }
}