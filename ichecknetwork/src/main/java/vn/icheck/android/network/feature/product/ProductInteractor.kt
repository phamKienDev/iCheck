package vn.icheck.android.network.feature.product

import com.google.gson.JsonObject
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.product.detail.ICProductVariant
import vn.icheck.android.network.models.product.report.ICReportContribute
import vn.icheck.android.network.models.product.report.ICReportForm
import vn.icheck.android.network.models.product_detail.IckProductDetailLayoutModel
import vn.icheck.android.network.models.product_need_review.ICProductNeedReview
import vn.icheck.android.network.models.stamp_hoa_phat.ICGetIdPageSocial
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.network.models.v1.ICProductQuestions
import vn.icheck.android.network.models.v1.ICRelatedProductV1

class ProductInteractor : BaseInteractor() {

    suspend fun getProductByBarcode(barcode: String): ICBarcodeProductV1 {
        val host = APIConstants.defaultHost + APIConstants.SCAN().replace("{barcode}", barcode)
        return ICNetworkClient.getSimpleApiClient().scanBarcode(host)
    }

    suspend fun getProductCriteria(id: Long): ICCriteria {
        val host = APIConstants.defaultHost + APIConstants.CRITERIADETAIL().replace("{id}", id.toString())
        return ICNetworkClient.getSimpleApiClient().getProductCriteria(host)
    }

    suspend fun getProductQuestions(id: Long?): ICProductQuestions {
        val host = APIConstants.defaultHost + APIConstants.PRODUCTLISTQUESTION()
        return ICNetworkClient.getSimpleApiClient().getProductQuestions(host, id)
    }

    suspend fun getProductReviews(id: Long?): ICProductReviews {
        val host = APIConstants.defaultHost + APIConstants.CRITERIALISTREVIEW().replace("{id}", id.toString())
        return ICNetworkClient.getSimpleApiClient().getProductReview(host)
    }

    suspend fun getProductRelated(id: Long?): ICRelatedProductV1 {
        val host = APIConstants.defaultHost + APIConstants.PRODUCTLIST()
        return ICNetworkClient.getSimpleApiClient().getSameOwnerProduct(host, id, 0)
    }

    suspend fun getProductAnswers(id: Long): ICProductQuestions {
        val host = APIConstants.defaultHost + APIConstants.PRODUCTLISTANSWER().replace("{id}", id.toString())
        return ICNetworkClient.getSimpleApiClient().getProductQuestionAnswer(host)
    }

    fun checkBookmark(productID: Long, listener: ICNewApiListener<ICResponse<ICBookmark>>) {
        val url = APIConstants.socialHost + APIConstants.Product.CHECK_BOOKMARK.replace("{id}", productID.toString())
        requestNewApi(ICNetworkClient.getNewSocialApi().checkBookmark(url), listener)
    }

    fun addBookmark(productID: Long, listener: ICNewApiListener<ICResponseCode>) {
        val url = APIConstants.socialHost + APIConstants.Product.ADD_BOOKMARK.replace("{id}", productID.toString())
        requestNewApi(ICNetworkClient.getNewSocialApi().postRequest(url), listener)
    }

    fun deleteBookmark(productID: Long, listener: ICNewApiListener<ICResponseCode>) {
        val url = APIConstants.socialHost + APIConstants.Product.DELETE_BOOKMARK.replace("{id}", productID.toString())
        requestNewApi(ICNetworkClient.getNewSocialApi().postRequest(url), listener)
    }

    fun getReviewSummary(path: String, listener: ICNewApiListener<ICResponse<ICReviewSummary>>) {
        val url = APIConstants.socialHost + APIConstants.PATH + path
        requestNewApi(ICNetworkClient.getNewSocialApi().getReviewSummary(url), listener)
    }

    fun getContribution(path: String, listener: ICNewApiListener<ICResponse<ICProductContribution>>) {
        val url = APIConstants.socialHost + APIConstants.PATH + path
        requestNewApi(ICNetworkClient.getNewSocialApi().getContribution(url), listener)
    }

    fun getTransparency(path: String, listener: ICNewApiListener<ICResponse<ICTransparency>>) {
        val url = APIConstants.socialHost + APIConstants.PATH + path
        requestNewApi(ICNetworkClient.getNewSocialApi().getTransparency(url), listener)
    }

    fun getListPage(path: String, listener: ICNewApiListener<ICResponse<ICListResponse<ICPage>>>) {
        val url = APIConstants.socialHost + APIConstants.PATH + path
        requestNewApi(ICNetworkClient.getNewSocialApi().getListPage(url), listener)
    }

    fun getListShopVariant(path: String, listener: ICNewApiListener<ICResponse<ICListResponse<ICShopVariantV2>>>) {
        val url = APIConstants.socialHost + APIConstants.PATH + path
        val params = hashMapOf<String, Any>()
        params["lat"] = APIConstants.LATITUDE
        params["lon"] = APIConstants.LONGITUDE
        requestNewApi(ICNetworkClient.getNewSocialApi().getListShopVariant(url, params), listener)
    }

    fun getMyReview(path: String, pageId: Long?, listener: ICNewApiListener<ICResponse<ICProductMyReview>>) {
        val url = APIConstants.socialHost + APIConstants.PATH + path
        val query = hashMapOf<String, Any>()
        if (pageId != null)
            query["pageId"] = pageId
        else
            query[""] = ""

        requestNewApi(ICNetworkClient.getNewSocialApi().getMyReview(url, query), listener)
    }

    fun registerBuyProduct(productId: Long, description: String?, listener: ICNewApiListener<ICResponse<JsonObject>>) {
        val url = APIConstants.socialHost + APIConstants.Product.REGISTER_BUY_PRODUCT
        val body = hashMapOf<String, Any>()
        body["productId"] = productId
        if (!description.isNullOrEmpty()) {
            body["description"] = description
        }
        requestNewApi(ICNetworkClient.getNewSocialApi().registerBuyProduct(url, body), listener)
    }

    fun getListInformation(path: String, listener: ICNewApiListener<ICResponse<ICListResponse<ICProductInformations>>>) {
        val url = APIConstants.socialHost + APIConstants.PATH + path
        requestNewApi(ICNetworkClient.getNewSocialApi().getListProductInformation(url), listener)
    }

    fun getListReview(path: String, listener: ICNewApiListener<ICResponse<ICListResponse<ICPost>>>) {
        val url = APIConstants.socialHost + APIConstants.PATH + path
        requestNewApi(ICNetworkClient.getNewSocialApi().getListProductReview(url), listener)
    }

    fun getListQuestion(path: String, listener: ICNewApiListener<ICResponse<ICListResponse<ICProductQuestion>>>) {
        val url = APIConstants.socialHost + APIConstants.PATH + path
        requestNewApi(ICNetworkClient.getNewSocialApi().getListProductQuestion(url), listener)
    }

    fun getListTrend(path: String, listener: ICNewApiListener<ICResponse<ICListResponse<ICProductTrend>>>) {
        val url = APIConstants.socialHost + APIConstants.PATH + path
        requestNewApi(ICNetworkClient.getNewSocialApi().getListProductTrend(url), listener)
    }

    suspend fun detailProductById(id: Long): IckProductDetailLayoutModel {
        return ICNetworkClient.getNewSocialApi().detailProductByID(id)
    }

    suspend fun getReviewSummary(url: String?): ICResponse<ICReviewSummary> {
        return ICNetworkClient.getNewSocialApi().getReviewSummary2(APIConstants.PATH + url)
    }

    suspend fun getShopVariant(url: String?): ICResponse<ICProductVariant> {
        return ICNetworkClient.getNewSocialApi().getShopVariant(APIConstants.PATH + url)
    }

    suspend fun getMyReview2(url: String?): ICResponse<ICProductMyReview> {
        return ICNetworkClient.getNewSocialApi().getMyReview2(APIConstants.PATH + url)
    }

    suspend fun getReviews(url: String?): ICResponse<ICListResponse<ICPost>> {
        return ICNetworkClient.getNewSocialApi().getReview(APIConstants.PATH + url)
    }

    fun scanProduct(barcode: String, listener: ICNewApiListener<ICLayoutData<JsonObject>>) {
        val url = APIConstants.socialHost + APIConstants.Product.SCAN
        val requestBody = hashMapOf<String, Any?>()
        requestBody["barcode"] = barcode
        requestBody["layout"] = "product-detail";
        requestNewApi(ICNetworkClient.getNewSocialApi().getProductDetail(url, requestBody), listener)
    }

    fun getProductDetail(barcode: String, listener: ICNewApiListener<ICLayoutData<JsonObject>>) {
        val url = APIConstants.socialHost + APIConstants.Product.PRODUCT_DETAIL_BY_BARCODE.replace("{barcode}", barcode)
        requestNewApi(ICNetworkClient.getNewSocialApi().getProductDetail(url, "product-detail"), listener)
    }

    fun getProductDetail(id: Long, listener: ICNewApiListener<ICLayoutData<JsonObject>>) {
        val url = APIConstants.socialHost + APIConstants.Product.PRODUCT_DETAIL_BY_ID.replace("{id}", id.toString())
        requestNewApi(ICNetworkClient.getNewSocialApi().getProductDetail(url, "product-detail"), listener)
    }

    fun getProductDetailByBarcode(barcode: String, listener: ICNewApiListener<ICResponse<ICProductDetail>>) {
        val url = APIConstants.socialHost + APIConstants.Product.PRODUCT_DETAIL_BY_BARCODE.replace("{barcode}", barcode)
        requestNewApi(ICNetworkClient.getNewSocialApi().getProductDetail(url), listener)
    }

    fun getProductDetailByID(productID: Long, listener: ICNewApiListener<ICResponse<ICProductDetail>>) {
        val url = APIConstants.socialHost + APIConstants.Product.PRODUCT_DETAIL_BY_ID.replace("{id}", productID.toString())
        requestNewApi(ICNetworkClient.getNewSocialApi().getProductDetail(url), listener)
    }

    fun getListReportFormContribute(listener: ICNewApiListener<ICResponse<ICListResponse<ICReportForm>>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getListReportContribute(), listener)
    }

    fun sendReportContribute(id: Long, isVote: Boolean?, listReason: MutableList<Int>?, message: String?, listener: ICNewApiListener<ICResponse<ICReportContribute>>) {
        val body = hashMapOf<String, Any?>()

        body["isVote"] = isVote
        if (!listReason.isNullOrEmpty()) {
            body["reportIds"] = listReason
        }
        if (message != null) {
            body["reportMessage"] = message
        }
        requestNewApi(ICNetworkClient.getSocialApi().postReportFormContribute(id, body), listener)
    }

    fun postTransparency(yesOrno: Boolean, productId: Long, listener: ICNewApiListener<ICResponse<ICTransparency>>) {
        val body = hashMapOf<String, Boolean>()
        body["isVoted"] = yesOrno
        requestNewApi(ICNetworkClient.getSocialApi().postTransparencySocial(productId, body), listener)
    }

    fun sendReportContributor(id: Long, listReason: MutableList<Int>, message: String, listener: ICNewApiListener<ICResponse<ICReportContribute>>) {
        val body = hashMapOf<String, Any>()

        body["isVote"] = false
        if (!listReason.isNullOrEmpty()) {
            body["reportIds"] = listReason
        }
        if (!message.isNullOrEmpty()) {
            body["reportMessage"] = message
        }
        requestNewApi(ICNetworkClient.getSocialApi().postReportFormContributor(id, body), listener)
    }

    fun getListProduct(url: String, params: HashMap<String, Any>, listener: ICNewApiListener<ICResponse<ICListResponse<ICProductTrend>>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getListProduct(url, params), listener)
    }

    fun getExperienceCategory(path: String, listener: ICNewApiListener<ICResponse<ICListResponse<ICExperienceCategory>>>) {
        val url = APIConstants.socialHost + APIConstants.PATH + path
        requestNewApi(ICNetworkClient.getSocialApi().getExperienceCategory(url), listener)
    }

    fun getCategoryProducts(path: String): io.reactivex.Observable<ICResponse<ICListResponse<ICProduct>>> {
        val url = APIConstants.socialHost + APIConstants.PATH + path
        return ICNetworkClient.getSocialApi().getCategoryProducts(url)
    }

    fun getProductCategory(categoryID: Long, listener: ICNewApiListener<ICResponse<ICListResponse<ICProduct>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = 0
        params["limit"] = 3
        requestNewApi(ICNetworkClient.getSocialApi().getProductExperienceCategory(categoryID, params), listener)
    }

    fun getProductForYou(path: String, listener: ICNewApiListener<ICResponse<ICListResponse<ICProductForYou>>>) {
        val url = APIConstants.socialHost + APIConstants.PATH + path
        requestNewApi(ICNetworkClient.getSocialApi().getProductForYou(url), listener)
    }

    fun getProductNeedReview(path: String?, listener: ICNewApiListener<ICResponse<ICListResponse<ICProductNeedReview>>>) {
        val url = APIConstants.socialHost + APIConstants.PATH + path

        val params = hashMapOf<String, Any>()
        params["offset"] = 0
        params["limit"] = 5
        requestNewApi(ICNetworkClient.getSocialApi().getProductNeedReview(url, params), listener)
    }

    fun getProductsTrend(listener: ICNewApiListener<ICResponse<ICListResponse<ICProductTrend>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = 0
        params["limit"] = 5
        requestNewApi(ICNetworkClient.getNewSocialApi().getWidgetTrendProducts(params), listener)
    }

    fun getPagesTrend(listener: ICNewApiListener<ICResponse<ICListResponse<ICPageTrend>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = 0
        params["limit"] = 5
        requestNewApi(ICNetworkClient.getNewSocialApi().getWidgetTrendPages(params), listener)
    }

    fun getExpertsTrend(listener: ICNewApiListener<ICResponse<ICListResponse<ICPageTrend>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = 0
        params["limit"] = 5
        requestNewApi(ICNetworkClient.getNewSocialApi().getWidgetExpertsTrend(params), listener)
    }

    fun getFlashSale(path: String, listner: ICNewApiListener<ICResponse<ICFlashSale>>) {
        val url = APIConstants.socialHost + APIConstants.PATH + path
        requestNewApi(ICNetworkClient.getSocialApi().getFlashSale(url), listner)
    }

    fun getContributor(barcode: String, offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICContribute>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT
        requestNewApi(ICNetworkClient.getSocialApi().getListContributor(barcode, params), listener)
    }

    fun getInformationProduct(code: String, idInformation: Long, listener: ICNewApiListener<ICResponse<ICInformationProduct>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getInformationProduct(idInformation, code), listener)
    }

    fun getListReportProductForm(listener: ICNewApiListener<ICResponse<ICListResponse<ICReportForm>>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getReportProductFrom(), listener)
    }

    fun reportProduct(productID: Long, listReason: MutableList<Int>, inputReason: String?, listener: ICNewApiListener<ICResponse<ICListResponse<ICReportForm>>>) {
        val body = hashMapOf<String, Any>()
        body["productId"] = productID
        if (!listReason.isNullOrEmpty()) {
            body["reportElementIdList"] = listReason
        }
        if (!inputReason.isNullOrEmpty())
            body["description"] = inputReason

        requestNewApi(ICNetworkClient.getSocialApi().postReportForm(body), listener)
    }

    fun getListProductQuestion(productID: Long, offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICProductQuestion>>>) {
        val queries = hashMapOf<String, Any>()
        queries["offset"] = offset
        queries["limit"] = APIConstants.LIMIT
        requestNewApi(ICNetworkClient.getNewSocialApi().getListProductQuestions(productID, queries), listener)
    }

    fun getListProductAnswer(questionID: Long, notIds: String, offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICProductQuestion>>>) {
        val queries = hashMapOf<String, Any>()
        if (notIds.isNotEmpty()) {
            queries["notIds"] = notIds
        }
        queries["offset"] = offset
        queries["limit"] = APIConstants.LIMIT
        requestNewApi(ICNetworkClient.getNewSocialApi().getListProductAnswer(questionID, queries), listener)
    }

    fun deleteQuestion(questionID: Long, listener: ICNewApiListener<ICResponseCode>) {
        val url = APIConstants.socialHost + APIConstants.Social.QUESTION_DETAIL.replace("{id}", questionID.toString())
        requestNewApi(ICNetworkClient.getNewSocialApi().deleteComment(url, hashMapOf()), listener)
    }

    fun postQuestion(pageId: Long?, productId: Long, content: String, image: String?, listener: ICNewApiListener<ICResponse<ICProductQuestion>>) {
        val url = APIConstants.socialHost + APIConstants.Social.POST_PRODUCT_QUESTION.replace("{id}", productId.toString())

        val body = hashMapOf<String, Any>()
        if (pageId != null)
            body["pageId"] = pageId
        if (content.isNotEmpty()) {
            body["content"] = content
        }
        if (!image.isNullOrEmpty()) {
            val imgBody = JsonObject()
            imgBody.addProperty("content", image)
            imgBody.addProperty("type", if (image.contains(".mp4")) {
                "video"
            } else {
                "image"
            })
            body["media"] = listOf(imgBody)
        }

        requestNewApi(ICNetworkClient.getNewSocialApi().postProductQuestion(url, body), listener)
    }

    fun postAnswer(pageId: Long?, questionID: Long, image: String?, content: String, listener: ICNewApiListener<ICResponse<ICProductQuestion>>) {
        val url = APIConstants.socialHost + APIConstants.Product.COMMENT_QUESTION.replace("{id}", questionID.toString())

        val body = hashMapOf<String, Any>()
        if (pageId != null)
            body["pageId"] = pageId
        if (content.isNotEmpty()) {
            body["content"] = content
        }
        if (!image.isNullOrEmpty()) {
            val imgBody = JsonObject()
            imgBody.addProperty("content", image)
            imgBody.addProperty("type", if (image.contains(".mp4")) {
                "video"
            } else {
                "image"
            })
            body["media"] = listOf(imgBody)
        }

        requestNewApi(ICNetworkClient.getNewSocialApi().postProductAnswer(url, body), listener)
    }

    fun postLikeQuestion(questionId: Long, listener: ICNewApiListener<ICResponse<ICNotification>>) {
        val url = APIConstants.socialHost + APIConstants.Product.POST_LIKE_QUESTION.replace("{id}", questionId.toString())
        val body = hashMapOf<String, String>()
        body["type"] = "like"
        requestNewApi(ICNetworkClient.getSocialApi().postLikeQuestion(url, body), listener)
    }

    fun getBookMark(offset: Int, search: String?, listener: ICNewApiListener<ICResponse<ICListResponse<ICProductTrend>>>) {
        val params = hashMapOf<String, Any>()
        params["limit"] = APIConstants.LIMIT
        params["offset"] = offset
        if (!search.isNullOrEmpty()) {
            params["search"] = search
        }
        requestNewApi(ICNetworkClient.getSocialApi().getListProductBookmark("2", params), listener)
    }

    fun postVoteContributor(id: Long, isVote: Boolean?, listener: ICNewApiListener<ICResponse<ICContribute>>) {
        val query = hashMapOf<String, Any?>()
        query["isVote"] = isVote
        requestNewApi(ICNetworkClient.getSocialApi().postVoteContributor(id, query), listener)
    }

    fun getProductShareLink(productID: Long, listener: ICNewApiListener<ICResponse<String>>) {
        val url = APIConstants.socialHost + APIConstants.Social.PRODUCT_SHARE_LINK.replace("{id}", productID.toString())
        requestNewApi(ICNetworkClient.getNewSocialApi().getProductShareLink(url), listener)
    }

    fun checkScanQrCode(code: String, listener: ICNewApiListener<ICResponse<ICValidStampSocial>>) {
        val body = hashMapOf<String, Any>()
        body["code"] = code

        requestNewApi2(ICNetworkClient.getStampClientSocial().checkScanQrCode(body), listener)
    }

    fun addToCartStamp(name: String?, id: Long?, image: String?, price: Long, listener: ICNewApiListener<ICResponse<Int>>) {
        val product = hashMapOf<String, Any>()
        if (!name.isNullOrEmpty()) {
            product["name"] = name
        }
        product["id"] = id!!
        if (!image.isNullOrEmpty()) {
            product["imageUrl"] = image
        }

        val params = hashMapOf<String, Any>()
        params["product"] = product
        params["price"] = price
        params["originPrice"] = price
        params["quantity"] = 1
        requestNewApi(ICNetworkClient.getSocialApi().addToCart(params), listener)
    }

    fun getIdPageSocial(id: Long, listener: ICNewApiListener<ICResponse<ICListResponse<ICGetIdPageSocial>>>) {
        val list = mutableListOf<Any>()
        list.add(id)

        val body = hashMapOf<String, Any>()
        body["referenceIds"] = list
        requestNewApi(ICNetworkClient.getSocialApi().getIdPageSocial(body), listener)
    }

    suspend fun getProductsECommerce(barcode: String): ICResponse<ICListResponse<ICProductECommerce>> {
        val url = APIConstants.socialHost + APIConstants.productsECommerce().replace("{barcode}", barcode)
        return ICNetworkClient.getNewSocialApi().getProductsECommerce(url)
    }
}