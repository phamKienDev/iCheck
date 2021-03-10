package vn.icheck.android.screen.user.listnotification.friendrequest

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.base.adapter.RecyclerViewAdapter
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.network.models.ICNotification
import vn.icheck.android.component.friendrequest.FriendRequestHolder
import vn.icheck.android.network.models.ICSearchUser

class ListFriendRequestAdapter(listener: IRecyclerViewCallback) : RecyclerViewAdapter<ICSearchUser>(listener) {

    var onUpdateRequest:((Long)-> Unit)? = null

    fun removeItem(id: Long) {
        listData.removeAll {
            it.id == id
        }
        notifyDataSetChanged()
    }

    override fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder = FriendRequestHolder(parent).apply {
       this.onUpdateRequest = this@ListFriendRequestAdapter.onUpdateRequest
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        if (holder is FriendRequestHolder) {
            holder.bind(listData[position])
        }
    }
}