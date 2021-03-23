package vn.icheck.android.chat.icheckchat.helper

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import vn.icheck.android.chat.icheckchat.R
import vn.icheck.android.chat.icheckchat.base.ConstantChat.TOKEN_FIREBASE
import vn.icheck.android.chat.icheckchat.base.ConstantChat.USER_ID
import vn.icheck.android.chat.icheckchat.model.MCDetailMessage
import vn.icheck.android.chat.icheckchat.model.MCMedia
import vn.icheck.android.chat.icheckchat.model.MCResponse
import vn.icheck.android.chat.icheckchat.model.MCResponseCode
import vn.icheck.android.chat.icheckchat.network.MCNetworkClient
import vn.icheck.android.chat.icheckchat.network.requestNewApi

class FirebaseHelper {
    private val firebaseDatabase = FirebaseDatabase.getInstance()

    private var userID = ""

    private var myConversation: DatabaseReference? = null

    var offset = 0

    fun loginFirebase(success: () -> Unit, cancel: () -> Unit) {
        val token = ShareHelperChat.getString(TOKEN_FIREBASE)

        if (!token.isNullOrEmpty()) {
            FirebaseAuth.getInstance().signInWithCustomToken(token).addOnSuccessListener {
                FirebaseDatabase.getInstance().goOnline()
                userID = it.user?.uid ?: ShareHelperChat.getLong(USER_ID).toString()
                myConversation = firebaseDatabase.getReference("chat-conversations-v2/user|$userID")
                success()
            }.addOnFailureListener {
                FirebaseDatabase.getInstance().goOnline()
                if (it.message?.contains("token") == true) {
                    refreshFirebase({ obj ->
                        if (!obj.data.isNullOrEmpty()) {
                            FirebaseDatabase.getInstance().goOffline()
                            ShareHelperChat.putString(TOKEN_FIREBASE, obj.data)
                            loginFirebase(success, cancel)
                        }else{
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

    fun getConversation(isLoadMore: Boolean = false, success: (snapshot: DataSnapshot) -> Unit, cancel: (error: DatabaseError) -> Unit) {
        if (!isLoadMore) {
            offset = 0
        }

//        myConversation?.orderByChild("last_activity/time")?.limitToFirst(10)?.startAt(offset.toDouble())?.addValueEventListener(object : ValueEventListener {
        myConversation?.orderByChild("last_activity/time")?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                offset += 10

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

    fun getImageChatDetail(key: String, success: (snapshot: DataSnapshot) -> Unit, cancel: (error: DatabaseError) -> Unit) {
        firebaseDatabase.getReference("chat-details-v2/$key").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                success(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                cancel(error)
            }
        })
    }

    fun getMessageDetail(key: String, success: (snapshot: DataSnapshot) -> Unit, cancel: (error: DatabaseError) -> Unit) {
        firebaseDatabase.getReference("chat-details-v2/$key").orderByChild("time").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                success(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                cancel(error)
            }
        })
    }

    fun getChatRoom(key: String, success: (snapshot: DataSnapshot) -> Unit, cancel: (error: DatabaseError) -> Unit){
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