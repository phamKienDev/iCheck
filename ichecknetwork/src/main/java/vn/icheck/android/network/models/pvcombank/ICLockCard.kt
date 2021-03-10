package vn.icheck.android.network.models.pvcombank

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICLockCard (
        @Expose var verification: ICVerificationPVBank? = null,
        @Expose var fullCard: String? = null
): Serializable