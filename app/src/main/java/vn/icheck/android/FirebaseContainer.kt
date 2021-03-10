package vn.icheck.android

import android.util.Log
import androidx.preference.PreferenceManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST
import vn.icheck.android.constant.FIREBASE_REGISTER_DEVICE
import vn.icheck.android.network.BuildConfig
import vn.icheck.android.network.base.ICLoginProtocol
import vn.icheck.android.network.base.ICNetworkManager2
import vn.icheck.android.network.base.SessionManager

interface FirebaseMessage {
    @POST("devices")
    suspend fun registerDevice(@Body body: RequestBody): ResponseBody

    @POST("/users/me/tokens")
    suspend fun registerDeviceToOldServer(@Body body: RequestBody): ResponseBody
}

class FirebaseContainer: ICLoginProtocol {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    var firebaseDatabase: FirebaseDatabase? = null
    var roomMessage: DatabaseReference? = null
    var roomUser: DatabaseReference? = null
    var roomMetadata: DatabaseReference? = null
    var users: DatabaseReference? = null
    var reactionNoti: DatabaseReference? = null
    var currentRoomId: String = ""
    var relationships: DatabaseReference? = null

    private var token: String? = null
    private var icheckId: String? = null
        get() = auth.uid

    init {
        ICNetworkManager2.registerLogin(this)
        addFCMTokenListener()

        val firebaseToken = SessionManager.session.firebaseToken
        if (firebaseToken != null) {
            loginFirebase(firebaseToken)
//            addFCMTokenListener()
        }
    }

    fun loginFirebase(token: String) {
        if (SessionManager.session.user != null) {
            firebaseDatabase = FirebaseDatabase.getInstance()
            roomMessage = firebaseDatabase?.getReference("room-messages")
            roomUser = firebaseDatabase?.getReference("room-users")
            roomMetadata = firebaseDatabase?.getReference("room-metadata")
            users = firebaseDatabase?.getReference("users")
            reactionNoti = firebaseDatabase?.getReference("reaction-noti")
            relationships = firebaseDatabase?.getReference("relationships")
//            auth.signInWithCustomToken(token).addOnSuccessListener {
//                if (it.user != null) {
//                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_LOG_IN_FIREBASE))
//                    registerDisconnectEvent()
//                }
//            }
        }
    }

    fun renewFirebaseInstanceID() {
//        GlobalScope.launch(Dispatchers.IO) {
//            FirebaseInstanceId.getInstance().deleteInstanceId()
//        }
    }

    //Listen Token changed
    fun addFCMTokenListener() {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener {
            if (it.isSuccessful) {
                val token = it.result?.token
                registerFCMToken(token)
            }
        })
    }

    public fun registerFCMToken(token: String?) {
        if (token != null) {
            registerFCMTokenToServer(token)
            registerFCMTokenToFirebase(token)
        }
    }

    //Register fcmToken to own server
    private fun registerFCMTokenToServer(fcmToken: String) {
//        val baseUrl = if (BuildConfig.FLAVOR == "dev") {
//            "https://messaged.dev.icheck.vn/"
//        } else {
//            "https://messaged.icheck.com.vn/"
//        }
//        val retrofitBuilder = Retrofit.Builder()
//                .baseUrl(baseUrl)
//                .client(OkHttpClient())
//                .build()
//        val api = retrofitBuilder.create(FirebaseMessage::class.java)
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val body = HashMap<String, Any?>()
//                body["platform"] = "android"
//                body["deviceId"] = DeviceUtils.getUniqueDeviceId()
//                body["deviceToken"] = fcmToken
//
////                val requestBody = body.toString().toRequestBody("application/json".toMediaTypeOrNull())
//                ickApi.loginDevice(body)
//            } catch (e: Exception) {
//                Log.e("e", "${e.message}")
//            }
//        }
    }

    //Register fcmToken to own Old Server
    private fun registerFCMTokenToOldServer(fcmToken: String) {
        val baseUrl = if (BuildConfig.FLAVOR == "dev") {
            "https://api.dev.icheck.vn/api/v1/"
        } else {
            "https://api.icheck.com.vn/api/v1/"
        }
        val retrofitBuilder = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(OkHttpClient())
                .build()
        val api = retrofitBuilder.create(FirebaseMessage::class.java)
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val body = JSONObject()
                body.put("token", fcmToken)

                val requestBody = body.toString().toRequestBody("application/json".toMediaTypeOrNull())
                api.registerDeviceToOldServer(requestBody)
            } catch (e: Exception) {
                Log.e("e", "${e.message}")
            }
        }
    }

    //Register fcmToken to Firebase Database
    private fun registerFCMTokenToFirebase(fcmToken: String) {
        val oldToken = this.token
        val icheckId = this.icheckId
        if (icheckId.isNullOrBlank()) return
        if (oldToken != null && !oldToken.equals(icheckId)) {
            removeFCMToken(oldToken, icheckId)
        }
        registerFCMToken(fcmToken, icheckId)
        FirebaseMessaging.getInstance().subscribeToTopic("all")
    }

    private fun registerDisconnectEvent() {
        val firebaseToken = SessionManager.session.firebaseToken
        if (firebaseToken.isNullOrBlank()) return
        FirebaseDatabase.getInstance().goOnline()

        if (auth.currentUser != null) {
            registerDisconnect()
            return
        }
        loginFirebase(firebaseToken)
    }

    private fun registerDisconnect() {
        val icheckId = icheckId
        if (icheckId.isNullOrBlank()) return
        val token = token
        if (token != null) {
            registerFCMToken(token, icheckId)
        }

        //connect Event
        val user = SessionManager.session?.user
        val connectMap = HashMap<String, Any?>()
        connectMap.put("isOnline", true)
        connectMap.put("lastActivityTime", ServerValue.TIMESTAMP)
        connectMap.put("name", user?.name)
        users?.child(icheckId)?.updateChildren(connectMap)
//disconnect Event
        val map = HashMap<String, Any>()
        map.put("isOnline", false)
        map.put("lastActivityTime", ServerValue.TIMESTAMP)
        users?.child(icheckId)?.onDisconnect()
                ?.updateChildren(map)
    }

    private fun removeFCMToken(oldToken: String, icheckId: String) {
        FirebaseDatabase.getInstance()
                .getReference("/users/$icheckId/notificationTokens")
                .child(oldToken)
                .removeValue()
    }

    private fun registerFCMToken(fcmToken: String, icheckId: String) {
//        registerFCMTokenToOldServer(fcmToken)

        FirebaseDatabase.getInstance()
                .getReference("/users/$icheckId/notificationTokens")
                .child(fcmToken)
                .setValue(true)
    }

    fun registerRelationship(key: String, id: String, event: ValueEventListener, userId: String? = null) {
        ICheckApplication.getInstance().mFirebase.auth.currentUser?.let { user ->
            ICheckApplication.getInstance().mFirebase.relationships
                    ?.child(userId ?: user.uid)
                    ?.child(key)
                    ?.child(id)
                    ?.addValueEventListener(event)
        }
    }

    override fun onLogin() {
        renewFirebaseInstanceID()
        val firebaseToken = SessionManager.session.firebaseToken
        if (firebaseToken.isNullOrBlank()) return
        loginFirebase(firebaseToken)
        listenCounts()
    }

    override fun onLogout() {
        val token = token
        val icheckId = icheckId
        if (token != null && icheckId != null) {
            removeFCMToken(token, icheckId)
        }
        logoutFirebase()
        stopListenCounts()
        renewFirebaseInstanceID()
        PreferenceManager.getDefaultSharedPreferences(ICheckApplication.getInstance()).edit().putBoolean(FIREBASE_REGISTER_DEVICE, false).apply()
    }

    private fun logoutFirebase() {
        if (auth.currentUser != null) {
            auth.signOut()
        }
    }

    private fun listenCounts() {}
    private fun stopListenCounts() {}
}
