package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICProvince(
        @Expose val id: Int = 0,
        @Expose val name: String = "",
        @Expose val country_id: Int = 0,
        var searchKey: String = ""
) : Serializable