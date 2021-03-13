package vn.icheck.android.screen.user.social_chat

import android.graphics.Color
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.icheck.android.R
import vn.icheck.android.databinding.ConversationHolderBinding
import vn.icheck.android.model.chat.ChatConversation
import vn.icheck.android.util.ick.*

class ConversationAdapter(var data:List<ChatConversation>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val bd = ConversationHolderBinding.inflate(parent.getLayoutInflater(), parent, false)
        return MessageHolder(bd)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MessageHolder).bind(data.get(position))
    }

    override fun getItemCount() = data.size

    class MessageHolder(val binding: ConversationHolderBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(conversation: ChatConversation) {
            binding.tvTime simpleText conversation.time?.getNotifyTimeVn()
            binding.tvUsername simpleText conversation.targetUserName
            binding.tvUnread simpleText conversation.unreadCount.toString()
            binding.tvMessage simpleText conversation.lastMessage
            binding.userThumb.post {
                Glide.with(itemView.context.applicationContext)
                        .load(conversation.imageTargetUser)
                        .placeholder(R.drawable.ic_user_svg)
                        .error(R.drawable.ic_user_svg)
                        .into( binding.userThumb)
            }
            when{
                conversation.unreadCount > 9L -> {
                    binding.tvUnread.beVisible()
                    binding.tvUnread simpleText "9+"
                    binding.root.setBackgroundColor(Color.parseColor("#1A057DDA"))
                }
                conversation.unreadCount > 0L -> {
                    binding.tvUnread.beVisible()
                    binding.tvUnread simpleText conversation.unreadCount.toString()
                    binding.root.setBackgroundColor(Color.parseColor("#1A057DDA"))
                }
                else -> {
                    binding.tvUnread.beGone()
                    binding.root.setBackgroundColor(Color.WHITE)
                }
            }
//            if (conversation.isOnline) {
//                binding.icIsOnline.setImageResource(R.drawable.ic_online)
//            } else {
//                binding.icIsOnline.setImageResource(R.drawable.ic_offline)
//            }
            binding.root.setOnClickListener {
                SocialChatActivity.createRoomChat(it.context, conversation.listUser.toTypedArray(), roomId = conversation.key)
            }
        }
    }
}