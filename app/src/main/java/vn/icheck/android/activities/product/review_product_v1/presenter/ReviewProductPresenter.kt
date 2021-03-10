package vn.icheck.android.activities.product.review_product_v1.presenter

import android.content.Intent
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.feature.review_product_v1.ReviewProductInteractor
import vn.icheck.android.network.models.ICCriteria
import vn.icheck.android.network.models.ICProductReviews
import vn.icheck.android.network.models.upload.UploadResponse
import vn.icheck.android.screen.user.review_product.model.ICReviewProduct
import vn.icheck.android.activities.product.review_product_v1.view.IReviewProductView
import vn.icheck.android.helper.ImageHelperV1
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import java.io.File
import java.lang.Exception

class ReviewProductPresenter(val view: IReviewProductView) : BaseActivityPresenter(view) {
    val reviewProductInteractor = ReviewProductInteractor()
    var offset = 0

    private val listImageCriteria = mutableListOf<String>()
    private var barcode: String? = null
    private var productID: Long = -1L

    private var setHeaderReview = true

    private val listImageComment = mutableListOf<String>()

    fun getBarcodeProduct(intent: Intent) {
        barcode = try {
            intent.getStringExtra("barcode")
        } catch (e: Exception) {
            null
        }

        productID = try {
            intent.getLongExtra("productId", -1L)
        } catch (e: Exception) {
            -1L
        }

        if (barcode.isNullOrEmpty() || productID == -1L) {
            view.onGetDataError(R.drawable.ic_error_request, getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            return
        }

        checkNetwork()


    }

    fun checkNetwork() {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDataError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        view.onResetData()
        offset = 0
        setHeaderReview = true
        getProduct()
        getProductCriteria()
        getProductReviews()
    }

    private fun getProduct() {
        reviewProductInteractor.getProduct(barcode!!, object : ICApiListener<ICBarcodeProductV1> {
            override fun onSuccess(obj: ICBarcodeProductV1) {
                view.onSetInfoProduct(obj)
            }

            override fun onError(error: ICBaseResponse?) {

            }
        })
    }

    fun getProductCriteria() {
        reviewProductInteractor.getProductCriteria(productID, object : ICApiListener<ICCriteria> {
            override fun onSuccess(obj: ICCriteria) {
                val criteria = ICReviewProduct()
                criteria.criteria = obj

                if (obj.customerEvaluation == null) {
                    criteria.type = Constant.TYPE_POST_YOUR_REVIEW
                } else {
                    criteria.type = Constant.TYPE_YOUR_REVIEW
                }
                view.onYourReview(criteria)

            }

            override fun onError(error: ICBaseResponse?) {
                view.onGetDataError(R.drawable.ic_error_request, getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    fun getProductReviews() {
        reviewProductInteractor.getProductCriteriaReviews(offset, APIConstants.LIMIT, productID, object : ICApiListener<ICProductReviews> {
            override fun onSuccess(obj: ICProductReviews) {
                offset += APIConstants.LIMIT
                val listReviews = mutableListOf<ICReviewProduct>()
                if (setHeaderReview) {
                    val header = ICReviewProduct()
                    header.type = Constant.TYPE_HEADER_REVIEW
                    listReviews.add(header)
                    setHeaderReview = false
                }
                for (item in obj.rows) {
                    val review = ICReviewProduct()
                    review.type = Constant.TYPE_ALL_REVIEW
                    review.reviews = item
                    listReviews.add(review)
                }
                view.onGetProductCriteriaReviewsSuccess(listReviews)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onGetDataError(R.drawable.ic_error_request, getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }

        })
    }

    fun uploadImageReview(msg: String, files: MutableList<File>, listCriteria: MutableList<HashMap<String, Any>>) {
        listImageCriteria.clear()
        for (i in 0 until files.size) {
            ImageHelperV1.uploadImage(view.mContext, files[i], object : ICApiListener<UploadResponse> {
                override fun onSuccess(obj: UploadResponse) {
                    listImageCriteria.add(obj.fileId)
                    if (i == files.size - 1) {
                        postProductReview(msg, listCriteria)
                    }
                }

                override fun onError(error: ICBaseResponse?) {
                    view.onGetDataError(R.drawable.ic_error_request, getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            })
        }
    }

    fun postProductReview(msg: String, listCriteria: MutableList<HashMap<String, Any>>) {
        reviewProductInteractor.postProductReview(productID, msg, listImageCriteria, listCriteria, object : ICApiListener<ICProductReviews.ReviewsRow> {
            override fun onSuccess(obj: ICProductReviews.ReviewsRow) {
                view.onShareYourReview(obj)
                checkNetwork()
            }

            override fun onError(error: ICBaseResponse?) {
                view.onGetDataError(R.drawable.ic_error_request, getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }


    fun getListComment(reviewId: Long, offset: Int, reviewPos: Int, positionShowComment: Int) {
        reviewProductInteractor.getCommentReview(offset, APIConstants.LIMIT, reviewId, object : ICApiListener<ICListResponse<ICProductReviews.Comments>> {
            override fun onSuccess(obj: ICListResponse<ICProductReviews.Comments>) {
                view.onGetListCommentsSuccess(reviewPos, positionShowComment, obj.rows)
            }

            override fun onError(error: ICBaseResponse?) {
            }

        })
    }

    fun uploadImageComment(msg: String, files: MutableList<File>, reviewId: Long, reviewPos: Int) {
        listImageComment.clear()
        for (i in 0 until files.size) {
            ImageHelperV1.uploadImage(view.mContext, files[i], object : ICApiListener<UploadResponse> {
                override fun onSuccess(obj: UploadResponse) {
                    listImageComment.add(obj.fileId)
                    if (i == files.size - 1) {
                        postComment(msg, reviewId, reviewPos)
                    }
                }

                override fun onError(error: ICBaseResponse?) {
                    if (i == files.size - 1) {
                        if (listImageComment.isEmpty()) {
                            view.onPostCommentError()
                            view.onGetDataError(R.drawable.ic_error_request, getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                        } else {
                            postComment(msg, reviewId, reviewPos)
                        }
                    }
                }
            })
        }

    }

    fun postComment(msg: String, reviewId: Long, reviewPos: Int) {
        reviewProductInteractor.postComment(msg, listImageComment, reviewId, object : ICApiListener<ICProductReviews.Comments> {
            override fun onSuccess(obj: ICProductReviews.Comments) {
                view.onPostCommentSuccess(reviewPos, obj)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onPostCommentError()
            }
        })
    }

    fun destroy() {
        reviewProductInteractor.dispose()
    }

}