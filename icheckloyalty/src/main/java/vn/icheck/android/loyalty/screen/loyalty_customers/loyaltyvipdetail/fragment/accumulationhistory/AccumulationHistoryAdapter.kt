package vn.icheck.android.loyalty.screen.loyalty_customers.loyaltyvipdetail.fragment.accumulationhistory

import android.annotation.SuppressLint
import android.os.Build
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_accumulation_history.view.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.*
import vn.icheck.android.loyalty.base.commons.RecyclerViewCustomAdapter
import vn.icheck.android.loyalty.base.listener.IRecyclerViewCallback
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.helper.TextHelper
import vn.icheck.android.loyalty.model.ICKPointHistory

internal class AccumulationHistoryAdapter(callback: IRecyclerViewCallback) : RecyclerViewCustomAdapter<ICKPointHistory>(callback) {

    override fun getItemType(position: Int): Int {
        return ICKViewType.ITEM_TYPE
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ViewHolder) {
            holder.bind(listData[position])
        } else {
            super.onBindViewHolder(holder, position)
        }
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICKPointHistory>(R.layout.item_accumulation_history, parent) {

        @SuppressLint("SetTextI18n")
        override fun bind(obj: ICKPointHistory) {
            if (adapterPosition == 0) {
                itemView.title.setVisible()
            } else {
                itemView.title.setGone()
            }

            val type = SharedLoyaltyHelper(itemView.context).getBoolean(ConstantsLoyalty.HAS_CHANGE_CODE_POINT_LONG_TIME)

            itemView.tvPoint.run {
                text = "+ ${TextHelper.formatMoneyPhay(obj.points)}"
                setTextColor(ContextCompat.getColor(context, R.color.green2))
            }

            itemView.tvHintSerial.apply {
                text = if (!type) {
                    context.getString(R.string.ma_serial)
                } else {
                    context.getString(R.string.ma_code)
                }
            }

            itemView.tvSerial.run {
                text = if (!type) {
                    if (obj.serial.isNullOrEmpty()) {
                        context.getString(R.string.dang_cap_nhat)
                    } else {
                        obj.serial
                    }
                } else {
                    if (obj.code.isNullOrEmpty()) {
                        context.getString(R.string.dang_cap_nhat)
                    } else {
                        obj.code
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    letterSpacing = 0.2F
                }
            }

            checkNullOrEmptyConvertDateTimeSvToTimeDateVn(itemView.tvDate, obj.created_at)
        }
    }
}