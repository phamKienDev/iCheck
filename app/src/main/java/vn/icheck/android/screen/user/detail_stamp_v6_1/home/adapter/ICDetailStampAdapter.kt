package vn.icheck.android.screen.user.detail_stamp_v6_1.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.base.adapter.RecyclerViewCustomAdapter
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.`null`.NullHolder
import vn.icheck.android.network.models.ICLayout
import vn.icheck.android.network.models.ICMedia
import vn.icheck.android.network.models.ICWidgetData
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.holder.ICProductImageHolder
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.holder.ICProductInfoHolder

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
            else -> super.onBindViewHolder(holder, position)
        }
    }
}