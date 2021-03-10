package vn.icheck.android.activities.product.product_questions_v1.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.network.models.v1.ICQuestionRow
import vn.icheck.android.network.models.v1.ICQuestionsAnswers

interface IProductQuestionsView : BaseActivityView {
    fun onSetToolbar(product: ICBarcodeProductV1)
    fun onCloseLoading()
    fun onGetListProductQuestionsSuccess(list: MutableList<ICQuestionRow>, isLoadmore: Boolean)
    fun onGetListProductQuestionsError(error: String)
    fun onLoadmore()
    fun onClickTryAgain()
    fun onClickGetListAnswer(position: Int, questionId: Long,offset:Int)
    fun onGetListAnswerSuccess(position: Int, list: MutableList<ICQuestionsAnswers>)


    fun onCreateQuestionSuccess(obj: ICQuestionRow)

    fun onClickCreateAnswer(questionId: Long,actorName:String,position: Int)
    fun onCreateAnswerSuccess(obj: ICQuestionsAnswers,position: Int)
    fun onCreateAnswerError(error: String)


    fun onClickDetailUser(type:String,id:Long)
}