package vn.icheck.android.screen.user.scan_history.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_toolbar_menu_history.view.*
import vn.icheck.android.R
import vn.icheck.android.screen.user.scan_history.view.IScanHistoryView

class ToolbarMenuHolder(parent: ViewGroup, val listener: IScanHistoryView) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_toolbar_menu_history, parent, false)) {
    fun bind() {
        itemView.btnUncheckAll.setOnClickListener {
            listener.unCheckAllFilterHistory()
        }

        itemView.imgCloseDrawer.setOnClickListener {
            listener.onCloseDrawer()
        }
    }
}