package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICJoinCampaign(
        @Expose
        var code: Int? = null,
        @Expose
        var message:String? = null
)