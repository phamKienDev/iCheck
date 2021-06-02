package vn.icheck.android.screen.user.scan_history.holder

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_product_header_holder.view.*
import kotlinx.android.synthetic.main.layout_list_product_header_history_holder.view.*
import vn.icheck.android.R
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.history.ICBigCorp
import vn.icheck.android.screen.user.scan_history.ScanHistoryFragment
import vn.icheck.android.screen.user.scan_history.view.IScanHistoryView
import vn.icheck.android.util.kotlin.WidgetUtils

class ListBigCorpHolder(parent: ViewGroup, val listener: IScanHistoryView) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_list_product_header_history_holder, parent, false)) {

    var selectedPos = 0

    fun bind(list: MutableList<ICBigCorp>) {
        val adapter = ProdutctHeaderAdapter(list, listener)
        itemView.rcvListBigCorp.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
        itemView.rcvListBigCorp.adapter = adapter
    }

    inner class ProdutctHeaderAdapter(val listData: MutableList<ICBigCorp>, val listener: IScanHistoryView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ItemProductHeader(LayoutInflater.from(parent.context).inflate(R.layout.item_product_header_holder, parent, false))
        }

        override fun getItemCount(): Int {
            return if (listData.size >= 10) {
                10
            } else {
                listData.size
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as ItemProductHeader).bind(listData[position])
        }

        inner class ItemProductHeader(view: View) : RecyclerView.ViewHolder(view) {
            fun bind(item: ICBigCorp) {
                if (ScanHistoryFragment.listIdBigCorp.isNullOrEmpty()) {
                    selectedPos = 0
                }
                if (item.avatar_all != null) {
                    itemView.imgBigCorp.setImageResource(item.avatar_all!!)
                } else {
                    WidgetUtils.loadImageUrl(itemView.imgBigCorp, item.avatar, R.drawable.ic_big_corp_40_px, R.drawable.ic_big_corp_40_px)
                }

                itemView.tvNameBigCorp.text = item.name
                        ?: itemView.context.getString(R.string.dang_cap_nhat)

                if (selectedPos == absoluteAdapterPosition) {
                    itemView.bgItem.background = ViewHelper.bgWhiteOutlinePrimary1Corners4(itemView.context)
                    itemView.tvNameBigCorp.setTextColor(vn.icheck.android.ichecklibs.Constant.getPrimaryColor(itemView.context))
                } else {
                    itemView.bgItem.background=ViewHelper.bgWhiteCorners4(itemView.context)
                    itemView.tvNameBigCorp.setTextColor(Color.parseColor(vn.icheck.android.ichecklibs.Constant.getSecondTextCode))
                }

                itemView.setOnClickListener {
                    if (selectedPos != absoluteAdapterPosition) {
                        val oldSelected = selectedPos
                        selectedPos = absoluteAdapterPosition
                        notifyItemChanged(oldSelected)
                        notifyItemChanged(absoluteAdapterPosition)
                        listener.onClickBigCorp(item)
                    }
                }
            }
        }
    }
}