package vn.icheck.android.loyalty.screen.loyalty_customers.gift_shop

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_gift_shop.view.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.ICMessageEvent
import vn.icheck.android.loyalty.helper.SizeHelper
import vn.icheck.android.loyalty.helper.TextHelper
import vn.icheck.android.loyalty.helper.WidgetHelper
import vn.icheck.android.loyalty.model.LoyaltyGiftItem
import java.util.*

class GiftShopAdapter(val listData:List<LoyaltyGiftItem>): PagingDataAdapter<LoyaltyGiftItem, RecyclerView.ViewHolder>(COMPARATOR) {

    companion object{
        val COMPARATOR = object : DiffUtil.ItemCallback<LoyaltyGiftItem>() {
            override fun areItemsTheSame(oldItem: LoyaltyGiftItem, newItem: LoyaltyGiftItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: LoyaltyGiftItem, newItem: LoyaltyGiftItem): Boolean {
                return oldItem.quantityRemain == newItem.quantityRemain && oldItem.updatedAt == newItem.updatedAt
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val lf = LayoutInflater.from(parent.context)
        val v = lf.inflate(R.layout.item_gift_shop, parent, false)
        return ItemGiftShopHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ItemGiftShopHolder
        getItem(position)?.let {data ->
            WidgetHelper.loadImageUrlRounded(holder.view.img_product, data.gift?.image?.small.toString(), SizeHelper.size4)
            holder.view.tv_product_name.text = data.gift?.name
            holder.view.tv_quantity.text = "Còn lại: ${TextHelper.formatMoneyPhay(data.quantityRemain)}"
            holder.view.tv_gift_type.text = when (data.gift?.type?.toLowerCase(Locale.ROOT)){
                "ICOIN".toLowerCase(Locale.ROOT) -> {
                    "Quà xu"
                }
                "PHONE_CARD".toLowerCase(Locale.ROOT) -> {
                    "Quà thẻ cào"
                }
                "RECEIVE_STORE".toLowerCase(Locale.ROOT) -> {
                    "Quà nhận tại cửa hàng"
                }
                "PRODUCT".toLowerCase(Locale.ROOT) -> {
                    "Quà hiện vật"
                }
                else -> "Quà tinh thần"
            }
            holder.view.tv_business_name.text = data.pointName
            holder.view.tv_price.text = TextHelper.formatMoneyPhay(data.pointExchange)

            holder.view.setOnClickListener {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.OPEN_DETAIL_GIFT, data))
            }
        }
    }

    inner class ItemGiftShopHolder(val view: View): RecyclerView.ViewHolder(view)
}