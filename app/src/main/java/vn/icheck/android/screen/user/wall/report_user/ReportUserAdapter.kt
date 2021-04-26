package vn.icheck.android.screen.user.wall.report_user

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.databinding.ItemTickReportBinding
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.ick.simpleText

class ReportUserAdapter(val listData: List<ReportUserViewModel>, val onUpdate:(Int, Any) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TickHolder(ItemTickReportBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TickHolder)
        holder.itemTickReportBinding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                holder.itemTickReportBinding.checkBox.setTextColor(Constant.getNormalTextColor(holder.itemView.context))
                if (position == listData.lastIndex) {
                    holder.itemTickReportBinding.edtReport.beVisible()
                } else {
                    holder.itemTickReportBinding.edtReport.beGone()
                }
            } else {
                holder.itemTickReportBinding.edtReport.beGone()
                holder.itemTickReportBinding.checkBox.setTextColor(Color.parseColor(Constant.getNormalTextCode))
            }
            onUpdate(position, isChecked)
        }
        holder.itemTickReportBinding.edtReport.addTextChangedListener {
            onUpdate(position, it.toString())
        }
        holder.itemTickReportBinding.checkBox simpleText listData[position].data?.name

    }

    override fun getItemCount() = listData.size

    class TickHolder(val itemTickReportBinding: ItemTickReportBinding) : RecyclerView.ViewHolder(itemTickReportBinding.root)
}