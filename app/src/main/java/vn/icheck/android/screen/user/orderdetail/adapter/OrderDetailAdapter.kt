package vn.icheck.android.screen.user.orderdetail.adapter

import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_order_detail_status.view.*
import vn.icheck.android.R
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.ICOrderDetail
import vn.icheck.android.base.holder.LongMessageHolder
import vn.icheck.android.screen.user.orderdetail.view.IOrderDetailView
import vn.icheck.android.screen.user.orderhistory.OrderHistoryActivity
import vn.icheck.android.ui.layout.CustomLinearLayoutManager
import vn.icheck.android.util.kotlin.WidgetUtils

class OrderDetailAdapter(val listener: IOrderDetailView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var obj: ICOrderDetail? = null
    private val listData = mutableListOf<Int>()

    private var icon: Int = 0
    private var error = ""
    private var button: Int? = null

    fun setData(detail: ICOrderDetail, list: MutableList<Int>) {
        obj = detail
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun setMessage(icon: Int, error: String, button: Int?) {
        this.icon = icon
        this.error = error
        this.button = button
    }

    fun setError(icon: Int, error: String, button: Int?) {
        setMessage(icon, error, button)
        notifyDataSetChanged()
    }

    val getOrderDetail: ICOrderDetail?
        get() {
            return obj
        }

    override fun getItemCount(): Int {
        return if (obj != null) {
            listData.size
        } else {
            if (error.isNotEmpty()) {
                1
            } else {
                0
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (error.isEmpty()) {
            listData[position]
        } else {
            0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> {
                StatusHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_order_detail_status, parent, false))
            }
            2 -> {
                AddressHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_order_detail_address, parent, false))
            }
            3 -> {
                ShippingHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_order_detail_shipping, parent, false))
            }
            4 -> {
                PaymentHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_order_detail_payment, parent, false))
            }
            5 -> {
                PaymentMethodHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_order_detail_payment_method, parent, false))
            }
            6 -> {
                ProductHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_order_detail_parent, parent, false))
            }
            else -> {
                LongMessageHolder(parent)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StatusHolder -> {
                holder.bind()
            }
            is AddressHolder -> {
                holder.bind()
            }
            is ShippingHolder -> {
                holder.bind()
            }
            is PaymentHolder -> {
                holder.bind()
            }
            is PaymentMethodHolder -> {
                holder.bind()
            }
            is ProductHolder -> {
                holder.bind()
            }

            is LongMessageHolder -> {
                holder.bind(icon, error, button)

                holder.setListener(View.OnClickListener {
                    listener.onTryAgainClicked()
                })
            }
        }
    }

    private inner class StatusHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            itemView.tvAction.visibility = View.VISIBLE

            when (obj?.status) {
                OrderHistoryActivity.waitForPay -> {
                    itemView.layoutTop.setBackgroundColor(Color.parseColor("#C96915"))
                    itemView.tvStatus.setText(R.string.cho_thanh_toan)
                }
                OrderHistoryActivity.waitForConfirmation -> {
                    itemView.layoutTop.setBackgroundColor(Color.parseColor("#FBB800"))
                    itemView.tvStatus.setText(R.string.cho_xac_nhan)
                }
                OrderHistoryActivity.delivery -> {
                    itemView.layoutTop.setBackgroundColor(Color.parseColor(Constant.getAccentBlueCode))
                    itemView.tvStatus.setText(R.string.dang_giao)
                }
                OrderHistoryActivity.delivered -> {
                    itemView.layoutTop.setBackgroundColor(Color.parseColor("#49AA2D"))
                    itemView.tvStatus.setText(R.string.da_hoan_thanh)
                }
                OrderHistoryActivity.canceled -> {
                    itemView.layoutTop.setBackgroundColor(Color.parseColor("#EB5757"))
                    itemView.tvStatus.setText(R.string.da_huy)
                }
                OrderHistoryActivity.error -> {
                    itemView.layoutTop.setBackgroundColor(Color.parseColor("#A31F45"))
                    itemView.tvStatus.setText(R.string.loi_thanh_toan)
                }
                OrderHistoryActivity.refund -> {
                    itemView.layoutTop.setBackgroundColor(Color.parseColor("#9B51E0"))
                    itemView.tvStatus.setText(R.string.tra_hang_hoan_tien)
                }
            }

            itemView.tvAction.setOnClickListener {
                obj?.status?.let { status ->
                    listener.onActionClicked(status)
                }
            }
        }
    }

    private inner class AddressHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            obj?.shipping_address?.let { address ->
                itemView.findViewById<AppCompatTextView>(R.id.tvName).text = ("${address.last_name} ${address.first_name}")
                itemView.findViewById<AppCompatTextView>(R.id.tvPhone).text = address.phone

                val mAddress = address.address + ", " + address.ward?.name + ", " +
                        address.district?.name + ", " + address.city?.name + ", " +
                        address.country?.name

                itemView.findViewById<AppCompatTextView>(R.id.tvAddress).text = mAddress
            }
        }
    }

    private inner class ShippingHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            obj?.shipping_method?.let { shipping ->
                itemView.findViewById<AppCompatTextView>(R.id.tvShippingUnit).text = shipping.name
            }

            itemView.findViewById<AppCompatTextView>(R.id.tvFollow)?.run {
                visibility = if (!obj?.shared_link.isNullOrEmpty()) View.VISIBLE else View.GONE

                setOnClickListener {
                    obj?.shared_link?.let { link ->
                        listener.onTrackingClicked(link)
                    }
                }
            }
        }
    }

    private inner class PaymentHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            itemView.findViewById<AppCompatTextView>(R.id.tvTotalMoney).text = itemView.context.getString(R.string.xxx_d, TextHelper.formatMoney(obj?.grand_total))
            itemView.findViewById<AppCompatTextView>(R.id.tvCountProduct).text = itemView.context.getString(R.string.so_luong_xxx_san_pham, obj?.items?.size?.toString())

            if (obj?.shipping_method?.provider == "default") {
                itemView.findViewById<AppCompatTextView>(R.id.tvProductPrice).text = itemView.context.getString(R.string.xxx_d, TextHelper.formatMoney(obj?.sub_total))
                itemView.findViewById<AppCompatTextView>(R.id.tvShippingPrice).text = Html.fromHtml(itemView.context.getString(R.string.thoa_thuan_voi_cua_hang_red))
            } else {
                itemView.findViewById<AppCompatTextView>(R.id.tvProductPrice).text = itemView.context.getString(R.string.xxx_d, TextHelper.formatMoney(obj?.sub_total))
                itemView.findViewById<AppCompatTextView>(R.id.tvShippingPrice).text = itemView.context.getString(R.string.xxx_d, TextHelper.formatMoney(obj?.shipping_amount))
            }
        }
    }

    private inner class PaymentMethodHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            val tvName = itemView.findViewById<AppCompatTextView>(R.id.tvName)
            tvName.text = obj?.payment_method?.name

            when (obj?.payment_method?.provider) {
                "vnpay" -> {
                    tvName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_payment_vnpay_40dp, 0, 0, 0)
                }
                "cod" -> {
                    tvName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_payment_cod_40dp, 0, 0, 0)
                }
                else -> {
                    tvName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_payment_default_40dp, 0, 0, 0)
                }
            }
        }
    }

    private inner class ProductHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            itemView.findViewById<AppCompatImageView>(R.id.viewBottom).background=ViewHelper.lineDottedHorizontalSecondary()

            itemView.findViewById<CircleImageView>(R.id.imgAvatar).run {
                WidgetUtils.loadImageUrl(this, obj?.shop?.avatar_thumbnails?.small, R.drawable.img_shop_default, R.drawable.img_shop_default)

                setOnClickListener {
                    obj?.shop_id?.let { shopID ->
                        listener.onGoToShop(shopID)
                    }
                }
            }

            itemView.findViewById<AppCompatTextView>(R.id.tvName).run {
                text = obj?.shop?.name

                setOnClickListener {
                    obj?.shop_id?.let { shopID ->
                        listener.onGoToShop(shopID)
                    }
                }
            }

            itemView.findViewById<RecyclerView>(R.id.recyclerView).run {
                layoutManager = CustomLinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false, false)
                adapter = OrderDetailChildAdapter(obj?.items ?: mutableListOf())
            }

            itemView.findViewById<AppCompatTextView>(R.id.tvNote).text = obj?.note

        }
    }
}