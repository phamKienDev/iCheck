package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
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