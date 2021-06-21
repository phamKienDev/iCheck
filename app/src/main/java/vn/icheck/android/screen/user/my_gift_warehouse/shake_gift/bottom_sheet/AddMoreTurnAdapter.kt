package vn.icheck.android.screen.user.my_gift_warehouse.shake_gift.bottom_sheet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_add_more_turn.view.*
import kotlinx.android.synthetic.main.item_message_more_turn.view.*
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.ichecklibs.ViewHelper

class AddMoreTurnAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listData = mutableListOf<String>()

    private var errorCode = 4

    private val itemType = 1
    private val errorType = 2

    fun setListData(list: MutableList<String>) {
        errorCode = 0
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun setErrorCode(error: Int) {
        listData.clear()
        errorCode = error
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            itemType -> ViewHolder(inflater.inflate(R.layout.item_add_more_turn, parent, false))
            else -> MessageHolder(inflater.inflate(R.layout.item_message_more_turn, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData.size > 0) {
            itemType
        } else {
            errorType
        }
    }

    override fun getItemCount(): Int {
        return if (listData.size > 0) {
            listData.size
        } else {
            1
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = listData[position]
                holder.bind(item)
            }

            is MessageHolder -> {
                holder.bind(errorCode)
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: String) {
            itemView.appCompatTextView43.background = ViewHelper.bgAccentCyanCorners4(itemView.context)
        }
    }

    class MessageHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(errorCode: Int) {
            when (errorCode) {
                Constant.ERROR_EMPTY -> {
                    itemView.imgError.setImageResource(R.drawable.ic_emty_shake_list_mission)
                    itemView.tvMessage.setText(R.string.hien_tai_chua_co_su_kien_nao)
                }

                Constant.ERROR_SERVER -> {
                    itemView.imgError.setImageResource(R.drawable.ic_error_request)
                    itemView.tvMessage.setText(R.string.co_loi_xay_ra_vui_long_thu_lai)
                }

                Constant.ERROR_INTERNET -> {
                    itemView.imgError.setImageResource(R.drawable.ic_error_network)
                    itemView.tvMessage.setText(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                }
            }
        }
    }
}