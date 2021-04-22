package vn.icheck.android.screen.user.scan_history.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_load_more.view.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.constant.Constant
import vn.icheck.android.screen.user.campaign.holder.base.LongMessageHolder
import vn.icheck.android.base.holder.ActionMessageHolder
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.history.ICBigCorp
import vn.icheck.android.network.models.history.ICItemHistory
import vn.icheck.android.screen.user.scan_history.ScanHistoryFragment
import vn.icheck.android.screen.user.scan_history.holder.*
import vn.icheck.android.screen.user.scan_history.model.ICScanHistory
import vn.icheck.android.screen.user.scan_history.view.IScanHistoryView
//
class ScanHistoryAdapter(val listener: IScanHistoryView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val listData = mutableListOf<ICScanHistory>()
    private var isLoadMore = true
    private var isLoading = true
    private var icError: ICError? = null

    fun addData(data: MutableList<ICScanHistory>) {
        icError = null
        listData.clear()
        listData.addAll(data)
        notifyDataSetChanged()
    }

    fun setListData(list: MutableList<ICScanHistory>) {
        isLoadMore = list.size >= Constant.DEFAULT_ITEM_COUNT
        isLoading = false

        for (i in listData.size - 1 downTo 0) {
            if (listData[i].type != ICViewTypes.SUGGEST_SHOP_HISTORY && listData[i].type != ICViewTypes.LIST_BIG_CORP) {
                listData.removeAt(i)
//                notifyItemRemoved(i)
            }
        }

        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addListData(list: MutableList<ICScanHistory>) {
        isLoadMore = list.size >= Constant.DEFAULT_ITEM_COUNT
        isLoading = false
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun setErrorListData(it: MutableList<ICScanHistory>) {
        isLoadMore = it.size >= Constant.DEFAULT_ITEM_COUNT
        isLoading = false
        var position = -1

        for (i in listData.size - 1 downTo 0) {
            if (listData[i].type == ICViewTypes.SUGGEST_SHOP_HISTORY || listData[i].type == ICViewTypes.LIST_BIG_CORP) {
                position = i
                break
            }
            listData.removeAt(i)
        }

        if (position == -1) {
            listData.addAll(it)
        } else {
            listData.addAll(position + 1, it)
        }
        notifyDataSetChanged()
    }

    fun setError(icError: ICError) {
        this.icError = icError
        listData.clear()
        notifyDataSetChanged()
    }

    fun showLoading() {
        listData.clear()
        this.icError = null
        isLoadMore = true
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        listData.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addBigCop(obj: ICScanHistory) {
        if (listData.isNotEmpty()) {
            if (listData[0].type == ICViewTypes.LIST_BIG_CORP) {
                return
            }
            listData.add(0, obj)
        } else {
            listData.add(obj)
        }
        notifyDataSetChanged()
    }

    fun hideBigCopAndSuggest() {
        val listItr = listData.iterator()
        while (listItr.hasNext()) {
            val item = listItr.next()
            if (item.type == ICViewTypes.LIST_BIG_CORP || item.type == ICViewTypes.SUGGEST_SHOP_HISTORY) {
                listItr.remove()
            }
        }

        notifyDataSetChanged()
    }

    fun addBigCopAndSuggest(obj: ICScanHistory) {
        if (!listData.isNullOrEmpty()) {
            if (listData[0].type != ICViewTypes.LIST_BIG_CORP) {
                listData.add(0, obj)
            }
            if (ScanHistoryFragment.listIdBigCorp.isNullOrEmpty()) {
                if (listData.size > 1) {
                    if (listData[1].type != ICViewTypes.SUGGEST_SHOP_HISTORY) {
                        listData.add(1, ICScanHistory(ICViewTypes.SUGGEST_SHOP_HISTORY))
                    }
                } else {
                    listData.add(ICScanHistory(ICViewTypes.SUGGEST_SHOP_HISTORY))
                }
            } else {
                if (listData[1].type == ICViewTypes.SUGGEST_SHOP_HISTORY) {
                    listData.removeAt(1)
                }
            }
        } else {
            listData.add(obj)
            if (ScanHistoryFragment.listIdBigCorp.isNullOrEmpty())
                listData.add(ICScanHistory(ICViewTypes.SUGGEST_SHOP_HISTORY))

        }
        notifyDataSetChanged()
    }

    fun showSuggest() {
        if (listData.isNotEmpty()) {
            if (listData.find { it.type == ICViewTypes.SUGGEST_SHOP_HISTORY } == null) {
                if (listData[0].type == ICViewTypes.LIST_BIG_CORP) {
                    listData.add(1, ICScanHistory(ICViewTypes.SUGGEST_SHOP_HISTORY))
                } else {
                    listData.add(0, ICScanHistory(ICViewTypes.SUGGEST_SHOP_HISTORY))
                }
            }
        } else {
            listData.add(ICScanHistory(ICViewTypes.SUGGEST_SHOP_HISTORY))
        }
    }

    fun hideSuggestShop() {
        for (i in 0 until listData.size) {
            if (listData[i].type == ICViewTypes.SUGGEST_SHOP_HISTORY) {
                listData.removeAt(i)
                notifyDataSetChanged()
                return
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (icError != null) {
            Constant.ERROR_EMPTY
        } else {
            if (listData.isNotEmpty()) {
                if (position < listData.size) {
                    listData[position].type
                } else {
                    Constant.TYPE_LOAD_MORE
                }
            } else {
                Constant.ERROR_EMPTY
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            Constant.ERROR_SERVER -> LongMessageHolder(parent)
            Constant.TYPE_LOAD_MORE -> LoadHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_load_more, parent, false))
            Constant.ERROR_EMPTY -> ActionMessageHolder(parent)
            ICViewTypes.MESSAGE_SCAN_HISTORY -> MessageScanHistoryHolder(parent)
            ICViewTypes.LIST_BIG_CORP -> ListBigCorpHolder(parent, listener)
            ICViewTypes.SUGGEST_SHOP_HISTORY -> SuggestShopHistoryHolder(parent)
            ICViewTypes.PRODUCT_SCAN_HISTORY -> ProductHistoryHolder(parent)
            ICViewTypes.SHOP_SCAN_HISTORY -> ShopHistoryHolder(parent)
            ICViewTypes.QR_PRODUCT_SCAN_HISTORY -> QrProductHistoryHolder(parent, listener)
            ICViewTypes.QR_CODE_SCAN_HISTORY -> QrCodeHistoryHolder(parent, listener)
            else -> NotQrCodeHistoryHolder(parent)
        }
    }

    override fun getItemCount(): Int {
        return if (icError != null) {
            1
        } else {
            if (listData.isNotEmpty()) {
                if (isLoadMore) {
                    listData.size + 1
                } else {
                    listData.size
                }
            } else {
                1
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LongMessageHolder -> {
                icError?.let {
                    holder.bind(it.icon, it.message, null)

                    holder.setListener(View.OnClickListener {
                        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.MESSAGE_ERROR))
                    })
                }
            }
            is LoadHolder -> {
                holder.bind()
                if (!isLoading) {
                    listener.onLoadmore()
                    isLoading = true
                }
            }
            is ActionMessageHolder -> {
                icError?.let {
                    holder.bind(it.icon, it.message, it.subMessage, it.button)

                    holder.setListener(View.OnClickListener {
                        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.MESSAGE_ERROR))
                    })
                }
            }
            is ListBigCorpHolder -> {
                holder.bind(listData[position].data as MutableList<ICBigCorp>)
            }
            is SuggestShopHistoryHolder -> {
                holder.bind()
            }
            is MessageScanHistoryHolder -> {
                holder.bind(listData[position].data as ICError)
                if (listData.size > 1) {
                    holder.itemView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,SizeHelper.dpToPx(350))
                } else {
                    holder.itemView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                }
            }
            is ProductHistoryHolder -> {
                holder.bind(listData[position].data as ICItemHistory)
            }
            is ShopHistoryHolder -> {
                holder.bind(null)
            }
            is QrProductHistoryHolder -> {
                holder.bind(listData[position].data as ICItemHistory)
            }
            is QrCodeHistoryHolder -> {
                holder.bind(listData[position].data as ICItemHistory)
            }
        }
    }

    class LoadHolder constructor(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            view.progressBar.indeterminateDrawable.setColorFilter(ContextCompat.getColor(itemView.context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY)
        }
    }
}