package vn.icheck.android.network.feature.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import vn.icheck.android.network.models.ICFollowing
import vn.icheck.android.network.models.ICSyncContacts
import vn.icheck.android.network.models.ICUserId

class ContactsViewModel(application: Application): AndroidViewModel(application) {

    private val contactsRepository: ContactsRepository = ContactsRepository(application)
    val mError = MutableLiveData<Throwable>()
    val mContactCall = MutableLiveData<ICSyncContacts>()
    val mContacts = MutableLiveData<List<String>>()
    val mUserCall = MutableLiveData<ICUserId>()
    val mUserFollowing = MutableLiveData<ICFollowing>()

    fun getContacts(){
       contactsRepository.getAllContacts(mContacts)
    }

    fun callSyncContacts(phoneMap: HashMap<String, List<String>>){
        contactsRepository.postSyncContacts(phoneMap, mError, mContactCall)
    }

    fun getUserId(id: Long) {
        contactsRepository.getUserId(id, mError, mUserCall)
    }

    fun getUserFollowing(id: Long) {
        contactsRepository.getUserFollowing(id, mError, mUserFollowing)
    }
}