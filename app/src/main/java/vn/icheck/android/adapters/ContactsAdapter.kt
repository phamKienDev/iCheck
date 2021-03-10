package vn.icheck.android.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.icheck.android.R
import vn.icheck.android.network.models.ICFollowing

class ContactsAdapter(val listener: OnContactClick) : RecyclerView.Adapter<ContactsAdapter.ContactChildHolder>() {
    var list: ArrayList<ICFollowing.Rows> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactChildHolder {
        return ContactChildHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false))
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ContactChildHolder, position: Int) {
        if (list.get(position).rowObject != null) {
            holder.tvContactName.text = list.get(position).rowObject.name
            holder.root.setOnClickListener {
                listener.onContactChildClick(list.get(position))
            }
            Glide.with(holder.view.context.applicationContext)
                    .load(list.get(position).rowObject.avatarThumbnails.small)
                    .placeholder(R.drawable.ic_avatar_default_84px)
                    .into(holder.img)
        }
    }

    fun removeContact(name: ICFollowing.Rows) {
        if (list.remove(name)) {
            notifyDataSetChanged()
        }
    }

    fun clear() {
        list.clear()
    }
    fun addItem(name: ICFollowing.Rows) {
        list.add(name)
        notifyItemInserted(list.size)
    }

    class ContactChildHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvContactName = view.findViewById<TextView>(R.id.tv_contact_name)
        val root = view.findViewById<ViewGroup>(R.id.root)
        val img = view.findViewById<ImageView>(R.id.user_avar)
    }

    interface OnContactClick {
        fun onContactChildClick(childContact: ICFollowing.Rows)
    }
}