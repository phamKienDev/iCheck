package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICProvince(
        @Expose var id: Long = 0,
        @Expose var name: String = "",
        @Expose var country_id: Int = 0,
        var searchKey: String = "",
        var selected: Boolean = false,
        @Expose var countryId: Int = 0
) : Serializable