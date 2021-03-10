package vn.icheck.android.component.friendrequest

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.network.models.ICSearchUser

class FriendRequestAdapter : RecyclerView.Adapter<FriendRequestHolder>() {
    private val listData = mutableListOf<ICSearchUser>()

    fun setData(list: MutableList<ICSearchUser>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = if (listData.size > 3) {
        3
    } else {
        listData.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestHolder =
            FriendRequestHolder(parent)

    override fun onBindViewHolder(holder: FriendRequestHolder, position: Int) {
        holder.bind(listData[position])
    }
}