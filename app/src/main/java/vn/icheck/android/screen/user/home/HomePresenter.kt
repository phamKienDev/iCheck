package vn.icheck.android.screen.user.home

import androidx.preference.PreferenceManager
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.BuildConfig
import vn.icheck.android.ICheckApplication
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.ICK_TOKEN
import vn.icheck.android.helper.CartHelper
import vn.icheck.android.tracking.insider.InsiderHelper
import vn.icheck.android.helper.ShareSessionToModule
import vn.icheck.android.loyalty.sdk.LoyaltySdk
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.auth.AuthInteractor
import vn.icheck.android.network.feature.user.UserInteractor
import vn.icheck.android.network.models.ICSessionData
import vn.icheck.android.network.models.ICUser

class HomePresenter(val view: IHomeView) : BaseActivityPresenter(view) {

    fun checkVersionApp(): Int {
        var res = 0
        var newNumbers: List<String>? = null

        val versionAndroidClient = BuildConfig.VERSION_NAME
        val versionAndroidAll = SettingManager.clientSetting?.force_update?.android_all
        val targetAndroid = SettingManager.clientSetting?.force_update?.android

        val oldNumbers: List<String> = versionAndroidClient.split(".")

        if (!versionAndroidAll.isNullOrEmpty()) {
            newNumbers = versionAndroidAll.split(".")
        }

        if (!newNumbers.isNullOrEmpty()) {
            val minIndex = Math.min(oldNumbers.size, newNumbers.size)
            if (!versionAndroidAll.isNullOrEmpty()) {
                for (i in 0 until minIndex) {
                    val oldVersion = oldNumbers[i].toInt()
                    val newVersion = newNumbers[i].toInt()

                    if (oldVersion < newVersion) {
                        res = -1
                        view.showDialogUpdate()
                        break
                    } else if (oldVersion > newVersion) {
                        res = 1
                        break
                    }
                }
            }
        } else {
            if (targetAndroid != null) {
                for (i in targetAndroid) {
                    if (versionAndroidClient.equals(i)) {
                        view.showDialogUpdate()
                    }
                }
            }
        }

        if (newNumbers != null) {
            if (res == 0 && oldNumbers.size != newNumbers.size) {
                res = if (oldNumbers.size > newNumbers.size) 1 else -1
            }
        }

        return res
    }

    private val valueEventListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_UNREAD_NOTIFICATION, 0L))
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var sum = 0L

            if (dataSnapshot.childrenCount > 0) {
                dataSnapshot.children.forEach { data ->
                    if (data.value != null && data.value is Long) {
                        sum += data.value as Long
                    }
                }
            } else {
                if (dataSnapshot.value != null && dataSnapshot.value is Long) {
                    sum += dataSnapshot.value as Long
                }
            }

            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_UNREAD_NOTIFICATION, sum))
        }
    }

    private val unreadMessageListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {}

        override fun onDataChange(p0: DataSnapshot) {
//            var sum = 0L
//
//            p0.children.forEach { data ->
//                try {
//                    if (data.value is Long)
//                        sum += data.value as Long
//                } catch (e: Exception) {
//                }
//            }
//
//            if (sum >= 10L) {
//                view.onUpdateMessageCount("9+")
//            } else if (sum > 0) {
//                view.onUpdateMessageCount("$sum")
//            } else {
//                view.onUpdateMessageCount(null)
//            }
        }
    }

    fun updateUserInfo() {
        UserInteractor().getUserMe(object : ICApiListener<ICUser> {
            override fun onSuccess(obj: ICUser) {
                SessionManager.updateUser(obj)
                InsiderHelper.setUserAttributes()
                view.onUpdateUserInfo()
            }

            override fun onError(error: ICBaseResponse?) {

            }
        })
    }

    fun onLogOut() {
        unregisterNotificationCount()
        unregisterMessageCount()

        ICheckApplication.getInstance().mFirebase.auth.signOut()
        SessionManager.session = ICSessionData()
        SettingManager.clear()
        LoyaltySdk.clearSession()
        CartHelper().clearCart(view.mContext)
        CartHelper().clearCartSocial(view.mContext)
        FacebookSdk.sdkInitialize(ICheckApplication.getInstance())
        LoginManager.getInstance().logOut()

        view.onUpdateUserInfo()

        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_UNREAD_NOTIFICATION, 0L))
        ICNetworkManager.getLoginProtocol?.onLogout()

        InsiderHelper.onLogout()
        loginAnonymous()
    }

    fun loginAnonymous() {
        view.onShowLoading(true)

        AuthInteractor().loginAnonymous(object : ICApiListener<ICResponse<ICSessionData>> {
            override fun onSuccess(obj: ICResponse<ICSessionData>) {
                view.onShowLoading(false)
                if (!obj.data?.token.isNullOrEmpty()) {
                    obj.data?.user = ICUser().apply {
                        this.id = obj.data?.appUserId ?: 0L
                    }
                    SessionManager.session = obj.data!!
                    ShareSessionToModule.setSession(obj.data!!)
                    PreferenceManager.getDefaultSharedPreferences(ICheckApplication.getInstance()).edit().putString(ICK_TOKEN, obj.data?.token).apply()
                    InsiderHelper.onLogin()
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_LOG_OUT))
                } else {
                    view.onLogoutFalse()
                }
            }

            override fun onError(error: ICBaseResponse?) {
                view.onShowLoading(false)
                view.onLogoutFalse()
            }
        })
    }


    fun registerNotificationCount() {
        ICheckApplication.getInstance().mFirebase.auth.currentUser?.let { currentUser ->
            ICheckApplication.getInstance().mFirebase.reactionNoti
                    ?.child(currentUser.uid)
                    ?.child("unseenCount")
                    ?.addValueEventListener(valueEventListener)
        }
    }

    private fun unregisterNotificationCount() {
        ICheckApplication.getInstance().mFirebase.auth.currentUser?.let { currentUser ->
            ICheckApplication.getInstance().mFirebase.reactionNoti
                    ?.child(currentUser.uid)
                    ?.child("unseenCount")
                    ?.removeEventListener(valueEventListener)
        }
    }

    fun registerMessageCount() {
        val currentUserId = SessionManager.session.user?.id

        FirebaseDatabase.getInstance().getReference("users")
                .child("i-$currentUserId")
                .child("unreadRooms")
                .addValueEventListener(unreadMessageListener)
    }

    private fun unregisterMessageCount() {
        val currentUserId = SessionManager.session.user?.id

        FirebaseDatabase.getInstance().getReference("users")
                .child("i-$currentUserId")
                .child("unreadRooms")
                .removeEventListener(unreadMessageListener)
    }
}