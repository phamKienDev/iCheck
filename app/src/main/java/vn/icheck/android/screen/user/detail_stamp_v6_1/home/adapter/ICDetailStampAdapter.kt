package vn.icheck.android.screen.user.detail_stamp_v6_1.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.base.adapter.RecyclerViewCustomAdapter
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.`null`.NullHolder
import vn.icheck.android.network.models.ICLayout
import vn.icheck.android.network.models.ICMedia
import vn.icheck.android.network.models.ICWidgetData
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.holder.ICMessageResultHolder
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.holder.ICProductImageHolder
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.holder.ICProductInfoHolder
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.holder.ICStampInfoHolder

class ICDetailStampAdapter: RecyclerViewCustomAdapter<ICLayout>() {

    override fun getItemType(position: Int): Int {
        return if (listData[position].data != null) {
            listData[position].viewType
        } else {
            ICViewTypes.NULL_HOLDER
        }
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ICViewTypes.PRODUCT_IMAGE -> ICProductImageHolder(parent)
            ICViewTypes.PRODUCT_INFO -> ICProductInfoHolder(parent)
            ICViewTypes.MESSAGE_TYPE -> ICMessageResultHolder(parent)
            ICViewTypes.STAMP_INFO -> ICStampInfoHolder(parent)
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
            else -> super.onBindViewHolder(holder, position)
        }
    }
}