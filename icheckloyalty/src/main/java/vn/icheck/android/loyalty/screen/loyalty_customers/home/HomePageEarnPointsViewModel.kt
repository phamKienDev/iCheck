package vn.icheck.android.loyalty.screen.loyalty_customers.home

import androidx.lifecycle.MutableLiveData
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.BaseViewModel
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.helper.NetworkHelper
import vn.icheck.android.loyalty.model.*
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.repository.LoyaltyCustomersRepository

class HomePageEarnPointsViewModel : BaseViewModel<Any>() {
    private val repositoryHeader = LoyaltyCustomersRepository()
    private val repository = LoyaltyCustomersRepository()

    val setHeader = MutableLiveData<ICKLongTermProgram>()
    val setItem = MutableLiveData<MutableList<ICKTransactionHistory>>()

    fun getHeaderHomePage() {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            checkError(false)
            return
        }

        repositoryHeader.dispose()

        repositoryHeader.getHeaderHomePage(collectionID, object : ICApiListener<ICKResponse<ICKLongTermProgram>> {
            override fun onSuccess(obj: ICKResponse<ICKLongTermProgram>) {
                obj.data?.let {
                    setHeader.postValue(it)
                }
            }

            override fun onError(error: ICKBaseResponse?) {
                checkError(true, error?.message)
            }
        })
    }

    fun getTransactionHistory() {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            checkError(false)
            return
        }

        repository.dispose()

        repository.getTransactionHistory(collectionID, offset, 7, object : ICApiListener<ICKResponse<ICKListResponse<ICKTransactionHistory>>> {
            override fun onSuccess(obj: ICKResponse<ICKListResponse<ICKTransactionHistory>>) {
                if (!obj.data?.rows.isNullOrEmpty()) {
                    setItem.postValue(obj.data?.rows)
                } else {
                    setErrorEmpty(R.drawable.ic_loyalty_point_empty2,
                        vn.icheck.android.ichecklibs.util.getString(R.string.chua_co_lich_su_giao_dich), "", "", 0, R.color.white)
                }
            }

            override fun onError(error: ICKBaseResponse?) {
                checkError(true, error?.message)
            }
        })
    }
}