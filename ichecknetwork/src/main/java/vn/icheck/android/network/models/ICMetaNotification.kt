package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICMetaNotification(
        @Expose var object_id: Long = 0,
        @Expose var object_type: String? = null
)