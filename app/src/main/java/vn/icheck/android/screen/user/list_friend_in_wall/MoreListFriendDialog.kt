package vn.icheck.android.screen.user.list_friend_in_wall

import android.content.Context
import kotlinx.android.synthetic.main.dialog_more_list_friend.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialog
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.wall.ICUserFollowWall
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText

abstract class MoreListFriendDialog(context: Context, val item: ICUserFollowWall) : BaseBottomSheetDialog(context, R.layout.dialog_more_list_friend, true) {
    fun show() {
        dialog.tvNameUnfollow.apply {
            text = if (!item.lastName.isNullOrEmpty() && !item.firstName.isNullOrEmpty()) {
                context.getString(R.string.bo_theo_doi_s_s, item.lastName, item.firstName)
            } else if (!item.firstName.isNullOrEmpty()) {
                context.getString(R.string.bo_theo_doi_s, item.firstName)
            } else if (!item.lastName.isNullOrEmpty()) {
                context.getString(R.string.bo_theo_doi_s, item.lastName)
            } else {
                dialog.context.getString(R.string.dang_cap_nhat)
            }
        }

        dialog.tvUnfriend.setText(R.string.huy_ket_ban_s, item.getUserName())
        dialog.view.background = ViewHelper.bgWhiteCorners4(dialog.context)
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