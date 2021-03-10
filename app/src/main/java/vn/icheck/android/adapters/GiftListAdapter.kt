package vn.icheck.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.icheck.android.R
import vn.icheck.android.network.models.ICListGift

typealias gift = ICListGift.Rows

class GiftListAdapter(val context: Context): RecyclerView.Adapter<GiftListAdapter.GiftChildHolder>() {

    val listGift = mutableListOf<gift>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiftChildHolder {
        return GiftChildHolder(LayoutInflater.from(context).inflate(R.layout.item_gift_list, parent, false))
    }

    override fun getItemCount(): Int {
        return listGift.size
    }

    override fun onBindViewHolder(holder: GiftChildHolder, position: Int) {
        val child = listGift.get(position)
        holder.title.text = child.name
        holder.available.text = String.format("Còn lại: %d", child.remain)
        holder.cost.text = child.price.toString()
        if (!child.image.isNullOrEmpty()) {
            Glide.with(holder.itemView.context.applicationContext).load(child.image).into(holder.image)
        }

    }

    class GiftChildHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.img_image)
        val title = view.findViewById<TextView>(R.id.tv_title)
        val available = view.findViewById<TextView>(R.id.tv_available)
        val cost = view.findViewById<TextView>(R.id.tv_cost)
    }
}