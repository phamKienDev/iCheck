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

}