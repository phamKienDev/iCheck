package vn.icheck.android.screen.user.list_friend_in_wall

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_error_history_topup.view.*
import kotlinx.android.synthetic.main.item_list_friend_of_wall.view.*
import kotlinx.android.synthetic.main.item_load_more.view.*
import vn.icheck.android.R
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.ViewHelper.fillDrawableColor
import vn.icheck.android.network.models.wall.ICUserFollowWall

class ListFriendOfWallAdapter(val view: ListFriendListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listData = mutableListOf<ICUserFollowWall>()

    private var errorCode = 0
    private var isLoadMore = true
    private var isLoading = true

    private val itemType = 1
    private val showType = 2
    private val loadType = 3

    fun updateState(positionList: Int, status: Int) {
        listData[positionList].userRelationStatus = status
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

    fun removeItem(position: Int) {
        listData.removeAt(position)

        if (listData.isNullOrEmpty()) {
            errorCode = Constant.ERROR_EMPTY
            notifyDataSetChanged()
        } else {
            notifyItemRemoved(position)
            notifyItemChanged(position, itemCount)
        }
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
            itemType -> ViewHolder(inflater.inflate(R.layout.item_list_friend_of_wall, parent, false))
            loadType -> LoadHolder(inflater.inflate(R.layout.item_load_more, parent, false))
            else -> MessageHolder(inflater.inflate(R.layout.item_error_history_topup, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return if (listData.size > 0) {
            if (isLoadMore) listData.size + 1
            else listData.size
        } else {
            1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData.size > 0) {
            if (position < listData.size) itemType
            else loadType
        } else {
            if (isLoadMore) loadType
            else showType
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = listData[position]
                holder.bind(item)

                holder.itemView.imgMore.setOnClickListener {
                    view.showBottomSheetMoreAction(item, position)
                }

                holder.itemView.setOnClickListener {
                    view.clickUser(item)
                }

                holder.itemView.tvChat.setOnClickListener {
                    view.goToChat(item)
                }

                holder.itemView.tvAddFriend.setOnClickListener {
                    view.goToAddFriend(item, position)
                }

                holder.itemView.btnAcceptFriend.setOnClickListener {
                    view.goToAcceptFriend(item, position)
                }

                holder.itemView.btnRefuseFriend.setOnClickListener {
                    view.goToRefuseFriend(item, position)
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
            }
        }
    }

    private class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ICUserFollowWall) {
            itemView.imgMore.fillDrawableColor(R.drawable.ic_more_disable_24dp)
            itemView.btnRefuseFriend.apply {
                background = vn.icheck.android.ichecklibs.ViewHelper.bgWhiteStrokePrimary1Corners4(context)
            }

            vn.icheck.android.ichecklibs.ViewHelper.bgPrimaryCorners4(itemView.context).apply {
                itemView.tvChat.background = this
                itemView.tvAddFriend.background = this
                itemView.layoutAccept.background = this
            }

            itemView.tvSendRequest.apply {
                background=vn.icheck.android.ichecklibs.ViewHelper.bgGrayCorners4(context)
            }

            when (item.userRelationStatus) {
                //Chưa là bạn bè
                1 -> {
                    itemView.tvChat.visibility = View.GONE
                    itemView.tvAddFriend.visibility = View.VISIBLE
                    itemView.tvSendRequest.visibility = View.GONE
                    itemView.layoutInviteToMe.visibility = View.GONE
                }
                // B -> send friend request -> A
                2 -> {
                    itemView.layoutInviteToMe.visibility = View.VISIBLE
                    itemView.tvChat.visibility = View.GONE
                    itemView.tvAddFriend.visibility = View.GONE
                    itemView.tvSendRequest.visibility = View.GONE
                }

                // A -> send friend request -> B
                3 -> {
                    itemView.tvChat.visibility = View.GONE
                    itemView.tvAddFriend.visibility = View.GONE
                    itemView.tvSendRequest.visibility = View.VISIBLE
                    itemView.layoutInviteToMe.visibility = View.GONE
                }

                // Friend
                4 -> {
                    itemView.tvChat.visibility = View.VISIBLE
                    itemView.tvAddFriend.visibility = View.GONE
                    itemView.tvSendRequest.visibility = View.GONE
                    itemView.layoutInviteToMe.visibility = View.GONE
                }

                // Block
                5 -> {
                    itemView.tvChat.visibility = View.GONE
                    itemView.tvAddFriend.visibility = View.GONE
                    itemView.tvSendRequest.visibility = View.GONE
                    itemView.layoutInviteToMe.visibility = View.GONE
                }
            }

            itemView.avatar_friend.setData(item.avatar, item.rank?.level, R.drawable.ic_square_avatar_default)
            itemView.tvName.apply {
                text = item.getUserName()
                if (item.kycStatus == 2) {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_user_16dp, 0)
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            }
        }
    }

    private class LoadHolder constructor(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            itemView.progressBar.indeterminateDrawable.setColorFilter(vn.icheck.android.ichecklibs.Constant.getPrimaryColor(view.context), android.graphics.PorterDuff.Mode.MULTIPLY)
        }
    }

    private class MessageHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(errorCode: Int) {
            when (errorCode) {
                Constant.ERROR_EMPTY_SEARCH -> {
                    itemView.imgIcon.setImageResource(R.drawable.ic_search_90dp)
                    itemView.txtMessage.visibility = View.GONE

                    val layoutParams = ViewHelper.createLayoutParams()
                    layoutParams.setMargins(SizeHelper.size50, SizeHelper.size12, SizeHelper.size50, 0)
                    itemView.txtMessage2.visibility = View.VISIBLE
                    itemView.txtMessage2.layoutParams = layoutParams
                    itemView.txtMessage2.text = itemView.context.getString(R.string.xin_loi_chung_toi_khong_the_tim_duoc_ket_qua_phu_hop_voi_tim_kiem_cua_ban)
                }

                Constant.ERROR_EMPTY -> {
                    itemView.imgIcon.setImageResource(R.drawable.ic_group_120dp)
                    itemView.txtMessage.text = itemView.context.getString(R.string.ban_chua_co_ban_be_nao)

                    val layoutParams = ViewHelper.createLayoutParams()
                    layoutParams.setMargins(SizeHelper.size67, SizeHelper.size6, SizeHelper.size67, 0)
                    itemView.txtMessage2.visibility = View.VISIBLE
                    itemView.txtMessage2.text = "Kết bạn bốn phương để cùng thảo luận về sản phẩm chính hãng nhé"
                }

                Constant.ERROR_SERVER -> {
                    itemView.imgIcon.setImageResource(R.drawable.ic_error_request)
                    itemView.txtMessage.text = itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    itemView.txtMessage2.visibility = View.INVISIBLE
                }

                Constant.ERROR_INTERNET -> {
                    itemView.imgIcon.setImageResource(R.drawable.ic_error_network)
                    itemView.txtMessage.text = itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                    itemView.txtMessage2.visibility = View.INVISIBLE
                }
            }
        }
    }
}