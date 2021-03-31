package vn.icheck.android.chat.icheckchat.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class MCBasicInfo : Serializable {

    @Expose
    var id: Long? = null

    @Expose
    var basicInfo: MCProductFirebase? = null

    @Expose
    var media: MutableList<MCMedia>? = null
}