package vn.icheck.android.screen.user.shipping.ship.adpter.adapter

import android.os.Build
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.databinding.ItemCartBinding
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.ViewHelper.fillDrawableColor
import vn.icheck.android.network.model.cart.ItemCartItem
import vn.icheck.android.room.database.AppDatabase
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.util.ick.getLayoutInflater
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

        setupView(holder)

        holder.binding.tvGift simpleText cartItem.product?.name
        WidgetUtils.loadImageUrlRounded(holder.binding.imgGift, cartItem.product?.imageUrl, R.drawable.img_default_product_big, SizeHelper.size4)
        holder.binding.tvTotal simpleText cartItem.quantity.toString()
        holder.binding.tvQuantityGift simpleText "${TextHelper.formatMoneyPhay(cartItem.price)} Xu"

        holder.binding.checkBox.isChecked = cartItem.isSelected

        if (cartItem.quantity ?: 0 > 1) {
            holder.binding.imgMinus.setImageResource(R.drawable.ic_unadd_cart_40)
        } else {
            holder.binding.imgMinus.setImageResource(R.drawable.ic_cart_contract_unactive_40px)
        }
        if (cartItem.quantity ?: 0 < cartItem.productTotal ?: 0) {
            holder.binding.imgAdd.fillDrawableColor(R.drawable.ic_cart_add_active_40dp)
        } else {
            holder.binding.imgAdd.setImageResource(R.drawable.ic_cart_add_unactive_40dp)
        }

        setupListener(holder, cartItem, position)
    }

    private fun setupView(holder: CartItemHolder) {
        holder.binding.tvTotal.background = ViewHelper.bgGrayCorners4(holder.binding.tvTotal.context)

        holder.binding.imgAdd.fillDrawableColor(R.drawable.ic_cart_add_active_40dp)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.binding.checkBox.buttonTintList=ViewHelper.createColorStateList(
                ContextCompat.getColor(holder.itemView.context,R.color.grayB4),ColorManager.getPrimaryColor(holder.itemView.context))
        }
    }

    private fun setupListener(holder: CartItemHolder, cartItem: ItemCartItem, position: Int) {
        holder.binding.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            onCheckedChange?.let {
                it(position)
            }
        }

        holder.binding.imgAdd.setOnClickListener {
            if (cartItem.quantity ?: 0 < cartItem.productTotal ?: 0) {
                holder.binding.imgAdd.fillDrawableColor(R.drawable.ic_cart_add_active_40dp)
                onAdd(holder.bindingAdapterPosition)
            } else {
                holder.binding.imgAdd.setImageResource(R.drawable.ic_cart_add_unactive_40dp)
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

        holder.binding.imgGift.setOnClickListener {
            ICheckApplication.currentActivity()?.let {act ->
                IckProductDetailActivity.start(act, cartItem.product?.originId ?: 0L)
            }
        }

        holder.binding.imgRemove.setOnClickListener {
            AppDatabase.getDatabase(ICheckApplication.getInstance()).productIdInCartDao().deleteProductById(
                cartItem.product?.id
                    ?: -1
            )
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_PRICE))
            onDelete?.let {
                it(position)
            }
        }
    }

    override fun getItemCount() = listData.size

    inner class CartItemHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)
}