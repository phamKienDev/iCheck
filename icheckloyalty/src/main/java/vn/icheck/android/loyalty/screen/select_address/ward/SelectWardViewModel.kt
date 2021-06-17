package vn.icheck.android.loyalty.screen.select_address.ward

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import vn.icheck.android.ichecklibs.util.RStringUtils.rText
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.BaseViewModel
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.helper.NetworkHelper
import vn.icheck.android.loyalty.helper.TextHelper
import vn.icheck.android.loyalty.model.ICKBaseResponse
import vn.icheck.android.loyalty.model.ICKListResponse
import vn.icheck.android.loyalty.model.ICWard
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.repository.AddressRepository

class SelectWardViewModel : BaseViewModel<ICWard>() {
    private val repository = AddressRepository()

    val onEmptyString = MutableLiveData<String>()

    private var districtID = -1

    private val limit = 30

    fun getDataIntent(intent: Intent?) {
        districtID = try {
            intent?.getIntExtra(ConstantsLoyalty.DATA_1, -1) ?: -1
        } catch (e: Exception) {
            -1
        }

        getListWard(false)
    }

    fun getListWard(isLoadMore: Boolean) {
        if (districtID == -1) {
            onEmptyString.postValue("ERROR")
            return
        }

        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            checkError(false)
            return
        }

        if (!isLoadMore) {
            offset = 0
            repository.dispose()
        }

        repository.getListWard(districtID, offset, limit, object : ICApiListener<ICKListResponse<ICWard>> {
            override fun onSuccess(obj: ICKListResponse<ICWard>) {

                for (it in obj.rows) {
                    it.searchKey = TextHelper.unicodeToKoDauLowerCase(it.name)
                }

                if (!isLoadMore) {
                    if (obj.rows.isNullOrEmpty()) {
                        onEmptyString.postValue(rText(R.string.chua_co_du_lieu))
                    } else {
                        onSetData.postValue(obj.rows)
                    }
                } else {
                    onAddData.postValue(obj.rows ?: mutableListOf())
                }

                if (obj.rows.size >= limit) {
                    offset += limit
                    getListWard(true)
                }
            }

            override fun onError(error: ICKBaseResponse?) {
                checkError(true, error?.message)
            }
        })
    }
}