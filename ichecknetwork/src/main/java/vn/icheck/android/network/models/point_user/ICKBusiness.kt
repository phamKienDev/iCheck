package vn.icheck.android.network.models.point_user

import com.google.gson.annotations.Expose
import vn.icheck.android.network.models.ICThumbnail
import java.io.Serializable

class ICKBusiness : Serializable {

    @Expose
    val id: Long? = null

    @Expose
    val type: String? = null

    @Expose
    val name: String? = null

    @Expose
    val address: String? = null

    @Expose
    val logo: ICThumbnail? = null
}