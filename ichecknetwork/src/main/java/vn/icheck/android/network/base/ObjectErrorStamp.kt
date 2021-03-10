package vn.icheck.android.network.base

import com.google.gson.annotations.Expose
import vn.icheck.android.network.models.detail_stamp_v6_1.ICMessageErrorStamp

data class ObjectErrorStamp (
        @Expose
        var message: ICMessageErrorStamp? = null
)