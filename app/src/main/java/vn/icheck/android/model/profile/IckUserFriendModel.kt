package vn.icheck.android.model.profile

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.model.icklogin.IckUserInfoResponse
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.models.wall.ICUserFollowWall
import vn.icheck.android.network.models.wall.IcFriendResponse

class IckUserFriendModel (var listFriend: IcFriendResponse) : ICViewModel {
    var wallUserId:Long? = 0L
    var type = 1
    override fun getTag(): String = ""

    override fun getViewType(): Int {
        return ICViewTypes.FRIEND_WALL
    }
}