package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICDistrict(
        @Expose val id: Int = 0,
        @Expose val city_id: Int = 0,
        @Expose val name: String = "",
        var searchKey: String = ""
) : Serializable