package vn.icheck.android.network.base

import com.google.gson.annotations.Expose

open class ICResponseErrorStamp (
    @Expose
    var error: Boolean? = null,
    @Expose
    var status: Int? = null,
    @Expose
    var data: ObjectErrorStamp? = null
)