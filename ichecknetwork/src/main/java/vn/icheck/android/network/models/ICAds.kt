package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

class ICAds {
    @Expose
    var name: String? = null
    @Expose
    var id: Long = 0
    @Expose
    var account_id: Long = 0
    @Expose
    var type: String? = null
    @Expose
    var banner_id: String? = null
    @Expose
    var banner_thumbnails: ICThumbnail? = null
    @Expose
    var banner_url: String? = null
    @Expose
    var banner_size: String? = null
    @Expose
    var destination_url: String? = null
    @Expose
    var survey_id: Long = 0
    @Expose
    var campaign_id: Long = 0
    @Expose
    var adset_id: Long = 0
    @Expose
    var collection_id: Long = 0
    @Expose
    var created_at: String? = null
    @Expose
    var updated_at: String? = null
    @Expose
    var isIs_active: Boolean = false
    @Expose
    var isVerified: Boolean = false
    @Expose
    var html: String? = null
    @Expose
    var landing_duration: Int = 0
    @Expose
    var click_url: String? = null
    @Expose
    var respond_url: String? = null
    @Expose
    var notification_title: String? = null
    @Expose
    var notification_icon: String? = null
    @Expose
    var notification_message: String? = null
    @Expose
    var display_config: ICDisplayConfig? = null
    @Expose
    var start_time: String? = null
    @Expose
    var survey: ICSurvey? = null
    @Expose
    var collection: ICCollection? = null
}