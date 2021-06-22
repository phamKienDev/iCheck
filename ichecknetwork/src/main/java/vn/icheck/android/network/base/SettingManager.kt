package vn.icheck.android.network.base

import android.content.Context
import com.google.gson.Gson
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.network.models.ICClientSetting
import vn.icheck.android.network.models.ICCommentPermission
import vn.icheck.android.network.models.ICConfigUpdateApp
import vn.icheck.android.network.models.ICThemeSetting
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.network.util.SPStaticUtils

object SettingManager {

    fun setClientSetting(clientSetting: ICClientSetting) {
        val gson = Gson()
        val mClientSetting = gson.toJson(clientSetting)
        SPStaticUtils.put(TagConstants.CONFIG, mClientSetting)
    }

    val clientSetting: ICClientSetting?
        get() {
            val sessionJson = SPStaticUtils.getString(TagConstants.CONFIG)
            return JsonHelper.parseJson(sessionJson, ICClientSetting::class.java)
        }

    fun setUserCoin(coin: Long) {
        SPStaticUtils.put(TagConstants.COIN, coin)
    }

    val getUserCoin: Long
        get() {
            return SPStaticUtils.getLong(TagConstants.COIN, 0)
        }

    fun setRankLevel(rank: Int) {
        SPStaticUtils.put(TagConstants.RANK_LEVEL, rank)
    }

    val getRankLevel: Int
        get() {
            return SPStaticUtils.getInt(TagConstants.RANK_LEVEL, 1)
        }

    fun setVibrateSetting(isVibrate: Boolean) {
        SPStaticUtils.put(TagConstants.VIBRATE_SETTING, isVibrate)
    }

    val getVibrateSetting: Boolean
        get() {
            return SPStaticUtils.getBoolean(TagConstants.VIBRATE_SETTING, true)
        }

    fun setLanguageENSetting(english: Boolean) {
        SPStaticUtils.put(TagConstants.LANGUAGE_EN, english)
    }

    val getLanguageENSetting: Boolean
        get() {
            return SPStaticUtils.getBoolean(TagConstants.LANGUAGE_EN, false)
        }

    fun setSoundSetting(isVibrate: Boolean) {
        SPStaticUtils.put(TagConstants.SOUND_SETTING, isVibrate)
    }

    val getSoundSetting: Boolean
        get() {
            return SPStaticUtils.getBoolean(TagConstants.SOUND_SETTING, true)
        }

//    fun setThemeUpdateTime(updateTheme: Long) {
//        SPStaticUtils.put(TagConstants.THEME_UPDATE_TIME, updateTheme)
//    }

//    val getThemeUpdateTime: Long
//        get() {
//            return SPStaticUtils.getLong(TagConstants.THEME_UPDATE_TIME, 0)
//        }

    var themeSetting: ICThemeSetting?
        get() {
            return JsonHelper.parseJson(SPStaticUtils.getString(TagConstants.SETTING_THEME), ICThemeSetting::class.java)
        }
        set(value) {
            SPStaticUtils.put(TagConstants.SETTING_THEME, JsonHelper.toJson(value ?: ICThemeSetting()))
        }

    fun setDeviceID(deviceID: String) {
        SPStaticUtils.put(TagConstants.DEVICE_ID, deviceID)
    }

    fun setAppThemeColor(theme: ICThemeSetting.ICTheme?,context: Context) {
        ColorManager.saveColor(context,Constant.PRIMARY_COLOR,theme?.primaryColor)
        ColorManager.saveColor(context,Constant.SECONDARY_COLOR,theme?.secondaryColor)
        ColorManager.saveColor(context,Constant.NORMAL_TEXT_COLOR,theme?.normalTextColor)
        ColorManager.saveColor(context,Constant.SECOND_TEXT_COLOR,theme?.secondTextColor)
        ColorManager.saveColor(context,Constant.DISABLE_TEXT_COLOR,theme?.disableTextColor)
        ColorManager.saveColor(context,Constant.LINE_COLOR,theme?.lineColor)
        ColorManager.saveColor(context,Constant.APP_BACKGROUND_COLOR,theme?.appBackgroundColor)
        ColorManager.saveColor(context,Constant.POPUP_BACKGROUND_COLOR,theme?.popupBackgroundColor)
    }

    val getDeviceID: String
        get() {
            return SPStaticUtils.getString(TagConstants.DEVICE_ID)
        }

    fun setSessionIdPvcombank(session: String) {
        SPStaticUtils.put(TagConstants.SESSION_ID_PVCOMBANK, session)
    }

    val getSessionPvcombank: String
        get() {
            return SPStaticUtils.getString(TagConstants.SESSION_ID_PVCOMBANK)
        }

    fun clear() {
        setUserCoin(0L)
        setRankLevel(1)
        setSessionIdPvcombank("")
    }

    var domainQr: List<ICClientSetting>
        get() {
            return JsonHelper.parseDomainQr(SPStaticUtils.getString(TagConstants.DOMAIN_QR))
        }
        set(value) {
            SPStaticUtils.put(TagConstants.DOMAIN_QR, JsonHelper.toJson(value))
        }

    var trustDomain: List<ICClientSetting>
        get() {
            return JsonHelper.parseDomainQr(SPStaticUtils.getString(TagConstants.TRUST_DOMAIN))
        }
        set(value) {
            SPStaticUtils.put(TagConstants.TRUST_DOMAIN, JsonHelper.toJson(value))
        }

    var productContact: List<ICClientSetting>
        get() {
            return JsonHelper.parseDomainQr(SPStaticUtils.getString(TagConstants.PRODUCT_CONTACT))
        }
        set(value) {
            SPStaticUtils.put(TagConstants.PRODUCT_CONTACT, JsonHelper.toJson(value))
        }

    var configUpdateApp: ICConfigUpdateApp?
        get() {
            return JsonHelper.parseJson(SPStaticUtils.getString(TagConstants.CONFIG_UPDATE_APP), ICConfigUpdateApp::class.java)
        }
        set(value) {
            SPStaticUtils.put(TagConstants.CONFIG_UPDATE_APP, JsonHelper.toJson(value ?: ICConfigUpdateApp()))
        }

    var appVersion: String
        get() {
            return SPStaticUtils.getString(TagConstants.APP_VERSION)
        }
        set(value) {
            return SPStaticUtils.put(TagConstants.APP_VERSION, value)
        }

    fun getPostPermission(): ICCommentPermission? {
        return if (!SPStaticUtils.getString(TagConstants.PERMISSION_IN_POST).isNullOrEmpty()) {
            JsonHelper.parseJson(SPStaticUtils.getString(TagConstants.PERMISSION_IN_POST), ICCommentPermission::class.java)
        } else {
            if (SessionManager.isUserLogged) {
                val user = SessionManager.session.user
                ICCommentPermission(user?.id, user?.avatar ?: "", user?.getName, "user")
            } else {
                null
            }
        }

    }

    fun setPostPermission(permission: ICCommentPermission?) {
        val gson = Gson()
        val permissionString = when {
            permission != null -> {
                gson.toJson(permission)
            }
            else -> {
                ""
            }
        }
        SPStaticUtils.put(TagConstants.PERMISSION_IN_POST, permissionString)

    }
}