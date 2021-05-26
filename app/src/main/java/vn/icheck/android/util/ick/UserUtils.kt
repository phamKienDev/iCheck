package vn.icheck.android.util.ick

import vn.icheck.android.network.model.icklogin.IckUserInfoResponse

enum class Privacy{
    ONLY_ME,
    FRIEND,
    EVERYONE
}

fun IckUserInfoResponse.getInfoPrivacy():Privacy {
    if (this.data?.userPrivacyConfig != null) {
        return when (this.data?.userPrivacyConfig?.whoViewYourInfo) {
            "ONLY_ME" -> Privacy.ONLY_ME
            "FRIEND" -> Privacy.FRIEND
            "EVERYONE" -> Privacy.EVERYONE
            else -> Privacy.EVERYONE
        }
    }
    return Privacy.EVERYONE
}

fun IckUserInfoResponse.getInvitePrivacy():Privacy {
    if (this.data?.userPrivacyConfig != null) {
        return when (this.data?.userPrivacyConfig?.whoInviteFriend) {
            "ONLY_ME" -> Privacy.ONLY_ME
            "FRIEND" -> Privacy.FRIEND
            "EVERYONE" -> Privacy.EVERYONE
            else -> Privacy.EVERYONE
        }
    }
    return Privacy.EVERYONE
}