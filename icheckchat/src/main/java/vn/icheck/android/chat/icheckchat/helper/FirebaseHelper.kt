package vn.icheck.android.chat.icheckchat.helper

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import vn.icheck.android.chat.icheckchat.base.ConstantChat.TOKEN_FIREBASE
import vn.icheck.android.chat.icheckchat.base.ConstantChat.USER_ID
import vn.icheck.android.chat.icheckchat.model.MCResponse
import vn.icheck.android.chat.icheckchat.model.MCResponseCode
import vn.icheck.android.chat.icheckchat.network.MCNetworkClient
import vn.icheck.android.chat.icheckchat.network.requestNewApi

class FirebaseHelper {
    private val firebaseDatabase = FirebaseDatabase.getInstance()

    private var userID = ""

    var offset = 0

    fun loginFirebase(success: () -> Unit, cancel: () -> Unit) {
        val token = ShareHelperChat.getString(TOKEN_FIREBASE)

        if (!token.isNullOrEmpty()) {
            FirebaseAuth.getInstance().signInWithCustomToken(token).addOnSuccessListener {
                FirebaseDatabase.getInstance().goOnline()
                userID = it.user?.uid ?: ShareHelperChat.getLong(USER_ID).toString()
                success()
            }.addOnFailureListener {
                FirebaseDatabase.getInstance().goOnline()
                if (it.message?.contains("token") == true) {
                    refreshFirebase({ obj ->
                        if (!obj.data.isNullOrEmpty()) {
                            FirebaseDatabase.getInstance().goOffline()
                            ShareHelperChat.putString(TOKEN_FIREBASE, obj.data)
                            loginFirebase(success, cancel)
                        } else {
                            cancel()
                        }
                    }, { error ->
                        cancel()
                    })
                } else {
                    cancel()
                }
            }
        } else {
            cancel()
        }
    }

    private fun refreshFirebase(success: (obj: MCResponse<String>) -> Unit, cancel: (error: MCResponseCode) -> Unit) {
        val body = hashMapOf<String, Any>()
        if (!ShareHelperChat.getString(TOKEN_FIREBASE).isNullOrEmpty()) {
            body["token"] = ShareHelperChat.getString(TOKEN_FIREBASE) ?: ""
        }
        requestNewApi(MCNetworkClient.getNewSocialApi().updateFirebaseToken(body), success, cancel)
    }

    fun getConversation(lastTimeStamp: Long, success: (snapshot: DataSnapshot) -> Unit, cancel: (error: DatabaseError) -> Unit) {
        val myConversation = if (lastTimeStamp > 0) {
            firebaseDatabase.getReference("chat-conversations-v2/user|$userID").orderByChild("last_activity/time")
                    .startAt(0.0).endAt(lastTimeStamp.toDouble() - 1)
                    .limitToLast(10)
        } else {
            firebaseDatabase.getReference("chat-conversations-v2/user|$userID").orderByChild("last_activity/time")
                    .limitToLast(10)
        }

        myConversation.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                success(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                cancel(error)
            }
        })
    }

    fun getChatSender(child: String, success: (snapshot: DataSnapshot) -> Unit, cancel: (error: DatabaseError) -> Unit) {
        firebaseDatabase.getReference("chat-senders-v2").child(child).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                success(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                cancel(error)
            }
        })
    }

    fun getImageChatDetail(lastTimeStamp: Long, key: String, success: (snapshot: DataSnapshot) -> Unit, cancel: (error: DatabaseError) -> Unit) {
        val imageDatabase = if (lastTimeStamp > 0) {
            firebaseDatabase.getReference("chat-details-v2/$key").orderByChild("time")
                    .startAt(0.0).endAt(lastTimeStamp.toDouble() - 1)
                    .limitToLast(10)
        } else {
            firebaseDatabase.getReference("chat-details-v2/$key").orderByChild("time")
                    .limitToLast(10)
        }

        imageDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                success(snapshot)
                imageDatabase.removeEventListener(this)
            }

            override fun onCancelled(error: DatabaseError) {
                cancel(error)
                imageDatabase.removeEventListener(this)
            }
        })
    }

    fun getMessageDetailV2(lastTimeStamp: Long, key: String, success: (snapshot: DataSnapshot) -> Unit, cancel: (error: DatabaseError) -> Unit) {
        val messageDetails = if (lastTimeStamp > 0) {
            firebaseDatabase.getReference("chat-details-v2/$key").orderByChild("time")
                    .startAt(0.0).endAt(lastTimeStamp.toDouble() - 1)
                    .limitToLast(10)
        } else {
            firebaseDatabase.getReference("chat-details-v2/$key").orderByChild("time")
                    .limitToLast(10)
        }

        messageDetails.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                success(snapshot)
                messageDetails.removeEventListener(this)
            }

            override fun onCancelled(error: DatabaseError) {
                cancel(error)
                messageDetails.removeEventListener(this)
            }
        })
    }

    fun getChangeMessageChat(key: String, onAdd: (snapshot: DataSnapshot) -> Unit) {
        firebaseDatabase.getReference("chat-details-v2/$key").orderByChild("time").startAt(System.currentTimeMillis().toDouble()).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                onAdd(snapshot)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("onCancelled", "onCancelled: $error")
            }
        })
    }

    fun getChatRoom(key: String, success: (snapshot: DataSnapshot) -> Unit, cancel: (error: DatabaseError) -> Unit) {
        firebaseDatabase.getReference("chat-rooms-v2/$key").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                success(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                cancel(error)
            }
        })
    }
}