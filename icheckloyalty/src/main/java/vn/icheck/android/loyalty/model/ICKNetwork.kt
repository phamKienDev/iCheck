package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
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