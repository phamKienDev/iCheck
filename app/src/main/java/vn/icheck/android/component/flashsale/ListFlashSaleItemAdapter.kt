package vn.icheck.android.component.flashsale

import android.graphics.Paint
import android.text.Html
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.models.ICFlashSale
import vn.icheck.android.util.kotlin.WidgetUtils


class ListFlashSaleItemAdapter(val listData: MutableList<ICFlashSale.Products>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemFlashSaleHolder(parent)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemFlashSaleHolder).bind(listData[position])
    }

    inner class ItemFlashSaleHolder(parent: ViewGroup) : BaseViewHolder<ICFlashSale.Products>(ViewHelper.createListFlashSale(parent.context)) {
        lateinit var imgProduct: AppCompatImageView
        lateinit var tvSaleCount: AppCompatTextView
        lateinit var tvNameProduct: AppCompatTextView
        lateinit var tvSpecialPrice: AppCompatTextView
        lateinit var tvPrice: AppCompatTextView
        lateinit var tvVerifed: AppCompatTextView
        lateinit var viewLine: View

        override fun bind(obj: ICFlashSale.Products) {
            (itemView as ConstraintLayout).run {
                imgProduct=getChildAt(0) as AppCompatImageView
                tvSaleCount=getChildAt(1) as AppCompatTextView
                tvNameProduct=getChildAt(2) as AppCompatTextView
                tvSpecialPrice=getChildAt(3) as AppCompatTextView
                tvPrice=getChildAt(4) as AppCompatTextView
                tvVerifed=getChildAt(5) as AppCompatTextView
                viewLine=getChildAt(6) as View
            }
            if (obj.media.content.isNullOrEmpty()) {
                WidgetUtils.loadImageUrlRounded(imgProduct, "", SizeHelper.size4)
            } else {
                WidgetUtils.loadImageUrlRounded(imgProduct, obj.media.content, SizeHelper.size4)
            }

            if (obj.hasSold > 0) {
                tvSaleCount.visibility = View.VISIBLE
                tvSaleCount.text = itemView.context.getString(R.string.da_ban_x, obj.hasSold)
            } else {
                tvSaleCount.visibility = View.GONE
            }

            tvNameProduct.text = if (obj.name.isNullOrEmpty()) {
                itemView.context.getString(R.string.dang_cap_nhat)
            } else {
                obj.name
            }

            if (obj.bestprice > 0L) {
                tvSpecialPrice.visibility = View.VISIBLE
                tvSpecialPrice.text = Html.fromHtml(itemView.context.getString(R.string.xxx___d, TextHelper.formatMoney(obj.price)))
                tvSpecialPrice.paintFlags = tvSpecialPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                tvPrice.text = Html.fromHtml(itemView.context.getString(R.string.xxx___d, TextHelper.formatMoney(obj.bestprice)))
            } else {
                tvSpecialPrice.visibility = View.GONE
                tvPrice.text = Html.fromHtml(itemView.context.getString(R.string.xxx___d, TextHelper.formatMoney(obj.price)))
            }


            tvVerifed.visibility = if (obj.verified) {
                View.VISIBLE
            } else {
                View.GONE
            }

            viewLine.visibility = if ((adapterPosition + 1) % 3 == 0) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

    }
}