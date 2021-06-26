package vn.icheck.android.screen.user.scan_history.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_not_qr_code_history_holder.view.*
import vn.icheck.android.R

class NotQrCodeHistoryHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_not_qr_code_history_holder, parent, false)) {
    
    init {
        itemView.tvCreate.background = vn.icheck.android.ichecklibs.ViewHelper.bgPrimaryCorners4(itemView.context)
    }
}