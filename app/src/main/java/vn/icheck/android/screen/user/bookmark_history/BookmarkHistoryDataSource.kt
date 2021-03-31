package vn.icheck.android.screen.user.bookmark_history

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import vn.icheck.android.model.bookmark.BookmarkHistoryResponse
import vn.icheck.android.network.api.ICKApi
import javax.inject.Inject

class BookmarkHistoryDataSource @Inject constructor(val ickApi: ICKApi):PagingSource<Int, BookmarkHistoryResponse>() {
    var filterString:String = ""
    var count = MutableLiveData<Int?>()
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BookmarkHistoryResponse> {
        return try {
            val nextPageNumber = params.key ?: 0

            val response = if(filterString.isEmpty()) ickApi.getBookMarkHistory(nextPageNumber) else ickApi.getBookMarkHistory(nextPageNumber, filterString)
            if (nextPageNumber == 0) {
                count.postValue(response.data?.count)
            }
            LoadResult.Page(
                    data = response.data?.rows ?: arrayListOf(),
                    prevKey = null, // Only paging forward.
                    nextKey = if (!response.data?.rows.isNullOrEmpty()) nextPageNumber + 10 else null)

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, BookmarkHistoryResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}