package vn.icheck.android.screen.user.my_gift_warehouse.shake_gift.list_box_gift.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_box_gift.view.*
import vn.icheck.android.R
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.network.models.ICGridBoxShake
import vn.icheck.android.util.kotlin.WidgetUtils

class BoxGiftAdapter (val context: Context?) : RecyclerView.Adapter<BoxGiftAdapter.ViewHolder>() {

    private val listData = mutableListOf<ICGridBoxShake>()

    private var listener : ItemClickListener<ICGridBoxShake>? = null

    fun setListData(list: MutableList<ICGridBoxShake>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_box_gift,parent,false))
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
        fun bind(item: ICGridBoxShake) {
            WidgetUtils.loadImageUrl(itemView.image,item.imageUrl)
        }
    }

    fun setItemClickListener(listener : ItemClickListener<ICGridBoxShake>){
        this.listener = listener
    }
}