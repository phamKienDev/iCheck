package vn.icheck.android.screen.user.createpost.viewmodel

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.ImageHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.post.PostInteractor
import vn.icheck.android.network.models.ICPost
import vn.icheck.android.network.models.ICPrivacy
import vn.icheck.android.network.models.upload.UploadResponse
import java.io.File
import java.lang.Exception

/**
 * Created by VuLCL on 8/6/2020.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CreateOrUpdatePostViewModel : ViewModel() {
    private val repository = PostInteractor()

    val onSetTitle = MutableLiveData<Int>()
    val onSetPrivacy = MutableLiveData<ICPrivacy>()
    val onSetPost = MutableLiveData<ICPost>()
    val onCreatedPost = MutableLiveData<ICPost>()

    val onErrorGetData = MutableLiveData<Int>()
    val onShowMessage = MutableLiveData<String>()
    val onStatus = MutableLiveData<ICMessageEvent.Type>()

    var postDetail: ICPost? = null
    private val listImage = mutableListOf<String>()
    private var pageId: Long? = null

    private val listPrivacy = mutableListOf<ICPrivacy>()

    val getListPrivacy: MutableList<ICPrivacy>
        get() {
            return listPrivacy
        }

    fun updatePrivacy(privacyElementId: Long?) {
        for (i in listPrivacy.size - 1 downTo 0) {
            listPrivacy[i].selected = listPrivacy[i].privacyElementId == privacyElementId
        }
    }

    fun getData(intent: Intent?) {
        val postID = intent?.getLongExtra(Constant.DATA_1, -1) ?: -1
        pageId = intent?.getLongExtra(Constant.DATA_2, -1) ?: -1

        if (postID != -1L) {
            onSetTitle.postValue(R.string.chinh_sua_bai_viet)
            getPostDetail(postID)
        } else {
            onSetTitle.postValue(R.string.tao_bai_viet)
            getListPrivacy()
        }
    }

    private fun getListPrivacy() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onErrorGetData.postValue(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        onStatus.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        repository.getPostPrivacy(null, object : ICNewApiListener<ICResponse<ICListResponse<ICPrivacy>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICPrivacy>>) {
                listPrivacy.addAll(obj.data?.rows ?: mutableListOf())

                if (listPrivacy.isNotEmpty()) {
                    onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                    onSetPrivacy.postValue(listPrivacy.find { it.selected } ?: listPrivacy[0])
                } else {
                    onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                    onErrorGetData.postValue(R.string.co_loi_xay_ra_vui_long_thu_lai)
                }
            }

            override fun onError(error: ICResponseCode?) {
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onErrorGetData.postValue(R.string.co_loi_xay_ra_vui_long_thu_lai)
            }
        })
    }

    private fun getPostDetail(postID: Long) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onErrorGetData.postValue(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        onStatus.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        repository.getPostDetail(postID, object : ICNewApiListener<ICResponse<ICPost>> {
            override fun onSuccess(obj: ICResponse<ICPost>) {
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)

                if (obj.data?.privacy.isNullOrEmpty()) {
                    onErrorGetData.postValue(R.string.co_loi_xay_ra_vui_long_thu_lai)
                } else {
                    postDetail = obj.data
                    listPrivacy.addAll(obj.data!!.privacy!!)
                    onSetPrivacy.postValue(listPrivacy.find { it.selected } ?: listPrivacy[0])
                    onSetPost.postValue(obj.data)
                }
            }

            override fun onError(error: ICResponseCode?) {
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onErrorGetData.postValue(R.string.co_loi_xay_ra_vui_long_thu_lai)
            }
        })
    }

    fun createOrUpdate(privacyID: Long?, content: String, productID: Long?, listFile: MutableList<String>) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onShowMessage.postValue(ICheckApplication.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        if (content.isEmpty() && productID == null && listFile.isEmpty()) {
            onShowMessage.postValue(ICheckApplication.getString(R.string.vui_long_nhap_noi_dung))
            return
        }

        onStatus.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)
        listImage.clear()

        if (listFile.isNotEmpty()) {
            uploadImage(privacyID, content, productID, listFile)
        } else {
            if (postDetail?.id == null) {
                createPost(privacyID, content, productID)
            } else {
                updatePost(postDetail!!.id, privacyID, content, productID)
            }
        }
    }

    private fun uploadImage(privacyID: Long?, content: String, productID: Long?, listFile: MutableList<String>) {
        viewModelScope.launch {
            val listApi = mutableListOf<Deferred<ICResponse<UploadResponse>>>()
            listFile.forEach {
                if (it.startsWith("http")) {
                    listImage.add(it)
                } else {
                    listApi.add(async {
                        ImageHelper.uploadMediaV2(File(it))
                    })
                }
            }

            for (call in listApi) {
                try {
                    val response = call.await()
                    if (!response.data?.src.isNullOrEmpty()) {
                        listImage.add(response.data?.src!!)
                    }
                } catch (e: Exception) {
                }
            }

            if (postDetail?.id == null) {
                createPost(privacyID, content, productID)
            } else {
                updatePost(postDetail!!.id, privacyID, content, productID)
            }
        }
    }

    private fun createPost(privacyID: Long?, content: String, productID: Long?) {
        val privacy = listPrivacy.find { it.privacyElementId == privacyID } ?: listPrivacy[0]

        repository.createPost(pageId, privacy, content, productID, listImage, object : ICNewApiListener<ICResponse<ICPost>> {
            override fun onSuccess(obj: ICResponse<ICPost>) {
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onCreatedPost.postValue(obj.data)
            }

            override fun onError(error: ICResponseCode?) {
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onShowMessage.postValue(ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    private fun updatePost(postID: Long, privacyID: Long?, content: String, productID: Long?) {
        val privacy = listPrivacy.find { it.privacyElementId == privacyID } ?: listPrivacy[0]

        repository.updatePost(postID, privacy, content, productID, listImage, object : ICNewApiListener<ICResponse<ICPost>> {
            override fun onSuccess(obj: ICResponse<ICPost>) {
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onCreatedPost.postValue(obj.data)
            }

            override fun onError(error: ICResponseCode?) {
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onShowMessage.postValue(ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }
}