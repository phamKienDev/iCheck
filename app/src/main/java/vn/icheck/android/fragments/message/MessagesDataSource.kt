package vn.icheck.android.fragments.message

import androidx.lifecycle.MutableLiveData
import androidx.paging.ItemKeyedDataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.PositionalDataSource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.State
import vn.icheck.android.fragments.message.model.GroupMsgModel
import vn.icheck.android.fragments.message.model.MsgModel
import vn.icheck.android.fragments.message.model.MsgType
import vn.icheck.android.fragments.message.model.UserToUserModel
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICUserId

class MessagesDataSource : PageKeyedDataSource<Int, MsgModel>() {

    var currentUserId: Long? = 0L
    var dataSnapshot: DataSnapshot? = null
    var copy = mutableListOf<DataSnapshot>()
    var state: MutableLiveData<State> = MutableLiveData()
    val listData = arrayListOf<MsgModel>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, MsgModel>) {
        currentUserId = SessionManager.session.user?.id
        ICheckApplication.getInstance().mFirebase.roomUser
                ?.child("i-$currentUserId")
                ?.orderByChild("lastMessage/timestamp")
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (dataSnapshot == null) {
                            dataSnapshot = p0
                            for (item in p0.children.reversed()) {
                                item.key?.let {
                                    if (it.contains("|")) {
                                        addUserMsg(it, item, listData)
                                    } else {
                                        addGroupMsg(it, item, listData)
                                    }
                                }
                            }
                            getUserNames(listData, callback, params)
                        } else {
                            if (dataSnapshot!!.childrenCount == p0.childrenCount) {
                                val item = p0.children.last()
                                item.key?.let {
                                    if (it.contains("|")) {
                                        val model = UserToUserModel(it, item.child("lastMessage")
                                                .child("timestamp").value as Long)
                                        val filter = item.child("lastMessage").child("message")
                                        if (filter.hasChild("content")) {
                                            model.message = item.child("lastMessage")
                                                    .child("message")
                                                    .child("content")
                                                    .value as String?
                                        } else if (filter.hasChild("image")) {
                                            model.message = "Ảnh đính kèm!"
                                        } else {
                                            model.message = "Đính kèm!"
                                        }
                                        val f = listData.indexOfFirst {
                                            it.getFirebaseId() == model.getFirebaseId()
                                        }
                                        if (f != -1) {
                                            listData.removeAt(f)
                                        }
                                        GlobalScope.launch {
                                            val response = ICNetworkClient.getSimpleApiClient()
                                                    .getUserById(model.id)
                                            model.icUserId = response
                                            listData.add(0, model)
                                            invalidate()
                                        }
                                    } else {
                                        val groupmsg = GroupMsgModel(it, item.child("lastMessage")
                                                .child("timestamp").value as Long)
                                        val filter = item.child("lastMessage").child("message")
                                        if (filter.hasChild("content")) {
                                            groupmsg.lastMsg = item.child("lastMessage")
                                                    .child("message")
                                                    .child("content")
                                                    .value as String?
                                        } else if (filter.hasChild("image")) {
                                            groupmsg.lastMsg = "Ảnh đính kèm!"
                                        } else {
                                            groupmsg.lastMsg = "Đính kèm!"
                                        }
                                        val f = listData.indexOfFirst {
                                            it.getFirebaseId() == groupmsg.getFirebaseId()
                                        }
                                        if (f != -1) {
                                            listData.removeAt(f)
                                        }
                                        listData.add(0, groupmsg)
                                        invalidate()
                                    }
                                }
                            }
                        }
                    }
                })
    }

    private fun addGroupMsg(it: String, p: DataSnapshot, listData: ArrayList<MsgModel>): Boolean {
        val groupmsg = GroupMsgModel(it, p.child("lastMessage")
                .child("timestamp").value as Long)
        val filter = p.child("lastMessage").child("message")
        if (filter.hasChild("content")) {
            groupmsg.lastMsg = p.child("lastMessage")
                    .child("message")
                    .child("content")
                    .value as String?
        } else if (filter.hasChild("image")) {
            groupmsg.lastMsg = "Ảnh đính kèm!"
        } else {
            groupmsg.lastMsg = "Đính kèm!"
        }
        return listData.add(groupmsg)
    }

    private fun getUserNames(listData: ArrayList<MsgModel>, callback: LoadInitialCallback<Int, MsgModel>, params: LoadInitialParams<Int>) {
        GlobalScope.launch {
            val listUser = listData.filter {
                it.getType() == MsgType.TYPE_USER_2_USER
            }
            val listJob = arrayListOf<Deferred<Any?>>()
            for (item in listUser) {
                item as UserToUserModel
                listJob.add(async(Dispatchers.IO) {
                    try {
                        val response = ICNetworkClient.getSimpleApiClient()
                                .getUserById(item.id)
                        item.icUserId = response
                    } catch (e: Exception) {
                    }
                })
            }
            for (item in listJob) {
                item.await()
            }
            state.postValue(State.DONE)
            callback.onResult(listData,
                    null,
                    null)

        }

    }

    private fun addUserMsg(it: String, item: DataSnapshot, listData: ArrayList<MsgModel>) {
        /**
         * Type user to user
         */
        val model = UserToUserModel(it, item.child("lastMessage")
                .child("timestamp").value as Long)
        val filter = item.child("lastMessage").child("message")
        if (filter.hasChild("content")) {
            model.message = item.child("lastMessage")
                    .child("message")
                    .child("content")
                    .value as String?
        } else if (filter.hasChild("image")) {
            model.message = "Ảnh đính kèm!"
        } else {
            model.message = "Đính kèm!"
        }
        listData.add(model)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, MsgModel>) {
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, MsgModel>) {
    }
}