package vn.icheck.android.screen.user.social_chat

import androidx.paging.DataSource
import androidx.paging.PagingSource
import androidx.paging.PagingState
import vn.icheck.android.network.api.ICKApi
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.wall.RowsItem

class UserFriendSource(val ickApi: ICKApi):PagingSource<Int, RowsItem>() {
    val id = SessionManager.session.user?.id ?: 0L
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RowsItem> {
        return try {
            val nextPageNumber = params.key ?: 0
            val response = ickApi.getUserFriendList(id, 10, nextPageNumber)
            LoadResult.Page(
                    data = response.data?.rows ?: arrayListOf(),
                    prevKey = null, // Only paging forward.
                    nextKey = if (!response.data?.rows.isNullOrEmpty()) nextPageNumber + 10 else null)

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, RowsItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}