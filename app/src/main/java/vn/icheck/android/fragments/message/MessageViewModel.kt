package vn.icheck.android.fragments.message

import androidx.lifecycle.*
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.fragments.message.model.*
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.room.database.AppDatabase

class MessageViewModel : ViewModel() {

    var currentUserId: Long? = SessionManager.session.user?.id
    val listData = arrayListOf<MsgModel>()
    val messageDao: MessageDao = AppDatabase.getDatabase(ICheckApplication.getInstance())
            .messageDao()
    var newListMsg: LiveData<PagedList<ICMessage>>
    val factory: DataSource.Factory<Int, ICMessage>
    var listMsg = arrayListOf<ICMessage>()

    init {
        factory = messageDao.getMessageByLastSeen()
        val nconfig = PagedList.Config.Builder()
                .setPageSize(10)
                .setEnablePlaceholders(true)
                .build()
        invalidate()
        newListMsg = LivePagedListBuilder<Int, ICMessage>(factory, nconfig)
                .build()
    }

    fun invalidate() {
        currentUserId = SessionManager.session.user?.id
        ICheckApplication.getInstance().mFirebase
                .roomUser
                ?.child("i-$currentUserId")
                ?.orderByChild("lastMessage/timestamp")
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        listMsg.clear()
                        for (item in p0.children.reversed()) {
                            item.key?.also {

                                val msg = ICMessage(
                                        it,
                                        item.child("lastMessage")
                                                .child("timestamp").value as Long
                                )
                                if (!it.contains("|")) {
                                    msg.msgType = MsgType.TYPE_GROUP_MSG
                                }
                                val filter = item.child("lastMessage").child("message")
                                if (filter.hasChild("content")) {
                                    msg.lastMessage = item.child("lastMessage")
                                            .child("message")
                                            .child("content")
                                            .value as String?
                                } else if (filter.hasChild("image")) {
                                    msg.lastMessage = "Ảnh đính kèm!"
                                } else {
                                    msg.lastMessage = "Đính kèm!"
                                }
//                                messageDao.insertMessage(msg)
                                listMsg.add(msg)
                            }
                        }
                        setmCurrentSnapshot()
                    }
                })
    }

    fun setmCurrentSnapshot() {
        for (item in listMsg) {
            FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child("i-${currentUserId}")
                    .child("unreadRooms")
                    .child(item.id)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.exists()) {
                                val toInt = (p0.value as Long).toInt()
                                val filter = listMsg.firstOrNull() {
                                    it.id == p0.key
                                }
                                if (filter != null && filter.unreadCount != toInt) {
                                    filter.unreadCount = toInt
                                    messageDao.insertMessage(filter)
                                }
                            }
                        }
                    })
            if (item.msgType == MsgType.TYPE_USER_2_USER) {
                viewModelScope.launch {
                    item.userId = item.id.replace("i-$currentUserId", "").replace("i-", "").replace("|", "").toLong()
                    try {
                        val result = ICNetworkClient.getSimpleApiClient().getUserById(item.userId)
                        if (item.roomName.isNullOrEmpty()) {
                            item.avatar = result.avatarThumbnails?.small.toString()
                            item.roomName = result.name
                            item.userType = result.type
                            item.verified = result.verified
                            messageDao.insertMessage(item)
                        }
                    } catch (e: Exception) {
                    }
                }
                FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child("i-${item.userId}")
                        .addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                if (p0.exists()) {
                                    val result = p0.child("isOnline").value as Boolean?
                                    val filter = listMsg.firstOrNull {
                                        it.userId == p0.key!!.replace("i-","").toLong()
                                    }
                                    if (filter != null && filter.isOnline != result) {
                                        filter.isOnline = result
                                        messageDao.insertMessage(filter)
                                    }
                                }
                            }
                        })
            } else {
                ICheckApplication.getInstance().mFirebase
                        .roomMetadata
                        ?.child(item.id)
                        ?.addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                if (p0.exists()) {
                                    val s = p0.child("name").value as String?

                                    val s1 = if ((p0.child("logo").value as String?) != null) {
                                        p0.child("logo").value as String
                                    } else {
                                        ""
                                    }
                                    val filter = listMsg.firstOrNull() {
                                        it.id == p0.key
                                    }
                                    if (filter != null) {
                                        filter.roomName = s
                                        filter.avatar = s1
                                        messageDao.insertMessage(filter)
                                    }
                                }
                            }
                        })
            }
        }
    }

    fun delete(icMessage: ICMessage) {
        messageDao.deleteRow(icMessage.id)
        ICheckApplication.getInstance().mFirebase.roomUser
                ?.child("i-$currentUserId")
                ?.child(icMessage.id)
                ?.removeValue()
    }

}