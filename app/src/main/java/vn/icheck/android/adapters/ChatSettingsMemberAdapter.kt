package vn.icheck.android.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import vn.icheck.android.R

class ChatSettingsMemberAdapter: RecyclerView.Adapter<ChatSettingsMemberAdapter.MemberChild>(){

    private val mList = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberChild {
        return MemberChild(LayoutInflater.from(parent.context).inflate(R.layout.item_others_member, parent, false))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: MemberChild, position: Int) {
        if (position <= 5) {
            holder.tvPlus.visibility = View.GONE
            holder.member.borderColor = Color.TRANSPARENT
            holder.member.borderWidth = 0
        }
        if (position == 6) {
            holder.member.alpha = 0.6f
        }
        if (position == 7) {
            holder.tvPlus.visibility = View.GONE
            holder.member.borderColor = Color.TRANSPARENT
            holder.member.borderWidth = 0
            holder.member.setImageResource(R.drawable.ic_expand_member_list)
        }
    }

    fun updateList(list: List<String>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    class MemberChild(view: View) : RecyclerView.ViewHolder(view) {
        val tvPlus = view.findViewById<TextView>(R.id.tv_plus)
        val member = view.findViewById<CircleImageView>(R.id.other_member)
        val root = view.findViewById<ViewGroup>(R.id.root)
    }
}