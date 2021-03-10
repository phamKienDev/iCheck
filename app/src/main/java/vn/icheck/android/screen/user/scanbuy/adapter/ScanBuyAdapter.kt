package vn.icheck.android.screen.user.scanbuy.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_scan_buy.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.models.ICItemCart
import vn.icheck.android.network.models.ICShopVariant
import vn.icheck.android.screen.user.scanbuy.view.IScanBuyView
import vn.icheck.android.util.kotlin.WidgetUtils

class ScanBuyAdapter(val listener: IScanBuyView) : RecyclerView.Adapter<ScanBuyAdapter.ViewHolder>() {
    private val listData = mutableListOf<ICShopVariant>()
    private val listCart = mutableListOf<ICItemCart>()

    private val quantityMap = mutableMapOf<Long, Int>()

    fun addItem(obj: ICShopVariant) {
        if (quantityMap[obj.id] != null) {
            return
        }

        listData.add(0, obj)
        quantityMap[obj.id] = 1
        notifyDataSetChanged()
    }

    fun setListCart(list: MutableList<ICItemCart>) {
        listCart.clear()
        listCart.addAll(list)
        notifyItemRangeChanged(0, listData.size)
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

    val getListData: MutableList<ICShopVariant>
        get() {
            return listData
        }

    override fun getItemCount(): Int = listData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_scan_buy, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ViewHolder(view: View) : BaseViewHolder<ICShopVariant>(view) {

        override fun bind(obj: ICShopVariant) {
            val cart = listCart.find { it.product_id == obj.product_id }
            itemView.tvExist.visibility = if (cart != null) {
                quantityMap[obj.id] = cart.quantity
                View.VISIBLE
            } else {
                View.INVISIBLE
            }

            val imgProduct = if (!obj.image_thumbs.isNullOrEmpty()) {
                obj.image_thumbs!![0].square
            } else {
                null
            }
            WidgetUtils.loadImageUrlRounded(itemView.imgProduct, imgProduct, R.drawable.ic_default_square, SizeHelper.size10)
            itemView.tvProductName.text = obj.name

            if (obj.sale_off == null) {
                itemView.tvPriceNoSale.visibility = View.GONE

                if (obj.price != null && obj.price != 0L) {
                    itemView.tvProductPrice.text = itemView.context.getString(R.string.gia_ban_xxx, TextHelper.formatMoney(obj.price))
                } else {
                    itemView.tvProductPrice.text = null
                }
            } else {
                if (!obj.sale_off) {
                    itemView.tvPriceNoSale.visibility = View.GONE

                    if (obj.price != null && obj.price != 0L) {
                        itemView.tvProductPrice.text = itemView.context.getString(R.string.gia_ban_xxx, TextHelper.formatMoney(obj.price))
                    } else {
                        itemView.tvProductPrice.text = null
                    }
                } else {
                    if (obj.special_price != null && obj.special_price != 0L) {
                        itemView.tvPriceNoSale.visibility = View.VISIBLE
                        itemView.tvPriceNoSale.paintFlags = itemView.tvPriceNoSale.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        itemView.tvPriceNoSale.text = (TextHelper.formatMoney(obj.price) + "đ")

                        if (obj.price != null && obj.price != 0L) {
                            itemView.tvProductPrice.text = itemView.context.getString(R.string.gia_ban_xxx, TextHelper.formatMoney(obj.special_price))
                        } else {
                            itemView.tvProductPrice.text = null
                        }
                    } else {
                        itemView.tvPriceNoSale.visibility = View.GONE

                        if (obj.price != null && obj.price != 0L) {
                            itemView.tvProductPrice.text = itemView.context.getString(R.string.gia_ban_xxx, TextHelper.formatMoney(obj.price))
                        } else {
                            itemView.tvProductPrice.text = null
                        }
                    }
                }
            }

            WidgetUtils.loadImageUrlRounded(itemView.imgAvatarShop, obj.shop?.avatar_thumbnails?.square, R.drawable.img_default_shop_logo, SizeHelper.size10)
            itemView.tvShopName.text = obj.shop?.name

            if (obj.quantity == 0) {
                itemView.tvCount.text = "0"
                itemView.btnUnAdd.isEnabled = false
                itemView.btnAdd.isEnabled = false
            } else {
                itemView.tvCount.text = (quantityMap[obj.id] ?: 1).toString()
                itemView.btnUnAdd.isEnabled = true
                itemView.btnAdd.isEnabled = true
            }

            itemView.imgClose.setOnClickListener {
                quantityMap.remove(obj.id)
                listData.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
                notifyItemRangeChanged(0, listData.size)
                listener.onUpdateView()
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

            itemView.tvDetail.setOnClickListener {
                listener.onClickDetailProduct(obj)
            }

            itemView.layoutShop.setOnClickListener {
                listener.onClickShopDetail(obj)
            }

            itemView.btnAddToCart.setOnClickListener {
                listener.onAddToCart(obj, quantityMap[obj.id] ?: 1)
            }
        }
    }
}