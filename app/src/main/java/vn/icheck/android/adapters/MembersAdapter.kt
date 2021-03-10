package vn.icheck.android.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.icheck.android.R
import vn.icheck.android.network.models.ICFollowing

class MembersAdapter(val listener: OnMemberGroupClick): RecyclerView.Adapter<MembersAdapter.MembersHolder>() {

    val listData = mutableListOf<ICFollowing.Rows>()
    val dataSize = MutableLiveData<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembersHolder {
        return MembersHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_member_holder, parent, false))
    }

    override fun getItemCount(): Int {
       return listData.size
    }

    override fun onBindViewHolder(holder: MembersHolder, position: Int) {
        val child = listData.get(position)
        Glide.with(holder.view.context.applicationContext)
                .load(listData.get(position).rowObject.avatarThumbnails.small)
                .placeholder(R.drawable.ic_avatar_default_84px)
                .into(holder.imgAvatar)
        holder.tvUserName.text = child.rowObject.name
        holder.root.setOnClickListener {
            listener.onChildMemberClick(child)
        }
    }
    fun removeContact(name: ICFollowing.Rows) {
        if (listData.remove(name)) {
            notifyDataSetChanged()
        }
        dataSize.postValue(listData.size)
    }

    fun addData(data: ICFollowing.Rows) {
        listData.add(data)
        notifyItemInserted(listData.size)
        dataSize.postValue(listData.size)
    }

    class MembersHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val imgAvatar = view.findViewById<ImageView>(R.id.avatar)
        val tvUserName = view.findViewById<TextView>(R.id.tv_user_name)
        val root = view.findViewById<ViewGroup>(R.id.root)
    }

    interface OnMemberGroupClick{
        fun onChildMemberClick(row: ICFollowing.Rows)
    }
}