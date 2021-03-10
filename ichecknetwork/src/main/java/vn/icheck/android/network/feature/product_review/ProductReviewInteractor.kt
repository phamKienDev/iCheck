package vn.icheck.android.network.feature.product_review

import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.v1.ICBarcodeProductV1

class ProductReviewInteractor : BaseInteractor() {

    fun getProduct(barcode: String, listener: ICApiListener<ICBarcodeProductV1>) {
        requestApi(ICNetworkClient.getApiClient().scanBarcode1(barcode), listener)
    }

    fun getProductCriteria(id: Long, listener: ICApiListener<ICCriteria>) {
        requestApi(ICNetworkClient.getApiClient().getProductCriteria1(id), listener)
    }

    fun getProductCriteriaReviews(offset: Int, limit: Int, id: Long, listener: ICApiListener<ICProductReviews>) {
        requestApi(ICNetworkClient.getApiClient().getProductCriteriaReviews1(id, limit, offset), listener)
    }

    fun postProductReview(id: Long, msgReview: String, listImage: MutableList<String>, listCriteria: MutableList<HashMap<String, Any>>, listener: ICApiListener<ICProductReviews.ReviewsRow>) {
        val queries = hashMapOf<String, Any?>()
        queries["product_id"] = id
        queries["message"] = msgReview
        queries["criteria"] = listCriteria
        queries["images"] = listImage
        requestApi(ICNetworkClient.getApiClient().postProductReview1(queries), listener)
    }

    fun getCommentReview(offset: Int, limit: Int, reviewId: Long, listener: ICApiListener<ICListResponse<ICProductReviews.Comments>>) {
        requestApi(ICNetworkClient.getApiClient().getComments1(reviewId, limit, offset), listener)
    }

    fun postComment(msg: String, listImage: MutableList<String>, reviewId: Long, listener: ICApiListener<ICProductReviews.Comments>) {
        val queries = hashMapOf<String, Any?>()
        queries["message"] = msg
        queries["images"] = listImage
        requestApi(ICNetworkClient.getApiClient().postCommentReview1(reviewId, queries), listener)
    }

    fun getListReviewProduct(productId: Long, limit: Int, offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICPost>>>) {
        val queries = hashMapOf<String, Any>()
        queries["offset"] = limit
        queries["limit"] = offset

        requestNewApi(ICNetworkClient.getSocialApi().getListReviewProduct(productId, queries), listener)
    }

    fun getListReviewNotMe(productId: Long, limit: Int, offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICPost>>>) {
        val queries = hashMapOf<String, Any>()
        queries["offset"] = limit
        queries["limit"] = offset

        requestNewApi(ICNetworkClient.getSocialApi().getListReviewNotMe(productId, queries), listener)
    }

    fun postLikeReview(reviewId: Long, pageId: Long?, listener: ICNewApiListener<ICResponse<ICNotification>>) {
        val body = hashMapOf<String, Any>()
        body["type"] = "like"
        if (pageId != null) {
            body["pageId"] = pageId
        }
        requestNewApi(ICNetworkClient.getSocialApi().postLikeReview(reviewId, body), listener)
    }

    fun postReview(productID: Long, content: String?, criteria: MutableList<ICReqCriteriaReview>, media: MutableList<ICMedia>?, pageId: Long?, listener: ICNewApiListener<ICResponse<ICPost>>) {
        val body = hashMapOf<String, Any?>()
        body["content"] = content
        if (pageId != null) {
            body["pageId"] = pageId
        }
        if (!media.isNullOrEmpty()) {
            body["media"] = media
        }
        body["criteria"] = criteria
        requestNewApi(ICNetworkClient.getSocialApi().postProductReview(productID, body), listener)
    }

    fun getReviewSummary(productId: Long, listener: ICNewApiListener<ICResponse<ICReviewSummary>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getReviewSummaryProduct(productId), listener)
    }

    fun getMyReview(productId: Long, pageId: Long?, listener: ICNewApiListener<ICResponse<ICProductMyReview>>) {
        val query = hashMapOf<String, Any>()
        if (pageId != null)
            query["pageId"] = pageId
        else
            query[""] = ""

        requestNewApi(ICNetworkClient.getSocialApi().getMyReviewProduct(productId,query), listener)
    }

    fun getDetailReview(reviewId: Long, listener: ICNewApiListener<ICResponse<ICPost>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getDetailReview(reviewId), listener)
    }

    fun getCriteria(productId: Long, listener: ICNewApiListener<ICListResponse<ICCriteriaReview>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getCriteriaProduct(productId), listener)
    }

    fun getLinkOfProduct(productID: Long, listener: ICNewApiListener<ICResponse<String>>) {
        val url = APIConstants.socialHost + APIConstants.Social.PRODUCT_SHARE_LINK.replace("{id}", productID.toString())
        requestNewApi(ICNetworkClient.getNewSocialApi().getProductShareLink(url), listener)
    }

    fun getListComment(reviewId: Long, limit: Int, offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICCommentPost>>>) {
        val queries = hashMapOf<String, Any>()
        queries["offset"] = offset
        queries["limit"] = limit

        requestNewApi(ICNetworkClient.getSocialApi().getListCommentReview(reviewId, queries), listener)
    }

    fun getListChildComment(commentID: Long, limit: Int, offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICCommentPost>>>) {
        val queries = hashMapOf<String, Any>()
        queries["offset"] = offset
        queries["limit"] = limit

        requestNewApi(ICNetworkClient.getSocialApi().getListRepliesOfComment(commentID, queries), listener)
    }

    fun postCommentReview(reviewId: Long, message: String, pageId: Long?, media: ICMedia?, listener: ICNewApiListener<ICResponse<ICCommentPost>>) {
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

        requestNewApi(ICNetworkClient.getSocialApi().postCommentReviewSocail(reviewId, queries), listener)
    }

    fun postCommentReply(commentId: Long, message: String, pageId: Long?, media: ICMedia?, listener: ICNewApiListener<ICResponse<ICCommentPost>>) {
        val url = APIConstants.socialHost + APIConstants.Product.POST_COMMENT_REPLY.replace("{id}", commentId.toString())

        val queries = hashMapOf<String, Any>()
        if (pageId != null) {
            queries["pageId"] = pageId
        }
        queries["content"] = message.trim()
        if (media != null) {
            queries["media"] = listOf(ICMedia(media.content, "image"))
        }

        requestNewApi(ICNetworkClient.getSocialApi().postCommentReply(url, queries), listener)
    }

    fun turnOffNotification(postId: Long, type: String, listener: ICNewApiListener<ICResponse<Boolean>>) {
        val queries = hashMapOf<String, Any>()
        queries["entityId"] = postId
        queries["entityType"] = type

        requestNewApi(ICNetworkClient.getSocialApi().postTurnOffNotify(queries), listener)
    }

    fun deleteComment(id: Long, listener: ICNewApiListener<ICResponse<ICCommentPost>>) {
        val url = APIConstants.socialHost + APIConstants.Product.DELETE_COMMENT.replace("{id}", id.toString())

        val body = hashMapOf<String, Any>()
        body[""] = ""
        requestNewApi(ICNetworkClient.getSocialApi().deleteComment(url, body), listener)
    }

    fun getPrivacy(postId: Long, listener: ICNewApiListener<ICResponse<ICListResponse<ICPrivacy>>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getPrivacyPost(postId), listener)
    }

    fun postPrivacy(postId: Long, privacySelected: Long, listener: ICNewApiListener<ICResponse<Boolean>>) {
        val queries = hashMapOf<String, Any>()
        queries["privacyElementId"] = privacySelected

        requestNewApi(ICNetworkClient.getSocialApi().postPrivacyPost(postId, queries), listener)
    }
}