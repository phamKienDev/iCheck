package vn.icheck.android.screen.user.listnotification

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.`null`.NullHolder
import vn.icheck.android.screen.user.campaign.calback.IMessageListener
import vn.icheck.android.screen.user.campaign.holder.base.LongMessageHolder
import vn.icheck.android.screen.user.home_page.model.ICHomeItem
import vn.icheck.android.component.friendrequest.FriendRequestComponent
import vn.icheck.android.component.friendsuggestion.FriendSuggestionComponent
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.models.*
import vn.icheck.android.screen.user.listnotification.othernotification.OtherNotificationHolder
import vn.icheck.android.screen.user.listnotification.viewholder.interactive.InteractiveComponent
import vn.icheck.android.screen.user.listnotification.viewholder.pagerelated.NotificationPageComponent
import vn.icheck.android.screen.user.listnotification.viewholder.productnotice.ProductNoticeComponent

class ListNotificationAdapter(private val messageListener: IMessageListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listData = mutableListOf<ICHomeItem>()

    private var iconMessage = 0
    private var errorMessage: String? = null
    private var buttonID: Int? = null

    fun markReadAll() {
        for (obj in listData) {
            if (obj.data != null) {
                when (obj.viewType) {
                    ICViewTypes.INTERACTIVE_TYPE -> {
                        for (item in obj.data as MutableList<ICNotification>) {
                            item.isReaded = true
                            item.status = 1
                        }
                    }
                    ICViewTypes.OTHER_NOTIFICATION_TYPE -> {
                        (obj.data as ICNotification).isReaded = true
                        (obj.data as ICNotification).status = 1
                    }
                    ICViewTypes.PRODUCT_NOTICE_TYPE -> {
                        for (item in obj.data as MutableList<ICNotification>) {
                            item.isReaded = true
                            item.status = 1
                        }
                    }
                }
            }
        }
        notifyDataSetChanged()
    }

    @Synchronized
    fun addItem(obj: ICHomeItem) {
        listData.add(obj)
        if (errorMessage == null) {
            notifyItemInserted(listData.size - 1)
        } else {
            errorMessage = null
            notifyDataSetChanged()
        }
    }

    @Synchronized
    fun addItem(list: MutableList<ICHomeItem>) {
        listData.addAll(list)

        if (errorMessage == null) {
            notifyItemRangeInserted(listData.size - list.size, listData.size - 1)
        } else {
            errorMessage = null
            notifyDataSetChanged()
        }
    }

    @Synchronized
    fun updateItem(obj: ICHomeItem) {
        for (i in listData.size - 1 downTo 0) {
            if (listData[i].widgetID == obj.widgetID) {
                if (obj.data != null) {
                    listData[i].data = obj.data
                    notifyItemChanged(i)
                } else {
                    listData.removeAt(i)
                    notifyItemRemoved(i)
                }
                checkList()
                return
            }
        }
    }

    private fun checkList() {
        if (listData.isEmpty()) {
            setError(R.drawable.ic_no_campaign, ICheckApplication.getInstance().getString(R.string.ban_chua_co_thong_bao_nao), 0)
            notifyDataSetChanged()
        }
    }

    fun setError(icon: Int, error: String, button: Int?) {
        listData.clear()
        iconMessage = icon
        errorMessage = error
        buttonID = button
        notifyDataSetChanged()
    }

    fun removeAllView() {
        for (i in listData.size - 1 downTo 0) {
            listData.removeAt(i)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return listData.size + if (errorMessage != null) {
            1
        } else {
            0
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < listData.size) {
            if (listData[position].data != null) {
                listData[position].viewType
            } else {
                super.getItemViewType(position)
            }
        } else {
            if (errorMessage.isNullOrEmpty()) {
                super.getItemViewType(position)
            } else {
                ICViewTypes.MESSAGE_TYPE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ICViewTypes.INTERACTIVE_TYPE -> InteractiveComponent(parent)
            ICViewTypes.FRIEND_INVITATION_TYPE -> FriendRequestComponent(parent)
            ICViewTypes.FRIEND_SUGGESTION_TYPE -> FriendSuggestionComponent(parent)
            ICViewTypes.PRODUCT_NOTICE_TYPE -> ProductNoticeComponent(parent)
            ICViewTypes.RELATED_PAGE_TYPE -> NotificationPageComponent(parent)
            ICViewTypes.OTHER_NOTIFICATION_TYPE -> OtherNotificationHolder(parent)
            ICViewTypes.MESSAGE_TYPE -> LongMessageHolder(parent)
            else -> NullHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is InteractiveComponent -> {
                holder.bind(listData[position].data as MutableList<ICNotification>)

                holder.setOnRemoveListener(View.OnClickListener {
                    listData.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, itemCount)
                })
            }
            is FriendRequestComponent -> {
                holder.bind(listData[position].data as ICListResponse<ICSearchUser>)
            }
            is FriendSuggestionComponent -> {
                holder.bind(listData[position].data as MutableList<ICUser>)

                holder.setOnRemoveListener(View.OnClickListener {
                    listData.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, itemCount)
                })
            }
            is ProductNoticeComponent -> {
                holder.bind(listData[position].data as MutableList<ICNotification>)

                holder.setOnRemoveListener(View.OnClickListener {
                    listData.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, itemCount)
                })
            }
            is NotificationPageComponent -> {
                holder.bind(listData[position].data as MutableList<ICNotificationPage>)
            }
            is OtherNotificationHolder -> {
                holder.bind(listData[position].data as ICNotification)
                holder.setOnRemoveListener(View.OnClickListener {
                    listData.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, itemCount)
                })
            }
            is LongMessageHolder -> {
                holder.bind(iconMessage, errorMessage, buttonID)

                holder.setListener(View.OnClickListener {
                    messageListener.onMessageClicked()
                })
            }
        }
    }
}