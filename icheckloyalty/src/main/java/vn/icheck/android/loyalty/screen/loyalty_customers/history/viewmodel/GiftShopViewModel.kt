package vn.icheck.android.loyalty.screen.loyalty_customers.history.viewmodel

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import vn.icheck.android.loyalty.model.*
import vn.icheck.android.loyalty.network.ICNetworkClient
import vn.icheck.android.loyalty.screen.loyalty_customers.history.datasource.TransactionHistoryFilterDataSource
import vn.icheck.android.loyalty.screen.loyalty_customers.history.datasource.TransactionsHistoryDataSource
import vn.icheck.android.loyalty.screen.loyalty_customers.history.datasource.GiftShopDataSource
import java.util.*

class GiftShopViewModel : ViewModel() {

    var calendar = Calendar.getInstance()
    var checked = MutableLiveData<Boolean>()
    val arrFilter = arrayListOf<String>("Tất cả")
    val icNetworkAPI = ICNetworkClient.getApiClientLoyalty()
    var userId: Long = 0L
    val arrayGiftShop = arrayListOf<LoyaltyGiftItem>()
    val countLiveData = MutableLiveData<Int>()
    val querymap = hashMapOf<String, Any>()
    var selectedMsp: TopupServices.Service? = null
    var idGift:Long = 0L

    fun addFilter(filters: List<String>) {
        arrFilter.clear()
        arrFilter.addAll(filters)
    }

    fun resetFilter() {
        arrFilter.clear()
        arrFilter.add("Tất cả")
    }

    fun setOnlyFilter(filter: String) {
        if (arrFilter.size == 1) {
            arrFilter.clear()
            arrFilter.add(filter)
        } else {
            arrFilter[0] = filter
        }
    }

    fun setTypeAll() {
        querymap.remove("type")
    }

    fun setTypeGrant() {
        querymap.put("type","GRANT")
    }

    fun setTypeSpend() {
        querymap.put("type","SPEND")
    }

    fun setTime() {
        val startDay = "01"
        val month = calendar.get(Calendar.MONTH)
        val months = if (month + 1 < 10) {
            "0${month}"
        } else {
            "$month"
        }
        val year = calendar.get(Calendar.YEAR)
        val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        querymap.put("from","$year-$months-$startDay")
        querymap.put("to","$year-$months-$lastDay")
    }

    fun getGiftsShopFlow(filter:String?): Flow<PagingData<LoyaltyGiftItem>> {
        return Pager(PagingConfig(10)) {
            GiftShopDataSource(icNetworkAPI, userId, filter, countLiveData)
        }.flow.cachedIn(viewModelScope)
    }

    fun getTransactionsHistory(): Flow<PagingData<TransactionHistoryResponse>> {
        return Pager(PagingConfig(10)) {
            TransactionsHistoryDataSource(icNetworkAPI, userId, countLiveData)
        }.flow.cachedIn(viewModelScope)
    }

    fun filter(): Flow<PagingData<TransactionHistoryResponse>> {
        return Pager(PagingConfig(10)) {
            TransactionHistoryFilterDataSource(icNetworkAPI, userId, countLiveData, querymap)
        }.flow.cachedIn(viewModelScope)
    }

    fun addOnlyFilter(filter: String) {
        if (arrFilter.size == 1) {
            arrFilter.add(filter)
        } else {
            arrFilter[1] = filter
        }
    }
}