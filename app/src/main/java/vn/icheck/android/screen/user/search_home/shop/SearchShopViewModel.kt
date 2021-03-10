package vn.icheck.android.screen.user.search_home.shop

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.search.SearchInteractor
import vn.icheck.android.network.models.*

class SearchShopViewModel : ViewModel() {

    val onSetData = MutableLiveData<MutableList<ICShopQuery>>()
    val onAddData = MutableLiveData<MutableList<ICShopQuery>>()
    val onError = MutableLiveData<ICError>()

    val interactor = SearchInteractor()

    var offset = 0
    private var category = mutableListOf<ICCategorySearch>()
    private var isVerified: Boolean = false
    private var listCity = mutableListOf<ICProvince>()

    val getListCity: MutableList<ICProvince>?
        get() {
            return listCity
        }

    fun setCity(city: MutableList<ICProvince>?) {
        listCity.clear()
        listCity.addAll(city ?: mutableListOf())
    }

    val getCategory: MutableList<ICCategorySearch>?
        get() {
            return category
        }

    fun setCategory(category: MutableList<ICCategorySearch>?) {
        this.category.clear()
        this.category.addAll(category ?: mutableListOf())
    }

    val getIsVerified: Boolean
        get() {
            return isVerified
        }

    fun setIsVerified(isVerified: Boolean) {
        this.isVerified = isVerified
    }

    fun getData(key: String, isLoadmore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }
        if (!isLoadmore) {
            offset = 0
            interactor.dispose()
        }


        val listCityId = mutableListOf<Long>()
        var categoryId: Long? = null

        if (!listCity.isNullOrEmpty()) {
            for (item in listCity) {
                if (item.id != -1L) {
                    listCityId.add(item.id)
                }
            }
        }

        if (!category.isNullOrEmpty()) {
            categoryId = category.last().id
        }
        interactor.getDataShop(APIConstants.LIMIT, offset, key, isVerified, listCityId, categoryId, object : ICNewApiListener<ICResponse<ICListResponse<ICShopQuery>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICShopQuery>>) {
                offset += APIConstants.LIMIT
                if (!isLoadmore) {
                    onSetData.postValue(obj.data!!.rows)
                } else {
                    onAddData.postValue(obj.data!!.rows)
                }
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
            }
        })
    }
}