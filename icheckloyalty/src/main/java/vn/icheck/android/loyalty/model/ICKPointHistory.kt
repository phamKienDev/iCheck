package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICKPointHistory : Serializable {
    @Expose
    val id: Long? = null

    @Expose
    var points: Long? = null

    @Expose
    var code: String? = null

    @Expose
    val gift_name: String? = null

    @Expose
    val message: String? = null

    @Expose
    var created_at: String? = null

    @Expose
    val updated_at: String? = null

    @Expose
    val avatar: Any? = null

    @Expose
    var serial: String? = null
}