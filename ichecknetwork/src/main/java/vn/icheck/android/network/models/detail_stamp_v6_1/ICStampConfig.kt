package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose

data class ICStampConfig(
        @Expose
        var code: String? = null,
        @Expose
        var contacts: MutableList<ICStampContact>? = null,
        @Expose
        var id: Int? = null,
        @Expose
        var msg_error: String? = null,
        @Expose
        var msg_guarantee_active: String? = null,
        @Expose
        var msg_guarantee_after_active: String? = null,
        @Expose
        var msg_sms_error: String? = null,
        @Expose
        var msgSmsSuccess: String? = null,
        @Expose
        var msg_sms_warning: String? = null,
        @Expose
        var msg_success: String? = null,
        @Expose
        var msg_warning: String? = null,
        @Expose
        var prefix_length: Int? = null,
        @Expose
        var term_guarantee_content: String? = null,
        @Expose
        var term_guarantee_intro: String? = null,
        @Expose
        var term_guarantee_title: String? = null,
        @Expose
        var term_verify_content: String? = null,
        @Expose
        var term_verify_intro: String? = null,
        @Expose
        var term_verify_title: String? = null,

        var errorMessage: String? = null
)