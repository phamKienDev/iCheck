package vn.icheck.android.network.models


const val USER = 1
const val GROUP = 2
data class RoomMessages(
        var avatar: String?,
        var userName: String?,
        var message: String,
        var timeStamp: Long,
        var unreadCount: Long?
) {
    var id: String? = null
    var senderId: String? = null
    var idNumber: Long? = null
    var type = USER
    var logo: String? = null
}