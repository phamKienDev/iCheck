package vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.point_history.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_point_history.view.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.*
import vn.icheck.android.loyalty.base.listener.IRecyclerViewCallback
import vn.icheck.android.loyalty.base.commons.RecyclerViewCustomAdapter
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.helper.TextHelper
import vn.icheck.android.loyalty.helper.TimeHelper
import vn.icheck.android.loyalty.model.ICKPointHistory

internal class PointHistoryAllAdapter(callback: IRecyclerViewCallback) : RecyclerViewCustomAdapter<ICKPointHistory>(callback) {

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

    class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICKPointHistory>(R.layout.item_point_history, parent) {

        @SuppressLint("SetTextI18n")
        override fun bind(obj: ICKPointHistory) {
            /**
             * status = 1 là cộng
             * status = 2 là trừ
             */
            val type = SharedLoyaltyHelper(itemView.context).getBoolean(ConstantsLoyalty.HAS_CHANGE_CODE_REDEEM_POINTS)

            if (obj.points != null && obj.points!! > 0L) {
                itemView.tvPoint.run {
                    text = "+ ${TextHelper.formatMoneyPhay(obj.points)}"
                    setTextColor(ContextCompat.getColor(context, R.color.green2))
                }

                itemView.tvHintSerial.text = if (!type) {
                    "Số serial:"
                } else {
                    "Mã code:"
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

                itemView.tvTime.text = "Thời gian nhận"
            } else {
                itemView.tvPoint.run {
                    text = TextHelper.formatMoneyPhay(obj.points)
                    setTextColor(ContextCompat.getColor(context, R.color.colorAccentRed))
                }

                itemView.tvHintSerial.text = "Nội dung:"

                itemView.tvSerial.text = if (obj.message.isNullOrEmpty()) {
                    itemView.context.getString(R.string.dang_cap_nhat)
                } else {
                    obj.message
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    itemView.tvSerial.letterSpacing = 0F
                }


                itemView.tvTime.text = "Thời gian đổi:"
            }

            itemView.tvDate.text = if (!obj.created_at.isNullOrEmpty()) {
                TimeHelper.convertDateTimeSvToTimeDateVn(obj.created_at)
            } else {
                itemView.context.getString(R.string.dang_cap_nhat)
            }
        }
    }

}