package vn.icheck.android.network.feature.product_questions

import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.network.models.v1.ICQuestionRow
import vn.icheck.android.network.models.v1.ICQuestionsAnswers

class ProductQuestionsInteractor : BaseInteractor() {

    fun getProductDtail(productId: Long, listener: ICApiListener<ICBarcodeProductV1>) {
        val host = APIConstants.defaultHost + APIConstants.PRODUCTDETAIL().replace("{id}", productId.toString())
        requestApi(ICNetworkClient.getApiClient().getProductDetal(host), listener)
    }

    fun getListProductQuestions(offset: Int, limit: Int, productId: Long, listener: ICApiListener<ICListResponse<ICQuestionRow>>) {
        val params = hashMapOf<String, Any?>()
        params["offset"] = offset
        params["limit"] = limit
        params["product_id"] = productId
        params["include"] = "answers"
        params["skip_hidden"] = true

        val host = APIConstants.defaultHost + APIConstants.PRODUCTLISTQUESTION()
        requestApi(ICNetworkClient.getApiClient().getListProductQuestions(host, params), listener)
    }

    fun getListAnswersByQuestion(offset: Int, limit: Int, questionId: Long, listener: ICApiListener<ICListResponse<ICQuestionsAnswers>>) {
        val params = hashMapOf<String, Any?>()
        params["offset"] = offset
        params["limit"] = limit

        val host = APIConstants.defaultHost + APIConstants.PRODUCTLISTANSWER().replace("{id}", questionId.toString())
        requestApi(ICNetworkClient.getApiClient().getListAnswersByQuestion(host, params), listener)
    }

    fun createQuestion(obj: ICReqProductQuestion, listener: ICApiListener<ICQuestionRow>) {
        val host = APIConstants.defaultHost + APIConstants.PRODUCTLISTQUESTION()
        requestApi(ICNetworkClient.getApiClient().createQuestions(host, obj), listener)
    }

    fun createAnswer(questionId: Long, obj: ICReqProductAnswer, listener: ICApiListener<ICQuestionsAnswers>) {
        val host = APIConstants.defaultHost + APIConstants.PRODUCTLISTANSWER().replace("{id}", questionId.toString())
        requestApi(ICNetworkClient.getApiClient().createAnswer(host, obj), listener)
    }


}