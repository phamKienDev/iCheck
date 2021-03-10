package vn.icheck.android.network.models.detail_stamp_v6

import com.google.gson.annotations.Expose

data class ICListHistoryGuaranteeV6 (
        @Expose
        var error:Boolean? = null,
        @Expose
        var status:Int? = null,
        @Expose
        var data: ObjectLogHistoryV6? = null
)