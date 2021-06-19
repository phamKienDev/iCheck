package vn.icheck.android.screen.user.detail_stamp_v6_1.history_guarantee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_message.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.ItemHistoryGuaranteeV61Binding
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.ichecklibs.util.beGone
import vn.icheck.android.ichecklibs.util.beVisible
import vn.icheck.android.loyalty.helper.ActivityHelper
import vn.icheck.android.network.models.detail_stamp_v6_1.ICListHistoryGuarantee
import vn.icheck.android.screen.user.detail_stamp_v6_1.detail_history_guarantee.DetailHistoryGuaranteeActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.StampDetailActivity
import vn.icheck.android.util.ick.rText

class HistoryGuaranteeAdapter(val listener: IRecyclerViewCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listData = mutableListOf<ICListHistoryGuarantee>()

    private val itemType = -1
    private val emptyType = -3
    private var errCode = 0
    var isLoadMore = true

    fun setListData(list: MutableList<ICListHistoryGuarantee>) {
        listData.clear()
        listData.addAll(list)
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
            itemType -> ViewHolder(parent)
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
                holder.bind(listData[position])
            }
            is ErrorHolder -> {
                holder.setData(errCode)
            }
        }
    }

    inner class ViewHolder(parent: ViewGroup, val binding: ItemHistoryGuaranteeV61Binding = ItemHistoryGuaranteeV61Binding.inflate(LayoutInflater.from(parent.context), parent, false)) : BaseViewHolder<ICListHistoryGuarantee>(binding.root) {

        override fun bind(obj: ICListHistoryGuarantee) {
            binding.tvStatus.text = obj.status?.name
            binding.tvState.apply {
                text = if (!obj.state?.name.isNullOrEmpty()) {
                    context.getString(R.string.tinh_trang_v2_xxx, obj.state!!.name)
                } else {
                    context.getString(R.string.tinh_trang_v2_xxx, context.getString(R.string.dang_cap_nhat))
                }
            }
            binding.tvDateTime.apply {
                text = if (!obj.created_time.isNullOrEmpty()) {
                    context.getString(R.string.thoi_gian_v2_xxx, TimeHelper.convertDateTimeSvToTimeDateVn(obj.created_time))
                } else {
                    context.getString(R.string.thoi_gian_v2_xxx, context.getString(R.string.dang_cap_nhat))
                }
            }

            binding.root.setOnClickListener {
                ICheckApplication.currentActivity()?.let { activity ->
                    ActivityHelper.startActivity<DetailHistoryGuaranteeActivity, ICListHistoryGuarantee>(activity, Constant.DATA_1, obj)
                }
            }
        }
    }

    inner class ErrorHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(errorCode: Int) {
            itemView.btnTryAgain.beVisible()

            val message = when (errorCode) {
                Constant.ERROR_INTERNET -> {
                    if (StampDetailActivity.isVietNamLanguage == false) {
                        itemView.context.rText(R.string.checking_network_please_try_again)
                    } else {
                        itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                    }
                }
                Constant.ERROR_UNKNOW -> {
                    if (StampDetailActivity.isVietNamLanguage == false) {
                        itemView.context.rText(R.string.occurred_please_try_again)
                    } else {
                        itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    }
                }
                Constant.ERROR_EMPTY -> {
                    itemView.btnTryAgain.beGone()
                    if (StampDetailActivity.isVietNamLanguage == false) {
                        itemView.context.rText(R.string.no_data)
                    } else {
                        itemView.context.getString(R.string.khong_co_du_lieu)
                    }
                }
                else -> {
                    itemView.btnTryAgain.beGone()
                    null
                }
            }
            itemView.txtMessage.text = message

            itemView.btnTryAgain.setOnClickListener {
                listener.onMessageClicked()
            }
        }
    }

}