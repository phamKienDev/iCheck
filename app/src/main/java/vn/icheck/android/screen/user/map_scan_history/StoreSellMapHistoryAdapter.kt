package vn.icheck.android.screen.user.map_scan_history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_store_sell_in_map.view.*
import vn.icheck.android.R
import vn.icheck.android.base.adapter.RecyclerViewAdapter
import vn.icheck.android.databinding.ItemStoreSellInMapBinding
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.models.history.ICStoreNear
import vn.icheck.android.ichecklibs.util.dpToPx
import vn.icheck.android.util.kotlin.WidgetUtils

class StoreSellMapHistoryAdapter(val view: StoreSellMapHistoryView) : RecyclerViewAdapter<ICStoreNear>(view) {

    override fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if(holder is ViewHolder){
            holder.bindData(listData[position])
        }
    }

    private var selectedPos = 0

    fun setData(list: MutableList<ICStoreNear>, selectedID: Long): Int {
        checkLoadmore(list)

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


    private val sizeWidth = 301.dpToPx()
    private val sizeMargin = 7.5f.dpToPx().toInt()

    inner class ViewHolder(parent: ViewGroup, val binding: ItemStoreSellInMapBinding = ItemStoreSellInMapBinding.inflate(LayoutInflater.from(parent.context), parent, false)) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: ICStoreNear) {
            binding.layoutParent.layoutParams = if (listData.size > 1) {
                RecyclerView.LayoutParams(sizeWidth, RecyclerView.LayoutParams.WRAP_CONTENT).apply {
                    setMargins(sizeMargin, 0, sizeMargin, 0)
                }
            } else {
                RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(sizeMargin, 0, sizeMargin, 0)
                }
            }

            WidgetUtils.loadImageUrlRoundedFitCenter(
                itemView.imgAva,
                item.avatar,
                R.drawable.ic_error_load_shop_40_px,
                R.drawable.ic_error_load_shop_40_px,
                SizeHelper.size12
            )

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

            itemView.tvPrice.text = if (item.price != null) {
                TextHelper.formatMoneyPhay(item.price) + "đ"
            } else {
                null
            }

            itemView.tvAddress.text =
                item.address ?: itemView.context.getString(R.string.dang_cap_nhat)

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