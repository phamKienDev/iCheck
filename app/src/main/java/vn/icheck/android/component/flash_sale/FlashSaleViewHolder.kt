package vn.icheck.android.component.flash_sale

import android.os.CountDownTimer
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.network.models.ICFlashSale

class FlashSaleViewHolder(parent: ViewGroup) : BaseViewHolder<ICFlashSale>(ViewHelper.createFlashSaleViewHolder(parent.context)) {
    var timer: CountDownTimer? = null
    lateinit var layoutParent: LinearLayout
    lateinit var tvHour: AppCompatTextView
    lateinit var tvMinutes: AppCompatTextView
    lateinit var tvSecond: AppCompatTextView
    lateinit var rcvFlashSale: RecyclerView

    override fun bind(obj: ICFlashSale) {
        (itemView as LinearLayout).run {
            (getChildAt(0) as LinearLayout).run {
                tvHour = (getChildAt(1) as AppCompatTextView)
                tvMinutes = (getChildAt(2) as AppCompatTextView)
                tvSecond = (getChildAt(3) as AppCompatTextView)
            }
            rcvFlashSale = getChildAt(1) as RecyclerView
        }
        val adapter = obj.products?.let { ListFlashSaleItemAdapter(it.toMutableList()) }
        rcvFlashSale.layoutManager = GridLayoutManager(itemView.context, 3, RecyclerView.HORIZONTAL, false)
        rcvFlashSale.adapter = adapter
    }

     fun updateCountDownText(p0:Long) {

                var hour = p0 / 1000 / 60 / 60
                var minutes = (p0 / 1000 / 60) - (hour * 60)
                var second = p0 / 1000 % 60

                tvHour.text = String.format("%02d", hour)
                tvMinutes.text = String.format("%02d", minutes)
                tvSecond.text = String.format("%02d", second)

    }

}