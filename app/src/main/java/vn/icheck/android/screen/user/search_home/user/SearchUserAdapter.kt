package vn.icheck.android.screen.user.search_home.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.item_user_search_result.view.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.adapter.RecyclerViewSearchAdapter
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.IRecyclerViewSearchCallback
import vn.icheck.android.chat.icheckchat.screen.detail.ChatSocialDetailActivity
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.relationship.RelationshipInteractor
import vn.icheck.android.network.models.ICSearchUser
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.rText
import vn.icheck.android.util.kotlin.ToastUtils

class SearchUserAdapter(val typeView: Int, val callback: IRecyclerViewSearchCallback? = null) : RecyclerViewSearchAdapter<ICSearchUser>(callback) {
    private var typeResult = 1 // dạng view hiển thị màn searchResultActivity

    override fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(if (typeView == typeResult) {
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_user_search_result, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.item_user_search_result, parent, false)
        })
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        if (holder is ViewHolder) {
            holder.bind(listData[position])
        }
    }

    class ViewHolder(view: View) : BaseViewHolder<ICSearchUser>(view) {
        private val interaction = RelationshipInteractor()

        private var isMyFriendInvitationUser: Boolean? = null
        private var isMyFriend: Boolean? = null
        private var isFriendInvitationMeUser: Boolean? = null

        override fun bind(obj: ICSearchUser) {
            itemView.layoutAvatar.setData(obj.avatar, obj.rank?.level, R.drawable.ic_avatar_default_84px)
            itemView.tvTitle.apply {
                text = obj.getName
                if (obj.kycStatus == 2) {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_user_16dp, 0)
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            }

            isMyFriendInvitationUser = null
            isMyFriend = null
            isFriendInvitationMeUser = null

            if (obj.userPrivacyConfig != null) {
                if (obj.userPrivacyConfig!!.whoInviteFriend == Constant.ONLY_ME) {
                    itemView.btnConfirm.beGone()
                } else {
                    checkStatusFirebase(obj)
                }
            } else {
                checkStatusFirebase(obj)
            }

            itemView.tvMessage.setOnClickListener {
//                SocialChatActivity.createRoomChat(it.context, obj.id)
                ChatSocialDetailActivity.createRoomChat(it.context, obj.id, "user")
            }

            itemView.setOnClickListener {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.OPEN_DETAIL_USER, obj))
            }
        }

        private fun checkStatusFirebase(obj: ICSearchUser) {
            if (ICheckApplication.getInstance().mFirebase.auth.currentUser != null) {
                //người khác gửi kết bạn cho mình
                ICheckApplication.getInstance().mFirebase.registerRelationship(Constant.friendInvitationMeUserIdList, obj.id.toString(), object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        isFriendInvitationMeUser = snapshot.value != null && snapshot.value is Long
                        checkStatus(obj)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        isFriendInvitationMeUser = false
                        checkStatus(obj)
                    }
                })

                //mình gửi kết bạn đến người khác
                ICheckApplication.getInstance().mFirebase.registerRelationship(Constant.myFriendInvitationUserIdList, obj.id.toString(), object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        isMyFriendInvitationUser = snapshot.value != null && snapshot.value is Long
                        checkStatus(obj)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        isMyFriendInvitationUser = false
                        checkStatus(obj)
                    }
                })

                //friend của mình
                ICheckApplication.getInstance().mFirebase.registerRelationship(Constant.myFriendIdList, obj.id.toString(), object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        isMyFriend = snapshot.value != null && snapshot.value is Long
                        checkStatus(obj)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        isMyFriend = false
                        checkStatus(obj)
                    }
                })
            }
        }

        private fun checkStatus(obj: ICSearchUser) {
            val friend = if (obj.relateFriendCount > 0) {
                itemView.context.rText(R.string.d_ban_chung, obj.relateFriendCount)
            } else {
                ""
            }

            val city = if (!obj.city?.name.isNullOrEmpty()) {
                obj.city!!.name
            } else {
                ""
            }

            val content = if (friend.isNotEmpty()) {
                if (city.isNotEmpty()) {
                    val string = itemView.context.getString(R.string.xxx_center_dot_xxx, friend, city)
                    string
                } else {
                    friend
                }
            } else {
                if (city.isNotEmpty()) {
                    city
                } else {
                    ""
                }
            }

            if (isFriendInvitationMeUser != null && isMyFriend != null && isMyFriendInvitationUser != null) {
                when {
                    isMyFriend!! -> {
                        itemView.tvContent.text = city
                        itemView.btnConfirm.visibility = View.INVISIBLE
                        itemView.tvMessage.visibility = View.VISIBLE
                    }
                    isFriendInvitationMeUser!! -> {
                        itemView.tvContent.text = content

                        itemView.btnConfirm.isEnabled = true
                        itemView.btnConfirm.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        itemView.btnConfirm.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_corners_4_light_blue_solid)
                        itemView.btnConfirm rText R.string.dong_y_ket_ban
                        itemView.btnConfirm.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))

                        itemView.btnConfirm.visibility = View.VISIBLE
                        itemView.tvMessage.visibility = View.INVISIBLE

                        itemView.btnConfirm.setOnClickListener {
                            inviteFriend(obj, 2)
                        }
                    }
                    isMyFriendInvitationUser!! -> {
                        itemView.tvContent.text = content

                        itemView.btnConfirm.isEnabled = false
                        itemView.btnConfirm.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        itemView.btnConfirm.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_gray_f0_corners_4)
                        itemView.btnConfirm.setText(R.string.da_gui_loi_moi)
                        itemView.btnConfirm.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorSecondText))

                        itemView.btnConfirm.visibility = View.VISIBLE
                        itemView.tvMessage.visibility = View.INVISIBLE
                    }
                    else -> {
                        itemView.tvContent.text = content

                        itemView.btnConfirm.isEnabled = true
                        itemView.btnConfirm.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_white_12dp, 0, 0, 0)
                        itemView.btnConfirm.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_corners_4_light_blue_solid)
                        itemView.btnConfirm.setText(R.string.ket_ban)
                        itemView.btnConfirm.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))

                        itemView.btnConfirm.visibility = View.VISIBLE
                        itemView.tvMessage.visibility = View.INVISIBLE

                        itemView.btnConfirm.setOnClickListener {
                            inviteFriend(obj, null)
                        }
                    }
                }
            }
        }

        private fun inviteFriend(objSearch: ICSearchUser, status: Int?) {
            if (!SessionManager.isUserLogged) {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_REQUIRE_LOGIN))
                return
            }

            ICheckApplication.currentActivity()?.let { activity ->
                if (NetworkHelper.isNotConnected(activity)) {
                    ToastUtils.showLongError(activity, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                    return
                }

                DialogHelper.showLoading(activity)

                interaction.inviteFriend(objSearch.id, status, object : ICNewApiListener<ICResponse<Boolean>> {
                    override fun onSuccess(obj: ICResponse<Boolean>) {
                        DialogHelper.closeLoading(activity)
                    }

                    override fun onError(error: ICResponseCode?) {
                        DialogHelper.closeLoading(activity)
                        ToastUtils.showLongError(activity, R.string.co_loi_xay_ra_vui_long_thu_lai)
                    }
                })
            }
        }
    }
}
