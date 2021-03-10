package vn.icheck.android.screen.user.search_home.result

import vn.icheck.android.base.viewmodel.BaseViewModel
import vn.icheck.android.network.feature.search.SearchInteractor
import vn.icheck.android.network.feature.user.UserInteractor

class SearchResultViewModel : BaseViewModel() {
    private val searchInteractor = SearchInteractor()
    private val userInteractor = UserInteractor()

    fun getProduct(key: String) = request {
        searchInteractor.getDataProduct(10, 0, key, 0, null, null)
    }

    fun getReview(key: String) = request {
        searchInteractor.getDataReview(3, 0, key, false, null, null)
    }

    fun getUser(key: String) = request {
        userInteractor.searchUser(0, 10, key, null, null, null, null)
    }


    fun getPage(key: String) = request {
        searchInteractor.getDataPage(3, 0, key, false, null, null)
    }
}