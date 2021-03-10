package vn.icheck.android.loyalty.base.network

import vn.icheck.android.loyalty.BuildConfig

internal object APIConstants {
    const val SESSION = "SESSION"
    const val DEVICE_ID = "DEVICE_ID"

    const val LIMIT = 10

    val LOYALTY_HOST: String
        get() {
//            return if (BuildConfig.FLAVOR.contentEquals("dev")) "https://api.dev.icheck.vn/api/business/" else "https://api.icheck.com.vn/api/business/"
            return if (BuildConfig.FLAVOR.contentEquals("dev")) "https://api.dev.icheck.vn/api/business/" else "https://api-social.icheck.com.vn/api/business/"
        }

    val USER_HOST: String
        get() {
            return if (BuildConfig.FLAVOR.contentEquals("dev")) "https://api.dev.icheck.vn/api/v1/" else "https://api.icheck.com.vn/api/v1/"
        }

    val SOCIAL_HOST: String
        get() {
            return if (BuildConfig.FLAVOR.contentEquals("dev")) "https://apiv2.dev.icheck.vn/" else "https://api-social.icheck.com.vn/"
        }
}