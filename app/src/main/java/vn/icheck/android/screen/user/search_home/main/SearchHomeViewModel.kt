package vn.icheck.android.screen.user.search_home.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.RelationshipHelper
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.page.PageRepository
import vn.icheck.android.network.feature.search.SearchInteractor
import vn.icheck.android.network.models.ICCategorySearch
import vn.icheck.android.network.models.ICRelationshipsInformation

class SearchHomeViewModel : ViewModel() {
    private val searchInteractor = SearchInteractor()

    val onRecentSearch = MutableLiveData<MutableList<String>>()
    val onPopularSearch = MutableLiveData<MutableList<String>>()
    val onRelatedSearch = MutableLiveData<MutableList<String>>()
    val onDeleteRecentSearch = MutableLiveData<Boolean>()
    val onStatus = MutableLiveData<ICMessageEvent.Type>()

    val onError = MutableLiveData<ICError>()
    var listPopular = mutableListOf<String>()
    var listRecent = mutableListOf<String>()


    fun getData() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        getID()
        getPopularSearch()
    }

    fun getRecentSearch() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            return
        }

        searchInteractor.getRecentSearch(object : ICNewApiListener<ICResponse<ICListResponse<ICCategorySearch>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICCategorySearch>>) {
                val list = mutableListOf<String>()
                for (i in 0 until if (obj.data!!.rows.size > 7) {
                    7
                } else {
                    obj.data!!.rows.size
                }) {
                    obj.data!!.rows[i].content?.let {
                        list.add(it)
                    }
                }
                if (list.isNotEmpty()) {
                    listRecent.clear()
                    listRecent.addAll(list)
                    onRecentSearch.postValue(list)
                }
            }

            override fun onError(error: ICResponseCode?) {
            }

        })
    }

    fun deleteRecentSearch() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), " "))
            return
        }
        onStatus.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        searchInteractor.deleteRecentSearch(object : ICNewApiListener<ICResponse<Boolean>> {
            override fun onSuccess(obj: ICResponse<Boolean>) {
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                listRecent.clear()
                onDeleteRecentSearch.postValue(true)
            }

            override fun onError(error: ICResponseCode?) {
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onError.postValue(ICError(R.drawable.ic_error_request, if (error?.message.isNullOrEmpty()) {
                    getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                } else {
                    error?.message
                }, " "))
            }
        })
    }

    fun getAutoSearch(key: String) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), " "))
            return
        }

        searchInteractor.getAutoSearch(key, object : ICNewApiListener<ICResponse<ICListResponse<String?>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<String?>>) {
                val list = mutableListOf<String>()
                obj.data?.rows?.forEach {
                    if (!it.isNullOrEmpty()) {
                        list.add(it)
                    }
                }
                list.add(key)
                onRelatedSearch.postValue(list)
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
            }

        })
    }

    private fun getPopularSearch() {
        searchInteractor.getPopularSearch(object : ICNewApiListener<ICResponse<ICListResponse<ICCategorySearch>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICCategorySearch>>) {
                if (!obj.data?.rows.isNullOrEmpty()) {
                    val list = mutableListOf<String>()
                    for (i in 0 until if (obj.data!!.rows.size > 7) {
                        7
                    } else {
                        obj.data!!.rows.size
                    }) {
                        obj.data!!.rows[i].content?.let {
                            list.add(it)
                        }
                    }
                    if (list.isNotEmpty()) {
                        listPopular.addAll(list)
                        onPopularSearch.postValue(list)
                    }
                }

            }

            override fun onError(error: ICResponseCode?) {

            }
        })
    }

    fun getID() {
        PageRepository().getRelationshipCurrentUser(object : ICNewApiListener<ICResponse<ICRelationshipsInformation>> {
            override fun onSuccess(obj: ICResponse<ICRelationshipsInformation>) {
                if (obj.data != null) {
                    RelationshipHelper.saveData(obj.data!!)
                }
            }

            override fun onError(error: ICResponseCode?) {
                error.toString()
            }
        })
    }
}