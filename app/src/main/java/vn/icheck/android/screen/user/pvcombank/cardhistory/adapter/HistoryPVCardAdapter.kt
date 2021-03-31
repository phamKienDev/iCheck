package vn.icheck.android.screen.user.pvcombank.cardhistory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.base.adapter.RecyclerViewAdapter
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.databinding.ItemInfoPvcardHolderBinding
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.models.pvcombank.ICListCardPVBank

class HistoryPVCardAdapter : RecyclerViewAdapter<ICListCardPVBank>() {
    override fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    fun showOrHide(cardId: String?, show: Boolean, fullCard: String) {
        for (i in 0 until listData.size) {
            if (listData[i].cardId == cardId) {
                listData[i].isShow = show
                if (fullCard.isNotEmpty()) {
                    listData[i].cardMasking = fullCard
                }
                notifyItemChanged(i)
                return
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (holder is ViewHolder) {
            holder.bind(listData[position])
        }
    }

    inner class ViewHolder(parent: ViewGroup, val binding: ItemInfoPvcardHolderBinding = ItemInfoPvcardHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)) : BaseViewHolder<ICListCardPVBank>(binding.root) {

        override fun bind(obj: ICListCardPVBank) {
            val expDate = if (!obj.expDate.isNullOrEmpty() && obj.expDate!!.length == 6) {
                val repYear = obj.expDate!!.substring(0, 4)
                val lastYear = repYear.substring(2, 4)
                val repMonth = obj.expDate!!.substring(4, 6)
                "$repMonth/$lastYear"
            } else {
                ""
            }

            if (obj.isShow) {
                binding.tvMoney.text = ("${TextHelper.formatMoney(obj.avlBalance ?: "0")} đ")
                binding.tvCardNumber.text = if (obj.cardMasking.isNullOrEmpty()) {
                    itemView.context.getString(R.string.dang_cap_nhat)
                } else {
                    if (obj.cardMasking!!.length == 16) {
                        obj.cardMasking!!.substring(0, 4) + " " + obj.cardMasking!!.substring(4, 8) + " " + obj.cardMasking!!.substring(8, 12) + " " + obj.cardMasking!!.substring(12, 16)
                    } else {
                        obj.cardMasking!!
                    }
                }
                binding.tvName.text = obj.embossName ?: itemView.context.getString(R.string.dang_cap_nhat)
                binding.tvDateEnd.text = expDate
            } else {
                binding.tvMoney.text = "******"
                binding.tvCardNumber.text = "**** **** **** ****"
                binding.tvName.text = ""
                binding.tvDateEnd.text = "**/**"
            }

            updateWidth(binding.tvDateEnd)
            updateWidth(binding.tvCCV)
        }

        private fun updateWidth(textView: AppCompatTextView) {
            textView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    textView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    textView.layoutParams.apply {
                        width = ConstraintLayout.LayoutParams.WRAP_CONTENT
                    }
                    textView.requestLayout()
                }
            })
        }
    }
}