package vn.icheck.android.network.feature.setting

import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICClientSetting
import vn.icheck.android.network.models.ICConfigUpdateApp
import vn.icheck.android.network.models.ICSetting
import vn.icheck.android.network.models.ICThemeSetting

class SettingRepository : BaseInteractor() {

    suspend fun getThemeSetting(): ICResponse<ICThemeSetting> {
        val url = APIConstants.socialHost + APIConstants.themeSetting()
        return ICNetworkClient.getApiClient(5).getThemeSetting(url)
    }

    fun getClientSetting(listener: ICApiListener<ICClientSetting>) {
        requestApi(ICNetworkClient.getApiClient().getClientSetting, listener)
    }


    fun getCoinOfMe(listener: ICNewApiListener<ICResponse<ICSummary>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getCoinOfMe(), listener)
    }

    suspend fun getCoin(): ICResponse<ICSummary> {
        return ICNetworkClient.getSocialApi().getCoin(APIConstants.socialHost + APIConstants.User.ICOIN)
    }

    fun getSystemSetting(key: String?, keyGroup: String?, listener: ICNewApiListener<ICResponse<ICListResponse<ICClientSetting>>>) {
        val params = hashMapOf<String, Any>()
        if (!key.isNullOrEmpty()) {
            params["key"] = key
        }
        if (!keyGroup.isNullOrEmpty()) {
            params["keyGroup"] = keyGroup
        }
        requestNewApi(ICNetworkClient.getSocialApi().getSystemSetting(params), listener)
    }

    suspend fun getClientSetting(keyGroup: String?, key: String?): ICResponse<ICListResponse<ICClientSetting>> {
        val url = APIConstants.socialHost + APIConstants.Settings.SETTING_SOCIAL

        val params = hashMapOf<String, Any>()
        if (!keyGroup.isNullOrEmpty()) {
            params["keyGroup"] = keyGroup
        }
        if (!key.isNullOrEmpty()) {
            params["key"] = key
        }

        return ICNetworkClient.getSocialApi().getClientSettingSocial(url, params)
    }

    fun getClientSettingSocialVerify(key: String?, isVerified: Boolean?, productId: Long?, listener: ICNewApiListener<ICResponse<ICClientSetting>>) {
        val params = hashMapOf<String, Any>()
        if (isVerified != null) {
            params["isVerified"] = isVerified
        }
        if (productId != null || productId != 0L) {
            params["productId"] = productId!!
        }
        requestNewApi(ICNetworkClient.getNewSocialApi().getSettingMessageSocial(params), listener)
    }

    fun postNotifySettingSocial(notifyType: String, isActive: Boolean, listener: ICNewApiListener<ICResponse<ICSetting>>) {
        val params = hashMapOf<String, Any>()
        params["notifyType"] = notifyType
        params["isActive"] = isActive

        requestNewApi(ICNetworkClient.getSocialApi().postNotifySetting(params), listener)
    }

    fun getNotifySettingSocial(listener: ICNewApiListener<ICResponse<ICSetting>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getNotifySetting(), listener)
    }

    suspend fun getConfigUpdateApp(code: String, path: String? = null): ICResponse<ICConfigUpdateApp> {
        val url = APIConstants.socialHost + if (path != null) {
            APIConstants.PATH + path
        } else {
            APIConstants.Settings.CONFIG_UPDATE_APP
        }

        val params = hashMapOf<String, String>()
        params["code"] = code
        params["platform"] = "android"

        return ICNetworkClient.getSocialApi().getConfigUpdateApp(url, params)
    }
}