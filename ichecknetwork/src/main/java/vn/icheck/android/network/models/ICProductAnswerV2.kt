package vn.icheck.android.network.models

import vn.icheck.android.network.models.chat.Stickers
import kotlin.Any

data class ICProductAnswerV2(
        val attachmentThumbnails: List<ICThumbnailAttachment>? = null,
        val likeCount: Int? = null,
        val expressived: String? = null,
        val postId: Int? = null,
        val userId: Long? = null,
        val content: String? = null,
        val objectType: String? = null,
        val isHidden: Boolean? = null,
        val createdAt: String? = null,
        val attachment: Any? = null,
        val replies: List<Any?>? = null,
        val commentId: Any? = null,
        val id: Long = -1L,
        val objectId: Int? = null,
        val updatedAt: String? = null,
        val user: ICUser? = null,
        var liked: Boolean = false,
        var parentID:Long?=null,
        var marginTop:Int=0,
        var marginStart:Int=0,
        var stickers: Stickers?=null,
        val pinned: Boolean? = null,
        val productId: Long? = null,
        val hidden: Boolean? = null,
        var answers: MutableList<ICProductAnswerV2>? = null,
        val pageId: Long? = null,
        var answerCount: Int=0,
        val page: ICPage? = null

)

