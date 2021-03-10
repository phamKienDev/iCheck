package vn.icheck.android.screen.user.scan_history.holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_filter_type_history.view.*
import vn.icheck.android.R
import vn.icheck.android.network.models.history.ICTypeHistory
import vn.icheck.android.screen.user.scan_history.adapter.FilterTypeAdapter
import vn.icheck.android.screen.user.scan_history.adapter.ScanMenuHistoryAdapter
import vn.icheck.android.screen.user.scan_history.view.IScanHistoryView

class FilterTypeHistoryHolder(parent: ViewGroup, val listener: IScanHistoryView) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_filter_type_history, parent, false)) {

    private lateinit var adapter: FilterTypeAdapter

    fun bind(list: MutableList<ICTypeHistory>) {
        adapter = FilterTypeAdapter(list, listener)
        itemView.recyclerView.layoutManager = GridLayoutManager(itemView.context, 2, GridLayoutManager.VERTICAL, false)
        itemView.recyclerView.adapter = adapter

        if (list.size > 4) {
            itemView.tvMoreType.visibility = View.VISIBLE
        } else {
            itemView.tvMoreType.visibility = View.GONE
        }

        checkShowMore(ScanMenuHistoryAdapter.isShowType)

        itemView.tvMoreType.setOnClickListener {
            checkShowMore(!ScanMenuHistoryAdapter.isShowType)
        }
    }

    private fun checkShowMore(isShow:Boolean) {
        if (isShow) {
            itemView.tvMoreType.text = "Thu gọn"
            itemView.tvMoreType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up_gray, 0)
            adapter.showMore()
            ScanMenuHistoryAdapter.isShowType = true
        } else {
            itemView.tvMoreType.text = "Hiển thị thêm"
            itemView.tvMoreType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_gray_10dp, 0)
            adapter.hide()
            ScanMenuHistoryAdapter.isShowType = false
        }
    }
}