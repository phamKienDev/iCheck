package vn.icheck.android.screen.user.campaign.calback

import vn.icheck.android.network.models.ICUserFollowing

interface IFollowingListener {

    fun onMessageFollowingClicked()
    fun onFollowingClicked(obj: ICUserFollowing)
}