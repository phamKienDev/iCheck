package vn.icheck.android

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.model.chat.ChatConversation
import vn.icheck.android.network.api.ICKApi
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.network.util.DeviceUtils
import vn.icheck.android.room.database.AppDatabase
import vn.icheck.android.room.entity.ICFriendInvitationMeUserId
import vn.icheck.android.room.entity.ICMeFollowUser
import vn.icheck.android.room.entity.ICMyFriendIdUser
import vn.icheck.android.room.entity.ICMyFriendInvitationUserId
import vn.icheck.android.util.ick.logDebug
import vn.icheck.android.util.ick.logError
import vn.icheck.android.util.ick.toStringNotNull
import java.util.concurrent.TimeUnit

object RelationshipManager {
    const val FRIEND_LIST_UPDATE = 1
    var lastUpdate: Long = System.currentTimeMillis()

    /**
     * Total follow of main user
     */
    private var totalFollowing = 0L
    fun getTotalFollow() = totalFollowing
    private var totalFollowed = 0L
    fun getTotalFollowed() = totalFollowed

    private var isInitiated = false

    /**
     * Total unread notifications
     */
    var unreadNotify = 0L
    val unreadNotifyLiveData = MutableLiveData<Long>()

    /**
     * List roomId
     */

    val conversationList = arrayListOf<ChatConversation>()
    private val chatSenderListenerList = arrayListOf<ValueEventListener>()
    var unreadCount = 0L

    /**
     * My Friend invitation database actions
     */


    fun removeMyFriendInvitation(userId: Long) {
        AppDatabase.getDatabase().myFriendInvitationUserIdDao().deleteUserById(userId)
    }

    private fun addMyFriendInvitation(userId: Long) {
        AppDatabase.getDatabase().myFriendInvitationUserIdDao().insertMyFriendInvitationUserID(ICMyFriendInvitationUserId(userId))
    }

    private fun clearAllMyFriendInvitation() {
        AppDatabase.getDatabase().myFriendInvitationUserIdDao().deleteAll()
    }

    /**
     * Friend invitation me database actions
     */
    fun checkFriendInvitationMe(userId: Long): Boolean {
        return AppDatabase.getDatabase().friendInvitationMeUserIdDao().getUserByID(userId) != null
    }

    fun removeFriendInvitationMe(userId: Long) {
        AppDatabase.getDatabase().friendInvitationMeUserIdDao().deleteUserById(userId)
    }

    private fun addFriendInvitationMe(userId: Long) {
        AppDatabase.getDatabase().friendInvitationMeUserIdDao().insertFriendInvitationMeUserID(ICFriendInvitationMeUserId(userId))
    }

    private fun clearAllFriendInvitationMe() {
        AppDatabase.getDatabase().friendInvitationMeUserIdDao().deleteAll()
    }

    /**
     * Friend list database actions
     */
    private fun addFriend(userId: Long) {
        AppDatabase.getDatabase().myFriendIdDao().insertMyFriendIDUser(ICMyFriendIdUser(userId))
    }

    fun checkFriend(userId: Long): Boolean {
        return AppDatabase.getDatabase().myFriendIdDao().getUserByID(userId) != null
    }

    private fun clearAllFriend() {
        AppDatabase.getDatabase().myFriendIdDao().deleteAll()
    }

    fun getFriendList(): LiveData<List<ICMyFriendIdUser>> {
        return AppDatabase.getDatabase().myFriendIdDao().getAllFriends()
    }

    /**
     * My following users
     */
    private fun addFollowUser(userId: Long) {
        AppDatabase.getDatabase().meFollowUserDao().insertMeFollowUser(ICMeFollowUser(userId))
    }

    fun checkFollowUser(userId: Long): Boolean {
        return AppDatabase.getDatabase().meFollowUserDao().getUserByID(userId) != null
    }

    private fun clearFollowingUser() {
        AppDatabase.getDatabase().meFollowUserDao().deleteAll()
    }

    /**
     * Conversation
     */
    private fun updateConversation(e: ChatConversation) {
        AppDatabase.getDatabase().chatConversationDao().updateConversation(e)
    }

    private fun clearConversation() {
        AppDatabase.getDatabase().chatConversationDao().dropChat()
    }

    private fun addConversation(e: ChatConversation) {
        AppDatabase.getDatabase().chatConversationDao().addConversation(e)
    }

    fun searchConversation(filter: String): List<ChatConversation> = AppDatabase.getDatabase().chatConversationDao().searchConversation(filter)


    /**
     * App database
     */
    private val firebaseDatabase = FirebaseDatabase.getInstance()

    /**
     * Friend list reference
     */
    private lateinit var friendListReference: DatabaseReference
    private lateinit var friendInvitationReference: DatabaseReference
    private lateinit var friendInvitationMeReference: DatabaseReference
    private lateinit var myFollowingUsersReference: DatabaseReference
    private lateinit var myFollowedUserReference: DatabaseReference

    /**
     * Notifications
     */
    private lateinit var myNotifyReference: DatabaseReference

    /**
     * Chat references
     */
    private lateinit var myConversation: DatabaseReference
    private lateinit var chatSenders: DatabaseReference


    private val friendListListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            clearAllFriend()
            if (snapshot.hasChildren()) {
                for (item in snapshot.children) {
                    addFriend(item.value as Long)
                }
            }
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.FRIEND_LIST_UPDATE, FRIEND_LIST_UPDATE))
            logDebug(snapshot.toString())
        }

        override fun onCancelled(error: DatabaseError) {
            logError(error.toException())
        }
    }

    private val friendInvitationListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            clearAllMyFriendInvitation()
            if (snapshot.hasChildren()) {
                for (item in snapshot.children) {
                    addMyFriendInvitation(item.value as Long)
                }
            }
            logDebug(snapshot.toString())
        }

        override fun onCancelled(error: DatabaseError) {
            logError(error.toException())
        }
    }

    private val friendInvitationMeListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            clearAllFriendInvitationMe()
            if (snapshot.hasChildren()) {
                for (item in snapshot.children) {
                    addFriendInvitationMe(item.value as Long)
                }
            }
            logDebug(snapshot.toString())
        }

        override fun onCancelled(error: DatabaseError) {
            logError(error.toException())
        }
    }

    private val myFollowingUserListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            clearFollowingUser()
            totalFollowing = snapshot.childrenCount
            if (snapshot.hasChildren()) {
                for (item in snapshot.children) {
                    addFollowUser(item.value as Long)
                }
            }
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_CHANGE_FOLLOW))
            logDebug(snapshot.toString())
        }

        override fun onCancelled(error: DatabaseError) {
            logError(error.toException())
        }
    }

    private val myFollowedUsersListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            totalFollowed = snapshot.childrenCount
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_CHANGE_FOLLOW))
            logDebug(snapshot.toString())
        }

        override fun onCancelled(error: DatabaseError) {
            logError(error.toException())
        }
    }

    private val myNotifyListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                unreadNotify = snapshot.value as Long
                unreadNotifyLiveData.postValue(unreadNotify)
            } else {
                unreadNotify = 0L
            }

            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_UNREAD_NOTIFICATION, unreadNotify))
        }

        override fun onCancelled(error: DatabaseError) {
            unreadNotify = 0L
            unreadNotifyLiveData.postValue(unreadNotify)
        }
    }

    /**
     * Chat listener
     */
    private val myConversationListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.hasChildren()) {
                conversationList.clear()
                clearConversation()
                for (item in snapshot.children) {
                    if (item.hasChild("key_room")) {
                        val element = ChatConversation(item?.key.toString()).apply {
                            enableAlert = item.child("enable_alert").value as Long? ?: 0L
                            keyRoom = item.child("key_room").value.toString()
                            unreadCount = item.child("unread_count").value as Long? ?: 0L
                            time = item.child("last_activity").child("time").value as Long?
                                ?: System.currentTimeMillis()
                            lastMessage = item.child("last_activity").child("content").value.toStringNotNull()

                        }
                        conversationList.add(element)
                        addConversation(element)
                    }
                }
                for (item in conversationList) {
                    setSender(item)
                }
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_CONVERSATION))
            }
        }

        override fun onCancelled(error: DatabaseError) {
            conversationList.clear()
        }
    }

    private val mainUserListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            unreadCount = try {
                if (snapshot.hasChildren()) {
                    if (snapshot.child("unread_count").exists()) {
                        snapshot.child("unread_count").value as Long
                    } else {
                        0
                    }
                } else {
                    0
                }
            } catch (e: Exception) {
                0
            }
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UNREAD_COUNT, unreadCount))
        }

        override fun onCancelled(error: DatabaseError) {
            unreadCount = 0
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UNREAD_COUNT, unreadCount))
        }
    }

    private fun setSender(conversation: ChatConversation) {
        val obj = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()) {
                    conversation.targetUserName = snapshot.child("name").value.toString()
                    conversation.isOnline = if (snapshot.child("is_online").value !is Boolean) false else snapshot.child("is_online").value as Boolean?
                        ?: false
                    conversation.imageTargetUser = snapshot.child("image").value.toString()
                    updateConversation(conversation)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        chatSenderListenerList.add(obj)
        chatSenders.child(conversation.getTargetUser().toString()).addValueEventListener(obj)
    }


    /**
     * Method to listen data updates
     */
    fun observe() {
        if (!isInitiated) {
            initReferences()
            setListeners()
            isInitiated = true
        }
    }


    private fun setListeners() {

        if (FirebaseAuth.getInstance().currentUser != null) {// Relationship
            friendListReference.addValueEventListener(friendListListener)
            friendInvitationReference.addValueEventListener(friendInvitationListener)
            friendInvitationMeReference.addValueEventListener(friendInvitationMeListener)
            myFollowingUsersReference.addValueEventListener(myFollowingUserListener)
            myFollowedUserReference.addValueEventListener(myFollowedUsersListener)
            // References
            myNotifyReference.addValueEventListener(myNotifyListener)
            // Chat
            myConversation
                .orderByChild("last_activity/time")
                .addValueEventListener(myConversationListener)

            chatSenders.child("user|${SessionManager.session.user?.id}").addValueEventListener(mainUserListener)
        }
    }

    fun refreshToken(onLogin: Boolean = false) {

        GlobalScope.launch {
            doLogin(onLogin)
        }
    }

    private suspend fun doLogin(onLogin: Boolean = false) {
        try {
//            val httpLoggingInterceptor = HttpLoggingInterceptor()
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer ${SessionManager.session.token}")
                        .addHeader("device-id", DeviceUtils.getUniqueDeviceId())
                        .addHeader("appVersion", SettingManager.appVersion)
                        .build()
                    val hasMultipart = request.headers.names().contains("multipart")
//                        if (hasMultipart) httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE) else httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                    chain.proceed(request)
                }
//                    .addInterceptor(httpLoggingInterceptor)
                .build()
            val api = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .baseUrl(APIConstants.socialHost)
                .build()
                .create(ICKApi::class.java)
            val request = hashMapOf<String, Any?>()
            request["token"] = SessionManager.session.firebaseToken
            val response = api.updateFirebaseToken(request)
            if (!response.data.isNullOrEmpty()) {
                FirebaseDatabase.getInstance().goOffline()
//                createNewToken()
                SessionManager.session.firebaseToken = response.data
            }
            lastUpdate = System.currentTimeMillis()
            FirebaseAuth.getInstance().signInWithCustomToken(SessionManager.session.firebaseToken.toString()).addOnSuccessListener {
                FirebaseDatabase.getInstance().goOnline()
                observe()
                if (onLogin) {
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_LOG_IN_FIREBASE))
                    FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                        GlobalScope.launch {
                            val body = HashMap<String, Any?>()
                            body["platform"] = "android"
                            body["deviceId"] = DeviceUtils.getUniqueDeviceId()
                            body["deviceToken"] = token
//                val requestBody = body.toString().toRequestBody("application/json".toMediaTypeOrNull())
                            api.loginDevice(body)
                        }
                    }.addOnFailureListener {
                        FirebaseDatabase.getInstance().goOnline()
                        observe()
                        logError(it)
                    }
                }
            }.addOnFailureListener {
                FirebaseDatabase.getInstance().goOnline()
                observe()
                logError(it)
            }
        } catch (e: Exception) {
            logError(e)
        }
    }

    fun checkUpdate() {
        val diff = System.currentTimeMillis() - lastUpdate
        val diffMinutes = TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS)
        if (diffMinutes >= 30) {
            refreshToken()
        }
    }

    private fun initReferences() {
        // Relationship
        val id = FirebaseAuth.getInstance().currentUser?.uid
        friendInvitationReference = firebaseDatabase
            .getReference("relationships/$id/myFriendInvitationUserIdList")
        friendInvitationMeReference = firebaseDatabase
            .getReference("relationships/$id/friendInvitationMeUserIdList")
        friendListReference = firebaseDatabase
            .getReference("relationships/$id/myFriendIdList")
        myFollowingUsersReference = firebaseDatabase
            .getReference("relationships/$id/myFollowingUserIdList")
        myFollowedUserReference = firebaseDatabase
            .getReference("relationships/$id/userFollowingMeIdList")
        // Notifications
        myNotifyReference = firebaseDatabase
            .getReference("reaction-noti/$id/unseenCount")
        // Chat
        chatSenders = firebaseDatabase
            .getReference("chat-senders")
        myConversation = firebaseDatabase
            .getReference("chat-conversations/user|$id")

    }

    fun removeListener() {
        try {
            isInitiated = false
            unreadNotify = 0L
            totalFollowing = 0L
            totalFollowed = 0L

            conversationList.clear()
            clearConversation()
            clearAllMyFriendInvitation()
            clearFollowingUser()
            clearAllFriend()

            if (::friendInvitationReference.isInitialized) {
                friendInvitationReference.removeEventListener(friendInvitationListener)
            }
            if (::friendInvitationMeReference.isInitialized) {
                friendInvitationMeReference.removeEventListener(friendInvitationMeListener)
            }
            if (::friendListReference.isInitialized) {
                friendListReference.removeEventListener(friendListListener)
            }
            if (::myFollowingUsersReference.isInitialized) {
                myFollowingUsersReference.removeEventListener(myFollowingUserListener)
            }
            if (::myFollowedUserReference.isInitialized) {
                myFollowedUserReference.removeEventListener(myFollowedUsersListener)
            }

            if (::myNotifyReference.isInitialized) {
                myNotifyReference.removeEventListener(myNotifyListener)
            }

            if (::myConversation.isInitialized) {
                myConversation.removeEventListener(myConversationListener)
            }
            if (::chatSenders.isInitialized) {
                chatSenders.removeEventListener(mainUserListener)
                for (item in chatSenderListenerList) {
                    chatSenders.removeEventListener(item)
                }
            }
//            createNewToken()
            FirebaseAuth.getInstance().signOut()
            FirebaseDatabase.getInstance().goOffline()
        } catch (e: Exception) {
            logError(e)
        }
    }

    private fun createNewToken() {
        GlobalScope.launch(Dispatchers.IO) {
            FirebaseInstallations.getInstance().delete()
            FirebaseMessaging.getInstance().deleteToken()
        }
    }

}