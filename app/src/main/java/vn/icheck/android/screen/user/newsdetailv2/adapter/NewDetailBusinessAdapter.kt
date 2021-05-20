package vn.icheck.android.screen.user.newsdetailv2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_new_detail_business.view.*
import vn.icheck.android.R
import vn.icheck.android.ichecklibs.WidgetHelper
import vn.icheck.android.network.models.ICPage

class NewDetailBusinessAdapter(val listData: MutableList<ICPage>) : RecyclerView.Adapter<NewDetailBusinessAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewDetailBusinessAdapter.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: NewDetailBusinessAdapter.ViewHolder, position: Int) {
        WidgetHelper.loadImageUrl(holder.itemView.image, listData[position].avatar, R.drawable.ic_default_avatar_page_chat, R.drawable.ic_default_avatar_page_chat)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_new_detail_business, parent, false))
}