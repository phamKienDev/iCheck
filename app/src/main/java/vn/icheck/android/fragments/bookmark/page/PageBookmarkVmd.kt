package vn.icheck.android.fragments.bookmark.page

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.models.bookmark.ICBookmarkPage

class PageBookmarkVmd:ViewModel() {
    val icBookmarkPage = MutableLiveData<ICBookmarkPage>()
    var bookmark: ICBookmarkPage? = null
    val complete = MutableLiveData<Int>()

    init {
        viewModelScope.launch {
            try {
                val response = ICNetworkClient.getApiClient().bookmarkPages()
                icBookmarkPage.postValue(response)
                bookmark = response
                complete.postValue(1)
            } catch (e: Exception) {
            }
        }
    }

    fun getBookmark(){
        viewModelScope.launch {
            try {
                val response = ICNetworkClient.getApiClient().bookmarkPages()
                icBookmarkPage.postValue(response)
                bookmark = response
                complete.postValue(1)
            } catch (e: Exception) {
            }
        }
    }
}