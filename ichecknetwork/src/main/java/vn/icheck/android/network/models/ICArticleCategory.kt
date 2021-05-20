package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICArticleCategory : Serializable {

    @Expose
    val id: Long? = null

    @Expose
    val code: String? = null

    @Expose
    val name: String? = null

    @Expose
    val description: String? = null

    @Expose
    val createdById: String? = null
}