package vn.icheck.android.activities.product.review_product_v1.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.screen.user.review_product.model.ICReviewProduct
import java.io.File

interface IReviewProductView : BaseActivityView {
    fun onLoadmore()
    fun onClickTryAgain()
    fun onResetData()
    fun onSetInfoProduct(product: ICBarcodeProductV1)
    fun onYourReview(criteria: ICReviewProduct)
    fun onGetProductCriteriaReviewsSuccess(reviews: MutableList<ICReviewProduct>)
    fun onPostProductReview(msg: String, listCriteria: MutableList<HashMap<String, Any>>)
    fun onShareYourReview(review: ICProductReviews.ReviewsRow)
    fun onClickEditReview(criteria: ICCriteria)
    fun onGetDataError(icon: Int, error: String)
    fun pickPhotoCriteria()
    fun deletePhotoCriteria(pos: Int)
    fun onGetListComments(reviewPosition: Int, positionShowCommemt: Int, reviewId: Long, dataSize: Int)
    fun onGetListCommentsSuccess(reviewPosition: Int, positionShowComment: Int, list: MutableList<ICProductReviews.Comments>)
    fun onClickCreateComment(reviewPosition: Int, nameOwner: String, reviewId: Long)
    fun onPostCommentSuccess(reviewPosition: Int, comment: ICProductReviews.Comments)
    fun onPostCommentError()
    fun onVoteReviews(id: Long, vote: String)
    fun onShowDetailUser(id: Long?, type: String?)
    fun onProductReviewStartInsider()
}