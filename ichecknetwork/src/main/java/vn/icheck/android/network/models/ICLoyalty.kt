package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICLoyalty (
    @Expose
    var id: Long = 0,
    @Expose
    val type: String? = null,
    @Expose
    var target_type: String? = null,
    @Expose
    var image: String? = null,
    @Expose
    var benefit: Long? = 0,
    @Expose
    var description: String? = null,
    @Expose
    var status: String? = null,
    @Expose
    var creator_id: Long? = null,
    @Expose
    var owner_id: Long? = null,
    @Expose
    var user_count: Long? = null,
    @Expose
    var winner_count: Long? = null,
    @Expose
    var publish_type: String? = null,
    @Expose
    var start_at: String? = null,
    @Expose
    var end_at: String? = null,
    @Expose
    var publish_at: String? = null,
    @Expose
    var created_at: String? = null,
    @Expose
    var update_at: String? = null,
    @Expose
    var delete_at: String? = null
)