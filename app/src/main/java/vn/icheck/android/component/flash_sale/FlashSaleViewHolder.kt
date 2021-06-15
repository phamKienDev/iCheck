package vn.icheck.android.component.flash_sale

import android.os.CountDownTimer
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.network.models.ICFlashSale

class FlashSaleViewHolder(parent: ViewGroup) :
    BaseViewHolder<ICFlashSale>(ViewHelper.createFlashSaleViewHolder(parent.context)) {
    var timer: CountDownTimer? = null
    lateinit var layoutParent: LinearLayout
    lateinit var tvHour: AppCompatTextView
    lateinit var tvMinutes: AppCompatTextView
    lateinit var tvSecond: AppCompatTextView
    lateinit var rcvFlashSale: RecyclerView

    override fun bind(obj: ICFlashSale) {
        (itemView as LinearLayout).run {
            (getChildAt(1) as LinearLayout).run {
                tvHour = (getChildAt(1) as AppCompatTextView)
                tvMinutes = (getChildAt(2) as AppCompatTextView)
                tvSecond = (getChildAt(3) as AppCompatTextView)
            }
            rcvFlashSale = getChildAt(2) as RecyclerView
        }

        val adapter = ListFlashSaleItemAdapter(obj.products.toMutableList())
        rcvFlashSale.layoutManager =
            GridLayoutManager(itemView.context, 3, RecyclerView.HORIZONTAL, false)
        rcvFlashSale.adapter = adapter
    }

    fun updateCountDownText(p0: Long) {
        val hour = p0 / 1000 / 60 / 60
        val minutes = (p0 / 1000 / 60) - (hour * 60)
        val second = p0 / 1000 % 60

        tvHour.text = tvHour.context.getString(R.string.format_02_d, hour)
        tvMinutes.text = tvMinutes.context.getString(R.string.format_02_d, minutes)
        tvSecond.text = tvSecond.context.getString(R.string.format_02_d, second)

    }

}