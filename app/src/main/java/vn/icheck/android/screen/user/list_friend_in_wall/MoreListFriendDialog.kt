package vn.icheck.android.screen.user.list_friend_in_wall

import android.content.Context
import kotlinx.android.synthetic.main.dialog_more_list_friend.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialog
import vn.icheck.android.network.models.wall.ICUserFollowWall

abstract class MoreListFriendDialog(context: Context, val item: ICUserFollowWall) : BaseBottomSheetDialog(context, R.layout.dialog_more_list_friend, true) {
    fun show() {
        dialog.tvNameUnfollow.text = if (!item.lastName.isNullOrEmpty() && !item.firstName.isNullOrEmpty()) {
            "Bỏ theo dõi ${item.lastName + item.firstName}"
        } else if (!item.firstName.isNullOrEmpty()) {
            "Bỏ theo dõi ${item.firstName}"
        } else if (!item.lastName.isNullOrEmpty()) {
            "Bỏ theo dõi ${item.lastName}"
        } else {
            dialog.context.getString(R.string.dang_cap_nhat)
        }

        dialog.tvUnfriend.text = "Hủy kết bạn ${item.getUserName()}"

        dialog.show()
        listener()
    }

    private fun listener() {
        dialog.layoutUnfollow.setOnClickListener {
            onClickUnfollow()
        }
        dialog.layoutUnFriend.setOnClickListener {
            onClickUnFriend()
        }
        dialog.layoutReportUser.setOnClickListener {
            onClickReportUser()
        }
    }

    protected abstract fun onClickUnfollow()
    protected abstract fun onClickUnFriend()
    protected abstract fun onClickReportUser()
}