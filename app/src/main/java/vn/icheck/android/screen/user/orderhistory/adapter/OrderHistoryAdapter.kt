package vn.icheck.android.screen.user.orderhistory.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_mission_detail.*
import kotlinx.android.synthetic.main.item_order_manager.view.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.adapter.RecyclerViewAdapter
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.order.OrderInteractor
import vn.icheck.android.network.models.ICOrderHistoryV2
import vn.icheck.android.network.models.ICRespID
import vn.icheck.android.network.models.OrderItemItem
import vn.icheck.android.screen.user.orderhistory.OrderHistoryActivity
import vn.icheck.android.screen.user.report.ReportActivity
import vn.icheck.android.screen.user.shipping.ship.ShipActivity
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.ick.showSimpleErrorToast
import vn.icheck.android.util.kotlin.WidgetUtils

class OrderHistoryAdapter(val status: Int, callback: IRecyclerViewCallback) : RecyclerViewAdapter<ICOrderHistoryV2>(callback) {

    fun addOrderDelivered(obj: ICOrderHistoryV2) {
        if (listData.isNullOrEmpty()) {
            listData.add(obj)
        } else {
            listData.add(0, obj)
        }
        notifyDataSetChanged()
    }

    override fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_order_manager, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(listData[position])
        } else {
            super.onBindViewHolder(holder, position)
        }
    }

    private inner class ViewHolder(view: View) : BaseViewHolder<ICOrderHistoryV2>(view) {

        @SuppressLint("SetTextI18n")
        @Suppress("DEPRECATION")
        override fun bind(obj: ICOrderHistoryV2) {
            itemView.tvOrderId.text = "Mã đơn: ${obj.code}"

            when (status) {
                OrderHistoryActivity.waitForConfirmation -> {
                    itemView.tvTime.text = "Ngày tạo đơn: ${TimeHelper.convertDateTimeSvToDateTimeVn(obj.createdAt, "HH:mm, dd/MM/yyyy")}"
                    itemView.bgButton.beGone()
                    itemView.tvCancelOrder.beVisible()
                }
                OrderHistoryActivity.delivery -> {
                    itemView.tvTime.text = "Thời gian cập nhật: ${TimeHelper.convertDateTimeSvToDateTimeVn(obj.updatedAt, "HH:mm, dd/MM/yyyy")}"
                    itemView.bgButton.beVisible()
                    itemView.tvCancelOrder.beGone()
                }
                OrderHistoryActivity.delivered -> {
                    itemView.tvTime.text = "Đã giao: ${TimeHelper.convertDateTimeSvToDateTimeVn(obj.completedAt, "HH:mm, dd/MM/yyyy")}"
                    itemView.bgButton.beGone()
                    itemView.tvCancelOrder.beGone()
                }
                OrderHistoryActivity.canceled -> {
                    itemView.tvTime.text = "Đã hủy: ${TimeHelper.convertDateTimeSvToDateTimeVn(obj.cancelledAt, "HH:mm, dd/MM/yyyy")}"
                    itemView.bgButton.beGone()
                    itemView.tvCancelOrder.beGone()
                }
                else -> {
                    itemView.bgButton.beGone()
                    itemView.tvCancelOrder.beGone()
                }
            }

            if (obj.shop?.id != 1L) {
                itemView.tvOrderId.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_mission_gift_24dp, 0)
            } else {
                itemView.tvOrderId.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }

            val adapter = ProductInOrderAdapter()
            itemView.rcvOrders.adapter = adapter

            if (!obj.orderItem.isNullOrEmpty()) {
                for (order in obj.orderItem!!) {
                    order.id = obj.id
                }
                adapter.setListData(obj.orderItem!!)

                if (obj.orderItem!!.size > 2) {
                    itemView.layoutCount.beVisible()
                    itemView.tvCount.text = "${obj.orderItem!!.size - 2} sản phẩm khác"
                    itemView.rcvOrders.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.dpToPx(82) * 2)
                } else {
                    itemView.layoutCount.beGone()
                    itemView.rcvOrders.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                }
            }


            itemView.tvError.apply {
                background = ViewHelper.bgOutlinePrimary1Corners4(context)
                setOnClickListener {
                    ICheckApplication.currentActivity()?.let {
                        ReportActivity.start(ReportActivity.order, obj.id, "Báo lỗi đơn hàng", it)
                    }
                }
            }

            itemView.tvConfirm.setOnClickListener {
                DialogHelper.showConfirm(itemView.context, "Bạn đã nhận được đơn hàng này từ nhà vận chuyển?", null, "Chưa", "Đã nhận hàng", true, object : ConfirmDialogListener {
                    override fun onDisagree() {

                    }

                    override fun onAgree() {
                        obj.id?.let { id ->
                            updateStatusOrder(id, OrderHistoryActivity.delivered)
                        }
                    }
                })
            }

            itemView.tvCancelOrder.setOnClickListener {
                DialogHelper.showConfirm(itemView.context, "Bạn chắc chắn muốn hủy đơn hàng này?", null, "Để sau", "Chắc chắn", true, object : ConfirmDialogListener {
                    override fun onDisagree() {

                    }

                    override fun onAgree() {
                        obj.id?.let { id ->
                            updateStatusOrder(id, OrderHistoryActivity.canceled)
                        }
                    }
                })
            }

            itemView.setOnClickListener {
                ICheckApplication.currentActivity()?.let {
                    obj.id?.let { id -> ShipActivity.startDetailOrder(it, id) }
                }
            }
        }

        fun updateStatusOrder(orderId: Long, status: Int) {
            if (NetworkHelper.isNotConnected(itemView.context)) {
                itemView.context.showSimpleErrorToast(itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
                return
            }

            OrderInteractor().updateStatusOrder(orderId, status, object : ICNewApiListener<ICResponse<ICRespID>> {
                override fun onSuccess(obj: ICResponse<ICRespID>) {
                    listData.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                    if (status == OrderHistoryActivity.delivered)
                        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_STATUS_ORDER_HISTORY, obj))
                }

                override fun onError(error: ICResponseCode?) {
                    itemView.context.showSimpleErrorToast(if (error?.message.isNullOrEmpty()) {
                        itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                    } else {
                        error!!.message
                    })

                }
            })
        }

    }
}