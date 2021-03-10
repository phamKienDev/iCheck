package vn.icheck.android.screen.user.option_manger_user_follow

import vn.icheck.android.network.models.wall.ICUserFollowWall

interface IUserFollowWallView {
    fun onRefresh()
    fun onLoadMore()
    fun addFriend(item: ICUserFollowWall, position: Int)
}