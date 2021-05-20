package vn.icheck.android.screen.user.checkoutcart.holder

import android.graphics.Paint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.item_checkout_order.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.models.ICCheckoutOrder
import vn.icheck.android.network.models.ICItemCart
import vn.icheck.android.network.models.ICShipping
import vn.icheck.android.screen.user.checkoutcart.view.ICheckoutCartView
import vn.icheck.android.util.kotlin.WidgetUtils

class CheckoutOrderHolder(view: View, val listener: ICheckoutCartView) : BaseViewHolder<ICCheckoutOrder>(view) {
    private var textWatcher: TextWatcher? = null

    override fun bind(obj: ICCheckoutOrder) {
        WidgetUtils.loadImageUrlRounded(itemView.imgAvatar, obj.shop.avatar_thumbnails?.small, R.drawable.ic_circle_avatar_default, R.drawable.ic_circle_avatar_default, SizeHelper.size12)
        itemView.tvName.text = obj.shop.name

        if (obj.sub_total < obj.shop.min_order_value ?: 0) {
            itemView.layoutNote.visibility = View.VISIBLE
            itemView.viewIndicator.visibility = View.VISIBLE
            itemView.tvNote.text = itemView.context.getString(R.string.text_checkout_cart_min_value, itemView.context.getString(R.string.xxx_d, TextHelper.formatMoney(obj.shop.min_order_value)))
        } else {
            itemView.layoutNote.visibility = View.GONE
            itemView.viewIndicator.visibility = View.INVISIBLE
        }

        addItem(itemView.layoutContent, obj.items)

        val shippingMethod = getShippingMethod(obj.shipping_method_id, obj.shipping_methods)
        if (shippingMethod != null) {
            itemView.tvShippingName.text = if (shippingMethod.shipping_amount > 0L) {
                itemView.context.getString(R.string.xxx_xxx, shippingMethod.method.name, itemView.context.getString(R.string.xxx_d, TextHelper.formatMoney(shippingMethod.shipping_amount)))
            } else {
                shippingMethod.method.name
            }
            itemView.tvShippingTime.text = shippingMethod.name
        } else {
            itemView.tvShippingName.setText(R.string.dang_cap_nhat)
            itemView.tvShippingTime.setText(R.string.dang_cap_nhat)
        }

        itemView.tvMoney.text = itemView.context.getString(R.string.xxx_d, TextHelper.formatMoney(obj.grand_total))

        itemView.tvShippingUnit.setOnClickListener {
            listener.onChangeShippingUnit(obj.shop_id, obj.shipping_method_id, obj.shipping_methods)
        }

        itemView.layoutShop.setOnClickListener {
            listener.onShopClicked(obj.shop_id)
        }

        itemView.tvBuyMore.setOnClickListener {
            listener.onShopClicked(obj.shop_id)
        }

        if (textWatcher != null) {
            itemView.edtNote.removeTextChangedListener(textWatcher)
            textWatcher = null
        }

        itemView.edtNote.setText(obj.note)

        textWatcher = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                obj.note = itemView.edtNote.text.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        }

        itemView.edtNote.addTextChangedListener(textWatcher)
    }

    private fun addItem(parent: LinearLayout, items: MutableList<ICItemCart>) {
        parent.removeAllViews()

        for (item in items) {
            if (parent.childCount > 0) {
                val view = View(parent.context)
                view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.size1)
                view.setBackgroundColor(ContextCompat.getColor(parent.context, R.color.grayLoyalty))
                parent.addView(view)
            }

            val layout = LayoutInflater.from(parent.context).inflate(R.layout.item_checkout_item, parent, false)
            val imgAvatar = layout.findViewById<AppCompatImageView>(R.id.imgAvatar)
            val tvName = layout.findViewById<AppCompatTextView>(R.id.tvName)
            val tvAttributes = layout.findViewById<AppCompatTextView>(R.id.tvAttributes)
            val tvCount = layout.findViewById<AppCompatTextView>(R.id.tvCount)
            val tvOldPrice = layout.findViewById<AppCompatTextView>(R.id.tvOldPrice)
            val tvPrice = layout.findViewById<AppCompatTextView>(R.id.tvPrice)

            WidgetUtils.loadImageUrlRounded6(imgAvatar, item.image?.square, R.drawable.ic_default_square, R.drawable.ic_default_square)
            tvName.text = item.name
            tvCount.text = ("x" + item.quantity)
            tvPrice.text = itemView.context.getString(R.string.xxx_d, TextHelper.formatMoney(item.price))

            if (item.origin_price > item.price) {
                tvOldPrice.text = itemView.context.getString(R.string.xxx_d, TextHelper.formatMoney(item.origin_price))
                tvOldPrice.paintFlags = tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                tvOldPrice.text = null
            }

            var attributes = ""

            for (atr in item.attributes ?: mutableListOf()) {
                if (attributes.isNotEmpty()) {
                    attributes += ", " + atr.value
                } else {
                    attributes += atr.value
                }
            }

            tvAttributes.text = attributes

            layout.setOnClickListener {
                listener.onProductClicked(item)
            }

            parent.addView(layout)
        }
    }

    private fun getShippingMethod(id: Int, list: MutableList<ICShipping>): ICShipping? {
        for (it in list) {
            if (it.id == id) {
                return it
            }
        }

        return null
    }


}