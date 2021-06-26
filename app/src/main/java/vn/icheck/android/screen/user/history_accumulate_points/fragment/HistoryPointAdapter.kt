package vn.icheck.android.screen.user.history_accumulate_points.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_history_accumulate_points.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.base.model.ICError
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.models.ICHistoryPoint
import vn.icheck.android.base.holder.LoadingHolder
import vn.icheck.android.screen.user.page_details.fragment.page.widget.message.MessageHolder

class HistoryPointAdapter(val listener: IRecyclerViewCallback, val type: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listData = mutableListOf<ICHistoryPoint>()
    private val itemType = 1
    private val itemLoadMore = 2
    private val itemMessage = 3

    private var mMessageError: String? = null
    private var iconMessage = R.drawable.ic_error_request
    private var titleError: String? = null
    private var msgButtonError = R.string.dung_diem
    private var isLoading = false
    private var isLoadMore = false

    private fun checkLoadMore(listCount: Int) {
        isLoadMore = listCount >= APIConstants.LIMIT
        isLoading = false
        mMessageError = ""
    }

    fun setError(error: String, icon: Int) {
        listData.clear()

        isLoadMore = false
        isLoading = false
        mMessageError = error
        iconMessage = icon
        notifyDataSetChanged()
    }

    fun setError(obj: ICError) {
        listData.clear()

        isLoadMore = false
        isLoading = false
        titleError = obj.message
        mMessageError = obj.subMessage
        iconMessage = obj.icon
        msgButtonError = obj.button ?: 0
        notifyDataSetChanged()
    }

    fun isEmpty(): Boolean {
        return listData.isEmpty()
    }

    fun disableLoadMore() {
        isLoading = true
        notifyItemChanged(0, itemCount)
    }

    fun setData(obj: List<ICHistoryPoint>) {
        checkLoadMore(obj.size)

        listData.clear()
        listData.addAll(obj)
        notifyDataSetChanged()
    }

    fun addData(obj: List<ICHistoryPoint>) {
        checkLoadMore(obj.size)

        listData.addAll(obj)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemType -> ViewHolder(parent, type)
            itemLoadMore -> LoadingHolder(parent)
            else -> MessageHolder(parent)
        }
    }

    override fun getItemCount(): Int {
        return if (listData.isEmpty()) {
            if (mMessageError != null) {
                1
            } else {
                0
            }
        } else {
            if (isLoadMore) {
                listData.size + 1
            } else {
                listData.size
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData.isNotEmpty()) {
            if (position < listData.size) {
                itemType
            } else {
                itemLoadMore
            }
        } else {
            if (mMessageError != null) {
                itemMessage
            } else {
                return super.getItemViewType(position)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                holder.bind(listData[position])
            }
            is LoadingHolder -> {
                if (!isLoading) {
                    isLoading = true
                    listener.onLoadMore()
                }
            }
            is MessageHolder -> {
                if (mMessageError.isNullOrEmpty()) {
                    holder.bind(R.drawable.ic_group_120dp, getString(R.string.ban_chua_dung_diem_nao), getString(R.string.hay_tich_luy_va_su_dung_diem_de_doi_nhung_mon_qua_hap_dan_o_muc_dac_quyen_rieng_nhe), R.string.dung_diem)
                } else {
                    holder.bind(iconMessage, titleError, mMessageError, msgButtonError)
                }

                holder.listener(View.OnClickListener {
                    listener.onMessageClicked()
                })
            }
        }
    }

    inner class ViewHolder(parent: ViewGroup, val type: String) : BaseViewHolder<ICHistoryPoint>(LayoutInflater.from(parent.context).inflate(R.layout.item_history_accumulate_points, parent, false)) {
        override fun bind(obj: ICHistoryPoint) {
            itemView.tvTitle.text = obj.title

            itemView.tvPoint.text = if (type == "used") {
                "-${TextHelper.formatMoney(obj.amount)}"
            } else {
                "+${TextHelper.formatMoney(obj.amount)}"
            }
            itemView.tvPoint.setTextColor(if (type == "used") {
                ColorManager.getAccentRedColor(itemView.context)
            } else {
                ColorManager.getAccentGreenColor(itemView.context)
            })

            itemView.tvMessage.text = obj.description

            itemView.tvDate.text = TimeHelper.convertDateTimeSvToTimeDateVnV1(obj.createdAt)
        }
    }
}