package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import kotlin.Any

class ICProduct_Search {
    @Expose
    var id: Long? = null
    @Expose
    var name: String? = null
    @Expose
    var price: Long? = null
    @Expose
    var image: String? = null
    @Expose
    var barcode: String? = null
    @Expose
    var rating: Float? = null
    @Expose
    var review_count: Int? = null
    @Expose
    var thumbnails: ICProduct_Search.ObjectThumbnails? = null

    class ObjectThumbnails {
        @Expose
        var original: String? = null
        @Expose
        var square: String? = null
        @Expose
        var thumbnail: String? = null
        @Expose
        var small: String? = null
        @Expose
        var medium: String? = null
    }

    @Expose
    var status: Int? = null
    @Expose
    var question_count: Int? = null
    @Expose
    var owner: Any? = null
    @Expose
    var vendor: Any? = null
    @Expose
    var distributors: Any? = null
    @Expose
    var other_pages: Any? = null
    @Expose
    var created_at: String? = null
    @Expose
    var updated_at: String? = null
    @Expose
    var disable_contribution: Boolean? = null
    @Expose
    var alert_start_date: String? = null
    @Expose
    var alert_end_date: String? = null
    @Expose
    var scan_count: Int? = null
        @Expose
    var hide_fields: Any? = null
    @Expose
    var hide_start_date: String? = null
    @Expose
    var hide_end_date: String? = null
    @Expose
    var type: String? = null
    @Expose
    var verified: Boolean? = null
    @Expose
    var verify_by_user_id: Int? = null
    @Expose
    var should_hide_fields: Boolean? = null
//    @Expose
//    var meta:String? = null
}