package vn.icheck.android.network.models.pvcombank

import com.google.gson.annotations.Expose

data class ICAuthenPVCard (
        @Expose var authUrl: String?,
        @Expose var sessionId: String?,
        @Expose var redirectUrl: String?
)