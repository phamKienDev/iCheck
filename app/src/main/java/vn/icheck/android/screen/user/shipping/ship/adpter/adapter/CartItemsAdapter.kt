package vn.icheck.android.screen.user.shipping.ship.adpter.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_product_in_order.view.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.R
import vn.icheck.android.databinding.ItemCartBinding
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.model.cart.ItemCartItem
import vn.icheck.android.room.database.AppDatabase
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.util.ick.getLayoutInflater
import vn.icheck.android.util.ick.loadImageWithHolder
import vn.icheck.android.util.ick.loadRoundedImage
import vn.icheck.android.util.ick.simpleText
import vn.icheck.android.util.kotlin.WidgetUtils

class CartItemsAdapter(val listData: List<ItemCartItem>, val onAdd: (Int) -> Unit, val onRemove: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onCheckedChange: ((Int) -> Unit?)? = null
    var onDelete: ((Int) -> Unit?)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val lf = parent.getLayoutInflater()
        val v = ItemCartBinding.inflate(lf, parent, false)
        return CartItemHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as CartItemHolder
        val cartItem = listData[position]
        holder.binding.tvGift simpleText cartItem.product?.name
        /**
         * Đúng rồi đừng có sửa nữa anh ơi3
         */
        WidgetUtils.loadImageUrlRounded(holder.binding.imgGift, cartItem.product?.imageUrl, R.drawable.img_default_product_big, SizeHelper.size4)
//        holder.binding.imgGift.loadRoundedImage(cartItem.product?.imageUrl, R.drawable.img_product_shop_default, corner = 4)
        holder.binding.imgGift.setOnClickListener {
            ICheckApplication.currentActivity()?.let {act ->
                IckProductDetailActivity.start(act, cartItem.product?.originId ?: 0L)
            }
        }
        holder.binding.tvTotal simpleText cartItem.quantity.toString()
        holder.binding.tvQuantityGift simpleText "${TextHelper.formatMoneyPhay(cartItem.price)} Xu"
        holder.binding.checkBox.isChecked = cartItem.isSelected
        holder.binding.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            onCheckedChange?.let {
                it(position)
            }
        }
        holder.binding.imgRemove.setOnClickListener {
            AppDatabase.getDatabase(ICheckApplication.getInstance()).productIdInCartDao().deleteProductById(cartItem.product?.id
                    ?: -1)
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_PRICE))
            onDelete?.let {
                it(position)
            }
        }
        if (cartItem.quantity ?: 0 > 1) {
            holder.binding.imgMinus.setImageResource(R.drawable.ic_unadd_cart_40)
        } else {
            holder.binding.imgMinus.setImageResource(R.drawable.ic_cart_contract_unactive_40px)
        }
        if (cartItem.quantity ?: 0 < cartItem.productTotal ?: 0) {
            holder.binding.imgAdd.setImageResource(R.drawable.ic_add_cart_40)
        } else {
            holder.binding.imgAdd.setImageResource(R.drawable.ic_cart_add_unactive_40px)
        }
        holder.binding.imgAdd.setOnClickListener {
            if (cartItem.quantity ?: 0 < cartItem.productTotal ?: 0) {
                holder.binding.imgAdd.setImageResource(R.drawable.ic_add_cart_40)
                onAdd(holder.bindingAdapterPosition)
            } else {
                holder.binding.imgAdd.setImageResource(R.drawable.ic_cart_add_unactive_40px)
            }
        }
        holder.binding.imgMinus.setOnClickListener {
            if (cartItem.quantity ?: 0 > 1) {
                holder.binding.imgMinus.setImageResource(R.drawable.ic_unadd_cart_40)
                onRemove(holder.bindingAdapterPosition)
            } else {
                holder.binding.imgMinus.setImageResource(R.drawable.ic_cart_contract_unactive_40px)
            }
        }
    }

    override fun getItemCount() = listData.size

    inner class CartItemHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)
}