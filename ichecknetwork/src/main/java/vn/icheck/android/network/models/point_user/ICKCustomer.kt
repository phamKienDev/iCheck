package vn.icheck.android.network.models.point_user

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICKCustomer : Serializable {

    @Expose
    val name: String? = null

    @Expose
    val points: Long? = null

    @Expose
    val point: Long? = null

    @Expose
    val code: String? = null
}
