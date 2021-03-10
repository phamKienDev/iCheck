package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICPageInformations(
        @Expose
        var key: String? = null,
        @Expose
        var name: String? = null,
        @Expose
        var description: String? = null,
        @Expose
        var avatar: String? = null,
        @Expose
        var data: MutableList<ICPrizePage>?=null
): Serializable