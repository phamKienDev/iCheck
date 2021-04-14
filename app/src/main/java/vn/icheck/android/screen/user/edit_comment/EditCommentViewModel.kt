package vn.icheck.android.screen.user.edit_comment

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.comment.CommentRepository
import vn.icheck.android.network.feature.product.ProductInteractor
import vn.icheck.android.network.models.ICCommentPost
import vn.icheck.android.network.models.ICProductDetail
import vn.icheck.android.network.models.ICProductQuestion

/**
 * Created by VuLCL on 7/16/2020.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class EditCommentViewModel : ViewModel() {
    private val interaction = CommentRepository()

    val onSetProductImage = MutableLiveData<String>()
    val onSetCommentQuestion = MutableLiveData<ICProductQuestion>()
    val onSetCommentPost = MutableLiveData<ICCommentPost>()
    val onUpdateQuestion = MutableLiveData<ICProductQuestion>()
    val onUpdatePost = MutableLiveData<ICCommentPost>()
    val onStatus = MutableLiveData<ICMessageEvent.Type>()
    val onError = MutableLiveData<String>()

    private var commentQuestion: ICProductQuestion? = null
    private var commentPost: ICCommentPost? = null

    fun getData(intent: Intent?) {
        commentQuestion = intent?.getSerializableExtra(Constant.DATA_1) as ICProductQuestion?
        commentPost = intent?.getSerializableExtra(Constant.DATA_3) as ICCommentPost?
        val barcode = intent?.getStringExtra(Constant.DATA_2)

        if (!barcode.isNullOrEmpty()) {
            getProductDetail(barcode)
        }

        if (commentQuestion != null) {
            onSetCommentQuestion.postValue(commentQuestion!!)
        } else if (commentPost != null) {
            onSetCommentPost.postValue(commentPost!!)
        } else {
            onError.postValue("")
        }
    }

    private fun getProductDetail(barcode: String) {
        if (NetworkHelper.isConnected(ICheckApplication.getInstance().applicationContext)) {
            ProductInteractor().getProductDetailByBarcode(barcode, object : ICNewApiListener<ICResponse<ICProductDetail>> {
                override fun onSuccess(obj: ICResponse<ICProductDetail>) {
                    obj.data?.let { data ->
                        data.media?.firstOrNull()?.content?.let { content ->
                            onSetProductImage.postValue(content)
                        }
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    error.toString()
                }
            })
        }
    }

    fun update(mContent: String) {
        if (commentQuestion != null) {
            updateQuestion(mContent)
        } else if (commentPost != null) {
            updatePost(mContent)
        }
    }

    private fun updateQuestion(mContent: String) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance().applicationContext)) {
            onError.postValue(ICheckApplication.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        if (commentQuestion == null) {
            onError.postValue("")
            return
        }

        onStatus.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        val image = if ( commentQuestion!!.media.isNullOrEmpty()) {
            null
        } else {
            commentQuestion!!.media?.first()?.content
        }

        if (commentQuestion!!.parentID == null) {
            interaction.updateQuestion(commentQuestion!!.id, mContent, image, object : ICNewApiListener<ICResponseCode> {
                override fun onSuccess(obj: ICResponseCode) {
                    onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                    onUpdateQuestion.postValue(commentQuestion!!.apply {
                        content = mContent
                    })
                }

                override fun onError(error: ICResponseCode?) {
                    onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                    onError.postValue(error?.message ?: ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            })
        } else {
            interaction.updateComment(commentQuestion!!.id, mContent, image, object : ICNewApiListener<ICResponseCode> {
                override fun onSuccess(obj: ICResponseCode) {
                    onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                    onUpdateQuestion.postValue(commentQuestion!!.apply {
                        content = mContent
                    })
                }

                override fun onError(error: ICResponseCode?) {
                    onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                    onError.postValue(error?.message
                            ?: ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            })
        }
    }

    private fun updatePost(mContent: String) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance().applicationContext)) {
            onError.postValue(ICheckApplication.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        if (commentPost == null) {
            onError.postValue("")
            return
        }

        onStatus.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        val image = if ( commentPost!!.media.isNullOrEmpty()) {
            null
        } else {
            commentPost!!.media?.first()?.content
        }

        interaction.updateComment(commentPost!!.id, mContent, image, object : ICNewApiListener<ICResponseCode> {
            override fun onSuccess(obj: ICResponseCode) {
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onUpdatePost.postValue(commentPost!!.apply {
                    content = mContent
                })
            }

            override fun onError(error: ICResponseCode?) {
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onError.postValue(error?.message ?: ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }
}