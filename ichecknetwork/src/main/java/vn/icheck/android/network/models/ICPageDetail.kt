package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICPageDetail : Serializable {

    @Expose
    var id: Long? = null

    @Expose
    var location: ICLocation? = null

    @Expose
    var lat: Double = 0.0

    @Expose
    var lon: Double = 0.0

    @Expose
    var address: String? = null

    @Expose
    var mail: String? = null

    @Expose
    var phone: String? = null

    @Expose
    var website: String? = null

    @Expose
    var objectType: String? = null

    @Expose
    var verified: Boolean = false

    @Expose
    var newDetail: ICNewDetail? = null

    @Expose
    var infomations: MutableList<ICPageInformations>? = null

    @Expose
    var unsubscribeNotice: Boolean? = null

    var icPageOverView : ICPageOverview? = null
}