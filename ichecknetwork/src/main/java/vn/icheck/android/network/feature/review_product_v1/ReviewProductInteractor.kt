package vn.icheck.android.network.feature.review_product_v1

import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICCriteria
import vn.icheck.android.network.models.ICProductReviews
import vn.icheck.android.network.models.v1.ICBarcodeProductV1

class ReviewProductInteractor : BaseInteractor() {

    fun getProduct(barcode: String, listener: ICApiListener<ICBarcodeProductV1>) {
        val host = APIConstants.defaultHost + APIConstants.SCAN().replace("{barcode}", barcode)
        requestApi(ICNetworkClient.getApiClient().scanBarcode1(host), listener)
    }

    fun getProductCriteria(id: Long, listener: ICApiListener<ICCriteria>) {
        val host = APIConstants.defaultHost + APIConstants.CRITERIADETAIL().replace("{id}", id.toString())
        requestApi(ICNetworkClient.getApiClient().getProductCriteria1(host), listener)
    }

    fun getProductCriteriaReviews(offset: Int, limit: Int, id: Long, listener: ICApiListener<ICProductReviews>) {
        val host = APIConstants.defaultHost + APIConstants.CRITERIALISTREVIEW().replace("{id}", id.toString())
        requestApi(ICNetworkClient.getApiClient().getProductCriteriaReviews1(host, limit, offset), listener)
    }

    fun postProductReview(id: Long, msgReview: String, listImage: MutableList<String>?, listCriteria: MutableList<HashMap<String, Any>>, listener: ICApiListener<ICProductReviews.ReviewsRow>) {
        val queries = hashMapOf<String, Any?>()
        queries["product_id"] = id
        queries["message"] = msgReview
        queries["criteria"] = listCriteria
        queries["images"] = listImage
        val host = APIConstants.defaultHost + APIConstants.CRITERIAREVIEWPRODUCT()
        requestApi(ICNetworkClient.getApiClient().postProductReview1(host, queries), listener)
    }

    fun getCommentReview(offset: Int, limit: Int, reviewId: Long, listener: ICApiListener<ICListResponse<ICProductReviews.Comments>>) {
        val host = APIConstants.defaultHost + APIConstants.CRITERIALISTCOMMENT().replace("{review_id}", reviewId.toString())
        requestApi(ICNetworkClient.getApiClient().getComments1(host, limit, offset), listener)
    }

    fun postComment(msg: String, listImage: MutableList<String>, reviewId: Long, listener: ICApiListener<ICProductReviews.Comments>) {
        val queries = hashMapOf<String, Any?>()
        queries["message"] = msg
        queries["images"] = listImage

        val host = APIConstants.defaultHost + APIConstants.CRITERIALISTPRODUCTCOMMENT().replace("{id}", reviewId.toString())
        requestApi(ICNetworkClient.getApiClient().postCommentReview1(host, queries), listener)
    }
}
