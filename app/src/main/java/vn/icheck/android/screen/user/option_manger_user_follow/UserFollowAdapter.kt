package vn.icheck.android.screen.user.option_manger_user_follow

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_load_more.view.*
import kotlinx.android.synthetic.main.item_message_campaign.view.*
import kotlinx.android.synthetic.main.item_user_follow.view.*
import vn.icheck.android.R
import vn.icheck.android.RelationshipManager
import vn.icheck.android.constant.Constant
import vn.icheck.android.constant.MAIN_USER
import vn.icheck.android.constant.MAIN_USER_FRIEND
import vn.icheck.android.constant.MAIN_USER_NOT_FRIEND
import vn.icheck.android.loyalty.base.setGone
import vn.icheck.android.network.models.wall.ICUserFollowWall
import vn.icheck.android.screen.user.social_chat.SocialChatActivity
import vn.icheck.android.screen.user.wall.IckUserWallActivity
import vn.icheck.android.util.checkTypeUser
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.ick.simpleText

class UserFollowAdapter constructor(val view: IUserFollowWallView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val listData = mutableListOf<ICUserFollowWall>()

    private var errorCode = 0
    private var isLoadMore = false
    private var isLoading = false

    private val itemType = 1
    private val showType = 2
    private val loadType = 3

    fun updateState(positionList: Int?) {
        listData[positionList!!].sendAddFriend = true
        notifyItemChanged(positionList)
    }

    fun setListData(list: MutableList<ICUserFollowWall>) {
        isLoadMore = list.size >= Constant.DEFAULT_ITEM_COUNT
        isLoading = false
        errorCode = 0
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addListData(list: MutableList<ICUserFollowWall>) {
        isLoadMore = list.size >= Constant.DEFAULT_ITEM_COUNT
        isLoading = false
        errorCode = 0
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun clearListData() {
        errorCode = 0
        isLoadMore = false
        isLoading = false
        listData.clear()
        notifyDataSetChanged()
    }

    fun showLoading() {
        listData.clear()
        errorCode = 0
        isLoadMore = true
        notifyDataSetChanged()
    }

    fun removeDataWithoutUpdate() {
        listData.clear()
    }

    fun setErrorCode(error: Int) {
        listData.clear()
        errorCode = error
        isLoadMore = false
        notifyDataSetChanged()
    }

    fun disableLoadMore() {
        isLoading = true
        notifyItemChanged(0, itemCount)
    }

    val isListNotEmpty: Boolean
        get() {
            return listData.isNotEmpty()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            itemType -> ViewHolder(inflater.inflate(R.layout.item_user_follow, parent, false))
            loadType -> LoadHolder(inflater.inflate(R.layout.item_load_more, parent, false))
            else -> MessageHolder(inflater.inflate(R.layout.item_message_campaign, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return if (listData.size > 0) {
            if (isLoadMore)
                listData.size + 1
            else
                listData.size
        } else {
            1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData.size > 0) {
            if (position < listData.size)
                itemType
            else
                loadType
        } else {
            if (isLoadMore)
                loadType
            else
                showType
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = listData[position]
                holder.bind(item)

                holder.itemView.setOnClickListener {
                    IckUserWallActivity.create(item.id, holder.itemView.context)
                }

                holder.itemView.layoutAddFriend.setOnClickListener {
                    view.addFriend(item, position)
                }

                holder.itemView.btnMessenger.setOnClickListener {
                    SocialChatActivity.createRoomChat(holder.itemView.context, item.id)
//                    ChatSocialDetailActivity.createRoomChat(holder.itemView.context, item.id ?: -1, "user")
                }
            }
            is LoadHolder -> {
                holder.bind()
                if (!isLoading) {
                    view.onLoadMore()
                    isLoading = true
                }
            }
            is MessageHolder -> {
                holder.bind(errorCode)

                holder.itemView.setOnClickListener {
                    view.onRefresh()
                }
            }
        }
    }

    private class ViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: ICUserFollowWall) {
            itemView.imgAvatar.setData(item.avatar, item.rank?.level, R.drawable.ic_square_avatar_default)
            itemView.tvName.apply {
                text = item.getUserName()
                if (item.kycStatus == 2) {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_user_16dp, 0)
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            }

            if (item.relateFriendCount > 0) {
                itemView.tv_related_friend.beVisible()
                itemView.tv_related_friend simpleText "${item.relateFriendCount} bạn chung"
            } else {
                itemView.tv_related_friend.beGone()
            }

            if (item.sendAddFriend == true) {
                itemView.btnMessenger.visibility = View.INVISIBLE
                itemView.btnDaGuiLoiMoi.visibility = View.VISIBLE
                itemView.layoutAddFriend.visibility = View.INVISIBLE
            } else {
                when (checkTypeUser(item.id!!)) {
                    MAIN_USER -> {

                    }
                    MAIN_USER_FRIEND -> {
                        itemView.btnMessenger.visibility = View.VISIBLE
                        itemView.btnDaGuiLoiMoi.visibility = View.INVISIBLE
                        itemView.layoutAddFriend.visibility = View.INVISIBLE
                        itemView.tv_related_friend.visibility = View.INVISIBLE
                    }
                    MAIN_USER_NOT_FRIEND -> {
                        if (RelationshipManager.checkMyFriendInvitation(item.id ?: 0L)) {
                            itemView.btnMessenger.visibility = View.INVISIBLE
                            itemView.btnDaGuiLoiMoi.visibility = View.VISIBLE
                            itemView.layoutAddFriend.visibility = View.INVISIBLE
                        } else {
                            if (item.userPrivacyConfig?.whoInviteFriend == "ONLY_ME") {
                                itemView.btnMessenger.visibility = View.INVISIBLE
                                itemView.btnDaGuiLoiMoi.visibility = View.INVISIBLE
                                itemView.layoutAddFriend.visibility = View.INVISIBLE
                                itemView.tv_related_friend.visibility = View.INVISIBLE
                            } else {
                                itemView.btnMessenger.visibility = View.INVISIBLE
                                itemView.btnDaGuiLoiMoi.visibility = View.INVISIBLE
                                itemView.layoutAddFriend.visibility = View.VISIBLE
                                itemView.tv_related_friend.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    private class LoadHolder constructor(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            view.progressBar.indeterminateDrawable.setColorFilter(if (vn.icheck.android.ichecklibs.Constant.primaryColor.isNotEmpty()) {
                Color.parseColor(vn.icheck.android.ichecklibs.Constant.primaryColor)
            } else {
                ContextCompat.getColor(view.context, vn.icheck.android.ichecklibs.R.color.colorPrimary)
            }, android.graphics.PorterDuff.Mode.MULTIPLY)
        }
    }

    private class MessageHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(errorCode: Int) {
            itemView.txtMessage.setGone()

            when (errorCode) {
                Constant.ERROR_EMPTY_SEARCH -> {
                    itemView.imgIcon.setImageResource(R.drawable.ic_group_120dp)
                    itemView.txtMessageDetail.text = "Xin lỗi chúng tôi không thể tìm kiếm được kết quả phù hợp với tìm kiếm của bạn"
                }
                Constant.ERROR_EMPTY_WATCHING -> {
                    itemView.imgIcon.setImageResource(R.drawable.ic_group_120dp)
                    itemView.txtMessageDetail.text = itemView.context.getString(R.string.ban_chua_theo_doi_nguoi_nao)
                }
                Constant.ERROR_EMPTY_FOLLOW -> {
                    itemView.imgIcon.setImageResource(R.drawable.ic_group_120dp)
                    itemView.txtMessageDetail.text = itemView.context.getString(R.string.chua_co_nguoi_nao_theo_doi_ban)
                }
                Constant.ERROR_SERVER -> {
                    itemView.imgIcon.setImageResource(R.drawable.ic_error_request)
                    itemView.txtMessageDetail.text = itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                }

                Constant.ERROR_INTERNET -> {
                    itemView.imgIcon.setImageResource(R.drawable.ic_error_network)
                    itemView.txtMessageDetail.text = itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                }
            }
        }
    }

}