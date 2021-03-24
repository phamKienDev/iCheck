package vn.icheck.android.chat.icheckchat.screen.conversation

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.chat.icheckchat.R
import vn.icheck.android.chat.icheckchat.base.recyclerview.BaseRecyclerView
import vn.icheck.android.chat.icheckchat.base.recyclerview.IRecyclerViewCallback
import vn.icheck.android.chat.icheckchat.base.recyclerview.holder.BaseViewHolder
import vn.icheck.android.chat.icheckchat.base.view.*
import vn.icheck.android.chat.icheckchat.base.view.MCViewType.TYPE_CONVERSATION
import vn.icheck.android.chat.icheckchat.databinding.ItemConversationBinding
import vn.icheck.android.chat.icheckchat.model.MCConversation

class ListConversationAdapter(callback: IRecyclerViewCallback) : BaseRecyclerView<MCConversation>(callback) {
    private var listenerHolder: IListener? = null

    fun setData(obj: MutableList<MCConversation>) {
        listData.clear()

        listData.addAll(obj)
        notifyDataSetChanged()
    }

    fun refreshItem(obj: MCConversation) {
        for (i in listData.size - 1 downTo 0) {
            if (listData[i].keyRoom == obj.keyRoom) {
                notifyItemChanged(i)
                return
            }
        }
    }

    override fun getItemType(position: Int): Int {
        return TYPE_CONVERSATION
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ConversationHolder(ItemConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ConversationHolder) {
            holder.bind(listData[position])
        } else {
            super.onBindViewHolder(holder, position)
        }
    }

    inner class ConversationHolder(private val binding: ItemConversationBinding) : BaseViewHolder<MCConversation>(binding) {

        @SuppressLint("SetTextI18n")
        override fun bind(obj: MCConversation) {
            binding.imgMuteNotification.setGone()

            checkNullOrEmpty(binding.tvNameUser, obj.targetUserName)

            checkNullOrEmpty(binding.tvMessage, obj.lastMessage)

            binding.imgAvatar.apply {
                if (obj.type == "user") {
                    setBackgroundResource(0)
                    loadImageUrl(this@apply, obj.imageTargetUser, R.drawable.ic_user_default_52dp, R.drawable.ic_user_default_52dp)
                    binding.tvNameUser.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                } else {

                    if (obj.isVerified) {
                        setBackgroundResource(R.drawable.ic_bg_avatar_page)
                        binding.tvNameUser.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_18px, 0)
                    } else {
                        setBackgroundResource(0)
                        binding.tvNameUser.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    }

                    loadImageUrl(this@apply, obj.imageTargetUser, R.drawable.ic_default_avatar_page_chat, R.drawable.ic_default_avatar_page_chat)
                }
            }

            binding.tvTime.text = convertDateTimeSvToCurrentDay(obj.time)

            binding.tvCountMessage.visibleOrGone(obj.unreadCount != null && obj.unreadCount!! > 0)

            if (obj.unreadCount != null) {
                binding.tvCountMessage.setVisible()
                when {
                    obj.unreadCount!! > 9 -> {
                        binding.tvCountMessage.text = "9+"
                        binding.layout.setBackgroundColor(Color.parseColor("#1A057DDA"))
                    }
                    obj.unreadCount!! > 0 -> {
                        binding.tvCountMessage.text = "${obj.unreadCount}"
                        binding.layout.setBackgroundColor(Color.parseColor("#1A057DDA"))
                    }
                    else -> {
                        binding.tvCountMessage.setGone()
                        binding.tvCountMessage.text = "${obj.unreadCount}"
                        binding.layout.setBackgroundColor(Color.WHITE)
                    }
                }
            } else {
                binding.tvCountMessage.setGone()
                binding.tvCountMessage.text = "0"
                binding.layout.setBackgroundColor(Color.WHITE)
            }

            binding.imgMuteNotification.apply {
                if (binding.tvCountMessage.isVisible) {
                    setGone()
                } else {
                    if (!obj.isNotification) {
                        setVisible()
                    } else {
                        setGone()
                    }
                }
            }

            itemView.setOnClickListener {
                listenerHolder?.onClickConversation(obj)
            }
        }
    }

    fun setListener(listener: IListener) {
        listenerHolder = listener
    }

    interface IListener {
        fun onClickConversation(obj: MCConversation)
    }
}