package vn.icheck.android.network.models.product_detail

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICManager(
        @Expose var id: Long? = null,
        @Expose var code: Int? = null,
        @Expose var message : String? = null,
        @Expose var name : String? = null,
        @Expose var phone: String? = null,
        @Expose var email: String? = null,
        @Expose var avatar: String? = null,
) : Serializable
