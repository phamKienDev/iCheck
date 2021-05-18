package vn.icheck.android.screen.user.detail_stamp_v6_1.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.base.adapter.RecyclerViewCustomAdapter
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.`null`.NullHolder
import vn.icheck.android.component.product.enterprise.ICPageInfoHolder
import vn.icheck.android.network.models.ICLayout
import vn.icheck.android.network.models.ICMedia
import vn.icheck.android.network.models.ICWidgetData
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.holder.*

class ICDetailStampAdapter : RecyclerViewCustomAdapter<ICLayout>() {

    override fun getItemType(position: Int): Int {
        return if (listData[position].data != null) {
            listData[position].viewType
        } else {
            ICViewTypes.NULL_HOLDER
        }
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ICViewTypes.PRODUCT_IMAGE_TYPE -> ICProductImageHolder(parent)
            ICViewTypes.PRODUCT_INFO_TYPE -> ICProductInfoHolder(parent)
            ICViewTypes.MESSAGE_TYPE -> ICMessageResultHolder(parent)
            ICViewTypes.STAMP_INFO_TYPE -> ICStampInfoHolder(parent)
            ICViewTypes.SCAN_INFO_TYPE -> ICScanInfoHolder(parent)
            ICViewTypes.GUARANTEE_INFO_TYPE -> ICGuaranteeHolder(parent)
            ICViewTypes.LAST_GUARANTEE_INFO_TYPE -> ICLastGuaranteeHolder(parent)
            ICViewTypes.VENDOR_TYPE -> ICPageInfoHolder(parent)
            else -> NullHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ICProductImageHolder -> {
                holder.bind(listData[position].data as List<ICMedia>)
            }
            is ICProductInfoHolder -> {
                holder.bind(listData[position].data as ICWidgetData)
            }
            is ICMessageResultHolder -> {
                holder.bind(listData[position].data as ICWidgetData)
            }
            is ICStampInfoHolder -> {
                holder.bind(listData[position].data as String)
            }
            is ICScanInfoHolder -> {
                holder.bind(listData[position].data as ICWidgetData)
            }
            is ICGuaranteeHolder -> {
                holder.bind(listData[position].data as ICWidgetData)
            }
            is ICLastGuaranteeHolder -> {
                holder.bind(listData[position].data as ICWidgetData)
            }
            is ICPageInfoHolder -> {
                holder.bind(listData[position].data as ICWidgetData)
            }
            else -> super.onBindViewHolder(holder, position)
        }
    }
}