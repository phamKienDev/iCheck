package vn.icheck.android.screen.user.detail_stamp_v6_1.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_config_error_stamp.view.*
import vn.icheck.android.R
import vn.icheck.android.network.models.detail_stamp_v6_1.IC_Config_Error
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.view.IDetailStampView

class ConfigErrorAdapter(val view: IDetailStampView) : RecyclerView.Adapter<ConfigErrorAdapter.ViewHolder>() {

    private val listData = mutableListOf<IC_Config_Error.ObjectConfigError.ObjectConfigErrorContact>()

    fun setListData(list: MutableList<IC_Config_Error.ObjectConfigError.ObjectConfigErrorContact>?) {
        listData.clear()
        listData.addAll(list!!)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_config_error_stamp, parent, false))
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        holder.bind(item)

        holder.itemView.tvHotline.setOnClickListener {
            view.onItemHotlineClick(item.hotline)
        }

        holder.itemView.tvEmail.setOnClickListener {
            view.onItemEmailClick(item.email)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: IC_Config_Error.ObjectConfigError.ObjectConfigErrorContact) {
            itemView.tvTitle.text = item.title
            itemView.tvHotline.text = item.hotline
            itemView.tvEmail.text = item.email
        }
    }
}