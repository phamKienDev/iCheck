package vn.icheck.android.screen.user.pvcombank.specialoffer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_error_pvcombank.view.*
import kotlinx.android.synthetic.main.item_load_more.view.*
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.pvcombank.ICListCardPVBank
import vn.icheck.android.screen.user.pvcombank.specialoffer.callback.SpecialOfferPVCardListener

class SpecialOfferPVCardAdapter (private val listener: SpecialOfferPVCardListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listData = mutableListOf<ICListCardPVBank>()

    private var errorCode = 0

    private var isLoadMore = false
    private var isLoading = false

    private val itemType = 1
    private val loadType = 2
    private val errorType = 3

    fun setListData(list: MutableList<ICListCardPVBank>) {
        isLoadMore = list.size >= Constant.DEFAULT_ITEM_COUNT
        isLoading = false
        errorCode = 0
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addListData(list: MutableList<ICListCardPVBank>) {
        isLoadMore = list.size >= Constant.DEFAULT_ITEM_COUNT
        isLoading = false
        errorCode = 0
        listData.addAll(list)
        notifyDataSetChanged()
    }
    fun setErrorCode(error: Int) {
        listData.clear()
        errorCode = error
        notifyDataSetChanged()
    }

    fun removeDataWithoutUpdate() {
        listData.clear()
    }

    fun showLoading() {
        listData.clear()
        errorCode = 0
        isLoadMore = true
        notifyDataSetChanged()
    }

    fun disableLoadMore() {
        isLoading = true
        notifyItemChanged(0, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            itemType -> ViewHolder(inflater.inflate(R.layout.item_news_list_v2, parent, false))
            loadType -> LoadHolder(inflater.inflate(R.layout.item_load_more, parent, false))
            else -> ErrorHolder(inflater.inflate(R.layout.item_error_pvcombank, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData.size > 0) {
            if (position < listData.size)
                itemType
            else
                loadType
        } else {
            if (isLoadMore)
                loadType
            else
                errorType
        }
    }

    override fun getItemCount(): Int {
        return if (listData.size > 0) {
            if (isLoadMore)
                listData.size + 1
            else
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

            is LoadHolder -> {
                holder.bind()
                if (!isLoading) {
                    listener.onLoadMore()
                    isLoading = true
                }
            }

            is ErrorHolder -> {
                holder.bind(errorCode)
                holder.itemView.btnTryAgain.setOnClickListener {
                    listener.onTryAgain()
                }
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: ICListCardPVBank) {
//            val millisecond = TimeHelper.convertDateTimeSvToMillisecond(obj.createdAt) ?: 0
//            val time = System.currentTimeMillis() - millisecond
//
//            if (time < 172800000) {
//                itemView.txtTime.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(itemView.context, R.drawable.ic_tag_new_news_32px), null)
//            } else {
//                itemView.txtTime.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
//            }
//
//            if (!obj.title.isNullOrEmpty()) {
//                itemView.txtTitle.text = obj.title
//            } else {
//                itemView.txtTile.text = itemView.context.getString(R.string.dang_cap_nhat)
//            }
//
//            if (!obj.introtext.isNullOrEmpty() || !obj.introtext2.isNullOrEmpty()) {
//                itemView.txtDescription.text = obj.introtext ?: obj.introtext2
//            } else {
//                itemView.txtDescription.text = itemView.context.getString(R.string.dang_cap_nhat)
//            }
//
//            WidgetUtils.loadImageUrlRounded4(itemView.imgNews, obj.thumbnail?.trim())
//
//            if (obj.createdAt != null) {
//                itemView.txtTime.text = TestTimeUtil(obj.createdAt ?: "").getTime()
//            } else {
//                itemView.txtTime.text = itemView.context.getString(R.string.dang_cap_nhat)
//            }
        }
    }

    inner class LoadHolder constructor(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            view.progressBar.indeterminateDrawable.setColorFilter(ContextCompat.getColor(itemView.context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY)
        }
    }

    inner class ErrorHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(errorCode: Int) {
            when (errorCode) {
                Constant.ERROR_EMPTY -> {
                    itemView.imgIcon.setImageResource(R.drawable.ic_error_request)
                    itemView.tvTitle.text = itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                }

                Constant.ERROR_SERVER -> {
                    itemView.imgIcon.setImageResource(R.drawable.ic_error_request)
                    itemView.tvTitle.text = itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                }

                Constant.ERROR_INTERNET -> {
                    itemView.btnTryAgain.visibility = View.GONE
                    itemView.imgIcon.setImageResource(R.drawable.ic_error_network)
                    itemView.tvTitle.text = itemView.context.getString(R.string.ket_noi_mang_cua_ban_co_van_de_vui_long_thu_lai)
                }
            }
        }
    }
}