package vn.icheck.android.screen.user.detail_stamp_v6_1.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_message.view.*
import kotlinx.android.synthetic.main.item_suggest_product_verified.view.*
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.detail_stamp_v6_1.ICObjectListMoreProductVerified
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.view.IDetailStampView
import vn.icheck.android.util.kotlin.WidgetUtils

class MoreProductVerifiedAdapter(val view: IDetailStampView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listData = mutableListOf<ICObjectListMoreProductVerified>()
    private val itemType = -1
    private val emptyType = -3
    private var errCode = 0
    var isLoadMore = true

    fun setListData(list: MutableList<ICObjectListMoreProductVerified>?) {
        errCode = 0
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
            itemType -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_suggest_product_verified, parent, false))
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
                    view.onItemClick(item)
                }
            }

            is ErrorHolder -> {
                holder.setData(errCode)

                holder.itemView.btnTryAgain.setOnClickListener {
                    view.onTryAgain()
                }
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(obj: ICObjectListMoreProductVerified) {
            obj.image?.let {
                val image = if (it.isNotEmpty()) {
                    if (it.startsWith("http")) {
                        it
                    } else {
                        "http://icheckcdn.net/images/200x200/$it.jpg"
                    }
                } else {
                    null
                }
                WidgetUtils.loadImageUrlRounded10FitCenter(itemView.imgSuggesVerified, image)
            }

            itemView.tvNameSuggestVerified.text = if (!obj.name.isNullOrEmpty()){
                obj.name
            }else{
                itemView.context.getString(R.string.dang_cap_nhat)
            }

        }
    }

    inner class ErrorHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(errorCode: Int) {
            val message = when (errorCode) {
                Constant.ERROR_INTERNET -> {
                    itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                }
                Constant.ERROR_UNKNOW -> {
                    itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                }
                Constant.ERROR_EMPTY -> {
                    itemView.context.getString(R.string.khong_co_san_pham_nao)
                }
                else -> {
                    itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                }
            }
            itemView.txtMessage.text = message
        }
    }
}