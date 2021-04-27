package vn.icheck.android.screen.user.scan_history.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_filter_type.view.*
import vn.icheck.android.R
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.network.models.history.ICTypeHistory
import vn.icheck.android.screen.user.scan_history.ScanHistoryFragment
import vn.icheck.android.screen.user.scan_history.view.IScanHistoryView

class FilterTypeAdapter(var listData: MutableList<ICTypeHistory>, val listener: IScanHistoryView) : RecyclerView.Adapter<FilterTypeAdapter.ViewHolder>() {

    private var show = if (listData.size > 4) {
        4
    } else {
        listData.size
    }

    fun hide() {
        show = if (listData.size > 4) {
            4
        } else {
            listData.size
        }
        notifyDataSetChanged()
    }

    fun showMore() {
        show = if (listData.size > 10) {
            listData.size
        } else {
            listData.size
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_filter_type, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]

        holder.bindData(item)
    }

    override fun getItemCount(): Int {
        return show
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(item: ICTypeHistory) {
            itemView.tvName.text = item.name

            if (item.select) {
                itemView.layoutContent.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_corner_4_light_blue_white)
                itemView.img_tick.visibility = View.VISIBLE
                itemView.tvName.setTextColor(vn.icheck.android.ichecklibs.Constant.getPrimaryColor(itemView.context))
            } else {
                itemView.layoutContent.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_corner_4_f2f2f2)
                itemView.img_tick.visibility = View.INVISIBLE
                itemView.tvName.setTextColor(Color.parseColor(Constant.getNormalTextCode))
            }

            itemView.layoutContent.setOnClickListener {
                listData[absoluteAdapterPosition].select = !listData[absoluteAdapterPosition].select
                notifyItemChanged(absoluteAdapterPosition)
            }
        }
    }
}