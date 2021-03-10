package vn.icheck.android.activities.chat.group

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICFollowing

class CreateGroupViewModel:ViewModel() {

    val friendLiveData = MutableLiveData<ICFollowing>()
    val listMember = MutableLiveData<List<MemberView>>()
    val groupName = MutableLiveData<String>()
    val groupAvatar = MutableLiveData<String>()
    init {
        viewModelScope.launch {
            try {
                val response = ICNetworkClient.getSimpleApiClient().getFollow("user",
                        SessionManager.session.user?.id)
                friendLiveData.postValue(response)
            } catch (e: Exception) {
            }
        }
    }
}