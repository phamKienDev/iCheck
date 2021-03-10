package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICWard(
        @Expose val id: Int = 0,
        @Expose val name: String = "",
        @Expose val district_id: Int = 0,
        var searchKey: String = ""
) : Serializable