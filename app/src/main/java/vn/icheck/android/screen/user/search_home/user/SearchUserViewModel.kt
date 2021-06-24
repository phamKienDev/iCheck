package vn.icheck.android.screen.user.search_home.user

import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.viewmodel.BaseViewModel
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.feature.user.UserInteractor
import vn.icheck.android.network.models.ICProvince

class SearchUserViewModel : BaseViewModel() {
    private val interaction = UserInteractor()

    private var listGender = mutableListOf<String>()
    private var listCity = mutableListOf<ICProvince>()

    val getListCity: MutableList<ICProvince>
        get() {
            return listCity
        }

    fun setCity(city: MutableList<ICProvince>?) {
        listCity.clear()
        listCity.addAll(city ?: mutableListOf())
    }

    val getGender: MutableList<String>
        get() {
            return listGender
        }

    fun setGender(gender: MutableList<String>?) {
        this.listGender.clear()
        if ((gender ?: mutableListOf()).size != 3) {
            this.listGender.addAll(gender ?: mutableListOf())
        }
    }

    fun getData(filter: String, offset:Int)=request {
        val listCityId = mutableListOf<Long>()

        if (!listCity.isNullOrEmpty()) {
            for (item in listCity) {
                if (item.id != -1L) {
                    listCityId.add(item.id)
                }
            }
        }

        val listGenderId = mutableListOf<Int>()
        if (listGender.isNullOrEmpty() || listGender.size != 3) {
            for (item in listGender) {
                when (item) {
                    getString(R.string.nam) -> {
                        listGenderId.add(1)
                    }
                    getString(R.string.nu) -> {
                        listGenderId.add(2)
                    }
                    else -> {
                        listGenderId.add(3)
                    }
                }
            }
        }

        interaction.searchUser(offset, APIConstants.LIMIT, filter, listCityId, null, null, listGenderId)
    }
}
