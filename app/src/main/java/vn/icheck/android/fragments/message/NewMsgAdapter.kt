package vn.icheck.android.fragments.message

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.adapters.base.BaseHolder
import vn.icheck.android.fragments.message.model.GroupMsgModel
import vn.icheck.android.fragments.message.model.MsgModel
import vn.icheck.android.fragments.message.model.MsgType
import vn.icheck.android.fragments.message.model.UserToUserModel
import vn.icheck.android.helper.ImageHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.util.DimensionUtil

class NewMsgAdapter(val data:List<MsgModel>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MsgType.TYPE_USER_2_USER -> UserMsgHolder.create(parent)
            MsgType.TYPE_NO_MSG -> NoMessageHolder.create(parent)
            MsgType.TYPE_GROUP_MSG -> GroupMsgHolder.create(parent)
            else -> UserMsgHolder.create(parent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            MsgType.TYPE_USER_2_USER -> (holder as UserMsgHolder).bind(data.get(position))
            MsgType.TYPE_GROUP_MSG -> (holder as GroupMsgHolder).bind(data.get(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return data.get(position).getType()
    }

    class GroupMsgHolder(view: View) : BaseHolder(view) {

        fun bind(msgModel: MsgModel?) {
            msgModel?.let {
                it as GroupMsgModel
                val userThumb = view.findViewById<CircleImageView>(
                        R.id.user_thumb)
                hideView(R.id.ic_is_online)
                if (it.name.isNullOrEmpty()) {
                    ICheckApplication.getInstance().mFirebase.roomMetadata
                            ?.child(it.fbId)
                            ?.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    it.name = p0.child("name").value as String?
                                    getTv(R.id.tv_username).text = it.name
                                    it.logo = p0.child("logo").value as String?
                                    Glide.with(view.context.applicationContext)
                                            .load(ImageHelper.getImageUrl(it.logo, ImageHelper.thumbSmallSize))
                                            .error(R.drawable.group_chat_placeholder)
                                            .placeholder(R.drawable.group_chat_placeholder)
                                            .into(userThumb)
                                }
                            })
                } else {
                    getTv(R.id.tv_username).text = it.name
                    Glide.with(view.context.applicationContext)
                            .load(ImageHelper.getImageUrl(it.logo, ImageHelper.thumbSmallSize))
                            .error(R.drawable.group_chat_placeholder)
                            .placeholder(R.drawable.group_chat_placeholder)
                            .into(userThumb)
                }
                getTv(R.id.tv_message).text = it.lastMsg
                getTv(R.id.tv_time).text = it.timeDisplay
                val currentUserId = SessionManager.session.user?.id
                FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child("i-${currentUserId}")
                        .child("unreadRooms")
                        .child(it.fbId)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                try {
                                    it.unreadCount = p0.value as Long
                                    if (it.unreadCount > 0) {
                                        view.findViewById<ImageView>(R.id.img_seen).visibility = View.GONE
                                        val tv = getTv(R.id.tv_unread)
                                        tv.visibility = View.VISIBLE
                                        if (it.unreadCount < 10) {
                                            tv.text = it.unreadCount.toString()
                                        } else {
                                            tv.text = "9+"
                                        }
                                        getTv(R.id.tv_message).setTextColor(Color.parseColor("#3C5A99"))
                                        view.findViewById<ConstraintLayout>(R.id.root)
                                                .setBackgroundColor(Color.parseColor("#F2F8FD"))
                                    } else {
                                        hideView(R.id.img_seen)
                                        hideView(R.id.tv_unread)
                                        getTv(R.id.tv_message).setTextColor(Color.parseColor("#9197A3"))
                                        view.findViewById<ConstraintLayout>(R.id.root)
                                                .setBackgroundColor(Color.WHITE)
                                    }
                                } catch (e: Exception) {

                                }
                            }
                        })

            }
        }

        companion object {
            fun create(parent: ViewGroup): GroupMsgHolder {
                return GroupMsgHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.user_msg_holder, parent, false))
            }
        }
    }

    class UserMsgHolder(view: View) : BaseHolder(view) {

        fun bind(msgModel: MsgModel?) {
            msgModel?.let {
                it as UserToUserModel
                view.findViewById<ViewGroup>(R.id.root).setOnClickListener { _ ->
                    NewMessagesFragment.instance?.openMessage(it)
                }
                view.findViewById<ViewGroup>(R.id.root).setOnLongClickListener { _ ->
//                    NewMessagesFragment.instance?.confirmDelete(it)
                    return@setOnLongClickListener true
                }
                val userThumb = view.findViewById<CircleImageView>(
                        R.id.user_thumb)
                val tag = view.findViewById<ImageView>(R.id.img_text)
                val icOnline = view.findViewById<ImageView>(R.id.ic_is_online)
                getTv(R.id.tv_username).text = it.icUserId?.name
                getTv(R.id.tv_message).text = it.message
                if ("user".equals(it.icUserId?.type, true)) {
                    showView(R.id.ic_is_online)
                    userThumb.borderWidth = 0
                    inviView(R.id.img_verified)
                    inviView(R.id.img_text)
                } else {
                    hideView(R.id.ic_is_online)
                    showView(R.id.img_text)
                    userThumb.borderWidth = DimensionUtil.convertDpToPixel(1.5f, view.context).toInt()
                    if ("page".equals(it.icUserId?.type, true)) {
                        it.icUserId?.verified?.also { verified ->
                            if (verified) {
                                showView(R.id.img_verified)
                                tag.setImageResource(R.drawable.text_verified)
                                userThumb.borderColor = Color.parseColor("#27AE60")
                            } else {
                                inviView(R.id.img_verified)
                                tag.setImageResource(R.color.transparent)
                                userThumb.borderColor = Color.parseColor("#057DDA")
                            }
                        }
                    } else {
                        tag.setImageResource(R.drawable.text_seller)
                        hideView(R.id.img_verified)
                        userThumb.borderColor = Color.parseColor("#FF6422")
                    }
                }
                getTv(R.id.tv_time).text = it.timeDisplay
                if (it.isOnline) {
                    icOnline.setImageResource(R.drawable.ic_online)
                } else {
                    icOnline.setImageResource(R.drawable.ic_offline)
                }
                if (it.icUserId?.type == "user") {
                    val currentUserId = SessionManager.session.user?.id
                    FirebaseDatabase.getInstance()
                            .getReference("users")
                            .child("i-${currentUserId}")
                            .child("unreadRooms")
                            .child(it.fbId)
                            .addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    try {
                                        it.unreadCount = p0.value as Long
                                        if (it.unreadCount > 0) {
                                            view.findViewById<ImageView>(R.id.img_seen).visibility = View.GONE
                                            val tv = getTv(R.id.tv_unread)
                                            tv.visibility = View.VISIBLE
                                            if (it.unreadCount < 10) {
                                                tv.text = it.unreadCount.toString()
                                            } else {
                                                tv.text = "9+"
                                            }
                                            getTv(R.id.tv_message).setTextColor(Color.parseColor("#3C5A99"))
                                            view.findViewById<ConstraintLayout>(R.id.root)
                                                    .setBackgroundColor(Color.parseColor("#F2F8FD"))
                                        } else {
                                            hideView(R.id.img_seen)
                                            hideView(R.id.tv_unread)
                                            getTv(R.id.tv_message).setTextColor(Color.parseColor("#9197A3"))
                                            view.findViewById<ConstraintLayout>(R.id.root)
                                                    .setBackgroundColor(Color.WHITE)
                                        }
                                    } catch (e: Exception) {

                                    }
                                }
                            })
                    FirebaseDatabase.getInstance()
                            .getReference("users")
                            .child("i-${it.id}")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    val result = p0.child("isOnline").value as Boolean?
                                    if (result != null) {
                                        it.isOnline = result
                                        if (it.isOnline) {
                                            icOnline.setImageResource(R.drawable.ic_online)
                                        } else {
                                            icOnline.setImageResource(R.drawable.ic_offline)
                                        }
                                    }
                                }
                            })
                }
                Glide.with(view.context.applicationContext)
                        .load(it.icUserId?.avatarThumbnails?.small.toString())
                        .error(R.drawable.ic_avatar_default_84px)
                        .placeholder(R.drawable.ic_avatar_default_84px)
                        .into(userThumb)
            }
        }

        companion object {
            fun create(parent: ViewGroup): UserMsgHolder {
                return UserMsgHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.user_msg_holder, parent, false))
            }
        }
    }

    class NoMessageHolder(view: View) : BaseHolder(view) {
        companion object {
            fun create(parent: ViewGroup): NoMessageHolder {
                return NoMessageHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.no_message_holder, parent, false))
            }
        }
    }
}