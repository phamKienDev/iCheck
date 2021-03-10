package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICWard(
        @Expose var id: Int = 0,
        @Expose var name: String = "",
        @Expose val district_id: Int = 0,
        var searchKey: String = "",
        @Expose val districtId: Int = 0
) : Serializable