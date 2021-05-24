package vn.icheck.android.chat.icheckchat.screen.contact

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.chat.icheckchat.R
import vn.icheck.android.chat.icheckchat.base.recyclerview.BaseRecyclerView
import vn.icheck.android.chat.icheckchat.base.recyclerview.IRecyclerViewCallback
import vn.icheck.android.chat.icheckchat.base.recyclerview.holder.BaseViewHolder
import vn.icheck.android.chat.icheckchat.base.view.MCViewType.TYPE_CONTACT
import vn.icheck.android.chat.icheckchat.base.view.loadImageUrl
import vn.icheck.android.chat.icheckchat.base.view.setRankUserBig
import vn.icheck.android.chat.icheckchat.databinding.ItemContactChatBinding
import vn.icheck.android.chat.icheckchat.model.MCUser
import vn.icheck.android.chat.icheckchat.screen.detail.ChatSocialDetailActivity

class ContactAdapter(callback: IRecyclerViewCallback) : BaseRecyclerView<MCUser>(callback) {

    override fun getItemType(position: Int): Int {
        return TYPE_CONTACT
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ContactViewHolder(ItemContactChatBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ContactViewHolder) {
            holder.bind(listData[position])
        } else {
            super.onBindViewHolder(holder, position)
        }
    }

    inner class ContactViewHolder(val binding: ItemContactChatBinding) : BaseViewHolder<MCUser>(binding) {
        override fun bind(obj: MCUser) {
            loadImageUrl(binding.imgAvatarFriend, obj.avatar, R.drawable.ic_user_default_52dp, R.drawable.ic_user_default_52dp)
            checkNullOrEmpty(binding.tvNameUser, obj.getName)
            binding.imgRank.setRankUserBig(obj.rank?.level)

            if (obj.kycStatus == 2){
                binding.tvNameUser.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_user_16px, 0)
            }else{
                binding.tvNameUser.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }

            binding.root.setOnClickListener {
                ChatSocialDetailActivity.createRoomChat(itemView.context, obj.id ?: -1, "user")
            }
        }
    }
}