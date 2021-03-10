package vn.icheck.android.network.models.wall

import com.google.gson.annotations.Expose
import vn.icheck.android.network.models.ICUser

data class ICUserPublicInfor(
        @Expose var privacyId:Int?,
        @Expose var privacyCode:String?,
        @Expose var privacyName:String?,
        @Expose var privacyType:String?,
        @Expose var privacyOrder:Int?,
        @Expose var privacyElementId:Int?,
        @Expose var privacyElementCode:String?,
        @Expose var privacyElementName:String?,
        @Expose var privacyElementOrder:Int?,
        @Expose var selected:Boolean? = null,
        var user : ICUser?
)