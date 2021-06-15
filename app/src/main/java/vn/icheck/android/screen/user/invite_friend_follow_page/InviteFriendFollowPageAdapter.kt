package vn.icheck.android.screen.user.invite_friend_follow_page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_header_invite_friend_follow_page.view.*
import kotlinx.android.synthetic.main.layout_invite_user_follow_page.view.*
import okhttp3.internal.wait
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.adapter.RecyclerViewCustomAdapter
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.network.models.ICFriendNofollowPage
import vn.icheck.android.network.models.ICUser
import vn.icheck.android.screen.user.page_details.fragment.page.widget.message.MessageHolder
import vn.icheck.android.screen.user.wall.IckUserWallActivity
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.ick.rText
import vn.icheck.android.util.ick.setRankUser
import vn.icheck.android.util.kotlin.WidgetUtils

class InviteFriendFollowPageAdapter(val callback: InviteFriendFollowPageCallback, val listSelected: MutableList<ICUser>) : RecyclerViewCustomAdapter<Any>(callback) {

    private val headerType = 3
    private val itemType = 4
    private val empityType = 5

    private var iconEmpity: Int? = null
    private var titleEmpity: String? = null
    private var messageEmpity: String? = null

    fun addItem(obj: ICFriendNofollowPage) {
        listData.clear()
        listData.add(obj)
        notifyDataSetChanged()
    }

    fun setEmpity(icon: Int?, title: String?, message: String?) {
        isLoadMore = false
        isLoading = false

        iconEmpity = icon
        titleEmpity = title
        messageEmpity = message

        listData.clear()
        listData.add("")
        notifyDataSetChanged()
    }

    override fun getItemType(position: Int): Int {
        return when {
            listData[position] is ICFriendNofollowPage -> {
                headerType
            }
            listData[position] is ICUser -> {
                itemType
            }
            listData[position] is String -> {
                empityType
            }
            else -> {
                super.getItemViewType(position)
            }
        }
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            headerType -> HeaderHolder(parent)
            itemType -> ViewHolder(parent)
            empityType -> MessageHolder(parent)
            else -> super.createViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        when (holder) {
            is HeaderHolder -> {
                holder.bind(listData[position] as ICFriendNofollowPage)
            }
            is ViewHolder -> {
                holder.bind(listData[position] as ICUser)
            }
            is MessageHolder -> {
                holder.bind(iconEmpity, titleEmpity, messageEmpity, -1)
            }
            else -> {
                super.onBindViewHolder(holder, position, payloads)
            }
        }
    }

    inner class HeaderHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_header_invite_friend_follow_page, parent, false)) {
        fun bind(obj: ICFriendNofollowPage) {
            if (obj.invitationCount ?: 0 >= obj.maxInvitationCount ?: 0) {
                itemView.tvSpam.beVisible()
            } else {
                itemView.tvSpam.beGone()
            }

            if (!obj.listHideIds.isNullOrEmpty()) {
                val listId = obj.listHideIds!!.iterator()
                var show = true

                while (listId.hasNext()) {
                    val item = listId.next()
                    if (item == obj.pageId.toString()) {
                        show = false
                    }
                }

                if (show) {
                    itemView.container.beVisible()
                } else {
                    itemView.container.beGone()
                }

            } else {
                itemView.container.beVisible()

            }

            if (obj.rows.isNullOrEmpty()) {
                itemView.tvFriendCount.beGone()
                itemView.tvNote.beGone()
                itemView.view45.beGone()
            } else {
                itemView.tvFriendCount.beVisible()
                itemView.tvNote.beVisible()
                itemView.view45.beVisible()

                itemView.tvFriendCount.rText(R.string.ban_be_d, obj.count)
            }

            itemView.imgClose.setOnClickListener {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.HIDE_CONTAINER_FOLLOW_PAGE))
                itemView.container.beGone()
                obj.listHideIds?.toHashSet()?.add(obj.pageId.toString())
            }
        }
    }

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_invite_user_follow_page, parent, false)) {

        fun bind(obj: ICUser) {
            if (obj.selected) {
                itemView.selectButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkbox_single_on_24px, 0)
            } else {
                itemView.selectButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_ellipse, 0)
            }

            itemView.imgRank.setRankUser(obj.rank?.level)

            WidgetUtils.loadImageUrl(itemView.imgAvatar, obj.avatar, R.drawable.ic_avatar_default_84px)
            itemView.tvName.apply {
                text = obj.getName

                if (obj.kycStatus == 2) {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_user_24dp, 0)
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            }

            itemView.imgAvatar.setOnClickListener {
                ICheckApplication.currentActivity()?.let {
                    IckUserWallActivity.create(obj.id, it)
                }
            }

            itemView.setOnClickListener {
                if (!obj.selected) {
                    obj.selected = !obj.selected
                    listSelected.add(obj)
                } else {
                    listSelected.remove(obj)
                    obj.selected = !obj.selected
                }
                callback.getListSeleted(listSelected)
                notifyDataSetChanged()
            }
        }
    }
}