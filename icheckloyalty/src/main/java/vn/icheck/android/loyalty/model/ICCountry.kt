package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICCountry(
        @Expose val id: Int = 0,
        @Expose val code: String? = null,
        @Expose val name: String = ""
) : Serializable