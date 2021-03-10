package vn.icheck.android.activities.chat.setting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import vn.icheck.android.R
import vn.icheck.android.adapters.base.BaseHolder
import vn.icheck.android.network.models.ICUserId

class GroupMemberAdapter(var listUser: List<ICUserId>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MemberChild.create(parent)
    }

    override fun getItemCount(): Int {
        return listUser.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MemberChild).bind(listUser[position])
    }

    class MemberChild(view: View) : BaseHolder(view) {

        fun bind(icUserId: ICUserId) {
           val img =  view.findViewById<CircleImageView>(R.id.other_member)
            Glide.with(view.context.applicationContext)
                    .load(icUserId.avatarThumbnails?.small.toString())
                    .error(R.drawable.user_placeholder)
                    .placeholder(R.drawable.user_placeholder)
                    .into(img)
        }

        companion object{
            fun create(parent: ViewGroup):MemberChild{
                return MemberChild(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_others_member, parent, false))
            }
        }
    }

}