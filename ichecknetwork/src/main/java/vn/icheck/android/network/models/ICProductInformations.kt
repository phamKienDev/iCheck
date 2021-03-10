package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICProductInformations(
        @Expose val id: Long? = null,
        @Expose val title: String? = null,
        @Expose val code: String? = null,
        @Expose val image: String? = null,
        @Expose val name: String? = null,
        @Expose val shortContent: String? = null,
        var productID: Long = 0,
        var productImage: String? = null
) : Serializable