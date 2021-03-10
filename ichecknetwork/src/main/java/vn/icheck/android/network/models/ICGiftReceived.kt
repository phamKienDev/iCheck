package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICGiftReceived(
        @Expose val image: String? = null,
        @Expose val expiredAt: String? = null,
        @Expose val number: Int? = null,
        @Expose val businessName: String? = null,
        @Expose val entityId: Int? = null,
        @Expose val remainTime: String? = null,
        @Expose val id: String? = null,
        @Expose val state: Int? = null,
        @Expose val type: Int? = null,
        @Expose val title: String? = null,
        @Expose val receivedAt: String? = null,
        @Expose val businessType: Int? = null)


