package vn.icheck.android.screen.user.detail_stamp_v6_1.more_product_verified_by_distributor.adapter

import android.graphics.Color
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_load_more.view.*
import kotlinx.android.synthetic.main.item_message.view.*
import kotlinx.android.synthetic.main.item_suggest_product_verified_in_business.view.*
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.detail_stamp_v6_1.ICObjectListMoreProductVerified
import vn.icheck.android.screen.user.detail_stamp_v6_1.more_product_verified_by_distributor.view.IMoreProductVerifiedByDistributorView
import vn.icheck.android.util.kotlin.WidgetUtils

class MoreProductVerifiedByDistributorAdapter(val viewCallback: IMoreProductVerifiedByDistributorView, val vietNamLanguage: Boolean?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val listData = mutableListOf<ICObjectListMoreProductVerified>()

    private var errorCode = ""
    private var isLoadMore = true
    private var isLoading = true

    private val itemType = 1
    private val loadType = 2
    private val showType = 3

    fun setListData(list: MutableList<ICObjectListMoreProductVerified>) {
        isLoadMore = list.size >= Constant.DEFAULT_ITEM_COUNT
        isLoading = false
        errorCode = ""

        listData.clear()
        listData.addAll(list)

        Handler().postDelayed({
            notifyDataSetChanged()
        }, 100)
    }

    fun addListData(list: MutableList<ICObjectListMoreProductVerified>) {
        isLoadMore = list.size >= Constant.DEFAULT_ITEM_COUNT
        isLoading = false
        errorCode = ""

        listData.addAll(list)

        notifyDataSetChanged()
    }

    fun showLoading() {
        listData.clear()
        errorCode = ""
        isLoadMore = true

        Handler().postDelayed({
            notifyDataSetChanged()
        }, 100)
    }

    fun setErrorCode(error: String) {
        listData.clear()
        errorCode = error
        isLoadMore = false

        Handler().postDelayed({
            notifyDataSetChanged()
        }, 100)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            itemType -> ViewHolder(inflater.inflate(R.layout.item_suggest_product_verified_in_business, parent, false))
            loadType -> LoadHolder(inflater.inflate(R.layout.item_load_more, parent, false))
            else -> MessageHolder(inflater.inflate(R.layout.item_message, parent, false))
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
                showType
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = listData[position]
                holder.bind(item)

                holder.itemView.setOnClickListener {
                    viewCallback.onClickItem(item)
                }
            }
            is LoadHolder -> {
                holder.bind()
                if (!isLoading) {
                    viewCallback.onLoadMore()
                    isLoading = true
                }
            }
            is MessageHolder -> {
                holder.bind(errorCode)

                holder.itemView.setOnClickListener {
                    viewCallback.onRefresh()
                }
            }
        }
    }

    inner class ViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: ICObjectListMoreProductVerified) {
            item.image?.let {
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

            itemView.tvNameSuggestVerified.text = if (!item.name.isNullOrEmpty()) {
                item.name
            } else {
                if (vietNamLanguage == false){
                    "updating"
                } else {
                    itemView.context.getString(R.string.dang_cap_nhat)
                }
            }
        }
    }

    class LoadHolder constructor(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            view.progressBar.indeterminateDrawable.setColorFilter(vn.icheck.android.ichecklibs.Constant.getPrimaryColor(view.context), android.graphics.PorterDuff.Mode.MULTIPLY)
        }
    }

    class MessageHolder constructor(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(errorCode: String) {
            itemView.txtMessage.text = if (errorCode.isNotEmpty())
                errorCode
            else
                itemView.context.getString(R.string.khong_lay_duoc_du_lieu_vui_long_thu_lai)
        }
    }

}