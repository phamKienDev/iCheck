package vn.icheck.android.screen.user.list_facebook_friend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_facebook_friend.view.*
import vn.icheck.android.R
import vn.icheck.android.base.adapter.RecyclerViewAdapter
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.network.models.ICUser
import vn.icheck.android.util.ick.setRankUser
import vn.icheck.android.util.kotlin.WidgetUtils

class ListFacebookFriendAdapter(val callback: IRecyclerViewCallback) : RecyclerViewAdapter<ICUser>(callback) {
    override fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (holder is ViewHolder) {
            holder.bind(listData[position])
        }
    }

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_facebook_friend, parent, false)) {
        fun bind(obj: ICUser) {
            WidgetUtils.loadImageUrl(itemView.avatar, obj.avatar, R.drawable.ic_circle_avatar_default)
            itemView.imgRank.setRankUser(obj.rank?.level)
            itemView.tvName.text = obj.getName

            itemView.tvChat.apply {
                background = vn.icheck.android.ichecklibs.ViewHelper.bgPrimaryCorners4(itemView.context)
                setOnClickListener {

                }
            }
        }
    }
}