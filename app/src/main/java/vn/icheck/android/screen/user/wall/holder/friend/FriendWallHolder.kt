package vn.icheck.android.screen.user.wall.holder.friend

import android.annotation.SuppressLint
import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.friend_in_wall_holder.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.constant.SHOW_LIST_FRIEND
import vn.icheck.android.constant.SHOW_LIST_MUTUAL_FRIEND
import vn.icheck.android.constant.USER_WALL_BROADCAST
import vn.icheck.android.constant.USER_WALL_EVENT_SETTING
import vn.icheck.android.databinding.FriendInWallHolderBinding
import vn.icheck.android.network.model.profile.IckUserFriendModel
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.screen.user.list_friend_in_wall.ListFriendOfWallActivity
import vn.icheck.android.screen.user.wall.USER_ID
import vn.icheck.android.ui.layout.CustomGridLayoutManager
import vn.icheck.android.util.ick.beGone

class FriendWallHolder(val binding: FriendInWallHolderBinding) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var adapter: FriendWallAdapter

    @SuppressLint("SetTextI18n")
    fun bind(model: IckUserFriendModel) {
        listener(model)
        if (model.type == 1) {
            itemView.tvCountFriend.text = "Bạn bè (${model.listFriend.data?.count})"
        } else {
            itemView.tvCountFriend.text = "Bạn bè chung (${model.listFriend.data?.count})"
        }
//        itemView.tvCountFriend.text = "Bạn bè (${model.listFriend.data?.count})"
//        if (SessionManager.session.user?.id == model.wallUserId) {
//            itemView.tvCountFriend.text = "Bạn bè (${model.listFriend.data?.count})"
//        } else {
//            itemView.tvCountFriend.text = "Bạn bè chung (${model.listFriend.data?.count})"
//        }

        if (!model.listFriend.data?.rows.isNullOrEmpty()) {
            adapter = FriendWallAdapter(model.listFriend.data?.rows!!)
            itemView.rcv_friends_wall.layoutManager = CustomGridLayoutManager(itemView.context, 3, GridLayoutManager.VERTICAL, false)
            itemView.rcv_friends_wall.adapter = adapter
        }
    }

    private fun listener(model: IckUserFriendModel) {
        itemView.tv_more_friends.setOnClickListener {
            if (SessionManager.session.user?.id == model.wallUserId) {
                it.context.sendBroadcast(Intent(USER_WALL_BROADCAST).apply {
                    putExtra(USER_WALL_BROADCAST, SHOW_LIST_FRIEND)
                })
            } else {
                if (model.type == 1) {
                    it.context.sendBroadcast(Intent(USER_WALL_BROADCAST).apply {
                        putExtra(USER_WALL_BROADCAST, SHOW_LIST_FRIEND)
                    })
                } else {
                    it.context.sendBroadcast(Intent(USER_WALL_BROADCAST).apply {
                        putExtra(USER_WALL_BROADCAST, SHOW_LIST_MUTUAL_FRIEND)
                    })
                }
            }

//            ICheckApplication.currentActivity()?.let {
//                val intent = Intent(it, ListFriendOfWallActivity::class.java)
//                intent.putExtra(USER_ID,model.wallUserId)
//                it.startActivity(intent)
//            }
        }
    }
}