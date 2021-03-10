package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICNewDetail : Serializable {
    @Expose
    var avatar: String? = null

    @Expose
    var title: String? = null

    @Expose
    var description: String? = null

    @Expose
    var website: String? = null

    @Expose
    var content: String? = null
}