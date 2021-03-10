package vn.icheck.android.loyalty.model

import java.io.Serializable

data class ICSessionData(
        val accessToken: String? = null,
        val tokenType: String? = null,
        val expiresIn: Long = 0,
        val refreshToken: String? = null,
        val firebaseToken: String? = null,
        val user: ICUser? = null,
        val userType: Int? = null
): Serializable