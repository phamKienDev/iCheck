package vn.icheck.android.network.base

import android.text.TextUtils
import com.google.gson.Gson
import vn.icheck.android.network.feature.user.UserInteractor
import vn.icheck.android.network.models.*
import vn.icheck.android.network.util.SPStaticUtils

object SessionManager {

    var session: ICSessionData
        get() {
            val sessionJson = SPStaticUtils.getString(TagConstants.SESSION)
            var session: ICSessionData? = null
            if (!TextUtils.isEmpty(sessionJson)) {
                val gson = Gson()
                session = gson.fromJson(sessionJson, ICSessionData::class.java)
            }
            return session ?: ICSessionData()
        }
        set(session) {
            val gson = Gson()
            val _session = gson.toJson(session)
            SPStaticUtils.put(TagConstants.SESSION, _session)
        }

    val isUserLogged: Boolean
        get() {
            val session = session
            return !session.token.isNullOrEmpty() && session.userType != 1 && session.userType != null
        }

    val isDeviceLogged: Boolean
        get() {
            val session = session
            return !session.token.isNullOrEmpty() && (session.type == null || session.type == 1)
        }

    val isLoggedAnyType: Boolean
        get() {
            return !session.token.isNullOrEmpty()
        }

    fun getCoin(): Long {
        return if (isUserLogged) {
            session.user?.coin ?: 0
        } else {
            0
        }
    }

    fun setCoin(coin: Long) {
        val mSession = session
        val mUser = mSession.user
        mUser?.coin = if (isUserLogged) {
            coin
        } else {
            0
        }
        mSession.user = mUser
        session = mSession
    }

    fun updateUser(obj: ICReqUpdateUser) {
        val mSession = session
        val user = mSession.user

        if (user != null) {
            user.first_name = obj.first_name
            user.last_name = obj.last_name
            user.name = obj.name
            user.phone = obj.phone
            user.email = obj.email
            user.gender = obj.gender
            user.birth_day = obj.birth_day
            user.birth_month = obj.birth_month
            user.birth_year = obj.birth_year
            user.avatar = obj.avatar
            user.cover = obj.cover
            user.city_id = obj.city_id
            user.district_id = obj.district_id
            user.country_id = obj.country_id
            user.address = obj.address
        }

        mSession.user = user
        session = mSession
    }

    fun updateUser(obj: ICUser?) {
        val mSession = session
        mSession.user = obj
//        val user = mSession.user
//
//        if (user != null && obj != null) {
//            user.first_name = obj.first_name
//            user.last_name = obj.last_name
//            user.name = obj.name
//            user.phone = obj.phone
//            user.email = obj.email
//            user.gender = obj.gender
//            user.birth_day = obj.birth_day
//            user.birth_month = obj.birth_month
//            user.bd = obj.bd
//            user.birth_year = obj.birth_year
//            user.avatar = obj.avatar
//            user.avatar_thumbnails = obj.avatar_thumbnails
//            user.cover = obj.cover
//            user.cover_thumbnails = obj.cover_thumbnails
//            user.city_id = obj.city_id
//            user.district_id = obj.district_id
//            user.country_id = obj.country_id
//            user.address = obj.address
//        }
//        mSession.user = user
        session = mSession
    }

    fun updateUser() {
        UserInteractor().getUserMe(object : ICApiListener<ICUser> {
            override fun onError(error: ICBaseResponse?) {}
            override fun onSuccess(obj: ICUser) {
                updateUser(obj)
            }
        })
    }
}