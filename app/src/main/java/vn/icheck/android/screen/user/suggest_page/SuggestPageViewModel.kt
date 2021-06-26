package vn.icheck.android.screen.user.suggest_page

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.suggest.SuggestInteractor
import vn.icheck.android.network.models.ICSuggestPage
import vn.icheck.android.network.models.ICUser

class SuggestPageViewModel : ViewModel() {
    val onError = MutableLiveData<ICError>()
    val onSuggestPage = MutableLiveData<MutableList<Any>>()
    val onSuggestFriend = MutableLiveData<ICListResponse<ICUser>>()
    val onPostSuccess = MutableLiveData<Int>()

    val interactor = SuggestInteractor()
    var offset = 0

    var listCaterogies: MutableList<Int>? = null

    fun getData(intent: Intent) {
        listCaterogies = try {
            intent.getIntegerArrayListExtra(Constant.DATA_1)
        } catch (e: Exception) {
            null
        }

        if (!SessionManager.session.user?.linkedFbId.isNullOrEmpty()) {
            getSuggestFriend()
        }
        getSuggestPage()
    }

    fun getSuggestPage(isLoadmore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        if (!isLoadmore) {
            offset = 0
        }

        interactor.getSuggestPage(offset, listCaterogies, object : ICNewApiListener<ICResponse<ICListResponse<ICSuggestPage>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICSuggestPage>>) {
                offset += APIConstants.LIMIT

                val list = mutableListOf<Any>()
                if (!isLoadmore) {
                    list.add("")
                }
                list.addAll(obj.data!!.rows)

                onSuggestPage.postValue(list)

            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
            }
        })
    }

    fun getSuggestFriend() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        SessionManager.session.user?.let { it ->
            interactor.getSuggestFriend(it.id, offset, 30, null, object : ICNewApiListener<ICResponse<ICListResponse<ICUser>>> {
                override fun onSuccess(obj: ICResponse<ICListResponse<ICUser>>) {
                    obj.data?.let {
                        onSuggestFriend.postValue(it)
                    }
                }


                override fun onError(error: ICResponseCode?) {
                    onError.postValue(ICError(R.drawable.ic_error_request, getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
                }
            })
        }
    }

    fun postFavouriteTopic(listPage: MutableList<ICSuggestPage>?) {
        val listId = mutableListOf<Int>()
        listPage?.forEach { page ->
            page.id?.let {
                listId.add(it)
            }
        }

        viewModelScope.launch {
            try {
                val listApi = mutableListOf<Deferred<Any?>>()
                listApi.add(async {
                    try {
                        interactor.postFavouriteTopic(listCaterogies)
                    } catch (e: Exception) {
                    }
                })
                listApi.add(async {
                    try {
                        interactor.postFollowPage(listId)
                    } catch (e: Exception) {
                    }
                })
                listApi.awaitAll()
                onPostSuccess.postValue(1)

            } catch (e: Exception) {
                onPostSuccess.postValue(1)
            }
        }
    }


}