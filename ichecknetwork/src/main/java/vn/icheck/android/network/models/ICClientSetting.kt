package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import vn.icheck.android.network.base.DiffDistance

data class ICClientSetting(
        @Expose val goong_api_key: String? = null,
        @Expose val scan_notices: MutableList<ICMessage>? = null,
        @Expose val force_update: ICForceUpdate? = null,
        @Expose val faqs: String? = null,
        @Expose val version: Int? = null,
        @Expose val share_product_on_facebook: String? = null,
        @Expose val supports: MutableList<ICSupport>? = null,
        @Expose val seller_supports: MutableList<ICSupport>? = null,
        @Expose val messages: MutableList<ICMessage>? = null,
        @Expose val evs_url: ICEvsUrl? = null,
        @Expose val trust_domains: String? = null,
        @Expose val hotline: String? = null,
        @Expose val support_links: MutableList<ICLink>? = null,
        @Expose val seller_support_links: MutableList<ICLink>? = null,
        @Expose val scan_extra_info: MutableList<ICInfo>? = null,
        @Expose val location: DiffDistance,
        @Expose val my_card: ICMyCard? = null,
        @Expose val keyGroup: String? = null,
        @Expose val key: String? = null,
        @Expose val description: String? = null,
        @Expose val value: String? = null,
        @Expose val verifyMessage: String? = null,
        @Expose val supportUrl: String? = null,
        @Expose val fp_url: String? = null,
        @Expose val regex: String? = null
)