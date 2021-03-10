package vn.icheck.android.screen.user.report_product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_report_product_success.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.models.product.report.ICReportForm

class ProductReportReasonAdapter(val list: MutableList<ICReportForm>) : RecyclerView.Adapter<ProductReportReasonAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICReportForm>(LayoutInflater.from(parent.context).inflate(R.layout.item_report_product_success,parent,false)) {
        override fun bind(obj: ICReportForm) {
            itemView.tvTitle.text = obj.name
        }
    }
}