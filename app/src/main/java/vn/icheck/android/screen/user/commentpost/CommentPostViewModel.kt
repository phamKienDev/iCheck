package vn.icheck.android.screen.user.commentpost

import android.content.Intent
import android.os.Handler
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
import vn.icheck.android.network.feature.post.PostInteractor
import vn.icheck.android.network.feature.product_review.ProductReviewInteractor
import vn.icheck.android.network.feature.social.SocialRepository
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.chat.Stickers
import vn.icheck.android.network.models.upload.UploadResponse
import vn.icheck.android.room.database.AppDatabase
import vn.icheck.android.network.models.ICCommentPermission
import vn.icheck.android.util.kotlin.ToastUtils
import java.io.File

/**
 * Created by VuLCL on 7/3/2020.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CommentPostViewModel : ViewModel() {
    private val pageInteraction = PageRepository()
    private val socialRepository = SocialRepository()
    private val postInteraction = PostInteractor()
    private val commentRepository = CommentRepository()

    private val packagesDao = AppDatabase.getDatabase(ICheckApplication.getInstance()).stickerPackagesDao()

    val onSetPostInfo = MutableLiveData<ICPost>()
    val onSetPermission = MutableLiveData<MutableList<ICCommentPermission>>()

    val onSetListComment = MutableLiveData<MutableList<Any>>()
    val onAddListComment = MutableLiveData<MutableList<Any>>()
    val onAddComment = MutableLiveData<ICCommentPost>()

    val onAddListReplies = MutableLiveData<MutableList<ICCommentPost>>()
    val onAddReply = MutableLiveData<ICCommentPost>()
    val onDeleteComment = MutableLiveData<ICCommentPost>()

    val onSetParentEmoji = MutableLiveData<MutableList<StickerPackages>>()
    val onSetChildEmoji = MutableLiveData<List<Stickers>>()

    val onError = MutableLiveData<ICError>()
    val onShowMessage = MutableLiveData<String>()
    val onStatus = MutableLiveData<ICMessageEvent.Type>()

    val onLikeComment = MutableLiveData<Int>()

    var postID = -1L
    var post: ICPost? = null // data chuyền chung giữa các màn
    var barcode: String? = null
    var isReply: Boolean = true
    private val listSticker = hashMapOf<String, List<Stickers>>()
    private var offset = 0
    private var involeType: String? = null

    fun getData(intent: Intent?) {
        postID = try {
            intent?.getLongExtra(Constant.DATA_1, -1) ?: -1
        } catch (e: Exception) {
            -1
        }
        post = try {
            intent?.getSerializableExtra(Constant.DATA_1) as ICPost
        } catch (e: Exception) {
            null
        }

        getData()
    }

    fun getData() {
        if (post == null) {
            if (postID != -1L) {
                getPostDetail()
                getListPermission()
                getListComments()
            } else {
                onStatus.postValue(ICMessageEvent.Type.BACK)
            }
        } else {
            barcode = post!!.meta?.product?.barcode
            involeType = post!!.involveType
            onSetPostInfo.postValue(post!!)

            postID = post!!.id
            getListPermission()
            getListComments()
        }
    }

    private fun getPostDetail() {
        if (NetworkHelper.isConnected(ICheckApplication.getInstance())) {
            postInteraction.getPostDetail(postID, object : ICNewApiListener<ICResponse<ICPost>> {
                override fun onSuccess(obj: ICResponse<ICPost>) {
                    obj.data?.let {
                        barcode = it.meta?.product?.barcode
                        post = it
                        involeType = it.involveType
                        onSetPostInfo.postValue(it)
                    }
                }

                override fun onError(error: ICResponseCode?) {
                }
            })
        }
    }

    private fun getListPermission() {
        val listPermission = mutableListOf<ICCommentPermission>()

        SessionManager.session.user?.let { user ->
            listPermission.add(ICCommentPermission(null, user.avatar, user.getName))
        }

        onSetPermission.value = listPermission

        pageInteraction.getMyOwnerPage(null, 20, 0, object : ICNewApiListener<ICResponse<ICListResponse<ICPage>>> {
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

    fun getListComments(isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), null, null))
            return
        }

        if (!isLoadMore) {
            offset = 0
        }

        postInteraction.getListCommentsOfPost(postID, offset, APIConstants.LIMIT, object : ICNewApiListener<ICResponse<ICListResponse<ICCommentPost>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICCommentPost>>) {
                if(!isLoadMore)
                    setCommentPostData(obj.data?.rows)
                else
                    addCommentPostData(obj.data?.rows)

                // danh sách tổng chứa các câu hỏi & câu trả lời & loadmore
                val listData = mutableListOf<Any>()

                // Danh sách câu trả lời của câu hỏi
                val listChild = mutableListOf<ICCommentPost>()
                val stringBuilder = StringBuilder("")

                for (parentItem in obj.data!!.rows) {
                    // câu hỏi sẽ margin top 10dp
                    parentItem.marginTop = SizeHelper.size5
                    parentItem.isReply = isReply
                    parentItem.involveType = involeType

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
                    onAddListComment.postValue(listData)
                } else {
                    onSetListComment.postValue(listData)
                }
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, null))
            }
        })
    }

    fun getListReplies(objMore: ICCommentPostMore) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance().applicationContext)) {
            onAddListReplies.postValue(mutableListOf(ICCommentPost().apply {
                parentID = objMore.parentID
            }))
            onShowMessage.postValue(ICheckApplication.getInstance().applicationContext.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        commentRepository.getListReplies(objMore.parentID, objMore.notIDs, objMore.offset, object : ICNewApiListener<ICResponse<ICListResponse<ICCommentPost>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICCommentPost>>) {
                if (obj.data?.rows?.isNullOrEmpty() == false) {
                    for (childItem in obj.data!!.rows) {
                        childItem.parentID = objMore.parentID
                        childItem.marginTop = SizeHelper.size3
                        childItem.marginStart = SizeHelper.size36
                    }
                    onAddListReplies.postValue(obj.data!!.rows)
                } else {
                    onAddListReplies.postValue(mutableListOf(ICCommentPost().apply {
                        parentID = objMore.parentID
                    }))
                }
            }

            override fun onError(error: ICResponseCode?) {
                onShowMessage.postValue(error?.message
                        ?: ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                onAddListReplies.postValue(mutableListOf(ICCommentPost().apply {
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
                        for (item in socialRepository.getStickerPackages().data) {
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
                    var listEmoji = listSticker[id]

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

    fun send(pageID: Long?, parentID: Long?, media: Any?, content: String) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance().applicationContext)) {
            onShowMessage.postValue(ICheckApplication.getInstance().applicationContext.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        onStatus.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        if (media != null) {
            if (media is File) {
                ImageHelper.uploadMedia(media, object : ICApiListener<UploadResponse> {
                    override fun onSuccess(obj: UploadResponse) {
                        sendComment(pageID, parentID, obj.src, content)
                    }

                    override fun onError(error: ICBaseResponse?) {
                        onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                        onShowMessage.postValue(ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                    }
                })
            } else {
                sendComment(pageID, parentID, media as String, content)
            }
        } else {
            sendComment(pageID, parentID, null, content)
        }
    }

    private fun sendComment(pageID: Long?, parentID: Long?, image: String?, content: String) {
        if (parentID == null) {
            postComment(pageID, image, content)
        } else {
            postReply(parentID, pageID, image, content)
        }
    }

    private fun postComment(pageID: Long?, image: String?, content: String) {
        postInteraction.commentPost(postID, pageID, content, image,post?.involveType, object : ICNewApiListener<ICResponse<ICCommentPost>> {
            override fun onSuccess(obj: ICResponse<ICCommentPost>) {
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                obj.data?.let { data ->
                    data.isReply = isReply
                    data.postId = postID
                    data.involveType = involeType
                    onAddComment.postValue(data)
                    addCommentPostData(data)
                }
            }

            override fun onError(error: ICResponseCode?) {
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onShowMessage.postValue(if (!error?.message.isNullOrEmpty())
                    error?.message
                else
                    ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    private fun postReply(commentID: Long, pageID: Long?, image: String?, content: String) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance().applicationContext)) {
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().applicationContext.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), null, null))
            return
        }

        onStatus.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        commentRepository.replComment(commentID, pageID, content, image, object : ICNewApiListener<ICResponse<ICCommentPost>> {
            override fun onSuccess(obj: ICResponse<ICCommentPost>) {
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                obj.data?.let { data ->
                    data.parentID = commentID
                    data.marginTop = SizeHelper.size3
                    data.marginStart = SizeHelper.size36
                    onAddReply.postValue(data)
                }
            }

            override fun onError(error: ICResponseCode?) {
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onShowMessage.postValue(if (!error?.message.isNullOrEmpty())
                    error?.message
                else
                    ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    fun deleteComment(objComment: ICCommentPost) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onShowMessage.postValue(ICheckApplication.getInstance().applicationContext.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        onStatus.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        commentRepository.deleteComment(objComment.id, object : ICNewApiListener<ICResponseCode> {
            override fun onSuccess(obj: ICResponseCode) {
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onDeleteComment.postValue(objComment)
                deleteCommentPostData(objComment)
            }

            override fun onError(error: ICResponseCode?) {
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                val message = if (error?.message.isNullOrEmpty())
                    ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                else
                    error?.message
                onShowMessage.postValue(message)
            }
        })
    }


    fun likeReview() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance().applicationContext)) {
            ToastUtils.showShortError(ICheckApplication.getInstance(), ICheckApplication.getInstance().applicationContext.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        onStatus.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        ProductReviewInteractor().postLikeReview(postID, null, object : ICNewApiListener<ICResponse<ICNotification>> {
            override fun onSuccess(obj: ICResponse<ICNotification>) {
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)

                post?.let { post ->
                    likeCommentPostData(obj, post)
                    onLikeComment.postValue(1)
                }
            }

            override fun onError(error: ICResponseCode?) {
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onShowMessage.postValue(if (!error?.message.isNullOrEmpty())
                    error?.message
                else
                    ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    /*
    * Thao tác với data Post
    * */

    private fun setCommentPostData(comment: MutableList<ICCommentPost>?) {
        Handler().postDelayed({
            if (!comment.isNullOrEmpty()) {
                if (post?.comments.isNullOrEmpty()) {
                    post?.comments = mutableListOf()
                }
                post?.comments!!.clear()
                post?.comments!!.addAll(comment)
            }
        }, 800)
    }

    private fun addCommentPostData(comment: MutableList<ICCommentPost>?) {
        if (!comment.isNullOrEmpty()) {
            if (post?.comments.isNullOrEmpty()) {
                post?.comments = mutableListOf()
            }
            post?.comments!!.addAll(comment)
        }
    }

    private fun addCommentPostData(comment: ICCommentPost) {
        post?.commentCount = (post?.commentCount ?: 0) + 1
        if (post?.comments.isNullOrEmpty()) {
            post?.comments = mutableListOf()
            post?.comments!!.add(comment)
        } else {
            post?.comments!!.add(0, comment)
        }
    }

    private fun deleteCommentPostData(comment: ICCommentPost) {
        if (comment.commentId == null) {
            post?.commentCount = (post?.commentCount ?: 0) - 1
            post?.comments?.remove(post?.comments?.find { it.id == comment.id })
        }
    }

    private fun likeCommentPostData(obj: ICResponse<ICNotification>, post: ICPost) {
        if (obj.data?.objectId == null) {
            post.expressiveCount -= 1
            post.expressive = null
        } else {
            post.expressiveCount += 1
            post.expressive = "like"
        }
    }
}