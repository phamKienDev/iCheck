package vn.icheck.android.screen.user.user_follow_page

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.item_list_user_search_result.view.*
import kotlinx.android.synthetic.main.layout_info_user_follow_page_holder.view.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.adapter.RecyclerViewCustomAdapter
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.component.view.ListAvatar
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.relationship.RelationshipInteractor
import vn.icheck.android.network.models.ICPageOverview
import vn.icheck.android.network.models.ICSearchUser
import vn.icheck.android.network.models.feed.ICAvatarOfFriend
import vn.icheck.android.screen.user.invite_friend_follow_page.InviteFriendFollowPageActivity
import vn.icheck.android.screen.user.wall.IckUserWallActivity
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beInvisible
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.ToastUtils

class UserFollowPageAdapter(callback: IRecyclerViewCallback) : RecyclerViewCustomAdapter<Any>(callback) {

    private val infoPageType = 5
    private val countType = 6
    private val itemUserType = 7

    fun addHeader(obj: ICPageOverview) {
        if (listData.isNotEmpty()) {
            listData.add(0, obj)
        } else {
            listData.add(obj)
        }
        notifyDataSetChanged()
    }

    fun addCount(obj: Int) {
        listData.add(obj)
        notifyDataSetChanged()
    }

    override fun getItemType(position: Int): Int {
        return if (listData[position] is ICPageOverview) {
            infoPageType
        } else if (listData[position] is Int) {
            countType
        } else {
            itemUserType
        }
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            infoPageType -> {
                InfoHolder(parent)
            }
            countType -> {
                CountHolder(parent)
            }
            itemUserType -> {
                ListUserHolder(parent)
            }
            else -> {
                super.onCreateViewHolder(parent, viewType)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        when (holder) {
            is InfoHolder -> {
                holder.bind(listData[position] as ICPageOverview)
            }
            is CountHolder -> {
                holder.bind(listData[position] as Int)
            }
            is ListUserHolder -> {
                holder.bind(listData[position] as ICSearchUser)
            }
        }
    }

    inner class InfoHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_info_user_follow_page_holder, parent, false)) {
        fun bind(obj: ICPageOverview) {

            val listAvatar = mutableListOf<String>()
            for (item in obj.followers ?: mutableListOf()) {
                listAvatar.add(item.avatar)
            }
            val icAvatarOfFriend = ICAvatarOfFriend(listAvatar, listAvatar.size)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val imgListAvatar = itemView.findViewById<ListAvatar>(R.id.img_list_avatar)
                if (listAvatar.isNullOrEmpty()) {
                    imgListAvatar.visibility = View.GONE
                } else {
                    imgListAvatar.visibility = View.VISIBLE
                    imgListAvatar.bind(icAvatarOfFriend, null, true, R.drawable.ic_avatar_default_84px)
                }
            }


            itemView.tv_count_like.text = TextHelper.formatMoney(obj.likedCountOnPosts)
            itemView.tv_count_follow.text = TextHelper.formatMoney(obj.followCount)

            itemView.tvInvite.setOnClickListener {
                if (SessionManager.isUserLogged) {
                    ICheckApplication.currentActivity()?.let {
                        ActivityUtils.startActivity<InviteFriendFollowPageActivity, Long>(it, Constant.DATA_1, obj.id!!)
                    }
                } else {
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_LOG_IN, obj.id))
                }
            }
        }
    }

    class CountHolder(parent: ViewGroup) : RecyclerView.ViewHolder(createView(parent.context)) {
        fun bind(count: Int) {
            (itemView as AppCompatTextView).run {
                text = "$count Người theo dõi trang này"
            }
        }

        companion object {
            fun createView(context: Context): AppCompatTextView {
                return AppCompatTextView(context).also {
                    it.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                        it.setMargins(SizeHelper.size12, SizeHelper.size10, 0, 0)
                    }
                    it.ellipsize = TextUtils.TruncateAt.END
                    it.maxLines = 1
                    it.setTextColor(vn.icheck.android.ichecklibs.Constant.getSecondaryColor(context))
                    it.setTypeface(Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf"))
                    it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                }
            }
        }
    }

    inner class ListUserHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_user_search_result, parent, false)) {
        private val interaction = RelationshipInteractor()

        private var isMyFriendInvitationUser: Boolean? = null
        private var isMyFriend: Boolean? = null
        private var isFriendInvitationMeUser: Boolean? = null

        fun bind(obj: ICSearchUser) {
            itemView.layoutAvatar.setData(obj.avatar, obj.rank?.level, R.drawable.ic_avatar_default_84px)
            itemView.tvTitle.apply {
                text = obj.getName
                if (obj.kycStatus == 2) {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_user_16dp, 0)
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            }

            if (obj.userPrivacyConfig != null) {
                if (obj.userPrivacyConfig!!.whoInviteFriend == Constant.ONLY_ME) {
                    itemView.btnConfirm.beGone()
                } else {
                    checkStatusFirebase(obj)
                }
            } else {
                checkStatusFirebase(obj)
            }


            itemView.setOnClickListener {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.OPEN_DETAIL_USER, obj))
            }

            itemView.layoutAvatar.setOnClickListener {
                ICheckApplication.currentActivity()?.let {
                    IckUserWallActivity.create(obj.id, it)
                }
            }
            itemView.tvTitle.setOnClickListener {
                ICheckApplication.currentActivity()?.let {
                    IckUserWallActivity.create(obj.id, it)
                }
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
            if (SessionManager.session.user?.id == obj.id) {
                itemView.btnConfirm.beInvisible()
                itemView.tvMessage.beInvisible()
            } else {
                if (isFriendInvitationMeUser != null && isMyFriend != null && isMyFriendInvitationUser != null) {
                    val content = if (obj.relateFriendCount > 0) {
                        "${obj.relateFriendCount} bạn chung"
                    } else {
                        ""
                    }

                    when  {
                        isFriendInvitationMeUser!! -> {
                            itemView.tvContent.text = content

                            itemView.btnConfirm.isEnabled = true
                            itemView.btnConfirm.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                            itemView.btnConfirm.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_corners_4_light_blue_solid)
                            itemView.btnConfirm.text = "Đồng ý kết bạn"
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
                            itemView.btnConfirm.setTextColor(vn.icheck.android.ichecklibs.Constant.getSecondTextColor(itemView.context))

                            itemView.btnConfirm.visibility = View.VISIBLE
                            itemView.tvMessage.visibility = View.INVISIBLE
                        }
                        isMyFriend!! -> {
                            itemView.tvContent.text = ""
                            itemView.btnConfirm.visibility = View.INVISIBLE
                            itemView.tvMessage.visibility = View.VISIBLE
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

        }

        private fun inviteFriend(objSearch: ICSearchUser, status: Int?) {
            if (!SessionManager.isUserLogged) {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_LOG_IN))
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