package vn.icheck.android.screen.user.map_scan_history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_store_sell_in_map.view.*
import vn.icheck.android.R
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.models.history.ICStoreNear
import vn.icheck.android.util.kotlin.WidgetUtils

class StoreSellMapHistoryAdapter(val view: StoreSellMapHistoryView) : RecyclerView.Adapter<StoreSellMapHistoryAdapter.ViewHolder>() {

    private val listData = mutableListOf<ICStoreNear>()

    private var selectedPos = 0

    fun setData(list: MutableList<ICStoreNear>,selectedID: Long): Int {
        listData.clear()
        listData.addAll(list)

        for (i in 0 until listData.size) {
            if (listData[i].id == selectedID) {
                selectedPos = i
            }
        }

        notifyDataSetChanged()
        return selectedPos
    }

    fun getSelectedID(selectedID: Long): Int {
        for (i in 0 until listData.size) {
            if (listData[i].id == selectedID) {
                selectedPos = i
                return selectedPos
            }
        }

        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_store_sell_in_map, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        holder.bindData(item)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(item: ICStoreNear) {
            WidgetUtils.loadImageUrlRoundedFitCenter(itemView.imgAva, item.avatar, R.drawable.ic_error_load_shop_40_px, R.drawable.ic_error_load_shop_40_px, SizeHelper.size12)

            if (selectedPos == absoluteAdapterPosition) {
                itemView.layoutParent.setBackgroundResource(R.drawable.bg_blue_stroke_corners_16)
            } else {
                itemView.layoutParent.setBackgroundResource(R.drawable.bg_white_corners_16)
            }

            itemView.tvName.text = item.name ?: itemView.context.getString(R.string.dang_cap_nhat)

            if (item.distance != null) {
                TextHelper.convertMtoKm(item.distance!!, itemView.tvKhoangCach, "KC:")
            } else {
                itemView.tvKhoangCach.text = itemView.context.getString(R.string.dang_cap_nhat)
            }

            if (item.price != null) {
                itemView.tvPrice.text = TextHelper.formatMoneyPhay(item.price) + "đ"
            } else {
                itemView.tvPrice.text = itemView.context.getString(R.string.dang_cap_nhat)
            }

            itemView.tvAddress.text = item.address
                    ?: itemView.context.getString(R.string.dang_cap_nhat)

            itemView.tvPhone.text = item.phone ?: itemView.context.getString(R.string.dang_cap_nhat)

            itemView.setOnClickListener {
                if (selectedPos != absoluteAdapterPosition) {
                    val oldSelected = selectedPos
                    selectedPos = absoluteAdapterPosition
                    notifyItemChanged(oldSelected)
                    notifyItemChanged(absoluteAdapterPosition)
                    view.onClickShop(item)
                }
            }
        }
    }

}