package vn.icheck.android.network.models.detail_stamp_v6


import com.google.gson.annotations.Expose
import java.io.Serializable

data class User(
    @Expose
    var username: String? = null,
    @Expose
    var fullname: String? = null,
    @Expose
    var email: String? = null,
    @Expose
    var phone: String? = null
):Serializable