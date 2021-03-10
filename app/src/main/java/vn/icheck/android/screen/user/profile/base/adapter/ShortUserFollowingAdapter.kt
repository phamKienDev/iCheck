package vn.icheck.android.screen.user.profile.base.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_user_following.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICUserFollowing
import vn.icheck.android.screen.user.campaign.calback.IFollowingListener
import vn.icheck.android.util.kotlin.WidgetUtils

class ShortUserFollowingAdapter(val listener: IFollowingListener) : RecyclerView.Adapter<ShortUserFollowingAdapter.ViewHolder>() {
    private val listData = mutableListOf<ICUserFollowing>()

    fun setData(list: MutableList<ICUserFollowing>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_user_following, parent, false))
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])

        holder.itemView.setOnClickListener {
            listener.onFollowingClicked(listData[position])
        }
    }

    class ViewHolder(view: View) : BaseViewHolder<ICUserFollowing>(view) {
        override fun bind(obj: ICUserFollowing) {
            obj.user?.let {
                WidgetUtils.loadImageUrlRounded(itemView.imgAvatar, it.avatar_thumbnails?.medium,
                        R.drawable.ic_square_avatar_default,
                        R.drawable.ic_square_avatar_default,
                        SizeHelper.dpToPx(itemView.context, 10))

                itemView.tvName.text = it.name
            }
        }
    }
}