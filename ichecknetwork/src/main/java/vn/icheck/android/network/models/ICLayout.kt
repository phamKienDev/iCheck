package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ICLayout(
        @Expose val id: String? = "",
        @Expose var key: String = "",
        @Expose val request: ICRequest = ICRequest(),
        @Expose val custom: ICCustom? = null,
        @Expose val entityIdList: MutableList<Long>? = null,
        var viewType: Int = 0,
        var data: Any? = null,
        var subType: Int = 0
)