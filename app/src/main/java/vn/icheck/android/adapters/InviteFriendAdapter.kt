package vn.icheck.android.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.activities.FriendContact

class InviteFriendAdapter(val onInviteFriendClick: OnInviteFriendClick) : RecyclerView.Adapter<InviteFriendAdapter.InviteFriendChild>() {

    val listFriend = mutableListOf<FriendContact>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InviteFriendChild {
        return InviteFriendChild(LayoutInflater.from(parent.context).inflate(R.layout.item_invite_friend, parent, false))
    }

    override fun getItemCount(): Int {
        return listFriend.size
    }

    override fun onBindViewHolder(holder: InviteFriendChild, position: Int) {
        val friendContact = listFriend[position]
        holder.tvName.text = friendContact.name
        holder.tvPhone.text = friendContact.phone
        holder.btnInviteUser.setOnClickListener {
            onInviteFriendClick.onClick(friendContact)
        }
    }

    fun updateList(friendContacts: List<FriendContact>) {
        listFriend.clear()
        listFriend.addAll(friendContacts)
        notifyDataSetChanged()
    }

    class InviteFriendChild(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById<TextView>(R.id.tv_name)
        val tvPhone = view.findViewById<TextView>(R.id.tv_phone)
        val img = view.findViewById<ImageView>(R.id.img_user)
        val btnInviteUser = view.findViewById<TextView>(R.id.btn_invite_user)

    }

    interface OnInviteFriendClick {
        fun onClick(friendContact: FriendContact)
    }
}