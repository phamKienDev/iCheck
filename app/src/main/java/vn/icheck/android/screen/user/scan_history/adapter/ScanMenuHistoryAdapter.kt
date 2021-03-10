package vn.icheck.android.screen.user.scan_history.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.`null`.NullHolder
import vn.icheck.android.network.models.history.ICTypeHistory
import vn.icheck.android.screen.user.campaign.holder.base.ShortMessageHolder
import vn.icheck.android.screen.user.scan_history.ScanHistoryFragment
import vn.icheck.android.screen.user.scan_history.holder.FilterShopHistoryHolder
import vn.icheck.android.screen.user.scan_history.holder.FilterTypeHistoryHolder
import vn.icheck.android.screen.user.scan_history.holder.ToolbarMenuHolder
import vn.icheck.android.screen.user.scan_history.model.ICScanHistory
import vn.icheck.android.screen.user.scan_history.view.IScanHistoryView

class ScanMenuHistoryAdapter(val listener: IScanHistoryView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val listData = mutableListOf<ICScanHistory>()

    private var iconMessage = R.drawable.ic_error_request
    private var errorMessage: String? = null

    var applyCode = hashMapOf<String, Boolean>()
    var applyShop = hashMapOf<Long, Boolean>()


    companion object {
        var isShowType = false
        var isShowShop = false
    }

    fun refreshSelected() {
        for (item in listData) {
            if (item.data != null) {
                if (item.type == ICViewTypes.FILTER_TYPE_HISTORY) {
                    for (child in (item.data as MutableList<ICTypeHistory>)) {
                        child.select = applyCode[child.type ?: ""] ?: false
                    }
                } else if (item.type == ICViewTypes.FILTER_SHOP_HISTORY) {
                    for (child in (item.data as MutableList<ICTypeHistory>)) {
                        child.select = applyShop[child.idShop ?: 0] ?: false
                    }
                }
            }
        }
    }

    @Synchronized
    fun addItem(obj: MutableList<ICScanHistory>) {
        if (!listData.isNullOrEmpty()) {
            for (i in listData.size - 1 downTo 0) {
                listData.removeAt(i)
            }
        }
        listData.addAll(obj)
        notifyDataSetChanged()
    }

    @Synchronized
    fun updateItem(obj: ICScanHistory) {
        for (i in listData.size - 1 downTo 0) {
            if (listData[i].type == obj.type) {
                if (obj.data != null) {
                    listData[i].data = obj.data
                    notifyItemChanged(i)
                } else {
                    listData.removeAt(i)
                    notifyItemRemoved(i)

                }
                return
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ICViewTypes.TOOLBAR_MENU_HISTORY -> {
                ToolbarMenuHolder(parent, listener)
            }
            ICViewTypes.FILTER_TYPE_HISTORY -> {
                FilterTypeHistoryHolder(parent, listener)
            }
            ICViewTypes.FILTER_SHOP_HISTORY -> {
                FilterShopHistoryHolder(parent, listener)
            }
            ICViewTypes.MESSAGE_TYPE -> {
                NullHolder(parent)
            }
            else -> {
                NullHolder(parent)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ToolbarMenuHolder -> {
                holder.bind()
            }
            is FilterTypeHistoryHolder -> {
                holder.bind(listData[position].data as MutableList<ICTypeHistory>)
            }
            is FilterShopHistoryHolder -> {
                holder.bind(listData[position].data as MutableList<ICTypeHistory>)
            }
            is ShortMessageHolder -> {
                if (errorMessage.isNullOrEmpty()) {
                    holder.bind(iconMessage, "")
                } else {
                    holder.bind(iconMessage, errorMessage!!)
                }

                holder.setListener(View.OnClickListener {
                    listener.onMessageErrorMenu()
                })
            }
        }
    }

    override fun getItemCount(): Int {
        return listData.size + if (errorMessage != null) {
            1
        } else {
            0
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < listData.size) {
            if (listData[position].data != null) {
                listData[position].type
            } else {
                if (listData[position].type == ICViewTypes.TOOLBAR_MENU_HISTORY) {
                    listData[position].type
                } else {
                    super.getItemViewType(position)
                }
            }
        } else {
            if (errorMessage.isNullOrEmpty()) {
                super.getItemViewType(position)
            } else {
                ICViewTypes.MESSAGE_TYPE
            }
        }
    }
}