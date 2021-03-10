package vn.icheck.android.network.models.pvcombank

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICVerificationPVCard (
        @Expose var bypass: String? = null,
        @Expose var requestId: String? = null,
        @Expose var otpTransId: String? = null
) : Serializable