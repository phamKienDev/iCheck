package vn.icheck.android.screen.user.list_friend_in_wall

import vn.icheck.android.network.models.wall.ICUserFollowWall

interface ListFriendListener {
    fun showBottomSheetMoreAction(item: ICUserFollowWall, position: Int)
    fun onRefresh()
    fun onLoadMore()
    fun clickUser(item: ICUserFollowWall)
    fun goToChat(item: ICUserFollowWall)
    fun goToAddFriend(item: ICUserFollowWall, position: Int)
    fun goToAcceptFriend(item: ICUserFollowWall, position: Int)
    fun goToRefuseFriend(item: ICUserFollowWall, position: Int)
}