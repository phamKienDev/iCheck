package vn.icheck.android.component.friendsuggestion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.RelationshipManager
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.ItemFriendSuggestionBinding
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.relationship.RelationshipInteractor
import vn.icheck.android.network.models.ICUser
import vn.icheck.android.screen.user.social_chat.SocialChatActivity
import vn.icheck.android.screen.user.wall.IckUserWallActivity
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class FriendSuggestionHolder(parent: ViewGroup, val binding: ItemFriendSuggestionBinding = ItemFriendSuggestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)) : BaseViewHolder<ICUser>(binding.root) {
    private val interaction = RelationshipInteractor()

    private var listener: View.OnClickListener? = null

    override fun bind(obj: ICUser) {
        // Image avatar
        binding.imgAvatar.apply {
            WidgetUtils.loadImageUrl(this, obj.avatar, R.drawable.ic_user_svg)

            setOnClickListener {
                IckUserWallActivity.create(obj.id, it.context)
            }
        }

        binding.tvName.apply {
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

            text = obj.getName
            if (obj.kycStatus == 2) {
                setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_user_16dp, 0)
            } else {
                setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }

        // Text agree
        binding.btnAgree.apply {
            background = ViewHelper.btnPrimaryCorners4(context)

            setOnClickListener {
                inviteFriend(obj)
            }
        }

        checkSend(RelationshipManager.checkMyFriendInvitation(obj.id), obj)
    }

    private fun checkSend(isSend: Boolean?, obj: ICUser) {
        if (isSend == true) {
            binding.btnAgree.visibility = View.INVISIBLE
            binding.btnDisagree.visibility = View.VISIBLE
            (binding.btnDisagree as TextView?)?.text = "Nhắn tin"
            binding.tvStatus.visibility = View.VISIBLE
            // Text nhan tin
            binding.btnDisagree.setOnClickListener {
//                    ChatSocialDetailActivity.createRoomChat(it.context, obj.id, "user")
                SocialChatActivity.createRoomChat(it.context, obj.id)
            }
        } else {
            binding.btnAgree.visibility = View.VISIBLE
            binding.btnDisagree.visibility = View.VISIBLE
            binding.tvStatus.visibility = View.GONE
            (binding.btnDisagree as TextView?)?.text = "Xóa"
            // Text disagree
            binding.btnDisagree.setOnClickListener {
                removeSuggestion(obj)
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