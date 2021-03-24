package vn.icheck.android.chat.icheckchat.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class MCRoom : Serializable {

    @Expose
    val room_id: String? = null

    @Expose
    val members: MutableList<MCMemberApi>? = null
}