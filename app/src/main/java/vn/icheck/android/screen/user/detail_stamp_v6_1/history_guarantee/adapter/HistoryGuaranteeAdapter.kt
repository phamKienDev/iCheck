package vn.icheck.android.screen.user.detail_stamp_v6_1.history_guarantee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_history_guarantee.view.*
import kotlinx.android.synthetic.main.item_message.view.*
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.models.detail_stamp_v6_1.ICListHistoryGuarantee
import vn.icheck.android.screen.user.detail_stamp_v6_1.history_guarantee.view.IHistoryGuaranteeView

class HistoryGuaranteeAdapter(val listener: IHistoryGuaranteeView, val vietNamLanguage: Boolean?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listData = mutableListOf<ICListHistoryGuarantee>()
    private val itemType = -1
    private val emptyType = -3
    private var errCode = 0
    var isLoadMore = true


    fun setListData(list: MutableList<ICListHistoryGuarantee>?) {
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
        fun setData(obj: ICListHistoryGuarantee) {
            obj.status?.let {
                itemView.tvStatusName.text = it.name
            }

            if (vietNamLanguage == false) {
                itemView.tvCreatedTime.text = if (!obj.created_time.isNullOrEmpty()) {
                    TimeHelper.convertDateTimeSvToDateVn(obj.created_time)
                } else {
                    "updating"
                }
            } else {
                itemView.tvCreatedTime.text = if (!obj.created_time.isNullOrEmpty()) {
                    TimeHelper.convertDateTimeSvToDateVn(obj.created_time)
                } else {
                    itemView.context.getString(R.string.dang_cap_nhat)
                }
            }
        }
    }

    inner class ErrorHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(errorCode: Int) {
            val message = when (errorCode) {
                Constant.ERROR_INTERNET -> {
                    if (vietNamLanguage == false) {
                       "Checking network. Please try again"
                    } else {
                        itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                    }
                }
                Constant.ERROR_UNKNOW -> {
                    if (vietNamLanguage == false) {
                        "Occurred. Please try again"
                    } else {
                        itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    }
                }
                Constant.ERROR_EMPTY -> {
                    if (vietNamLanguage == false) {
                        "No Data"
                    } else {
                        itemView.context.getString(R.string.du_lieu_trong)
                    }
                }
                else -> {
                    if (vietNamLanguage == false) {
                        "Occurred. Please try again"
                    } else {
                        itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    }
                }
            }
            itemView.txtMessage.text = message
        }
    }

}