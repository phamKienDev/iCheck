package vn.icheck.android.network.models.post

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICQAInPost (
        @Expose
        var id:Long?,
        @Expose
        var userId:Long?,
        @Expose
        var msg:String?,
        @Expose
        var typeMessage:String?
):Serializable