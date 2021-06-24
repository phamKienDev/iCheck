package vn.icheck.android.component.friendrequest

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_friend_request.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.relationship.RelationshipInteractor
import vn.icheck.android.network.models.ICNotification
import vn.icheck.android.network.models.ICSearchUser
import vn.icheck.android.screen.user.wall.IckUserWallActivity
import vn.icheck.android.ichecklibs.util.showShortSuccessToast
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class FriendRequestHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_friend_request, parent, false)) {
    private val interaction = RelationshipInteractor()
    var onUpdateRequest:((Long)-> Unit)? = null

    fun bind(obj: ICSearchUser) {
        // Image avatar
        WidgetUtils.loadImageUrl(itemView.imgAvatar, obj.avatar, R.drawable.ic_user_svg)

        itemView.tvIcoin.apply {
            when (obj.rank?.level) {
                Constant.USER_LEVEL_SILVER -> {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_avatar_silver_24dp, 0)
                }
                Constant.USER_LEVEL_GOLD -> {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_avatar_gold_24dp, 0)
                }
                Constant.USER_LEVEL_DIAMOND -> {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_avatar_diamond_24dp, 0)
                }
                else -> {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_avatar_standard_24dp, 0)
                }
            }
        }

        // Text name
        itemView.tvName.apply {
            text = obj.getName
            if (obj.kycStatus == 2) {
                setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_user_16dp, 0)
            } else {
                setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }

        // Text agree
        itemView.btnAgree.setOnClickListener {
            updateFriendInvitation(2, obj)
        }

        // Text disagree
        itemView.btnDisagree.setOnClickListener {
            updateFriendInvitation(3, obj)
        }

        checkFriend(obj.requestStatus)

        itemView.imgAvatar.setOnClickListener {
            IckUserWallActivity.create(obj.id, it.context)
        }
    }

    private fun checkFriend(requestStatus: Int) {
        when (requestStatus) {
            Constant.FRIEND_REQUEST_AWAIT -> {
                itemView.btnAgree.visibility = View.VISIBLE
                itemView.btnDisagree.visibility = View.VISIBLE
                itemView.tvStatus.visibility = View.GONE
            }
            Constant.FRIEND_REQUEST_ACCEPTED -> {
                itemView.btnAgree.visibility = View.GONE
                itemView.btnDisagree.visibility = View.GONE
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.tvStatus.text = Html.fromHtml(itemView.tvStatus.context.getString(R.string.xxx_da_tro_thanh_ban_be_cua_ban, itemView.tvName.text))
            }
            Constant.FRIEND_REQUEST_DENIED -> {
                itemView.btnAgree.visibility = View.GONE
                itemView.btnDisagree.visibility = View.GONE
                itemView.tvStatus.visibility = View.VISIBLE
                itemView.tvStatus.text = Html.fromHtml(itemView.tvStatus.context.getString(R.string.ban_da_tu_choi_loi_moi_ket_ban_cua_s, itemView.tvName.text))
            }
            else -> {
                itemView.btnAgree.visibility = View.VISIBLE
                itemView.btnDisagree.visibility = View.VISIBLE
                itemView.tvStatus.visibility = View.GONE
            }
        }
    }

    private fun updateFriendInvitation(status: Int, objFriend: ICNotification) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (NetworkHelper.isNotConnected(activity)) {
                ToastUtils.showLongError(activity, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                return
            }

            DialogHelper.showLoading(activity)

            interaction.updateFriendInvitation(objFriend.id, status, object : ICNewApiListener<ICResponse<Boolean>> {
                override fun onSuccess(obj: ICResponse<Boolean>) {
                    DialogHelper.closeLoading(activity)

                    objFriend.status = status
                    checkFriend(objFriend.status)
                }

                override fun onError(error: ICResponseCode?) {
                    DialogHelper.closeLoading(activity)
                    ToastUtils.showLongError(activity, R.string.co_loi_xay_ra_vui_long_thu_lai)
                }
            })
        }
    }

    private fun updateFriendInvitation(status: Int, objFriend: ICSearchUser) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (NetworkHelper.isNotConnected(activity)) {
                ToastUtils.showLongError(activity, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                return
            }

            DialogHelper.showLoading(activity)

            interaction.updateFriendInvitation(objFriend.id, status, object : ICNewApiListener<ICResponse<Boolean>> {
                override fun onSuccess(obj: ICResponse<Boolean>) {
                    DialogHelper.closeLoading(activity)

                    if (status == 2) {
                        objFriend.requestStatus = Constant.FRIEND_REQUEST_ACCEPTED
                    } else {
                        objFriend.requestStatus = Constant.FRIEND_REQUEST_DENIED
                        itemView.context.apply {
                            showShortSuccessToast(getString(R.string.ban_da_tu_choi_ket_ban_voi_s, objFriend.getName))
                        }
                    }

                    checkFriend(objFriend.requestStatus)
                    onUpdateRequest?.invoke(objFriend.id)
                }

                override fun onError(error: ICResponseCode?) {
                    DialogHelper.closeLoading(activity)
                    ToastUtils.showLongError(activity, R.string.co_loi_xay_ra_vui_long_thu_lai)
                }
            })
        }
    }
}