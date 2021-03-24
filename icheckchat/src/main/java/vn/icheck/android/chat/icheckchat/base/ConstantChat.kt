package vn.icheck.android.chat.icheckchat.base

import vn.icheck.android.chat.icheckchat.BuildConfig

object ConstantChat {
    const val PATH = "social/api"

    val SOCIAL_HOST: String
        get() {
            return if (BuildConfig.FLAVOR.contentEquals("dev")) "https://apiv2.dev.icheck.vn/" else "https://api-social.icheck.com.vn/"
        }

    val CHAT_HOST: String
        get() {
            return if (BuildConfig.FLAVOR.contentEquals("dev")) "http://proxy-chat.dev.icheck.vn/" else "http://proxy-chat.dev.icheck.vn/"
        }

    val UPLOAD_HOST: String
        get() {
            return "https://upload.icheck.com.vn/"
        }

    const val SCAN = 99

    const val DATA_1 = "DATA_1"
    const val DATA_2 = "DATA_2"
    const val DATA_3 = "DATA_3"
    const val TOKEN_FIREBASE = "TOKEN_FIREBASE"
    const val TOKEN = "TOKEN"
    const val DEVICE_ID = "DEVICE_ID"
    const val USER_ID = "USER_ID"
    const val BARCODE = "BARCODE"
    const val QR_CODE = "QR_CODE"
    const val VIDEO = "video"
    const val IMAGE = "image"
    const val KEY = "KEY"
}