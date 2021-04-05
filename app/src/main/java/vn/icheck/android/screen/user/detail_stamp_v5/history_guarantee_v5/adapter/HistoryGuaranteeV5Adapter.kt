package vn.icheck.android.screen.user.detail_stamp_v5.history_guarantee_v5.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_history_guarantee.view.*
import kotlinx.android.synthetic.main.item_message.view.*
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.models.detail_stamp_v6.RESP_Log_History_v6
import vn.icheck.android.screen.user.detail_stamp_v5.history_guarantee_v5.view.IHistoryGuaranteeV5View

class HistoryGuaranteeV5Adapter(val listener: IHistoryGuaranteeV5View) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listData = mutableListOf<RESP_Log_History_v6>()
    private val itemType = -1
    private val emptyType = -3
    private var errCode = 0
    var isLoadMore = true

    fun setListData(list: MutableList<RESP_Log_History_v6>?) {
        listData.clear()
        listData.addAll(list!!)
        notifyDataSetChanged()
    }

    fun setError(code: Int) {
        errCode = code
        isLoadMore = false
        listData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemType -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_history_guarantee, parent, false))
            else -> ErrorHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return if (listData.isNotEmpty()) {
            listData.size
        } else
            1
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData.isNotEmpty())
            itemType
        else
            emptyType
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = listData[position]
                holder.setData(item)
                holder.itemView.setOnClickListener {
                    listener.setOnItemClick(item)
                }
            }

            is ErrorHolder -> {
                holder.setData(errCode)
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(obj: RESP_Log_History_v6) {
            when (obj.type) {
                0 -> {
                    itemView.tvStatusName.text = "Đã kích hoạt"
                }
                1 -> {
                    itemView.tvStatusName.text = "Tiếp nhận bảo hành"
                }
                2 -> {
                    itemView.tvStatusName.text = "Trả bảo hành"
                }
                else -> {
                    itemView.tvStatusName.text = "Từ chối bảo hành"
                }
            }

            itemView.tvCreatedTime.text = if (obj.created_time != null) {
                TimeHelper.convertMillisecondToFomateSv(obj.created_time!! * 1000L, "HH:mm dd/MM/yyyy")
            } else {
                itemView.context.getString(R.string.dang_cap_nhat)
            }
        }
    }

    inner class ErrorHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(errorCode: Int) {

            when(errorCode){
                Constant.ERROR_INTERNET -> {
                    itemView.btnTryAgain.visibility = View.VISIBLE
                }
                Constant.ERROR_UNKNOW -> {
                    itemView.btnTryAgain.visibility = View.VISIBLE
                }
                Constant.ERROR_EMPTY -> {
                    itemView.btnTryAgain.visibility = View.INVISIBLE
                }
                else -> {
                    itemView.btnTryAgain.visibility = View.INVISIBLE
                }
            }

            val message = when (errorCode) {
                Constant.ERROR_INTERNET -> {
                    itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                }
                Constant.ERROR_UNKNOW -> {
                    itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                }
                Constant.ERROR_EMPTY -> {
                    itemView.context.getString(R.string.khong_co_du_lieu)
                }
                else -> {
                    itemView.context.getString(R.string.khong_co_du_lieu)
                }
            }
            itemView.txtMessage.text = message
        }
    }

}