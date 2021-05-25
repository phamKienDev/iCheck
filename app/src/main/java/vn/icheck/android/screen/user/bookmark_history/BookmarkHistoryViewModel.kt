package vn.icheck.android.screen.user.bookmark_history

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import vn.icheck.android.network.api.ICKApi
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ListTypeResponse
import vn.icheck.android.network.model.bookmark.BookmarkHistoryResponse
import vn.icheck.android.util.ick.logError

class BookmarkHistoryViewModel @ViewModelInject constructor(val ickApi: ICKApi, val bookmarkHistoryDataSource: BookmarkHistoryDataSource, @Assisted savedStateHandle: SavedStateHandle):ViewModel() {
    val countLiveData = MutableLiveData<Int?>()

    fun getBookmarks(): Flow<PagingData<BookmarkHistoryResponse>> {
        return Pager(PagingConfig(pageSize = 10, prefetchDistance = 10)) {
            bookmarkHistoryDataSource.apply {
                this.count = countLiveData
            }
        }.flow.cachedIn(viewModelScope)
    }

    fun filter(filter: String): Flow<PagingData<BookmarkHistoryResponse>>  {
        return Pager(PagingConfig(pageSize = 10, prefetchDistance = 10)) {
            bookmarkHistoryDataSource.apply {
                this.count = countLiveData
                this.filterString = filter
            }
        }.flow.cachedIn(viewModelScope)
    }

    fun bookmark(productId:Long) :LiveData<ICResponse<*>>{
        return liveData{
            try {
                val response = ickApi.bookmarkProduct(productId)
                emit(response)
            } catch (e: Exception) {
                logError(e)
            }
        }
    }

    fun deleteBookmark(productId:Long) :LiveData<ICResponse<*>>{
        return liveData{
            try {
                val response = ickApi.deleteBookMarkProduct(productId)
                emit(response)
            } catch (e: Exception) {
                logError(e)
            }
        }
    }
}