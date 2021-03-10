package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICQuestion(
        @Expose val id: Long,
        @Expose val actor: ICActorV2,
        @Expose val content: String?,
        @Expose val attachments: MutableList<String>?,
        @Expose val liked: Boolean,
        @Expose val createdAt: String,
        @Expose val updatedAt: String,
        @Expose val answerCount: Int,
        @Expose var answers: MutableList<ICQuestion>?,
        var marginTop: Int = 0,
        var marginStart: Int = 0,
        var parentID: Long? = null
)


