package vn.icheck.android.services

import android.content.Intent
import android.net.Uri
import androidx.annotation.WorkerThread
import androidx.preference.PreferenceManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tapadoo.alerter.Alerter
import com.useinsider.insider.Insider
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.chat.icheckchat.screen.conversation.ListConversationFragment
import vn.icheck.android.chat.icheckchat.screen.detail.ChatSocialDetailActivity
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.RingtoneHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.network.feature.mission.MissionInteractor
import vn.icheck.android.network.models.ICMissionDetail
import vn.icheck.android.screen.dialog.DialogNotificationFirebaseAds
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.screen.user.popup_complete_mission.PopupCompleteMissionActivity
import vn.icheck.android.screen.user.rank_of_user.RankUpActivity
import vn.icheck.android.tracking.teko.TekoHelper
import vn.icheck.android.util.ick.logDebug


class IcFcmService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        logDebug(remoteMessage.toString())
        if (remoteMessage.data.containsKey("source") && remoteMessage.data["source"].equals("Insider")) {
            Insider.Instance.handleFCMNotification(applicationContext, remoteMessage)
            return
        }
        var title = remoteMessage.notification?.title
        if (title.isNullOrEmpty()) {
            title = remoteMessage.data["title"]
        }

        var body = remoteMessage.notification?.body
        if (body.isNullOrEmpty()) {
            body = remoteMessage.data["body"]
        }

        var targetType = remoteMessage.data["target_type"] ?: ""
        if (targetType.isEmpty()) {
            targetType = remoteMessage.data["type"] ?: ""
        }

        var targetID = remoteMessage.data["target_id"]
        if (targetID.isNullOrEmpty()) {
            targetID = remoteMessage.data["id"]
        }

        val action = remoteMessage.data["type"]
        val path = remoteMessage.data["path"] ?: ""
        if (body.isNullOrEmpty()) {
            return
        }

        logDebug("$title - $body - $targetType - $targetID - $action - $path")

        playNotificationSound()

        if (path.contains("inbox") || path.contains("inbox_user")) {
            if (ListConversationFragment.isOpenConversation) {
                return
            }

            val activity = ICheckApplication.currentActivity()
            if (activity != null && activity is ChatSocialDetailActivity) {
                // ID của user gửi tin nhắn đến
                val inboxFromID = Uri.parse(path).getQueryParameter("id")

                // Trường hợp fcm là tin nhắn đến
                if (path.contains("inbox")) {
                    if (activity.inboxRoomID == inboxFromID) {
                        return
                    }
                } else if (path.contains("inbox_user")) {
                    if (activity.inboxUserID == inboxFromID) {
                        return
                    }
                }
            }
        }

        when {
            path.contains("completed_mission") -> {
                val pathUri = Uri.parse(path).getQueryParameter("id")
                getMissionDetail(pathUri)
            }
            path.contains("level_up") -> {
                ICheckApplication.currentActivity()?.let { act ->
//                    Alerter.create(act)
//                            .setBackgroundColorRes(R.color.green_popup_notifi)
//                            .setDuration(3000)
//                            .setText(body)
//                            .setOnClickListener {
//                                FirebaseDynamicLinksActivity.startTargetPath(act, path)
//                            }
//                            .show()
                    startActivity(Intent(act, RankUpActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                }
            }
            path.contains("popup_image") -> {
                showDialogNotification(image = targetID, schema = path)
            }
            path.contains("popup_html") -> {
                showDialogNotification(htmlText = targetID)
            }
            path.contains("popup_link") -> {
                showDialogNotification(link = targetID)
            }
            else -> {
                ICheckApplication.currentActivity()?.let { act ->
                    Alerter.create(act)
                            .setBackgroundColorRes(R.color.green_popup_notifi)
                            .setDuration(3000)
                            .setText(body)
                            .setOnClickListener {
                                FirebaseDynamicLinksActivity.startTargetPath(act, path)
                            }
                            .show()
                }
            }
        }
    }

    @WorkerThread
    private fun showDialogNotification(image: String? = null, htmlText: String? = null, link: String? = null, schema: String? = null) {
        ICheckApplication.currentActivity().let { activity ->
            activity?.runOnUiThread {
                object : DialogNotificationFirebaseAds(activity, image, htmlText, link, schema) {
                    override fun onDismiss() {

                    }
                }.show()
            }
        }
    }

    private fun getMissionDetail(missionId: String?) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            return
        }
        if (missionId != null) {
            MissionInteractor().getMissionDetail(missionId, object : ICNewApiListener<ICResponse<ICMissionDetail>> {
                override fun onSuccess(obj: ICResponse<ICMissionDetail>) {
                    obj.data?.let {
                        TekoHelper.tagMissionCTASuccess(it)
                        PopupCompleteMissionActivity.start(it.totalBox, it.campaignId, applicationContext)
                    }
                }

                override fun onError(error: ICResponseCode?) {
                }

            })
        }
    }

    private fun playNotificationSound() {
        if (SettingManager.getSoundSetting) {
            RingtoneHelper(ICheckApplication.getInstance()).playAudio(R.raw.new_notification)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        PreferenceManager.getDefaultSharedPreferences(applicationContext).edit().putString("fcm_token", token).apply()
        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_LOG_IN_FIREBASE))
//        ICheckApplication.getInstance().mFirebase.registerFCMToken(token)
    }
}