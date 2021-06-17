package vn.icheck.android.screen.user.cart.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.network.models.ICItemCart
import vn.icheck.android.screen.user.cart.view.ICartChildView
import vn.icheck.android.util.kotlin.WidgetUtils

class CartChildAdapter(val listener: ICartChildView, val listData: MutableList<ICItemCart>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int = listData.size

    override fun getItemViewType(position: Int): Int {
        return if (listData[position].can_add_to_cart == true && listData[position].stock > 0) {
            1
        } else {
            super.getItemViewType(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_cart_child, parent, false))
        } else {
            ViewHolderUnavailable(LayoutInflater.from(parent.context).inflate(R.layout.item_cart_child_unavailable, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                holder.bind(listData[position])
            }
            is ViewHolderUnavailable -> {
                holder.bind(listData[position])
            }
        }
    }

    inner class ViewHolder(view: View) : BaseViewHolder<ICItemCart>(view) {
//        private lateinit var layoutContainer: ConstraintLayout
        private lateinit var imgChecked: AppCompatImageButton
        private lateinit var tvCount: AppCompatTextView
        private lateinit var tvProductPrice: AppCompatTextView
        private lateinit var tvStatus: AppCompatTextView

        override fun bind(obj: ICItemCart) {
//            layoutContainer = itemView.findViewById(R.id.layoutContainer)
            imgChecked = itemView.findViewById(R.id.imgChecked)
            tvCount = itemView.findViewById(R.id.tvCount)
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice)
            tvStatus = itemView.findViewById(R.id.tvStatus)

            WidgetUtils.loadImageUrlRounded(itemView.findViewById<AppCompatImageView>(R.id.imgAvatar), obj.image?.square, WidgetUtils.defaultError, SizeHelper.size5)
            itemView.findViewById<AppCompatTextView>(R.id.tvName).text = obj.name

            if (CartParentAdapter.skipCart[obj.item_id] == true) {
//                layoutContainer.alpha = 0.5F
                imgChecked.setImageResource(R.drawable.ic_square_unchecked_light_blue_20dp)
                tvStatus.visibility = View.VISIBLE
            } else {
//                layoutContainer.alpha = 1F
                imgChecked.setImageResource(R.drawable.ic_square_checked_light_blue_20dp)
                tvStatus.visibility = View.GONE
            }

            tvCount.setBackgroundColor(Constant.getAppBackgroundWhiteColor(tvCount.context))
            tvCount.text = (CartParentAdapter.quantityCart[obj.item_id] ?: obj.quantity).toString()

            itemView.findViewById<AppCompatTextView>(R.id.tvOldPrice)?.run {
                if (obj.origin_price > obj.price) {
                    text = itemView.context.getString(R.string.xxx_d, TextHelper.formatMoney((obj.origin_price * tvCount.text.toString().toInt())))
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    text = null
                }
            }


            tvProductPrice.text = itemView.context.getString(R.string.xxx_d, TextHelper.formatMoney((obj.price * tvCount.text.toString().toInt())))

            var attributes = ""

            for (atr in obj.attributes ?: mutableListOf()) {
                attributes += if (attributes.isNotEmpty()) {
                    ", " + atr.value
                } else {
                    atr.value
                }
            }

            itemView.findViewById<AppCompatTextView>(R.id.tvAttributes).text = attributes

            itemView.setOnClickListener {
                listener.onClickItemProduct(obj.product_id)
            }

            imgChecked.setOnClickListener {
                val isChecked: Boolean

                if (CartParentAdapter.skipCart[obj.item_id] == null) {
                    CartParentAdapter.skipCart[obj.item_id] = true
                    isChecked = true
                    imgChecked.setImageResource(R.drawable.ic_square_unchecked_light_blue_20dp)
                    tvStatus.visibility = View.VISIBLE
                } else {
                    CartParentAdapter.skipCart.remove(obj.item_id)
                    isChecked = false
                    imgChecked.setImageResource(R.drawable.ic_square_checked_light_blue_20dp)
                    tvStatus.visibility = View.GONE
                }

                val skipAll = isSkipAll

//                if (skipAll) {
//                    layoutContainer.alpha = 1f
//                } else {
//                    layoutContainer.alpha = if (isChecked) {
//                        0.5f
//                    } else {
//                        1f
//                    }
//                }

                listener.onSkipItem(skipAll)
            }

            itemView.findViewById<AppCompatTextView>(R.id.tvDelete).setOnClickListener {
                listener.onUpdateItemQuantity(obj, 0, adapterPosition)
            }

            itemView.findViewById<AppCompatImageButton>(R.id.btnUnAdd).setOnClickListener {
                var count = CartParentAdapter.quantityCart[obj.item_id] ?: obj.quantity

                if (count > 0) {
                    count -= 1
                    CartParentAdapter.quantityCart[obj.item_id] = count
                    tvCount.text = count.toString()
                    tvProductPrice.text = itemView.context.getString(R.string.xxx_d, TextHelper.formatMoney((obj.price * tvCount.text.toString().toInt())))
                    listener.onUpdateItemQuantity(obj, count, adapterPosition)
                }
            }

            itemView.findViewById<AppCompatImageButton>(R.id.btnAdd).setOnClickListener {
                var count = CartParentAdapter.quantityCart[obj.item_id] ?: obj.quantity

                if (count < obj.stock) {
                    count += 1
                    CartParentAdapter.quantityCart[obj.item_id] = count
                    tvCount.text = count.toString()
                    tvProductPrice.text = itemView.context.getString(R.string.xxx_d, TextHelper.formatMoney((obj.price * tvCount.text.toString().toInt())))
                    listener.onUpdateItemQuantity(obj, count, adapterPosition)
                } else {
                    listener.onNotEnoughInStock()
                }
            }
        }

        fun updateQuantity() {
            val obj = listData[adapterPosition]
            listData[adapterPosition].quantity = CartParentAdapter.quantityCart[obj.item_id]
                    ?: obj.quantity
        }

        fun refreshQuantity() {
            val obj = listData[adapterPosition]
            tvCount.text = (CartParentAdapter.quantityCart[obj.item_id] ?: obj.quantity).toString()
            tvProductPrice.text = itemView.context.getString(R.string.xxx_d, TextHelper.formatMoney((obj.price * tvCount.text.toString().toInt())))
        }

        private val isSkipAll: Boolean
            get() {
                for (child in listData) {
                    if (child.can_add_to_cart == true && child.stock > 0 && CartParentAdapter.skipCart[child.item_id] != true) {
                        return false
                    }
                }

                return true
            }
    }

    inner class ViewHolderUnavailable(view: View) : BaseViewHolder<ICItemCart>(view) {

        override fun bind(obj: ICItemCart) {
            val viewAlpha = 0.5f

            itemView.findViewById<AppCompatImageView>(R.id.imgAvatar)?.run {
                WidgetUtils.loadImageUrlRounded(this, obj.image?.square, WidgetUtils.defaultError, SizeHelper.size5)
                alpha = viewAlpha
            }

            itemView.findViewById<AppCompatTextView>(R.id.tvName).run {
                text = obj.name
                alpha = viewAlpha
            }

            var attributes = ""

            for (atr in obj.attributes ?: mutableListOf()) {
                attributes += if (attributes.isNotEmpty()) {
                    ", " + atr.value
                } else {
                    atr.value
                }
            }

            itemView.findViewById<AppCompatTextView>(R.id.tvAttributes).run {
                text = attributes
                alpha = viewAlpha
            }

            itemView.findViewById<AppCompatTextView>(R.id.tvStatus).run {
                text = if (obj.stock > 0) {
                    context.getString(R.string.san_pham_da_ngung_ban)
                } else {
                    context.getString(R.string.san_pham_da_het_hang)
                }
            }

            itemView.findViewById<AppCompatTextView>(R.id.tvDelete).setOnClickListener {
                listener.onUpdateItemQuantity(obj, 0, adapterPosition)
            }
        }
    }
}