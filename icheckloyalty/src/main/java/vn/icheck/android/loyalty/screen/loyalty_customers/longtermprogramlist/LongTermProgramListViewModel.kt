package vn.icheck.android.loyalty.screen.loyalty_customers.longtermprogramlist

import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.network.APIConstants
import vn.icheck.android.loyalty.base.BaseViewModel
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.helper.NetworkHelper
import vn.icheck.android.loyalty.model.ICKBaseResponse
import vn.icheck.android.loyalty.model.ICKListResponse
import vn.icheck.android.loyalty.model.ICKLongTermProgram
import vn.icheck.android.loyalty.model.ICKResponse
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.repository.LoyaltyCustomersRepository

class LongTermProgramListViewModel : BaseViewModel<ICKLongTermProgram>() {
    private val repository = LoyaltyCustomersRepository()

    fun getLongTermProgramList(isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            checkError(false)
            return
        }

        if (!isLoadMore) {
            offset = 0
            repository.dispose()
        }

        repository.getLongTermProgramList(offset, object : ICApiListener<ICKResponse<ICKListResponse<ICKLongTermProgram>>> {
            override fun onSuccess(obj: ICKResponse<ICKListResponse<ICKLongTermProgram>>) {
                offset += APIConstants.LIMIT
                if (!isLoadMore) {
                    if (!obj.data?.rows.isNullOrEmpty()) {
                        onSetData.postValue(obj.data?.rows)
                    } else {
                        setErrorEmpty(R.drawable.ic_error_emty_history_topup, "Hiện tại bạn chưa là thành viên của chương trình nào", "", "", 0, R.color.white)
                    }
                } else {
                    onAddData.postValue(obj.data?.rows ?: mutableListOf())
                }
            }

            override fun onError(error: ICKBaseResponse?) {
                checkError(true, error?.message)
            }
        })
    }
}