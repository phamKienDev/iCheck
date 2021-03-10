package vn.icheck.android.fragments.message

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.AsyncPagedListDiffer
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
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
import vn.icheck.android.fragments.message.model.ICMessage
import vn.icheck.android.fragments.message.model.MessageDao
import vn.icheck.android.fragments.message.model.MsgType
import vn.icheck.android.helper.ImageHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.room.database.AppDatabase
import vn.icheck.android.util.DimensionUtil

class NewMsgPagedAdapter() : PagedListAdapter<ICMessage, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ICMessage>() {
            override fun areItemsTheSame(oldItem: ICMessage, newItem: ICMessage): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ICMessage, newItem: ICMessage): Boolean {
                return oldItem.lastSeen == newItem.lastSeen
                        && oldItem.roomName == newItem.roomName
                        && oldItem.unreadCount == newItem.unreadCount
                        && oldItem.isOnline == newItem.isOnline
                        && oldItem.avatar == newItem.avatar
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return UserMsgHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as UserMsgHolder).bind(getItem(position))
    }

    class UserMsgHolder(view: View) : BaseHolder(view) {

        fun bind(icMessage: ICMessage?) {
            icMessage?.let {
                getTv(R.id.tv_username).text = it.roomName
                getTv(R.id.tv_message).text = it.lastMessage
                getTv(R.id.tv_time).text = MsgTimeHelper(it.lastSeen).getTime()
                val userThumb = view.findViewById<CircleImageView>(
                        R.id.user_thumb)
                val icOnline = view.findViewById<ImageView>(R.id.ic_is_online)
                val tag = view.findViewById<ImageView>(R.id.img_text)
                view.findViewById<ViewGroup>(R.id.root).setOnLongClickListener { _ ->
                    NewMessagesFragment.instance?.confirmDelete(it)
                    return@setOnLongClickListener true
                }
                view.findViewById<ViewGroup>(R.id.root).setOnClickListener { _ ->
                    NewMessagesFragment.instance?.openNewMsg(it)
                }

                if (it.msgType == MsgType.TYPE_USER_2_USER) {
                    it.isOnline?.let { online ->
                        if (online) {
                            icOnline.setImageResource(R.drawable.ic_online)
                        } else {
                            icOnline.setImageResource(R.drawable.ic_offline)
                        }
                    }


                    if ("user".equals(it.userType, true)) {
                        showView(R.id.ic_is_online)
                        userThumb.borderWidth = 0
                        inviView(R.id.img_verified)
                        inviView(R.id.img_text)
                        Glide.with(view.context.applicationContext)
                                .load(it.avatar)
                                .error(R.drawable.ic_avatar_default_84px)
                                .placeholder(R.drawable.ic_avatar_default_84px)
                                .into(userThumb)
                    } else {
                        hideView(R.id.ic_is_online)
                        showView(R.id.img_text)
                        userThumb.borderWidth = DimensionUtil.convertDpToPixel(2f, view.context).toInt()
                        if ("page".equals(it.userType, true)) {
                            Glide.with(view.context.applicationContext)
                                    .load(it.avatar)
                                    .error(R.drawable.img_default_business_logo_big)
                                    .placeholder(R.drawable.img_default_business_logo_big)
                                    .into(userThumb)
                            it.verified?.also { verified ->
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
                            Glide.with(view.context.applicationContext)
                                    .load(it.avatar)
                                    .error(R.drawable.img_shop_default_big)
                                    .placeholder(R.drawable.img_shop_default_big)
                                    .into(userThumb)
                        }
                    }
                } else {
                    userThumb.borderWidth = 0
                    hideView(R.id.ic_is_online)
                    getTv(R.id.tv_username).text = it.roomName
                    inviView(R.id.img_verified)
                    inviView(R.id.img_text)
                    if (!URLUtil.isValidUrl(it.avatar)) {
                        Glide.with(view.context.applicationContext)
                                .load(ImageHelper.getImageUrl(it.avatar, ImageHelper.thumbSmallSize))
                                .error(R.drawable.group_chat_placeholder)
                                .placeholder(R.drawable.group_chat_placeholder)
                                .into(userThumb)
                    } else {
                        Glide.with(view.context.applicationContext)
                                .load(it.avatar)
                                .error(R.drawable.group_chat_placeholder)
                                .placeholder(R.drawable.group_chat_placeholder)
                                .into(userThumb)
                    }

                }
                if (it.unreadCount > 0) {
                    hideView(R.id.img_seen)
                    showView(R.id.tv_unread)
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
                    showView(R.id.img_seen)
                    hideView(R.id.tv_unread)
                    getTv(R.id.tv_message).setTextColor(Color.parseColor("#9197A3"))
                    view.findViewById<ConstraintLayout>(R.id.root)
                            .setBackgroundColor(Color.WHITE)
                }

            }
        }

        companion object {
            fun create(parent: ViewGroup): UserMsgHolder {
                return UserMsgHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.user_msg_holder, parent, false))
            }
        }
    }
}