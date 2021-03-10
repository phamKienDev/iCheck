package vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.home

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.loyalty.base.ICKViewType
import vn.icheck.android.loyalty.base.listener.IRecyclerViewCallback
import vn.icheck.android.loyalty.base.commons.RecyclerViewCustomAdapter
import vn.icheck.android.loyalty.model.ICKBoxGifts
import vn.icheck.android.loyalty.model.ICKPointUser
import vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.home.holder.OverViewHomePointHolder
import vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.home.holder.ProductHomeRedeemPointLoyaltyHolder

internal class HomeRedeemPointAdapter(callback: IRecyclerViewCallback) : RecyclerViewCustomAdapter<Any>(callback) {

    fun addOverView(obj: ICKPointUser) {
        listData.clear()
        listData.add(0, obj)
        notifyDataSetChanged()
    }

    fun setData(obj: MutableList<ICKBoxGifts>) {
        checkLoadMore(obj.size)

        for (i in listData.size - 1 downTo 0) {
            if (listData[i] is ICKBoxGifts) {
                listData.removeAt(i)
            }
        }
        listData.addAll(obj)
        notifyDataSetChanged()
    }

    fun addData(obj: MutableList<ICKBoxGifts>){
        checkLoadMore(obj.size)
        listData.addAll(obj)
        notifyDataSetChanged()
    }

    override fun getItemType(position: Int): Int {
        return when (listData[position]) {
            is ICKPointUser -> ICKViewType.HEADER_TYPE
            else -> ICKViewType.ITEM_TYPE
        }
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ICKViewType.HEADER_TYPE) {
            OverViewHomePointHolder(parent)
        } else {
            ProductHomeRedeemPointLoyaltyHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is OverViewHomePointHolder -> {
                if (listData[position] is ICKPointUser) {
                    holder.bind(listData[position] as ICKPointUser)
                }
            }
            is ProductHomeRedeemPointLoyaltyHolder -> {
                if (listData[position] is ICKBoxGifts) {
                    holder.bind(listData[position] as ICKBoxGifts)
                }
            }
            else -> {
                super.onBindViewHolder(holder, position)
            }
        }
    }
}