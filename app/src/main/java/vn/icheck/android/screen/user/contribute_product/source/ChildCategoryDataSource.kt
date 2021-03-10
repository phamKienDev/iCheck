package vn.icheck.android.screen.user.contribute_product.source

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import vn.icheck.android.model.category.CategoryItem
import vn.icheck.android.network.api.ICKApi
import javax.inject.Inject

class ChildCategoryDataSource @Inject constructor(val ickApi: ICKApi) : PagingSource<Int, CategoryItem>() {
    var parentId: Long = 0L
    var final = MutableLiveData<Long>()
    var level = 2
    var onTouch = false
    var filterString: String = ""
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CategoryItem> {
        return try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key ?: 0
            val response =
                    if (!onTouch) {
                        if (parentId == 0L) {
                            if (filterString.isNotEmpty()) {
                                ickApi.getChildCategories(params.loadSize, nextPageNumber, filterString)
                            } else {
                                ickApi.getChildCategories(params.loadSize, nextPageNumber, level = 1)
                            }
                        } else {
                            if (filterString.isNotEmpty()) {
                                ickApi.getChildCategories(parentId, params.loadSize, nextPageNumber, filterString = filterString)
                            } else {
                                ickApi.getChildCategories(parentId, params.loadSize, nextPageNumber)
                            }
                        }
                    } else {
                        onTouch = false
                        if (filterString.isEmpty()) {
                            ickApi.getChildCategories(parentId, params.loadSize, nextPageNumber)
                        } else {
                            ickApi.getChildCategories(params.loadSize, nextPageNumber, filterString)
                        }
                    }

            if (nextPageNumber == 0 && response.data?.rows.isNullOrEmpty() && filterString.isEmpty()) {
                final.postValue(parentId)
            }
            LoadResult.Page(
                    data = response.data?.rows ?: arrayListOf(),
                    prevKey = null, // Only paging forward.
                    nextKey = if (!response.data?.rows.isNullOrEmpty()) nextPageNumber + params.loadSize else null
            )
        } catch (e: Exception) {
            // expected error (such as a network failure).
            LoadResult.Error(e)
        }
    }
}