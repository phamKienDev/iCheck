package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICDetail_Campaign : Serializable {
    @Expose
    var image: String = ""

    @Expose
    var sponsors: MutableList<ICDetail_Campaign.ListSponsors>? = null

    class ListSponsors : Serializable {
        @Expose
        var businessName: String? = null

        @Expose
        var logo: String? = null

        @Expose
        var id: Long? = null

        @Expose
        var type: Int? = null
    }

    @Expose
    var logo: String? = null

    @Expose
    var information: String? = null

    @Expose
    var id: String? = null

    // 1-chua tham gia , 2-dang tham gia , 3-da het han
    @Expose
    var state: Int? = null

    @Expose
    var title: String? = null

    @Expose
    var conditions: MutableList<String>? = null

    @Expose
    var endedAt: String? = null

    @Expose
    var guide: String? = null

    @Expose
    var beginAt: String? = null

    @Expose
    var userRanks: MutableList<String>? = null

    @Expose
    var businessName: String? = null

    @Expose
    var successNumber: Long? = null
}