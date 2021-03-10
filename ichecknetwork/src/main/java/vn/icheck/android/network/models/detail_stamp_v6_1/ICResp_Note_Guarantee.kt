package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose

class ICResp_Note_Guarantee {
    @Expose
    var error: Boolean? = null

    @Expose
    var status: Int? = null

    @Expose
    var data: ObjectLog? = null

    class ObjectLog {
        @Expose
        var logs: ObjectChildLog? = null

        class ObjectChildLog {
            @Expose
            var total: Int? = null

            @Expose
            var list: MutableList<ICItemNote>? = null

            class ICItemNote {
                @Expose
                var _id: String? = null

                @Expose
                var note: String? = null

                @Expose
                var images: MutableList<String>? = null

                @Expose
                var log_id: String? = null

                @Expose
                var user_id: Long? = null

                @Expose
                var user_created: Int? = null

                @Expose
                var created_at: String? = null

                @Expose
                var __v: Int? = null
            }
        }
    }

}