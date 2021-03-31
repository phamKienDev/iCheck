package vn.icheck.android.loyalty.screen.loyalty_customers.history.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import vn.icheck.android.loyalty.model.LoyaltyGiftItem
import vn.icheck.android.loyalty.network.ICNetworkAPI

class GiftShopDataSource(private val icNetworkAPI: ICNetworkAPI, private val shopId:Long, val filter:String?, val count: MutableLiveData<Int>) : PagingSource<Int, LoyaltyGiftItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LoyaltyGiftItem> {
        return try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key ?: 0

            val response = if (filter.isNullOrEmpty()) {
                icNetworkAPI.getLoyaltyGiftShop(shopId, nextPageNumber, 10)
            } else {
                icNetworkAPI.getLoyaltyGiftShop(shopId, nextPageNumber, 10, filter)
            }
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

    override fun getRefreshKey(state: PagingState<Int, LoyaltyGiftItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}