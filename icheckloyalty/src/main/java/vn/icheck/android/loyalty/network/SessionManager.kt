package vn.icheck.android.loyalty.network

import android.os.Build
import android.text.TextUtils
import com.google.gson.Gson
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.network.APIConstants
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.model.ICSessionData
import java.io.File

internal object SessionManager {

    var session: ICSessionData
        get() {
            val sessionJson = SharedLoyaltyHelper(ApplicationHelper.getApplicationByReflect(), APIConstants.SESSION).getString(APIConstants.SESSION)
            var session: ICSessionData? = null
            if (!TextUtils.isEmpty(sessionJson)) {
                val gson = Gson()
                session = gson.fromJson(sessionJson, ICSessionData::class.java)
            }
            return session ?: ICSessionData()
        }
        set(session) {
            val gson = Gson()
            val _session = gson.toJson(session)
            SharedLoyaltyHelper(ApplicationHelper.getApplicationByReflect(), APIConstants.SESSION).putString(APIConstants.SESSION, _session)
        }

    var isLogged: Boolean
        get() {
            return SharedLoyaltyHelper(ApplicationHelper.getApplicationByReflect()).getBoolean(ConstantsLoyalty.IS_LOGGED)
        }
        set(isLogged) {
            SharedLoyaltyHelper(ApplicationHelper.getApplicationByReflect()).putBoolean(ConstantsLoyalty.IS_LOGGED, isLogged)
        }

    fun getUniqueDeviceId(): String? {
        return SharedLoyaltyHelper(ApplicationHelper.getApplicationByReflect(), APIConstants.DEVICE_ID).getString(APIConstants.DEVICE_ID)
    }

    private fun isDeviceRooted(): Boolean {
        val su = "su"
        val locations = arrayOf("/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/",
                "/system/bin/failsafe/", "/data/local/xbin/", "/data/local/bin/", "/data/local/",
                "/system/sbin/", "/usr/bin/", "/vendor/bin/")
        for (location in locations) {
            if (File(location + su).exists()) {
                return true
            }
        }
        return false
    }

    fun getModel(): String {
        return "Android OS " + Build.VERSION.RELEASE + "/" + Build.BRAND + " " + Build.MODEL +
                "/" + "Rooted :" + isDeviceRooted()
    }
}