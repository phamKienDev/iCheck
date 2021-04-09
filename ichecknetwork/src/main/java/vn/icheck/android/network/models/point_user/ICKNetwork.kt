package vn.icheck.android.network.models.point_user

import com.google.gson.annotations.Expose
import vn.icheck.android.network.models.ICThumbnail
import java.io.Serializable

class ICKNetwork : Serializable {

    @Expose
    val id: Long? = null

    @Expose
    val point_name: String? = null

    @Expose
    val banner: ICThumbnail? = null

    @Expose
    val name: String? = null

    @Expose
    val logo: ICThumbnail? = null
}
