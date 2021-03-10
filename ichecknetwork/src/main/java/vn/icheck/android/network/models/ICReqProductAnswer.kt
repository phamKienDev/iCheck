package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICReqProductAnswer(
        @Expose val actor_id: Long,
        @Expose val content: String,
        @Expose val question_id: Long,
        @Expose val attachments: MutableList<ICReqAttachments>
)