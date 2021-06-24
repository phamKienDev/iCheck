package vn.icheck.android.screen.user.media_in_post

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.post.PostInteractor
import vn.icheck.android.network.models.ICPost

class MediaInPostViewModel : ViewModel() {
    val postInteractor = PostInteractor()
    val onMediaData = MutableLiveData<MutableList<ICExoMedia>>()
    val onPostData = MutableLiveData<ICPost>()
    val onSharePost = MutableLiveData<String>()
    val onError = MutableLiveData<String>()
    val onShowMessage = MutableLiveData<String>()
    val onStatusCode = MutableLiveData<ICMessageEvent.Type>()

    var postDetail: ICPost? = null
    var postId: Long = -1
    var listMedia = mutableListOf<ICExoMedia>()


    fun getData(intent: Intent) {
        postDetail = if (intent.getSerializableExtra(Constant.DATA_1) != null && intent.getSerializableExtra(Constant.DATA_1) is ICPost) {
            intent.getSerializableExtra(Constant.DATA_1) as ICPost
        } else {
            null
        }
        postId = intent.getLongExtra(Constant.DATA_2, -1)

        if (postDetail != null) {
            getMedia(postDetail!!)
            onPostData.postValue(postDetail!!)
        } else {
            if (postId != -1L) {
                onStatusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)
                getPostDetail(postId)
            } else {
                onError.postValue( getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        }
    }

    fun getPostDetail(id: Long, media: Boolean = true) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance().applicationContext)) {
            onError.postValue(ICheckApplication.getInstance().applicationContext.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        postInteractor.getPostDetail(id, object : ICNewApiListener<ICResponse<ICPost>> {
            override fun onSuccess(obj: ICResponse<ICPost>) {
                onStatusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                if (obj.data != null) {
                    postDetail = obj.data
                    onPostData.postValue(obj.data!!)
                    if (media)
                        getMedia(obj.data!!)
                } else {
                    onError.postValue(ICheckApplication.getInstance().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            }

            override fun onError(error: ICResponseCode?) {
                onStatusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                val message = error?.message
                        ?: ICheckApplication.getInstance().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                onError.postValue(message)
            }
        })
    }

    private fun getMedia(icPost: ICPost) {
        listMedia.clear()
        if (!icPost.media.isNullOrEmpty()) {
            for (i in icPost.media!!) {
                listMedia.add(ICExoMedia(i.content, i.type).also {
                    it.checkTypeMedia()
                })
            }
        }

        if (listMedia.isNotEmpty()) {
            onMediaData.postValue(listMedia)
        }
    }

    fun onLikeReview() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance().applicationContext)) {
            onShowMessage.postValue(ICheckApplication.getInstance().applicationContext.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        if (!SessionManager.isUserLogged) {
            ICheckApplication.currentActivity()?.let { activity ->
                DialogHelper.showLoginPopup(activity)
            }
            return
        }
        onStatusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        postInteractor.likeOrDislikePost(postDetail!!.id, null, object : ICNewApiListener<ICResponse<ICPost>> {
            override fun onError(error: ICResponseCode?) {
                onStatusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                val message = error?.message
                        ?: ICheckApplication.getInstance().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                onShowMessage.postValue(message)
            }

            override fun onSuccess(obj: ICResponse<ICPost>) {
                onStatusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)

                if (obj.data?.id == null || obj.data?.id == 0L) {
                    postDetail!!.expressive = null
                    postDetail!!.expressiveCount += -1
                } else {
                    postDetail!!.expressive = "like"
                    postDetail!!.expressiveCount += 1

                }
                onPostData.postValue(postDetail)
            }
        })
    }

    fun onSharePost() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance().applicationContext)) {
            onShowMessage.postValue(ICheckApplication.getInstance().applicationContext.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }
        onStatusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        PostInteractor().getShareLinkOfPost(postDetail!!.id, object : ICNewApiListener<ICResponse<String>> {
            override fun onSuccess(obj: ICResponse<String>) {
                onStatusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                obj.data?.let {
                    onSharePost.postValue(it)
                }
            }

            override fun onError(error: ICResponseCode?) {
                onStatusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                val message = error?.message
                        ?: ICheckApplication.getInstance().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                onShowMessage.postValue(message)
            }
        })
    }
}