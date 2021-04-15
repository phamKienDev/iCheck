package vn.icheck.android.component.friendsuggestion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_friend_suggestion.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.RelationshipManager
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.relationship.RelationshipInteractor
import vn.icheck.android.network.models.ICUser
import vn.icheck.android.screen.user.social_chat.SocialChatActivity
import vn.icheck.android.screen.user.wall.IckUserWallActivity
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class FriendSuggestionHolder(parent: ViewGroup) : BaseViewHolder<ICUser>(LayoutInflater.from(parent.context).inflate(R.layout.item_friend_suggestion, parent, false)) {
    private val interaction = RelationshipInteractor()

    private var listener: View.OnClickListener? = null

    override fun bind(obj: ICUser) {
        // Layout parent
        (itemView as ViewGroup).apply {

            // Image avatar
            WidgetUtils.loadImageUrl(getChildAt(0) as CircleImageView, obj.avatar, R.drawable.ic_user_svg)
            getChildAt(0).setOnClickListener {
                IckUserWallActivity.create(obj.id, it.context)
            }

            itemView.tvName.apply {
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
            getChildAt(3).setOnClickListener {
                inviteFriend(obj)
            }



            checkSend(RelationshipManager.checkMyFriendInvitation(obj.id), obj)

        }
    }

    private fun checkSend(isSend: Boolean?, obj:ICUser) {
        (itemView as ViewGroup).apply {
            if (isSend == true) {
                getChildAt(3).visibility = View.INVISIBLE
                getChildAt(4).visibility = View.VISIBLE
                (getChildAt(4) as TextView?)?.text = "Nhắn tin"
                getChildAt(5).visibility = View.VISIBLE
                // Text nhan tin
                getChildAt(4).setOnClickListener {
//                    ChatSocialDetailActivity.createRoomChat(it.context, obj.id, "user")
                    SocialChatActivity.createRoomChat(it.context, obj.id)
                }
            } else {
                getChildAt(3).visibility = View.VISIBLE
                getChildAt(4).visibility = View.VISIBLE
                getChildAt(5).visibility = View.GONE
                (getChildAt(4) as TextView?)?.text = "Xóa"
                // Text disagree
                getChildAt(4).setOnClickListener {
                    removeSuggestion(obj)
                }
            }
        }
    }

    fun setOnRemoveListener(listener: View.OnClickListener) {
        this.listener = listener
    }

    private fun inviteFriend(objFriend: ICUser) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (NetworkHelper.isNotConnected(activity)) {
                ToastUtils.showLongError(activity, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                return
            }

            DialogHelper.showLoading(activity)

            interaction.inviteFriend(objFriend.id, object : ICNewApiListener<ICResponse<Boolean>> {
                override fun onSuccess(obj: ICResponse<Boolean>) {
                    DialogHelper.closeLoading(activity)
                    checkSend(true, objFriend)
                }

                override fun onError(error: ICResponseCode?) {
                    DialogHelper.closeLoading(activity)
                    ToastUtils.showLongError(activity, R.string.co_loi_xay_ra_vui_long_thu_lai)
                }
            })
        }
    }

    private fun removeSuggestion(objFriend: ICUser) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (NetworkHelper.isNotConnected(activity)) {
                ToastUtils.showLongError(activity, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                return
            }

            DialogHelper.showLoading(activity)

            interaction.removeFriendSuggestion(objFriend.id, object : ICNewApiListener<ICResponse<List<ICUser>>> {
                override fun onSuccess(obj: ICResponse<List<ICUser>>) {
                    DialogHelper.closeLoading(activity)
                    listener?.onClick(null)
                }

                override fun onError(error: ICResponseCode?) {
                    DialogHelper.closeLoading(activity)
                    ToastUtils.showLongError(activity, R.string.co_loi_xay_ra_vui_long_thu_lai)
                }
            })
        }
    }
}