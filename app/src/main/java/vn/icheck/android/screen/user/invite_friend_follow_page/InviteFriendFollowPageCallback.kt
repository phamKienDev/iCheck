package vn.icheck.android.screen.user.invite_friend_follow_page

import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.network.models.ICUser

interface InviteFriendFollowPageCallback : IRecyclerViewCallback {
    fun getListSeleted(selected: MutableList<ICUser>)
}