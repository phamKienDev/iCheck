package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICReqProductQuestion(
        @Expose val actor_id: Long,
        @Expose val content: String,
        @Expose val product_id: Long,
        @Expose val attachments: MutableList<ICReqAttachments>

)