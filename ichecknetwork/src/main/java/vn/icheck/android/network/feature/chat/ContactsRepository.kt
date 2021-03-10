package vn.icheck.android.network.feature.chat

import android.app.Application
import android.content.ContentResolver
import android.provider.ContactsContract
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.models.ICFollowing
import vn.icheck.android.network.models.ICSyncContacts
import vn.icheck.android.network.models.ICUserId
import java.util.*
import kotlin.collections.HashMap

class ContactsRepository(val application: Application) {

    companion object{
        val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        )
    }

    fun getAllContacts(contacts: MutableLiveData<List<String>>) {
        val contentResolver: ContentResolver = application.contentResolver
        val allContacts = ArrayList<String>()
        val cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null)
        if (cursor != null) {
            try {
                val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
//                val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                while (cursor.moveToNext()) {
//                    val name = cursor.getString(nameIndex)
                    val number = cursor.getString(numberIndex)
                    allContacts.add(number)
                }
            } finally {
                cursor.close()
            }
        }
        contacts.postValue(allContacts)
    }

    fun postSyncContacts(phoneMap: HashMap<String, List<String>>, error: MutableLiveData<Throwable>, response: MutableLiveData<ICSyncContacts>) {
        ICNetworkClient.getApiClient().syncContacts(phoneMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ICSyncContacts> {
                    override fun onComplete() {
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(t: ICSyncContacts) {
                        response.postValue(t)
                    }

                    override fun onError(e: Throwable) {
                        error.postValue(e)
                    }
                })
    }

    fun getUserId(id: Long, error: MutableLiveData<Throwable>, response: MutableLiveData<ICUserId>) {
        ICNetworkClient.getApiClient().getUser(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ICUserId>{
                    override fun onComplete() {
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(t: ICUserId) {
                        response.postValue(t)
                    }

                    override fun onError(e: Throwable) {
                        error.postValue(e)
                    }
                })
    }

    fun getUserFollowing(id: Long, error: MutableLiveData<Throwable>, response: MutableLiveData<ICFollowing>) {
        ICNetworkClient.getApiClient().getFollowing("user",id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ICFollowing>{
                    override fun onComplete() {
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(t: ICFollowing) {
                        response.postValue(t)
                    }

                    override fun onError(e: Throwable) {
                        error.postValue(e)
                    }
                })
    }
}