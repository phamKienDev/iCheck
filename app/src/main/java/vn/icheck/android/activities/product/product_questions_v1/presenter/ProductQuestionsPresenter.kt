package vn.icheck.android.screen.user.product_questions.presenter

import android.content.Intent
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.product_questions.ProductQuestionsInteractor
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.upload.UploadResponse
import vn.icheck.android.activities.product.product_questions_v1.view.IProductQuestionsView
import vn.icheck.android.helper.ImageHelperV1
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.network.models.v1.ICQuestionRow
import vn.icheck.android.network.models.v1.ICQuestionsAnswers
import java.io.File

class ProductQuestionsPresenter(val view: IProductQuestionsView) : BaseActivityPresenter(view) {
    private val productQuestionsInteractor = ProductQuestionsInteractor()
    private var productId: Long = -1L


    private var offset = 0
    private var offsetAnswer = 1
    private var questionAnswerId = -1L

    private var imageUrl = mutableListOf<String>()


    fun getProductID(intent: Intent?) {
        productId = intent?.getLongExtra(Constant.DATA_1, -1L)!!

        if (productId != -1L) {
            getProductDetail()
        } else {
            view.onCloseLoading()
            view.onGetListProductQuestionsError(view.mContext.resources.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
        }
    }

    fun getProductDetail() {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetListProductQuestionsError(view.mContext.resources.getString(R.string.ket_noi_mang_cua_ban_co_van_de_vui_long_thu_lai))
            return
        }

        productQuestionsInteractor.getProductDtail(productId, object : ICApiListener<ICBarcodeProductV1> {
            override fun onSuccess(obj: ICBarcodeProductV1) {
                view.onSetToolbar(obj)
                getListProductQuestions(false)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                view.onGetListProductQuestionsError(view.mContext.resources.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }

        })
    }

    fun getListProductQuestions(isLoadmore: Boolean) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetListProductQuestionsError(view.mContext.resources.getString(R.string.ket_noi_mang_cua_ban_co_van_de_vui_long_thu_lai))
            return
        }

        if (!isLoadmore) {
            offset = 0
            questionAnswerId = -1L
        }

        productQuestionsInteractor.getListProductQuestions(offset, APIConstants.LIMIT, productId, object : ICApiListener<ICListResponse<ICQuestionRow>> {
            override fun onSuccess(obj: ICListResponse<ICQuestionRow>) {
                view.onCloseLoading()
                offset += APIConstants.LIMIT
                for (it in obj.rows) {
                    if (it.answers.size > 1) {
                        for (i in it.answers.size - 1 downTo 1) {
                            it.answers.removeAt(i)
                        }
                    }
                }

                view.onGetListProductQuestionsSuccess(obj.rows, isLoadmore)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                view.onGetListProductQuestionsError(view.mContext.resources.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    fun getListAnswersByQuestion(position: Int, questionId: Long, offset: Int) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetListProductQuestionsError(view.mContext.resources.getString(R.string.ket_noi_mang_cua_ban_co_van_de_vui_long_thu_lai))
            return
        }

        if (questionAnswerId != questionId) {
            offsetAnswer = offset
        }
        questionAnswerId = questionId

        productQuestionsInteractor.getListAnswersByQuestion(offsetAnswer, APIConstants.LIMIT, questionId, object : ICApiListener<ICListResponse<ICQuestionsAnswers>> {
            override fun onSuccess(obj: ICListResponse<ICQuestionsAnswers>) {
                offsetAnswer += APIConstants.LIMIT
                view.onGetListAnswerSuccess(position, obj.rows)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onGetListProductQuestionsError(view.mContext.resources.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    fun uploadImage(questionId: Long, positionAnswer: Int, typeCreate: Int, content: String, files: MutableList<File>) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        imageUrl.clear()
        for (i in 0 until files.size) {
            ImageHelperV1.uploadImage(view.mContext, files[i], object : ICApiListener<UploadResponse> {
                override fun onSuccess(obj: UploadResponse) {
                    imageUrl.add(obj.fileId)
                    if (i == files.size - 1) {
                        if (typeCreate == 1) {
                            createQuestion(content, imageUrl)
                        } else {
                            createAnswer(questionId, positionAnswer, content, imageUrl)
                        }
                    }
                }

                override fun onError(error: ICBaseResponse?) {
                    if (i == files.size - 1) {
                        if (imageUrl.isEmpty()) {
                            view.onCreateAnswerError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                        } else {
                            if (typeCreate == 1) {
                                createQuestion(content, imageUrl)
                            } else {
                                createAnswer(questionId, positionAnswer, content, imageUrl)
                            }
                        }
                    }

                }
            })
        }

    }

    fun createQuestion(content: String, listImage: MutableList<String>) {
        val attachments = mutableListOf<ICReqAttachments>()
        if (!listImage.isNullOrEmpty()) {
            for (file in listImage) {
                attachments.add(ICReqAttachments(file, "image"))
            }
        }
        val quetions = ICReqProductQuestion(SessionManager.session.user!!.id, content, productId, attachments)

        productQuestionsInteractor.createQuestion(quetions, object : ICApiListener<ICQuestionRow> {
            override fun onSuccess(obj: ICQuestionRow) {
                view.onCreateQuestionSuccess(obj)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCreateAnswerError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }

        })
    }

    fun createAnswer(questionId: Long, position: Int, content: String, listImage: MutableList<String>) {

        val attachments = mutableListOf<ICReqAttachments>()
        if (!listImage.isNullOrEmpty()) {
            for (file in listImage) {
                attachments.add(ICReqAttachments(file, "image"))
            }
        }
        val answer = ICReqProductAnswer(SessionManager.session.user!!.id,
                content, questionId, attachments)

        productQuestionsInteractor.createAnswer(questionId, answer, object : ICApiListener<ICQuestionsAnswers> {
            override fun onSuccess(obj: ICQuestionsAnswers) {
                view.onCreateAnswerSuccess(obj, position)

            }

            override fun onError(error: ICBaseResponse?) {
                view.onCreateAnswerError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }

        })
    }

    fun destroyLoad() {
        productQuestionsInteractor.dispose()
    }

}