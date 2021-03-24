package vn.icheck.android.services

import android.content.Intent
import android.net.Uri
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
import vn.icheck.android.tracking.teko.TekoHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.mission.MissionInteractor
import vn.icheck.android.network.models.ICMissionDetail
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.screen.user.popup_complete_mission.PopupCompleteMissionActivity
import vn.icheck.android.screen.user.rank_of_user.RankUpActivity
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

        var targetType = remoteMessage.data["target_type"]
        if (targetType.isNullOrEmpty()) {
            targetType = remoteMessage.data["type"]
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

        if (!ListConversationFragment.isOpenConversation && !ChatSocialDetailActivity.isDetailChatOpen) {
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