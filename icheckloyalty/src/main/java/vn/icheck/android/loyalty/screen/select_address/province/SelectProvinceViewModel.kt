package vn.icheck.android.loyalty.screen.select_address.province

import androidx.lifecycle.MutableLiveData
import vn.icheck.android.ichecklibs.util.RStringUtils.rText
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.BaseViewModel
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.helper.NetworkHelper
import vn.icheck.android.loyalty.helper.TextHelper
import vn.icheck.android.loyalty.model.ICKBaseResponse
import vn.icheck.android.loyalty.model.ICKListResponse
import vn.icheck.android.loyalty.model.ICProvince
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.repository.AddressRepository

class SelectProvinceViewModel : BaseViewModel<ICProvince>() {
    private val repository = AddressRepository()

    val onEmptyString = MutableLiveData<String>()

    var limit = 30

    fun getListProvince(isLoadMore: Boolean) {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            checkError(false)
            return
        }

        if (!isLoadMore) {
            offset = 0
            repository.dispose()
        }

        repository.getListProvince(offset, limit, object : ICApiListener<ICKListResponse<ICProvince>> {
            override fun onSuccess(obj: ICKListResponse<ICProvince>) {

                for (it in obj.rows) {
                    it.searchKey = TextHelper.unicodeToKoDauLowerCase(it.name)
                }

                if (!isLoadMore) {
                    if (!obj.rows.isNullOrEmpty()) {
                        onSetData.postValue(obj.rows)
                    } else {
                        onEmptyString.postValue(rText(R.string.khong_co_du_lieu))
                    }
                } else {
                    onAddData.postValue(obj.rows ?: mutableListOf())
                }

                if (obj.rows.size >= limit) {
                    offset += limit
                    getListProvince(true)
                }
            }

            override fun onError(error: ICKBaseResponse?) {
                checkError(true, error?.message)
            }
        })
    }
}