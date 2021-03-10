package vn.icheck.android.helper

import android.content.ContentResolver
import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import vn.icheck.android.network.base.SettingManager

class RingtoneHelper constructor(private val context: Context) {
    private var ringtoneManager: Ringtone? = null
//    private var mediaLength = 0

    fun playAudio(audio: Int) {
        killMediaPlayer()

        if (!SettingManager.getSoundSetting) {
            return
        }

        try {
            val uriBeepSound = Uri.Builder()
                    .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                    .authority(context.resources.getResourcePackageName(audio))
                    .appendPath(context.resources.getResourceTypeName(audio))
                    .appendPath(context.resources.getResourceEntryName(audio))
                    .build()

            ringtoneManager = RingtoneManager.getRingtone(context, uriBeepSound)
            ringtoneManager?.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun killMediaPlayer() {
        try {
            ringtoneManager?.stop()
            ringtoneManager = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}