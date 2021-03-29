package vn.icheck.android.screen.user.contribute_product.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import vn.icheck.android.model.category.CategoryItem
import vn.icheck.android.network.api.ICKApi
import javax.inject.Inject

class CategoryDataSource @Inject constructor(val ickApi: ICKApi):PagingSource<Int, CategoryItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CategoryItem> {
        return try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key ?: 0
            val response =  ickApi.getCategories(params.loadSize, nextPageNumber, 1)
            if (nextPageNumber < response.data?.count!!) LoadResult.Page(
                    data = response.data.rows!!,
                    prevKey = null, // Only paging forward.
                    nextKey = nextPageNumber + params.loadSize
            ) else {
                LoadResult.Error(Exception("out of index"))
            }
        } catch (e: Exception) {
            // Handle errors in this block and return LoadResult.Error if it is an
            // expected error (such as a network failure).
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CategoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}