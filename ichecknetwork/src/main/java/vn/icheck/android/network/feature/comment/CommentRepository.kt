package vn.icheck.android.network.feature.comment

import com.google.gson.JsonObject
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICCommentPost
import vn.icheck.android.network.models.ICMedia

class CommentRepository : BaseInteractor() {

    fun getListReplies(commentID: Long, notIds: String, offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICCommentPost>>>) {
        val queries = hashMapOf<String, Any>()
        queries["offset"] = offset
        queries["limit"] = APIConstants.LIMIT
        if (notIds.isNotEmpty()) {
            queries["notIds"] = notIds
        }
        requestNewApi(ICNetworkClient.getSocialApi().getListRepliesOfComment(commentID, queries), listener)
    }

    fun replComment(commentID: Long, pageID: Long?, content: String, image: String?, listener: ICNewApiListener<ICResponse<ICCommentPost>>) {
        val url = APIConstants.socialHost + APIConstants.Product.POST_COMMENT_REPLY.replace("{id}", commentID.toString())

        val body = hashMapOf<String, Any>()
        if (pageID != null) {
            body["pageId"] = pageID
        }
        if (content.isNotEmpty()) {
            body["content"] = content.trim()
        }
        if (!image.isNullOrEmpty()) {
            body["media"] = listOf(ICMedia(image, "image"))
        }

        requestNewApi(ICNetworkClient.getSocialApi().postCommentReply(url, body), listener)
    }

    fun updateQuestion(commentID: Long, content: String, image: String?, listener: ICNewApiListener<ICResponseCode>) {
        val url = APIConstants.socialHost + APIConstants.Social.QUESTION_DETAIL.replace("{id}", commentID.toString())

        val body = hashMapOf<String, Any>()
        if (content.isNotEmpty()) {
            body["content"] = content
        }
        if (!image.isNullOrEmpty()) {
            val imgBody = JsonObject()
            imgBody.addProperty("content", image)
            imgBody.addProperty("type", "image")

            body["media"] = listOf(imgBody)
        }

        requestNewApi(ICNetworkClient.getNewSocialApi().updateComment(url, body), listener)
    }

    fun updateComment(commentID: Long, content: String, image: String?, listener: ICNewApiListener<ICResponseCode>) {
        val url = APIConstants.socialHost + APIConstants.Social.COMMENT_DETAIL.replace("{id}", commentID.toString())

        val body = hashMapOf<String, Any>()
        if (content.isNotEmpty()) {
            body["content"] = content
        }
        if (!image.isNullOrEmpty()) {
            val imgBody = JsonObject()
            imgBody.addProperty("content", image)
            imgBody.addProperty("type", "image")

            body["media"] = listOf(imgBody)
        }

        requestNewApi(ICNetworkClient.getNewSocialApi().updateComment(url, body), listener)
    }

    fun updateCommentPost(commentID: Long, content: String, image: String?, listener: ICNewApiListener<ICResponseCode>) {
        val url = APIConstants.socialHost + APIConstants.Social.COMMENT_DETAIL.replace("{id}", commentID.toString())

        val body = hashMapOf<String, Any>()
        if (content.isNotEmpty()) {
            body["content"] = content
        }
        if (!image.isNullOrEmpty()) {
            body["media"] = image
        }

        requestNewApi(ICNetworkClient.getNewSocialApi().updateComment(url, body), listener)
    }

    fun deleteComment(commentID: Long, listener: ICNewApiListener<ICResponseCode>) {
        val url = APIConstants.socialHost + APIConstants.Social.COMMENT_DETAIL.replace("{id}", commentID.toString())
        requestNewApi(ICNetworkClient.getNewSocialApi().deleteComment(url, hashMapOf()), listener)
    }

    fun likeQuestion(questionID: Long, listener: ICNewApiListener<ICResponseCode>) {
        val url = APIConstants.socialHost + APIConstants.Social.LIKE_QUESTION.replace("{id}", questionID.toString())
        val body = hashMapOf<String, Any>()
        body["type"] = "like"
        requestNewApi(ICNetworkClient.getNewSocialApi().likeComment(url, body), listener)
    }

    fun likeAnswer(questionID: Long, listener: ICNewApiListener<ICResponseCode>) {
        val url = APIConstants.socialHost + APIConstants.Social.LIKE_ANSWER.replace("{id}", questionID.toString())
        val body = hashMapOf<String, Any>()
        body["type"] = "like"
        requestNewApi(ICNetworkClient.getNewSocialApi().likeComment(url, body), listener)
    }
}