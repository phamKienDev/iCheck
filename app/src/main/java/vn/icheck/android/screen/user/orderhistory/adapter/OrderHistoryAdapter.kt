package vn.icheck.android.screen.user.orderhistory.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
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
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.order.OrderInteractor
import vn.icheck.android.network.models.ICOrderHistoryV2
import vn.icheck.android.network.models.ICRespID
import vn.icheck.android.screen.user.orderhistory.OrderHistoryActivity
import vn.icheck.android.screen.user.report.ReportActivity
import vn.icheck.android.screen.user.shipping.ship.ShipActivity
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.ichecklibs.util.showShortErrorToast
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText

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
                    TimeHelper.convertDateTimeSvToDateTimeVn(obj.createdAt, "HH:mm, dd/MM/yyyy")?.let {
                        itemView.tvTime.setText(R.string.ngay_tao_don_s, it)
                    }
                    itemView.bgButton.beGone()
                    itemView.tvCancelOrder.beVisible()
                }
                OrderHistoryActivity.delivery -> {
                    TimeHelper.convertDateTimeSvToDateTimeVn(obj.updatedAt, "HH:mm, dd/MM/yyyy")?.let {
                        itemView.tvTime.setText(R.string.thoi_gian_cap_nhat_s, it)
                    }
                    itemView.bgButton.beVisible()
                    itemView.tvCancelOrder.beGone()
                }
                OrderHistoryActivity.delivered -> {
                    TimeHelper.convertDateTimeSvToDateTimeVn(obj.completedAt, "HH:mm, dd/MM/yyyy")?.let{
                        itemView.tvTime.setText(R.string.da_giao_s, it)
                    }
                    itemView.bgButton.beGone()
                    itemView.tvCancelOrder.beGone()
                }
                OrderHistoryActivity.canceled -> {
                    TimeHelper.convertDateTimeSvToDateTimeVn(obj.cancelledAt, "HH:mm, dd/MM/yyyy")?.let {
                        itemView.tvTime.setText(R.string.da_huy_s, it)
                    }
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
                    obj.orderItem?.let {
                        itemView.tvCount.setText(R.string.d_san_pham_khac,it.size - 2)
                    }
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
                        ReportActivity.start(ReportActivity.order, obj.id, it.getString(R.string.bao_loi_don_hang), it)
                    }
                }
            }

            itemView.tvConfirm.apply {
                background = ViewHelper.bgPrimaryCorners4(itemView.context)
                setOnClickListener {
                    DialogHelper.showConfirm(itemView.context, it.context.getString(R.string.ban_da_nhan_duoc_don_hang_nay_tu_nha_van_chuyen), null, it.context.getString(R.string.chua), it.context.getString(R.string.da_nhan_hang), true, object : ConfirmDialogListener {
                        override fun onDisagree() {

                        }

                        override fun onAgree() {
                            obj.id?.let { id ->
                                updateStatusOrder(id, OrderHistoryActivity.delivered)
                            }
                        }
                    })
                }
            }

            itemView.tvCancelOrder.setOnClickListener {
                DialogHelper.showConfirm(itemView.context, it.context.getString(R.string.ban_chac_chan_muon_huy_don_hang_nay), null, it.context.getString(R.string.de_sau), it.context.getString(R.string.chac_chan), true, object : ConfirmDialogListener {
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
                itemView.context.showShortErrorToast(itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
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
                    itemView.context.showShortErrorToast(if (error?.message.isNullOrEmpty()) {
                        itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                    } else {
                        error!!.message
                    })

                }
            })
        }

    }
}