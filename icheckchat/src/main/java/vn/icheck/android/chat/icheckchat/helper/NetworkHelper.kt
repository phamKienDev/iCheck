package vn.icheck.android.chat.icheckchat.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.chat.icheckchat.model.MCConversation
import vn.icheck.android.chat.icheckchat.model.MCMedia
import vn.icheck.android.chat.icheckchat.model.MCMessageEvent

object NetworkHelper {

    const val LIMIT = 10

    val gson = GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create()

    private fun getNetworkInfo(context: Context): NetworkInfo? {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo
    }

    fun isNotConnected(context: Context?): Boolean {
        if (context == null)
            return true

        val info = getNetworkInfo(context)
        return info == null || !info.isConnectedOrConnecting
    }

    fun toJson(obj: Any): String {
        return gson.toJson(obj)
    }

    fun parseListAttachment(json: String?): MutableList<MCMedia>? {
        return try {
            val listType = object : TypeToken<List<MCMedia>>() {}.type
            gson.fromJson(json, listType)
        } catch (e: Exception) {
            null
        }
    }
}