package vn.icheck.android.screen.user.scanbuyv2

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_product_scan_buy.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.models.ICProductVariant
import vn.icheck.android.network.models.ICShopVariant
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class ScanBuyAdapterV2 : RecyclerView.Adapter<ScanBuyAdapterV2.ViewHolder>() {
    private val listData = mutableListOf<ICProductVariant>()

    private val quantityMap = mutableMapOf<Long, Int>()

    fun addItem(obj: ICProductVariant) {
        if (quantityMap[obj.id] != null) {
            return
        }

        listData.add(0, obj)
        quantityMap[obj.id] = 1
        notifyDataSetChanged()
    }

    fun removeItem(id: Long) {
        for (i in listData.size - 1 downTo 0) {
            if (listData[i].id == id) {
                quantityMap.remove(listData[i].id)
                listData.removeAt(i)
                notifyItemRemoved(i)
                notifyItemRangeChanged(i, listData.size)
                return
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanBuyAdapterV2.ViewHolder {
        return ViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ScanBuyAdapterV2.ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICProductVariant>(LayoutInflater.from(parent.context).inflate(R.layout.item_product_scan_buy, parent, false)) {
        override fun bind(obj: ICProductVariant) {

            WidgetUtils.loadImageUrlRounded4(itemView.imgProduct, obj.image)
            WidgetUtils.loadImageUrl(itemView.imgAvatarShop, obj.image_shop, R.drawable.img_default_shop_logo)

            if (!obj.name.isNullOrEmpty()) {
                itemView.tvNameProduct.text = obj.name
            }

            if (!obj.variant.isNullOrEmpty()) {
                itemView.tvVariant.text = "Phân loại: ${obj.variant}"
            }

            if (obj.sale_off) {
                if (obj.special_price != null && obj.special_price != 0L) {
                    itemView.tvPriceNoSale.visibility = View.VISIBLE
                    itemView.tvPriceNoSale.paintFlags = itemView.tvPriceNoSale.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    itemView.tvPriceNoSale.text = (TextHelper.formatMoney(obj.price) + "đ")

                    if (obj.price != null && obj.price != 0L) {
                        itemView.tvPrice.text = TextHelper.formatMoney(obj.special_price)
                    } else {
                        itemView.tvPrice.text = null
                    }
                } else {
                    itemView.tvPriceNoSale.visibility = View.GONE

                    if (obj.price != null && obj.price != 0L) {
                        itemView.tvPrice.text = TextHelper.formatMoney(obj.price)
                    } else {
                        itemView.tvPrice.text = null
                    }
                }
            } else {
                itemView.tvPriceNoSale.visibility = View.GONE

                if (obj.price != null && obj.price != 0L) {
                    itemView.tvPrice.text = TextHelper.formatMoney(obj.price)
                } else {
                    itemView.tvPrice.text = null
                }
            }

            if (!obj.name_shop.isNullOrEmpty()) {
                itemView.tvNameShop.text = obj.name_shop
            }

            if (obj.quantity == 0) {
                itemView.tvCount.text = "0"
                itemView.btnUnAdd.isEnabled = false
                itemView.btnAdd.isEnabled = false
            } else {
                itemView.tvCount.text = (quantityMap[obj.id] ?: 1).toString()
                itemView.btnUnAdd.isEnabled = true
                itemView.btnAdd.isEnabled = true
            }

            itemView.setOnClickListener {
                ToastUtils.showLongWarning(itemView.context, "onClickItemView")
            }

            itemView.imgClose.setOnClickListener {
                ToastUtils.showLongWarning(itemView.context, "onClickClose")
            }

            itemView.tvAddToCart.setOnClickListener {
                ToastUtils.showLongWarning(itemView.context, "onClickAddToCart")
            }

            itemView.btnUnAdd.setOnClickListener {
                var count = quantityMap[obj.id] ?: 1

                if (count > 1) {
                    count -= 1
                    quantityMap[obj.id] = count
                    itemView.tvCount.text = count.toString()
                }
            }

            itemView.btnAdd.setOnClickListener {
                var count = quantityMap[obj.id] ?: 1

                if (count < obj.quantity) {
                    count += +1
                    quantityMap[obj.id] = count
                    itemView.tvCount.text = count.toString()
                }
            }

            itemView.view.setOnClickListener {
                ToastUtils.showLongWarning(itemView.context, "onClickShop")
            }
        }
    }
}