package vn.icheck.android.screen.user.share_post_of_page.bottom_sheet_chat_share

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_friend_share.view.*
import vn.icheck.android.R
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.network.models.ICChoosePage
import vn.icheck.android.util.kotlin.WidgetUtils

class FriendShareAdapter (val context: Context?) : RecyclerView.Adapter<FriendShareAdapter.ViewHolder>() {

    private val listData = mutableListOf<ICChoosePage>()

    private var listener: ItemClickListener<ICChoosePage>? = null

    fun setListData(list: MutableList<ICChoosePage>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_friend_share,parent,false))
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            listener?.onItemClick(position,item)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ICChoosePage) {

        }
    }

    fun setOnClickItemListener(listener: ItemClickListener<ICChoosePage>) {
        this.listener = listener
    }
}