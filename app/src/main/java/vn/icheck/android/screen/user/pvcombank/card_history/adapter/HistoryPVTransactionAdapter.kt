package vn.icheck.android.screen.user.pvcombank.card_history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_history_transaction_pv_combank.view.*
import vn.icheck.android.R
import vn.icheck.android.base.adapter.RecyclerViewAdapter
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.network.models.pvcombank.ICItemPVCard
import vn.icheck.android.network.models.pvcombank.ICTransactionPVCard

class HistoryPVTransactionAdapter(callback: IRecyclerViewCallback) : RecyclerViewAdapter<ICTransactionPVCard.ICItemTransaction>(callback) {
    override fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (holder is ViewHolder) {
            holder.bind(listData[position])
        }
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICTransactionPVCard.ICItemTransaction>(LayoutInflater.from(parent.context).inflate(R.layout.item_history_transaction_pv_combank, parent, false)) {
        override fun bind(obj: ICTransactionPVCard.ICItemTransaction) {
            itemView.imgType.setImageResource(if (obj.amount!!.toInt() > 0) {
                R.drawable.ic_xu_add_20px
            } else {
                R.drawable.ic_xu_contract_20px
            })
            itemView.tvMoney.text = obj.amount
            itemView.tvMission.text = obj.tranType
            itemView.tvContent.text = obj.tranDescription
            itemView.tvTime.text = obj.tranTime
        }
    }

}