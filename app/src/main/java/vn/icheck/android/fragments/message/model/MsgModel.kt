package vn.icheck.android.fragments.message.model

interface MsgModel {
    fun getType():Int
    fun getFirebaseId():String
    fun getTimestamp():Long
}