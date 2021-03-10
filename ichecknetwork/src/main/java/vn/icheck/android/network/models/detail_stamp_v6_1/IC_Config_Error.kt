package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose

class IC_Config_Error {
    @Expose
    var error: Boolean? = null
    @Expose
    var status: Int? = null
    @Expose
    var data: ObjectConfigError? = null

    class ObjectConfigError {
        @Expose
        var code: String? = null
        @Expose
        var contacts: MutableList<ObjectConfigErrorContact>? = null
        @Expose
        var id: Int? = null
        @Expose
        var msg_error: String? = null
        @Expose
        var msg_guarantee_active: String? = null
        @Expose
        var msg_guarantee_after_active: String? = null
        @Expose
        var msg_sms_error: String? = null
        @Expose
        var msgSmsSuccess: String? = null
        @Expose
        var msg_sms_warning: String? = null
        @Expose
        var msg_success: String? = null
        @Expose
        var msg_warning: String? = null
        @Expose
        var prefix_length: Int? = null
        @Expose
        var term_guarantee_content: String? = null
        @Expose
        var term_guarantee_intro: String? = null
        @Expose
        var term_guarantee_title: String? = null
        @Expose
        var term_verify_content: String? = null
        @Expose
        var term_verify_intro: String? = null
        @Expose
        var term_verify_title: String? = null

        class ObjectConfigErrorContact{
            @Expose
            var description: String? = null
            @Expose
            var email: String? = null
            @Expose
            var hotline: String? = null
            @Expose
            var title: String? = null
        }
    }
}