package vn.icheck.android.loyalty.screen.loyalty_customers.history.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_transaction_history.view.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.getHHMMDate
import vn.icheck.android.loyalty.helper.TimeHelper
import vn.icheck.android.loyalty.model.TransactionHistoryResponse

class PointsHistoryAdapter: PagingDataAdapter<TransactionHistoryResponse, RecyclerView.ViewHolder>(COMPARTOR) {

    companion object{
        val COMPARTOR = object : DiffUtil.ItemCallback<TransactionHistoryResponse>() {
            override fun areItemsTheSame(oldItem: TransactionHistoryResponse, newItem: TransactionHistoryResponse): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: TransactionHistoryResponse, newItem: TransactionHistoryResponse): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val lf = LayoutInflater.from(parent.context)
        val v = lf.inflate(R.layout.item_transaction_history, parent, false)
        return LoyaltyPointsHistory(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as LoyaltyPointsHistory
        val data = getItem(position)
        holder.view.tvName.text = data?.reason
        holder.view.tvDate.text = TimeHelper.convertDateTimeSvToTimeDateVn(data?.createdAt)
        if (data?.type?.id == "02") {
            holder.view.imgCategory.setImageResource(R.drawable.ic_xu_contract_20px)

            holder.view.tvPoint.apply {
                text = String.format("- %,d", data.point)
                setTextColor(Color.RED)
            }
        } else {
            holder.view.imgCategory.setImageResource(R.drawable.ic_xu_add_20px)
            holder.view.tvPoint.apply {
                text = String.format("+ %,d", data?.point)
                setTextColor(Color.parseColor("#49aa2d"))
            }
        }
    }

    inner class LoyaltyPointsHistory(val view: View): RecyclerView.ViewHolder(view)
}