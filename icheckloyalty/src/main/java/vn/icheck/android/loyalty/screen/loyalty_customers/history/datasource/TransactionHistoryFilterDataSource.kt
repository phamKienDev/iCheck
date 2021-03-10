package vn.icheck.android.loyalty.screen.loyalty_customers.history.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import vn.icheck.android.loyalty.model.TransactionHistoryResponse
import vn.icheck.android.loyalty.network.ICNetworkAPI

class TransactionHistoryFilterDataSource (private val icNetworkAPI: ICNetworkAPI, val userId:Long, val count: MutableLiveData<Int>, val query: HashMap<String ,Any>) : PagingSource<Int, TransactionHistoryResponse>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TransactionHistoryResponse> {
        return try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key ?: 0
            val response = icNetworkAPI.getTransactionHistory(userId, nextPageNumber, 10, query)
            if (nextPageNumber == 0) {
                count.postValue(response.data?.count)
            }
            LoadResult.Page(
                    data = response.data?.rows ?: arrayListOf(),
                    prevKey = null, // Only paging forward.
                    nextKey = if(!response.data?.rows.isNullOrEmpty()) nextPageNumber + 10 else null
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}