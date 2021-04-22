package vn.icheck.android.screen.user.social_chat

import android.content.SharedPreferences
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import vn.icheck.android.network.model.bookmark.BookmarkHistoryResponse
import vn.icheck.android.network.api.ICKApi
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.models.wall.RowsItem
import vn.icheck.android.util.ick.logError

class SocialChatViewModel @ViewModelInject constructor(private val ickApi: ICKApi, private val sharedPreferences: SharedPreferences, @Assisted savedStateHandle: SavedStateHandle): ViewModel() {
    val userFriendSource = UserFriendSource(ickApi)
    fun getSyncPhoneBook():Boolean {
        return sharedPreferences.getBoolean("synced_phone_book", false)
    }

    fun setSyncPhoneBook() {
        sharedPreferences.edit().putBoolean("synced_phone_book", true).apply()
    }

    fun syncPhoneBook(arr:List<String>):LiveData<ICResponse<*>> {
        return liveData {
            try {
                val request = hashMapOf<String, Any?>()
                request["phoneList"] = arr
                request["isAppend"] = true
                val response = ickApi.syncContacts(request)
                emit(response)
            } catch (e: Exception) {
                logError(e)
            }
        }

    }

    fun getListFriend(): Flow<PagingData<RowsItem>> {
        return Pager(PagingConfig(pageSize = 10)) {
            userFriendSource
        }.flow.cachedIn(viewModelScope)
    }
}