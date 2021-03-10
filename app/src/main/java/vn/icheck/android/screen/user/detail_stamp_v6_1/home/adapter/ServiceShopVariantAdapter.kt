package vn.icheck.android.screen.user.detail_stamp_v6_1.home.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_service_shop_variant.view.*
import vn.icheck.android.R
import vn.icheck.android.network.models.detail_stamp_v6_1.ICServiceShopVariant

class ServiceShopVariantAdapter (val context: Context?) : RecyclerView.Adapter<ServiceShopVariantAdapter.ViewHolder>() {

    private val listData = mutableListOf<ICServiceShopVariant>()

    fun setListData(list: MutableList<ICServiceShopVariant>?) {
        listData.clear()
        listData.addAll(list!!)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_service_shop_variant,parent,false))
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        holder.bind(item)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ICServiceShopVariant) {
            itemView.layoutParent.setBackgroundResource(item.backgroundColor!!)
            itemView.tvNameSerice.text = item.title
            itemView.tvNameSerice.setTextColor(Color.parseColor(item.textColor))
            itemView.tvNameSerice.setCompoundDrawablesWithIntrinsicBounds(item.drawable!!,0,0,0)
        }
    }
}