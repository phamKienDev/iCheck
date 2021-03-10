package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICDistrict(
        @Expose var id: Int = 0,
        @Expose var city_id: Int = 0,
        @Expose var name: String = "",
        @Expose var code : String = "",
        var searchKey: String = "",
        @Expose var cityId: Int = 0
) : Serializable