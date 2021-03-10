package vn.icheck.android.screen.user.cart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_cart_parent.view.*
import vn.icheck.android.R
import vn.icheck.android.base.adapter.RecyclerViewAdapter
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.helper.CartHelper
import vn.icheck.android.network.models.ICItemCart
import vn.icheck.android.room.entity.ICCart
import vn.icheck.android.screen.user.cart.view.ICartChildView
import vn.icheck.android.screen.user.cart.view.ICartView
import vn.icheck.android.ui.layout.CustomLinearLayoutManager
import vn.icheck.android.util.kotlin.WidgetUtils

class CartParentAdapter(override val listener: ICartView) : RecyclerViewAdapter<ICCart>(listener) {
    private val cartHelper = CartHelper()

    companion object {
        val skipCart = hashMapOf<Long, Boolean>()
        val quantityCart = hashMapOf<Long, Int>()
    }

    val getTotalPrice: Long
        get() {
            var price = 0L

            for (cart in listData) {
                if (skipCart[cart.id] != true) {
                    for (item in cart.items) {
                        if (item.can_add_to_cart == true && item.stock > 0 && skipCart[item.item_id] != true) {
                            price += (item.price * item.quantity)
                        }
                    }
                }
            }

            return price
        }

    val isExistCart: Boolean
        get() {
            for (it in listData) {
                if (skipCart[it.id] == null) {
                    return true
                }
            }

            return false
        }

    fun skipCart(cartID: Long, position: Int) {
        skipCart[cartID] = true

        for (parent in listData) {
            if (parent.id == cartID) {
                for (child in parent.items) {
                    skipCart[child.item_id] = true
                }

                notifyItemChanged(position)
                return
            }
        }
    }

    override fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_cart_parent, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(listData[position])
        } else {
            super.onBindViewHolder(holder, position)
        }
    }

    inner class ViewHolder(view: View) : BaseViewHolder<ICCart>(view) {

        override fun bind(obj: ICCart) {
            WidgetUtils.loadImageUrl(itemView.imgAvatar, obj.shop.avatar_thumbnails?.small, R.drawable.img_shop_default, R.drawable.img_shop_default)
            itemView.tvName.text = obj.shop.name

            itemView.tvName.setOnClickListener {
                listener.onClickShopDetail(obj.shop_id)
            }

            if (skipCart[obj.id] == true) {
//                itemView.layoutContainer.alpha = 0.5f
                itemView.imgChecked.setImageResource(R.drawable.ic_square_unchecked_light_blue_20dp)
            } else {
//                itemView.layoutContainer.alpha = 1f
                itemView.imgChecked.setImageResource(R.drawable.ic_square_checked_light_blue_20dp)
            }

            itemView.recyclerView.layoutManager = CustomLinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false, false)

            itemView.recyclerView.adapter = CartChildAdapter(object : ICartChildView {
                override fun onUpdateItemQuantity(obj: ICItemCart, count: Int, position: Int) {
                    listener.onChangeQuantity(obj, count, adapterPosition, position)
                }

                override fun onNotEnoughInStock() {
                    listener.onNotEnoughInStock()
                }

                override fun onClickItemProduct(productId: Long) {
                    listener.onClickShopProductDetail(productId)
                }

                override fun onSkipItem(isSkipAll: Boolean) {
                    if (isSkipAll) {
                        skipCart[obj.id] = true
                        itemView.imgChecked.setImageResource(R.drawable.ic_square_unchecked_light_blue_20dp)
//                        itemView.layoutContainer.alpha = 0.5f
                    } else {
                        if (skipCart[obj.id] != null) {
                            skipCart.remove(obj.id)
                        }
                        itemView.imgChecked.setImageResource(R.drawable.ic_square_checked_light_blue_20dp)
//                        itemView.layoutContainer.alpha = 1f
                    }
                    listener.updateTotalMoney()
                }
            }, obj.items)

            itemView.imgChecked.setOnClickListener {
                if (skipCart[obj.id] == null) {
                    listener.onSkipCart(obj.id, adapterPosition)
                } else {
                    skipCart.remove(obj.id)
                    for (child in obj.items) {
                        if (skipCart[child.item_id] != null) {
                            skipCart.remove(child.item_id)
                        }
                    }
                    notifyItemChanged(adapterPosition)
                    listener.updateTotalMoney()
                }
            }
        }

        fun updateQuantity(childPosition: Int) {
            itemView.recyclerView.findViewHolderForAdapterPosition(childPosition)?.let {
                if (it is CartChildAdapter.ViewHolder) {
                    it.updateQuantity()
                }
            }
        }

        fun refreshQuantity(childPosition: Int) {
            itemView.recyclerView.findViewHolderForAdapterPosition(childPosition)?.let {
                if (it is CartChildAdapter.ViewHolder) {
                    it.refreshQuantity()
                }
            }
        }

        fun removeCart(childPosition: Int) {
            val adapter = itemView.recyclerView.adapter

            if (adapter is CartChildAdapter) {
                if (adapter.listData.size > 1) {
                    quantityCart.remove(adapter.listData[childPosition].item_id)
                    adapter.listData.removeAt(childPosition)
                    adapter.notifyItemRemoved(childPosition)
                    adapter.notifyItemChanged(childPosition, adapter.listData.size)
                } else {
                    val pos = adapterPosition
                    quantityCart.clear()
                    listData.removeAt(pos)
                    notifyItemRemoved(pos)
                    notifyItemChanged(pos, listData.size)
                }
            }
        }
    }
}