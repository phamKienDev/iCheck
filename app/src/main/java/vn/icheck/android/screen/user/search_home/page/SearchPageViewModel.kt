package vn.icheck.android.screen.user.search_home.page

import vn.icheck.android.base.viewmodel.BaseViewModel
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.feature.search.SearchInteractor
import vn.icheck.android.network.models.ICCategorySearch
import vn.icheck.android.network.models.ICProvince

class SearchPageViewModel : BaseViewModel() {
    val interactor = SearchInteractor()

    private var listCity = mutableListOf<ICProvince>()
    private var listCategory = mutableListOf<ICCategorySearch>()
    private var isVerified: Boolean = false

    val getListCity: MutableList<ICProvince>
        get() = listCity

    fun setCity(city: MutableList<ICProvince>?) {
        listCity.clear()
        listCity.addAll(city ?: mutableListOf())
    }

    val getListCategory: MutableList<ICCategorySearch>
        get() = listCategory

    fun setListCategory(category: MutableList<ICCategorySearch>?) {
        listCategory.clear()
        listCategory.addAll(category ?: mutableListOf())
    }

    val getIsVerified: Boolean
        get() = isVerified

    fun setIsVerified(isVerified: Boolean) {
        this.isVerified = isVerified
    }

    fun getData(key: String,offset:Int) = request {
        val listCityId = mutableListOf<Long>()
        var categoryId: Long? = null

        if (!listCity.isNullOrEmpty()) {
            for (item in listCity) {
                if (item.id != -1L) {
                    listCityId.add(item.id)
                }
            }
        }

        if (!listCategory.isNullOrEmpty()) {
            categoryId = listCategory.last().id
        }

        interactor.getDataPage(APIConstants.LIMIT, offset, key, isVerified, listCityId, categoryId)
    }
}