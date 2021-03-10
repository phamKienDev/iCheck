package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICRespHideSurvey(
        @Expose val id: Long? = null,
        @Expose val device_id: String? = null,
        @Expose val ad_id: Long? = null
)