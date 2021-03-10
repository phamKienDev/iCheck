package vn.icheck.android.screen.user.list_product_question.viewmodel

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.activities.chat.sticker.StickerPackages
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.commentpost.ICCommentPostMore
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.ImageHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.comment.CommentRepository
import vn.icheck.android.network.feature.page.PageRepository
import vn.icheck.android.network.feature.product.ProductInteractor
import vn.icheck.android.network.feature.social.SocialRepository
import vn.icheck.android.network.models.ICPage
import vn.icheck.android.network.models.ICProductDetail
import vn.icheck.android.network.models.ICProductQuestion
import vn.icheck.android.network.models.chat.Stickers
import vn.icheck.android.network.models.upload.UploadResponse
import vn.icheck.android.room.database.AppDatabase
import vn.icheck.android.network.models.ICCommentPermission
import java.io.File

class ListProductQuestionViewModel : ViewModel() {
    private val productInteraction = ProductInteractor()
    private val commentRepository = CommentRepository()
    private val socialRepository = SocialRepository()
    private val pageInteractor = PageRepository()
    private val packagesDao = AppDatabase.getDatabase(ICheckApplication.getInstance()).stickerPackagesDao()

    private var productId: Long = -1L
    var barcode: String? = null

    private val listSticker = hashMapOf<String, List<Stickers>>()

    val onSetProductName = MutableLiveData<String?>()
    val onSetProductImage = MutableLiveData<String>()

    var obj: ICProductQuestion? = null

    val onSetListQuestion = MutableLiveData<MutableList<Any>>()
    val onAddListQuestion = MutableLiveData<MutableList<Any>>()
    val onAddQuestion = MutableLiveData<ICProductQuestion>()

    val onAddListAnswers = MutableLiveData<MutableList<ICProductQuestion>>()
    val onAddAnswer = MutableLiveData<ICProductQuestion>()
    val onDeleteComment = MutableLiveData<ICProductQuestion>()

    val onSetParentEmoji = MutableLiveData<MutableList<StickerPackages>>()
    val onSetChildEmoji = MutableLiveData<List<Stickers>>()

    var onSetPermission = MutableLiveData<MutableList<ICCommentPermission>>()

    val onError = MutableLiveData<ICError>()
    val onShowMessage = MutableLiveData<String>()
    val onStatus = MutableLiveData<ICMessageEvent.Type>()

    private var offset = 0

    fun getData(intent: Intent?) {
        productId = intent?.getLongExtra(Constant.DATA_1, -1) ?: -1L
        barcode = intent?.getStringExtra(Constant.DATA_2)

        obj = try {
            intent?.getSerializableExtra(Constant.DATA_4) as ICProductQuestion
        } catch (e: Exception) {
            null
        }

        if (productId == -1L) {
            onStatus.postValue(ICMessageEvent.Type.BACK)
        } else {
            getData()
            getProductDetail()
            getListPermission()
        }
    }

    fun getData() {
        getListQuestion()
    }

    private fun getProductDetail() {
        if (NetworkHelper.isConnected(ICheckApplication.getInstance().applicationContext)) {
            ProductInteractor().getProductDetailByID(productId, object : ICNewApiListener<ICResponse<ICProductDetail>> {
                override fun onSuccess(obj: ICResponse<ICProductDetail>) {
                    obj.data?.let { data ->
                        onSetProductName.postValue(data.basicInfo?.name)

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

    fun getListQuestion(isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance().applicationContext)) {
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().applicationContext.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        if (!isLoadMore) {
            offset = 0
            productInteraction.dispose()
        }

        productInteraction.getListProductQuestion(productId, offset, object : ICNewApiListener<ICResponse<ICListResponse<ICProductQuestion>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICProductQuestion>>) {
                // danh sách tổng chứa các câu hỏi & câu trả lời & loadmore
                val listData = mutableListOf<Any>()

                // Danh sách câu trả lời của câu hỏi
                val listChild = mutableListOf<ICProductQuestion>()
                val stringBuilder = StringBuilder("")

                for (parentItem in obj.data!!.rows) {
                    // câu hỏi sẽ margin top 10dp
                    parentItem.marginTop = SizeHelper.size5
                    parentItem.isReply = true

                    // Add các câu trả lời của câu hỏi con vào list
                    listChild.addAll(parentItem.replies ?: mutableListOf())
                    parentItem.replies = null

                    listData.add(parentItem)

                    for (childItem in listChild) {
                        // Add các câu trả lời con vào danh sách tổng
                        childItem.parentID = parentItem.id
                        childItem.marginTop = SizeHelper.size3
                        childItem.marginStart = SizeHelper.size36
                        listData.add(childItem)

                        if (stringBuilder.isNotEmpty()) {
                            stringBuilder.append(",${childItem.id}")
                        } else {
                            stringBuilder.append(childItem.id.toString())
                        }
                    }

                    // Nếu tổng câu trả lời lớn hơn danh sách câu trả lời đang hiện thì add thêm option
                    if (parentItem.replyCount > listChild.size) {
                        listData.add(ICCommentPostMore(parentItem.id, parentItem.replyCount, listChild.size, 0, stringBuilder.toString()))
                    }

                    // Xóa các câu trả lời  để sử dụng cho các câu hỏi sau
                    listChild.clear()
                    stringBuilder.clear()
                }

                offset += APIConstants.LIMIT
                if (isLoadMore) {
                    onAddListQuestion.postValue(listData)
                } else {
                    onSetListQuestion.postValue(listData)
                }
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
            }
        })
    }

    fun getListAnswer(objMore: ICCommentPostMore) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance().applicationContext)) {
            onAddListAnswers.postValue(mutableListOf(ICProductQuestion().apply {
                parentID = objMore.parentID
            }))
            onShowMessage.postValue(ICheckApplication.getInstance().applicationContext.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        productInteraction.getListProductAnswer(objMore.parentID, objMore.notIDs, objMore.offset, object : ICNewApiListener<ICResponse<ICListResponse<ICProductQuestion>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICProductQuestion>>) {
                if (obj.data?.rows?.isNullOrEmpty() == false) {
                    for (childItem in obj.data!!.rows) {
                        childItem.parentID = objMore.parentID
                        childItem.marginTop = SizeHelper.size3
                        childItem.marginStart = SizeHelper.size36
                    }
                    onAddListAnswers.postValue(obj.data!!.rows)
                } else {
                    onAddListAnswers.postValue(mutableListOf(ICProductQuestion().apply {
                        parentID = objMore.parentID
                    }))
                }
            }

            override fun onError(error: ICResponseCode?) {
                onShowMessage.postValue(error?.message
                        ?: ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                onAddListAnswers.postValue(mutableListOf(ICProductQuestion().apply {
                    parentID = objMore.parentID
                }))
            }
        })
    }

    fun getEmoji(id: String? = null) {
        viewModelScope.launch {
            if (id == null) {
                try {
                    val listEmoji = packagesDao.getAllStickerPackges()

                    if (listEmoji.isEmpty()) {
                        val result = socialRepository.getStickerPackages()
                        for (item in result.data) {
                            listEmoji.add(StickerPackages(item.id, item.name, item.thumbnail, item.count))
                        }

                        packagesDao.insertStickerPackages(listEmoji)
                    }

                    onSetParentEmoji.postValue(listEmoji)
                    if (listEmoji.isNotEmpty()) {
                        getEmoji(listEmoji[0].id)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                try {
                    var listEmoji = listSticker[id] ?: listOf()

                    if (listEmoji.isNullOrEmpty()) {
                        listEmoji = socialRepository.getStickers(id).data
                        listSticker[id] = listEmoji
                    }

                    onSetChildEmoji.postValue(listEmoji)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getListPermission() {
        val listPermission = mutableListOf<ICCommentPermission>()

        SessionManager.session.user?.let { user ->
            listPermission.add(ICCommentPermission(user.id, user.avatar, user.name))
        }
        onSetPermission.postValue(listPermission)
        pageInteractor.getMyOwnerPage(null, 20, offset, object : ICNewApiListener<ICResponse<ICListResponse<ICPage>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICPage>>) {
                for (page in obj.data?.rows ?: mutableListOf()) {
                    listPermission.add(ICCommentPermission(page.id, page.avatar, page.name, Constant.PAGE))
                }
                onSetPermission.postValue(listPermission)
            }

            override fun onError(error: ICResponseCode?) {

            }
        })
    }

    fun send(userPost: ICCommentPermission?, parentID: Long?, attachment: Any?, content: String) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance().applicationContext)) {
            onShowMessage.postValue(ICheckApplication.getInstance().applicationContext.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        onStatus.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        when (attachment) {
            is File -> {
                ImageHelper.uploadMedia(attachment, object : ICApiListener<UploadResponse> {
                    override fun onSuccess(obj: UploadResponse) {
                        sendComment(userPost, parentID, obj.src, content)
                    }

                    override fun onError(error: ICBaseResponse?) {
                        onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                        onShowMessage.postValue(ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                    }
                })
            }
            is String -> {
                sendComment(userPost, parentID, attachment, content)
            }
            else -> {
                sendComment(userPost, parentID, null, content)
            }
        }
    }

    private fun sendComment(userPost: ICCommentPermission?, parentID: Long?, image: String?, content: String) {
        if (parentID == null) {
            postQuestion(userPost, image, content)
        } else {
            postAnswer(userPost, parentID, image, content)
        }
    }

    private fun postQuestion(userPost: ICCommentPermission?, image: String?, content: String) {
        val pageId = if (userPost != null && userPost.type == Constant.PAGE) {
            userPost.id
        } else {
            null
        }
        productInteraction.postQuestion(pageId, productId, content, image, object : ICNewApiListener<ICResponse<ICProductQuestion>> {
            override fun onSuccess(obj: ICResponse<ICProductQuestion>) {
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                obj.data?.let { data ->
                    data.isReply = true
                    onAddQuestion.postValue(obj.data!!)
                }
            }

            override fun onError(error: ICResponseCode?) {
                val message = error?.message
                        ?: ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onShowMessage.postValue(message)
            }
        })
    }

    private fun postAnswer(userPost: ICCommentPermission?, questionID: Long, image: String?, content: String) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance().applicationContext)) {
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().applicationContext.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        onStatus.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        val pageId = if (userPost != null && userPost.type != Constant.USER) {
            userPost.id
        } else {
            null
        }
        productInteraction.postAnswer(pageId, questionID, image, content, object : ICNewApiListener<ICResponse<ICProductQuestion>> {
            override fun onSuccess(obj: ICResponse<ICProductQuestion>) {
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                obj.data?.let { data ->
                    data.parentID = questionID
                    data.marginTop = SizeHelper.size3
                    data.marginStart = SizeHelper.size36
                    onAddAnswer.postValue(data)
                }
            }

            override fun onError(error: ICResponseCode?) {
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                val message = error?.message
                        ?: ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                onShowMessage.postValue(message)
            }
        })
    }

    fun deleteComment(objProduct: ICProductQuestion) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onShowMessage.postValue(ICheckApplication.getInstance().applicationContext.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        onStatus.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        if (objProduct.parentID == null) {
            productInteraction.deleteQuestion(objProduct.id, object : ICNewApiListener<ICResponseCode> {
                override fun onSuccess(obj: ICResponseCode) {
                    onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                    onDeleteComment.postValue(objProduct)
                }

                override fun onError(error: ICResponseCode?) {
                    onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                    val message = error?.message
                            ?: ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    onShowMessage.postValue(message)
                }
            })
        } else {
            commentRepository.deleteComment(objProduct.id, object : ICNewApiListener<ICResponseCode> {
                override fun onSuccess(obj: ICResponseCode) {
                    onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                    onDeleteComment.postValue(objProduct)
                }

                override fun onError(error: ICResponseCode?) {
                    onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                    val message = error?.message
                            ?: ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    onShowMessage.postValue(message)
                }
            })
        }
    }
}