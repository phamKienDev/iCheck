package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import vn.icheck.android.ichecklibs.R
import java.io.Serializable

open class ICPage : Serializable {
    @Expose
    var id: Long? = null

    @Expose
    var name: String? = null

    @Expose
    val avatar: String? = null

    @Expose
    var attachments: MutableList<ICAttachments>? = null

    @Expose
    var followers: MutableList<ICUserFollowPage>? = null

    @Expose
    var productCount: Long? = null

    @Expose
    var requestCount: Long? = null

    @Expose
    var rating: Double? = null

    @Expose
    var scanCount: Long? = null

    @Expose
    var followCount: Long? = null

    @Expose
    var description: String? = null

    @Expose
    var infomations: MutableList<ICPageInformations>? = null

    @Expose
    val cover: String? = null

    @Expose
    var phone: String? = null

    @Expose
    var email: String? = null

    @Expose
    var mail: String? = null

    @Expose
    var address: String? = null

    @Expose
    var tax: String? = null

    @Expose
    var website: String? = null

    @Expose
    val facebook: String? = null

    @Expose
    val youtube: String? = null

    @Expose
    var verified: Boolean? = null

    @Expose
    val title: String? = null

    @Expose
    val exclusive: Boolean = false

    @Expose
    val objectType: String? = null

    @Expose
    val objectId: Int = 0

    @Expose
    val isVerify: Boolean = false

    @Expose
    val pageId: Long? = null

    @Expose
    val owner: ICOwner? = null

    @Expose
    val location: ICLocation? = null

    val getName: String
        get() = name ?: "Chưa cập nhật"

    var icon: Int = 0
    var background: Int = R.color.colorDisableText
}