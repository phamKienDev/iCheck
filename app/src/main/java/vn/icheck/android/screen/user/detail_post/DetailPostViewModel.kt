package vn.icheck.android.screen.user.detail_post

import android.content.Intent
import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
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
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.page.PageRepository
import vn.icheck.android.network.feature.post.PostInteractor
import vn.icheck.android.network.feature.product_review.ProductReviewInteractor
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.chat.Stickers
import vn.icheck.android.network.models.upload.UploadResponse
import vn.icheck.android.room.database.AppDatabase
import java.io.File

class DetailPostViewModel : ViewModel() {
    var onDetailPost = MutableLiveData<ICPost>()
    var onAddComment = MutableLiveData<MutableList<Any>>()
    var onAddChilComment = MutableLiveData<MutableList<ICCommentPost>>()
    var onPostComment = MutableLiveData<ICCommentPost>()
    var onPostChildComment = MutableLiveData<ICCommentPost>()
    var onDeleteComment = MutableLiveData<ICCommentPost>()
    var onError = MutableLiveData<ICError>()
    var onStatus = MutableLiveData<ICMessageEvent>()
    var onSetPermission = MutableLiveData<MutableList<ICCommentPermission>>()
    var onDeletePost = MutableLiveData<ICCommentPost>()
    var onLikePost = MutableLiveData<ICPost>()

    val onSetParentEmoji = MutableLiveData<MutableList<StickerPackages>>()
    val onSetChildEmoji = MutableLiveData<List<Stickers>>()

    private val packagesDao = AppDatabase.getDatabase(ICheckApplication.getInstance()).stickerPackagesDao()
    val interactor = ProductReviewInteractor()
    val pageInteractor = PageRepository()
    val postInteractor = PostInteractor()

    var post: ICPost? = null
    var postId: Long = -1L
    var isReply: Boolean = true
    private var permission: ICCommentPermission? = null
    private var offset = 0
    private var involeType: String? = null

    fun setPermission(permission: ICCommentPermission?) {
        this.permission = permission
    }

    fun getData(intent: Intent) {
        if (intent.getSerializableExtra(Constant.DATA_1) is ICPost) {
            post = intent.getSerializableExtra(Constant.DATA_1) as ICPost
        }
        if (intent.getLongExtra(Constant.DATA_1, -1) != -1L) {
            postId = intent.getLongExtra(Constant.DATA_1, -1)
        }

        when {
            postId != -1L -> {
                getPostDetail()
                getListComment()
                getListPermission()
            }
            post != null -> {
                onDetailPost.postValue(post)
                involeType = post!!.involveType
                postId = post!!.id
                intent.putExtra(Constant.DATA_1, postId)
                getListComment()
                getListPermission()
            }
            else -> {
                onError.postValue(ICError(R.drawable.ic_error_request, getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, null))
            }
        }
    }

    private fun getPostDetail() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }
        onStatus.postValue(ICMessageEvent(ICMessageEvent.Type.ON_SHOW_LOADING))
        postInteractor.getPostDetail(postId, object : ICNewApiListener<ICResponse<ICPost>> {
            override fun onSuccess(obj: ICResponse<ICPost>) {
                onStatus.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING))

                if (obj.data != null) {
                    obj.data?.let {
                        onDetailPost.postValue(it)
                    }
                    post = obj.data
                    involeType = obj.data!!.involveType
                } else {
                    onError.postValue(ICError(R.drawable.ic_error_request,
                            ICheckApplication.getInstance().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, null))
                }
            }

            override fun onError(error: ICResponseCode?) {
                onStatus.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING))
                val message = if (error?.message.isNullOrEmpty()) {
                    ICheckApplication.getInstance().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                } else {
                    error?.message
                }
                onError.postValue(ICError(R.drawable.ic_error_request, message, null, null))
            }
        })
    }


    fun getListComment(isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), null, null))
            return
        }

        if (!isLoadMore) {
            offset = 0
        }

        postInteractor.getListCommentsOfPost(postId, offset, APIConstants.LIMIT, object : ICNewApiListener<ICResponse<ICListResponse<ICCommentPost>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICCommentPost>>) {
                if (!isLoadMore)
                    setCommentPostData(obj.data?.rows)
                else
                    addCommentPostData(obj.data?.rows)

                // Danh sách tổng chứa các comment và comment của comment
                val listData = mutableListOf<Any>()

                // Danh sách comment của comment
                val listChild = mutableListOf<ICCommentPost>()

                for (parentItem in obj.data!!.rows) {
                    // Comment cha sẽ margin top 10dp
                    parentItem.marginTop = SizeHelper.size10
                    //cho phép hiện trả lời
                    parentItem.isReply = isReply
                    parentItem.involveType = involeType

                    // Add các comment con vào list
                    listChild.addAll(parentItem.replies ?: mutableListOf())
                    // Xóa các comment con
                    parentItem.replies = null

                    // Add comment cha vào danh sách tổng
                    listData.add(parentItem)
                    for (childItem in listChild) {
                        // Add các comment con vào danh sách tổng
                        childItem.marginTop = SizeHelper.size3
                        childItem.marginStart = SizeHelper.size36
                        listData.add(childItem)
                    }

                    // Nếu tổng comment con lớn hơn danh sách comment con thì add thêm option
                    if (parentItem.replyCount > listChild.size) {
                        listData.add(ICCommentPostMore(parentItem.id, parentItem.replyCount, listChild.size, 0, ""))
                    }

                    // Xóa các comment con để sử dụng cho các comment cha sau
                    listChild.clear()
                }

                offset += APIConstants.LIMIT

                onAddComment.postValue(listData)
            }

            override fun onError(error: ICResponseCode?) {
                val message = if (error?.message.isNullOrEmpty()) {
                    ICheckApplication.getInstance().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                } else {
                    error?.message
                }
                onError.postValue(ICError(R.drawable.ic_error_request, message, null, null))
            }
        })
    }

    private fun getListPermission() {
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

    fun getEmoji(id: String? = null) {
        viewModelScope.launch {
            if (id == null) {
                try {
                    val listEmoji = packagesDao.getAllStickerPackges()

                    if (listEmoji.isEmpty()) {
                        val result = ICNetworkClient.getSimpleChat().getStickerPackages()

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
                    var listEmoji = listOf<Stickers>()

                    if (listEmoji.isNullOrEmpty()) {
                        val result = ICNetworkClient.getSimpleChat().getStickers(id)
                        listEmoji = result.data
                    }

                    onSetChildEmoji.postValue(listEmoji)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getListChildComment(obj: ICCommentPostMore) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), null, null))
            return
        }

        interactor.getListChildComment(obj.parentID, APIConstants.LIMIT, obj.currentCount, object : ICNewApiListener<ICResponse<ICListResponse<ICCommentPost>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICCommentPost>>) {
                for (item in obj.data!!.rows) {
                    item.marginTop = SizeHelper.size3
                    item.marginStart = SizeHelper.size36
                }
                onAddChilComment.postValue(obj.data!!.rows)
            }

            override fun onError(error: ICResponseCode?) {
                val message = if (error?.message.isNullOrEmpty()) {
                    ICheckApplication.getInstance().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                } else {
                    error?.message
                }
                onError.postValue(ICError(R.drawable.ic_error_request, message, null, null))
            }
        })
    }

    fun uploadImage(media: Any?, permission: ICCommentPermission?, message: String, parent: Any?) {
        /*
        * media có 2 dạng : File- String
        *  Content: File phải post lên Serer lấy String Image
        * Content: String: Post comment luôn
        * */
        var pageId: Long? = null
        if (permission != null) {
            pageId = if (permission.type == Constant.ENTERPRISE || permission.type == Constant.PAGE) {
                permission.id
            } else {
                null
            }
        }

        onStatus.postValue(ICMessageEvent(ICMessageEvent.Type.ON_SHOW_LOADING))

        if (media == null) {
            if (parent == null) {
                postComment(pageId, message, null)
            } else {
                postChildComment(pageId, message, parent as ICCommentPost, null)
            }
        } else {
            if (media is File) {
                ImageHelper.uploadMedia(media, object : ICApiListener<UploadResponse> {
                    override fun onSuccess(obj: UploadResponse) {
                        val typeMedia = if (obj.src.contains(".mp4")) {
                            Constant.VIDEO
                        } else {
                            Constant.IMAGE
                        }
                        if (parent == null) {
                            postComment(pageId, message, ICMedia(obj.src, type= typeMedia))
                        } else {
                            postChildComment(pageId, message, parent as ICCommentPost, ICMedia(obj.src, type= typeMedia))
                        }
                    }

                    override fun onError(error: ICBaseResponse?) {
                        onError.postValue(ICError(R.drawable.ic_error_request,
                                error?.message
                                        ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, null))
                    }
                })
            } else {
                postComment(pageId, message, ICMedia(media as String, Constant.IMAGE))
            }
        }
    }

    fun postComment(pageId: Long?, message: String, media: ICMedia? = null) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), null, null))
            return
        }

        postInteractor.commentPost(postId, message, pageId, media,post?.involveType, object : ICNewApiListener<ICResponse<ICCommentPost>> {
            override fun onSuccess(obj: ICResponse<ICCommentPost>) {
                onStatus.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING))
                if (obj.data != null) {
                    obj.data!!.marginTop = SizeHelper.size10
                    //cho phép hiện trả lời
                    obj.data!!.isReply = isReply
                    obj.data!!.involveType = involeType
                    obj.data?.let {
                        onPostComment.postValue(it)
                    }
                    addCommentPostData(obj.data!!)
                }
            }

            override fun onError(error: ICResponseCode?) {
                onStatus.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING))
                val message = if (error?.message.isNullOrEmpty()) {
                    ICheckApplication.getInstance().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                } else {
                    error?.message
                }
                onError.postValue(ICError(R.drawable.ic_error_request, message, null, null))
            }
        })

    }

    fun postChildComment(pageId: Long?, message: String, parent: ICCommentPost, media: ICMedia? = null) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), null, null))
            return
        }

        interactor.postCommentReply(parent.id, message, pageId, media, object : ICNewApiListener<ICResponse<ICCommentPost>> {
            override fun onSuccess(obj: ICResponse<ICCommentPost>) {
                onStatus.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING))
                if (obj.data != null) {
                    obj.data!!.marginTop = SizeHelper.size3
                    obj.data!!.marginStart = SizeHelper.size36
                    obj.data?.let {
                        onPostChildComment.postValue(it)
                    }
                }
            }

            override fun onError(error: ICResponseCode?) {
                onStatus.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING))
                val message = if (error?.message.isNullOrEmpty()) {
                    ICheckApplication.getInstance().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                } else {
                    error?.message
                }
                onError.postValue(ICError(R.drawable.ic_error_request, message, null, null))
            }
        })
    }

    fun deleteComment(comment: ICCommentPost) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), null, null))
            return
        }

        interactor.deleteComment(comment.id, object : ICNewApiListener<ICResponse<ICCommentPost>> {
            override fun onSuccess(obj: ICResponse<ICCommentPost>) {
                obj.data?.let {
                    onDeleteComment.postValue(it)
                }
                deleteCommentPostData(comment)
            }

            override fun onError(error: ICResponseCode?) {
                val message = if (error?.message.isNullOrEmpty()) {
                    error?.message!!
                } else {
                    getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                }
                onError.postValue(ICError(R.drawable.ic_error_request, message, null, null))
            }

        })
    }

    fun pinPost(objPost: ICPost, isPin: Boolean) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), null, null))
            return
        }

        onStatus.postValue(ICMessageEvent(ICMessageEvent.Type.ON_SHOW_LOADING))
        pageInteractor.pinPostOfPage(objPost.id, isPin, objPost.page?.id, object : ICNewApiListener<ICResponse<ICPost>> {
            override fun onSuccess(obj: ICResponse<ICPost>) {
                onStatus.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING))
                post?.pinned = obj.data?.pinned ?: false

                if (post?.pinned == true)
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.PIN_POST, objPost.id))
                else
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UN_PIN_POST, objPost.id))
            }

            override fun onError(error: ICResponseCode?) {
                onStatus.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING))
                val message = if (error?.message.isNullOrEmpty()) {
                    ICheckApplication.getInstance().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                } else {
                    error?.message
                }
                onError.postValue(ICError(R.drawable.ic_error_request, message, null, null))
            }
        })
    }

    fun deletePost(id: Long) {
        if (NetworkHelper.isNotConnected(ICheckApplication.currentActivity())) {
            onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        onStatus.postValue(ICMessageEvent(ICMessageEvent.Type.ON_SHOW_LOADING))


        postInteractor.deletePost(id, object : ICNewApiListener<ICResponse<ICCommentPost>> {
            override fun onSuccess(obj: ICResponse<ICCommentPost>) {
                onStatus.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING))
                obj.data?.let {
                    onDeletePost.postValue(it)
                }
            }

            override fun onError(error: ICResponseCode?) {
                onStatus.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING))
                val message = if (error?.message.isNullOrEmpty()) {
                    ICheckApplication.getInstance().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                } else {
                    error?.message
                }
                onError.postValue(ICError(R.drawable.ic_error_request, message, null, null))
            }
        })
    }

    fun likePost() {
        if (NetworkHelper.isNotConnected(ICheckApplication.currentActivity())) {
            onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        onStatus.postValue(ICMessageEvent(ICMessageEvent.Type.ON_SHOW_LOADING))

        PostInteractor().likeOrDislikePost(postId, null, object : ICNewApiListener<ICResponse<ICPost>> {
            override fun onSuccess(obj: ICResponse<ICPost>) {
                onStatus.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING))
                likeCommentPostData(obj)
                onLikePost.postValue(post)
            }

            override fun onError(error: ICResponseCode?) {
                onStatus.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING))
                if (error?.statusCode == "S402") {
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_REQUIRE_LOGIN))
                } else {
                    val message = if (error?.message.isNullOrEmpty()) {
                        ICheckApplication.getInstance().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    } else {
                        error?.message
                    }
                    onError.postValue(ICError(R.drawable.ic_error_request, message, null, null))
                }

//                val message = if (error?.message.isNullOrEmpty()) {
//                    ICheckApplication.getInstance().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
//                } else {
//                    error?.message
//                }
//                onError.postValue(ICError(R.drawable.ic_error_request, message, null, null))
            }
        })
    }

    /*
    * Thao tác với data Post
    * */

    fun setCommentPostData(comment: MutableList<ICCommentPost>?) {
        Handler().postDelayed({
            if (!comment.isNullOrEmpty()) {
                if (post?.comments.isNullOrEmpty()) {
                    post?.comments = mutableListOf()
                }
                post?.comments?.clear()
                post?.comments?.addAll(comment)
            }
        }, 2000)
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

    private fun likeCommentPostData(obj: ICResponse<ICPost>) {
        if (obj.data?.id == null || obj.data?.id == 0L) {
            post?.expressiveCount = (post?.expressiveCount ?: 0) - 1
            post?.expressive = null
        } else {
            post?.expressiveCount = (post?.expressiveCount ?: 0) + 1
            post?.expressive = "like"
        }
    }
}