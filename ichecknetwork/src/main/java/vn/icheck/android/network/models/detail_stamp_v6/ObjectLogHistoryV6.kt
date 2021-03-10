package vn.icheck.android.network.models.detail_stamp_v6

import com.google.gson.annotations.Expose

data class ObjectLogHistoryV6(
        @Expose
        var logs: MutableList<RESP_Log_History_v6>? = null,
        @Expose
        var total: Int? = null
)