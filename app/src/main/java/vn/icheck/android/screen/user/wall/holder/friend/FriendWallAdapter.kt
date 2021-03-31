package vn.icheck.android.screen.user.wall.holder.friend

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_friend_in_wall.view.*
import vn.icheck.android.R
import vn.icheck.android.network.models.wall.ICUserFollowWall
import vn.icheck.android.network.models.wall.RowsItem
import vn.icheck.android.screen.user.wall.IckUserWallActivity
import vn.icheck.android.util.kotlin.WidgetUtils

class FriendWallAdapter(val listData: MutableList<RowsItem>) : RecyclerView.Adapter<FriendWallAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_friend_in_wall, parent, false))
    }

    override fun getItemCount(): Int {
        return if (listData.size <= 6) listData.size else 6
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            IckUserWallActivity.create(item.id, holder.itemView.context)
        }
    }

    @SuppressLint("SetTextI18n")
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: RowsItem) {
            WidgetUtils.loadImageUrl(itemView.imgAvatar, item.avatar, R.drawable.ic_square_avatar_default)
            itemView.tvName.apply {
                text = if (!item.lastName.isNullOrEmpty() && !item.firstName.isNullOrEmpty()) {
                    item.lastName + " " + item.firstName
                } else if (!item.firstName.isNullOrEmpty()) {
                    item.firstName
                } else if (!item.lastName.isNullOrEmpty()) {
                    item.lastName
                } else {
                    itemView.context.getString(R.string.dang_cap_nhat)
                }

                if (item.kycStatus == 2) {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_user_24dp, 0)
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            }
        }
    }
}