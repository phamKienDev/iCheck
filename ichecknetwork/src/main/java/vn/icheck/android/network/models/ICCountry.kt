package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICCountry(
        @Expose val id: Int = 0,
        @Expose val code: String = "",
        @Expose val name: String = ""
): Serializable