package vn.icheck.android.network.models.point_user

import com.google.gson.annotations.Expose
import vn.icheck.android.network.models.ICThumbnail
import java.io.Serializable

class ICKOwner : Serializable {
    @Expose
    val id: Long? = null

    @Expose
    val type: String?=null

    @Expose
    var name: String?=null

    @Expose
    val address: String?=null

    @Expose
    val icheck_id: Long?=null

    @Expose
    var logo: ICThumbnail?=null
}
