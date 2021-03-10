package vn.icheck.android.network.models.pvcombank

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICVerificationPVBank(
        @Expose var bypass: String?,
        @Expose var requestId: String?,
        @Expose var otpTransId: String?
) : Serializable
