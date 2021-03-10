package vn.icheck.android.network.models.v1

import com.google.gson.annotations.SerializedName
import vn.icheck.android.network.models.ICAttachments

data class ICProductQuestions(
        @SerializedName("count")
        val count: Int,
        @SerializedName("rows")
        val questionsList: List<ICQuestionRow>
)

data class ICQuestionRow(
        @SerializedName("id")
        val id: Long,
        @SerializedName("content")
        val content: String,
        @SerializedName("actor")
        val actor: ICQuestionActor,
        @SerializedName("product_id")
        val product_id: Long,
        @SerializedName("answer_count")
        var answer_count: Int,
        @SerializedName("created_at")
        val created_at: String,
        @SerializedName("updated_at")
        val updated_at: String,
        @SerializedName("attachments")
        val attachments: MutableList<ICAttachments>,
        @SerializedName("answers")
        var answers: MutableList<ICQuestionsAnswers>,
        @SerializedName("is_hidden_by_content_owner")
        val hidden:Boolean
)

data class ICQuestionActor(
        @SerializedName("id")
        val id: Long,
        @SerializedName("name")
        val name: String,
        @SerializedName("type")
        val type: String,
        @SerializedName("verified")
        val verified: Boolean,
        @SerializedName("avatar_thumbnails")
        val avatarThumbnails: ICAvatarThumbnails?=null

)

data class ICQuestionsAnswers(
        @SerializedName("id")
        val id: Long,
        @SerializedName("actor_id")
        val actor_id: Long,
        @SerializedName("question_id")
        val question_id: Long,
        @SerializedName("content")
        val content: String,
        @SerializedName("attachments")
        val attachments: MutableList<ICAttachments>,
        @SerializedName("actor")
        val actor: ICQuestionActor,
        @SerializedName("created_at")
        val created_at: String,
        @SerializedName("updated_at")
        val updated_at: String
)

data class ICAvatarThumbnails(
        @SerializedName("original")
        val original: String
)

