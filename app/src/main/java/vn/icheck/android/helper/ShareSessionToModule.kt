package vn.icheck.android.helper

import vn.icheck.android.loyalty.sdk.LoyaltySdk
import vn.icheck.android.network.models.ICSessionData
import vn.icheck.android.network.util.DeviceUtils

object ShareSessionToModule {

    fun setSession(obj: ICSessionData) {
        val user = obj.user
        val city = user?.city
        val district = user?.district
        val ward = user?.ward
        val country = user?.country
        val avatar = user?.avatar_thumbnails

        LoyaltySdk.setCityData(city?.id?.toInt(), city?.name, city?.country_id)
        LoyaltySdk.setDistrictData(district?.id, district?.city_id, district?.name)
        LoyaltySdk.setCountryData(country?.id, country?.code, country?.name)
        LoyaltySdk.setWardData(ward?.id, ward?.name, ward?.district_id)
        LoyaltySdk.setAvatarData(avatar?.thumbnail, avatar?.original, avatar?.small, avatar?.medium, avatar?.square)

        LoyaltySdk.isLogged(!obj.token.isNullOrEmpty() && obj.userType != 1 && obj.userType != null)

        LoyaltySdk.setUserData(user?.id ?: 0,
                user?.name,
                user?.email,
                user?.phone,
                user?.avatar,
                user?.address,
                user?.city_id,
                user?.district_id,
                user?.country_id,
                user?.ward_id,
                user?.device_id)

        LoyaltySdk.setSessionData(obj.token, obj.tokenType, obj.expiresIn, obj.refreshToken, obj.firebaseToken, DeviceUtils.getUniqueDeviceId(), obj.userType)
    }
}