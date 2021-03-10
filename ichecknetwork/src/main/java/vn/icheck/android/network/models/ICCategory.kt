package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICCategory : Serializable {
    @Expose
    var id: Long = 0

    @Expose
    var name: String? = null

    @Expose
    var image: String? = null

    @Expose
    var parent_id: Long? = null

    @Expose
    var position = 0

    @Expose
    var status = 0

    @Expose
    var slug: String? = null

    @Expose
    var attribute_set_id: Long = 0

    @Expose
    var thumbnails: ICThumbnail? = null

    @Expose
    var level = 0

    @Expose
    var categoryId = 0
    var selected = false
}