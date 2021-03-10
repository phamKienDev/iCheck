package vn.icheck.android.activities.chat.setting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import vn.icheck.android.ICheckApplication
import vn.icheck.android.fragments.message.model.MessageDao
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICUserId
import vn.icheck.android.room.database.AppDatabase

class ChatV2SettingFactory(private val roomId: String,private val type: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatV2SettingViewModel(roomId, type) as T
    }
}

class ChatV2SettingViewModel(private val roomId: String, private val type: Int) : ViewModel() {

    val notifyLiveData = MutableLiveData<Boolean>()
    val leaveChatLd = MutableLiveData<Int>()
    val listUserById = MutableLiveData<List<ICUserId>>()
    val addMembers = MutableLiveData<Boolean>()
    val messageDao: MessageDao = AppDatabase.getDatabase(ICheckApplication.getInstance())
            .messageDao()
    private val currentUser: String = "i-${SessionManager.session.user?.id}"

    init {
        FirebaseDatabase.getInstance().getReference("room-metadata/$roomId/members/$currentUser/notification/turnOn")
                .addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(p0: DataSnapshot) {
                        try {
                            notifyLiveData.postValue(p0.value as Boolean)
                        } catch (e: Exception) {
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {
                    }

                })
        if (type == ChatV2SettingActivity.GROUP) {
            FirebaseDatabase.getInstance()
                    .getReference("room-metadata/$roomId/members")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            viewModelScope.launch {
                                try {
                                    val listResult = arrayListOf<ICUserId>()
                                    val listJob = arrayListOf<Deferred<Any?>>()
                                    for (item in p0.children) {
                                        listJob.add(
                                                async {
                                                    val result = ICNetworkClient.getSimpleApiClient()
                                                            .getUserById(item.key?.replace("i-","")?.toLong())
                                                    listResult.add(result)
                                                })
                                    }
                                    listJob.forEach {
                                        it.await()
                                    }
                                    listUserById.postValue(listResult)
                                } catch (e: Exception) {
                                }
                            }
                        }
                    })
            FirebaseDatabase.getInstance()
                    .getReference("room-metadata/$roomId/addMembers")
                    .addValueEventListener(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            addMembers.postValue(p0.getValue(Boolean::class.java))
                        }
                    })
        }
    }

    fun changeAddMem(isChecked: Boolean) {
        FirebaseDatabase.getInstance()
                .getReference("room-metadata/$roomId/addMembers")
                .setValue(isChecked)
    }

    fun setNotify(isChecked: Boolean) {
        FirebaseDatabase.getInstance().getReference("room-metadata/$roomId/members/$currentUser/notification/turnOn")
                .setValue(isChecked)
    }

    fun leaveChat() {
        viewModelScope.launch {
            try {
                if(type == ChatV2SettingActivity.GROUP) {
                    ICNetworkClient.getSimpleChat().leaveGroupRoomSp(roomId)
                }
                messageDao.deleteRow(roomId)
                FirebaseDatabase.getInstance()
                        .getReference("room-users/$currentUser/$roomId")
                        .removeValue(object : DatabaseReference.CompletionListener {
                            override fun onComplete(p0: DatabaseError?, p1: DatabaseReference) {
                                leaveChatLd.postValue(1)
                            }
                        })
            } catch (e: Exception) {
            }
        }

    }
}