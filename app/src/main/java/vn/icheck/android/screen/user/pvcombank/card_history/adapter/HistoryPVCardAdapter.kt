package vn.icheck.android.screen.user.pvcombank.card_history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_info_pvcard_holder.view.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.adapter.RecyclerViewAdapter
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.models.pvcombank.ICListCardPVBank

class HistoryPVCardAdapter : RecyclerViewAdapter<ICListCardPVBank>() {
    override fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    fun showOrHide(cardId: String?, show: Boolean) {
        for (i in 0 until listData.size) {
            if (listData[i].cardId == cardId) {
                listData[i].isShow = show
                notifyItemChanged(i)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (holder is ViewHolder) {
            holder.bind(listData[position])
        }
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICListCardPVBank>(LayoutInflater.from(parent.context).inflate(R.layout.item_info_pvcard_holder, parent, false)) {
        private var expDate = ""

        override fun bind(obj: ICListCardPVBank) {
            if (!obj.expDate.isNullOrEmpty() && obj.expDate!!.length == 6) {
                val repYear = obj.expDate!!.substring(0, 4)
                val lastYear = repYear.substring(2, 4)
                val repMonth = obj.expDate!!.substring(4, 6)
                expDate = "$repMonth/$lastYear"

            }

            if (obj.isShow) {
                itemView.btnShowHide.setImageResource(R.drawable.ic_eye_off_white_24px)
                itemView.tvMoney.text = "${TextHelper.formatMoney(obj.avlBalance ?: "0")} đ"
                itemView.tvCardNumber.text = obj.cardMasking
                        ?: itemView.context.getString(R.string.dang_cap_nhat)
                itemView.tvName.text = obj.embossName
                        ?: itemView.context.getString(R.string.dang_cap_nhat)
                itemView.tvTimeEnd.text = expDate
            } else {
                itemView.btnShowHide.setImageResource(R.drawable.ic_eye_on_white_24px)
                itemView.tvCardNumber.text = "**** **** **** ****"
                itemView.tvTimeEnd.text = "**/**"
            }

            itemView.btnShowHide.setOnClickListener {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.SHOW_OR_HIDE_PV_FULLCARD, obj))
            }
        }
    }
}