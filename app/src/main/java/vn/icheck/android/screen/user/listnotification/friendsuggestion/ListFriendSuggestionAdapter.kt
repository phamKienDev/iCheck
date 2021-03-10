package vn.icheck.android.screen.user.listnotification.friendsuggestion

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.base.adapter.RecyclerViewAdapter
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.component.friendsuggestion.FriendSuggestionHolder
import vn.icheck.android.network.models.ICFriendSuggestion
import vn.icheck.android.network.models.ICUser

class ListFriendSuggestionAdapter(listener: IRecyclerViewCallback) : RecyclerViewAdapter<ICUser>(listener) {

    override fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder = FriendSuggestionHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        if (holder is FriendSuggestionHolder) {
            holder.bind(listData[position])

            holder.setOnRemoveListener(View.OnClickListener {
                listData.removeAt(position)
                notifyDataSetChanged()
            })
        }
    }
}