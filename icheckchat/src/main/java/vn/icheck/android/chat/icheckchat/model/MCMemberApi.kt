package vn.icheck.android.chat.icheckchat.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class MCMemberApi : Serializable {

    @Expose
    val name: String? = null

    @Expose
    val avatar: String? = null

    @Expose
    val id: String? = null

    @Expose
    val source_id: Long? = null
}