package vn.icheck.android.network.feature.post

import vn.icheck.android.network.base.*
import com.google.gson.JsonObject
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICCommentPost
import vn.icheck.android.network.models.ICMedia
import vn.icheck.android.network.models.ICNotification
import vn.icheck.android.network.models.ICPost
import vn.icheck.android.network.models.ICPrivacy
import vn.icheck.android.network.models.*

class PostInteractor : BaseInteractor() {

    fun getPostDetail(id: Long, listener: ICNewApiListener<ICResponse<ICPost>>) {
        val url = APIConstants.socialHost + APIConstants.Post.GET_POST_DETAIL.replace("{id}", id.toString())
        requestNewApi(ICNetworkClient.getNewSocialApi().getPostDetail(url), listener)
    }

    suspend fun getPostDetailV2(id: Long,listener: ICNewApiListener<ICResponse<ICPost>>){

    }

    fun getPostPrivacy(postID: Long?, listener: ICNewApiListener<ICResponse<ICListResponse<ICPrivacy>>>) {
        val url = APIConstants.socialHost + APIConstants.Social.GET_POST_PRIVACY

        val params = hashMapOf<String, Long>()
        if (postID != null) {
            params["postId"] = postID
        }

        requestNewApi(ICNetworkClient.getNewSocialApi().getPostPrivacy(url, params), listener)
    }

    fun createPost(pageID: Long?, privacy: ICPrivacy, content: String, productID: Long?, listImage: MutableList<String>, listener: ICNewApiListener<ICResponse<ICPost>>) {
        val url = APIConstants.socialHost + APIConstants.Social.CREATE_POST

        val body = hashMapOf<String, Any>()
        if (pageID != null && pageID > 0L) {
            body["pageId"] = pageID
        }

        if (content.isNotEmpty()) {
            body["content"] = content
        }

        if (listImage.isNotEmpty()) {
            val list = mutableListOf<JsonObject>()

            for (item in listImage) {
                list.add(JsonObject().apply {
                    addProperty("content", item)
                    if (item.contains(".mp4")) {
                        addProperty("type", "video")
                    } else {
                        addProperty("type", "image")
                    }
                })
            }

            body["media"] = list
        }

        if (productID != null) {
            body["targetType"] = "product"
            body["targetId"] = productID
        }

        body["privacyElementId"] = privacy.privacyElementId

        requestNewApi(ICNetworkClient.getNewSocialApi().createPost(url, body), listener)
    }

    fun updatePost(postID: Long, privacy: ICPrivacy, content: String, productID: Long?, listImage: MutableList<String>, listener: ICNewApiListener<ICResponse<ICPost>>) {
        val url = APIConstants.socialHost + APIConstants.Social.UPDATE_POST.replace("{id}", postID.toString())

        val body = ICReqUpdatePost()
        if (content.isNotEmpty()) {
            body.content = content
        }
        if (listImage.isNotEmpty()) {
            val list = mutableListOf<ICMedia>()
            for (item in listImage) {
                if (item.contains(".mp4")) {
                    list.add(ICMedia(item, type = "video"))
                } else {
                    list.add(ICMedia(item, type = "image"))
                }
            }
            body.media = list
        }
        if (productID != null) {
            body.targetType = "product"
            body.targetId = productID
        }
        body.privacyElementId = privacy.privacyElementId

        requestNewApi(ICNetworkClient.getNewSocialApi().updatePost(url, body), listener)
    }

    fun getListCommentsOfPost(postId: Long, offset: Int, limit: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICCommentPost>>>) {
        val queries = hashMapOf<String, Any>()
        queries["offset"] = offset
        queries["limit"] = limit
        requestNewApi(ICNetworkClient.getSocialApi().getListCommentsOfPost(postId, queries), listener)
    }

    fun postLikeComment(commentId: Long, pageId: Long?, listener: ICNewApiListener<ICResponse<ICNotification>>) {
        val body = hashMapOf<String, Any>()
        body["type"] = "like"
        if (pageId != null) {
            body["pageId"] = pageId
        }
        requestNewApi(ICNetworkClient.getSocialApi().postLikeComment(commentId, body), listener)
    }

    fun deleteCommentOfPost(questionID: Long, listener: ICNewApiListener<ICResponseCode>) {
        val url = APIConstants.socialHost + APIConstants.Social.QUESTION_DETAIL.replace("{id}", questionID.toString())
        requestNewApi(ICNetworkClient.getNewSocialApi().deleteComment(url, hashMapOf()), listener)
    }

    fun commentPost(postID: Long, message: String, pageId: Long?, media: ICMedia?, listener: ICNewApiListener<ICResponse<ICCommentPost>>) {
        val url = APIConstants.socialHost + APIConstants.Post.POST_COMMENT.replace("{id}", postID.toString())

        val queries = hashMapOf<String, Any>()
        if (pageId != null) {
            queries["pageId"] = pageId
        }
        queries["content"] = message.trim()
        if (media != null) {
            val listMedia = mutableListOf<ICMedia>()
            listMedia.add(media)
            queries["media"] = listMedia
        }

        requestNewApi(ICNetworkClient.getSocialApi().postCommentPost(url, queries), listener)
    }

    fun commentPost(postID: Long, pageId: Long?, content: String, image: String?, listener: ICNewApiListener<ICResponse<ICCommentPost>>) {
        val url = APIConstants.socialHost + APIConstants.Post.POST_COMMENT.replace("{id}", postID.toString())

        val queries = hashMapOf<String, Any>()
        if (pageId != null) {
            queries["pageId"] = pageId
        }
        if (content.isNotEmpty()) {
            queries["content"] = content.trim()
        }
        if (!image.isNullOrEmpty()) {
            queries["media"] = listOf(ICMedia(image, type = if (image.contains(".mp4")) {
                "video"
            } else {
                "image"
            }))
        }

        requestNewApi(ICNetworkClient.getSocialApi().postCommentPost(url, queries), listener)
    }


    fun likeOrDislikePost(id: Long, pageId: Long?, listener: ICNewApiListener<ICResponse<ICPost>>) {
        val body = hashMapOf<String, Any>()
        if (pageId != null) {
            body["pageId"] = pageId
        }
        body["type"] = "like"
        requestNewApi(ICNetworkClient.getSocialApi().likePostOfPage(id, body), listener)
    }

    fun deletePost(id: Long, listener: ICNewApiListener<ICResponse<ICCommentPost>>) {
        val url = APIConstants.socialHost + APIConstants.Product.DELETE_POST.replace("{id}", id.toString())

        val body = hashMapOf<String, Any>()
        body[""] = ""
        requestNewApi(ICNetworkClient.getSocialApi().deletePost(url, body), listener)
    }

    fun getShareLinkOfPost(id: Long, listener: ICNewApiListener<ICResponse<String>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getShareLinkOfPost(id), listener)
    }

    fun postShareLinkOfPost(id: Long, listener: ICNewApiListener<ICResponse<String>>) {
        requestNewApi(ICNetworkClient.getSocialApi().postShareLinkOfPost(id, hashMapOf()), listener)
    }
}