package vn.icheck.android.loyalty.screen.loyalty_customers.longtermprogramlist

import android.content.Intent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_long_term_program.view.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.*
import vn.icheck.android.loyalty.base.listener.IRecyclerViewCallback
import vn.icheck.android.loyalty.base.commons.RecyclerViewCustomAdapter
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.helper.SizeHelper
import vn.icheck.android.loyalty.helper.TextHelper
import vn.icheck.android.loyalty.helper.WidgetHelper
import vn.icheck.android.loyalty.model.ICKLongTermProgram
import vn.icheck.android.loyalty.screen.loyalty_customers.home.HomePageEarnPointsActivity

internal class LongTermProgramListAdapter(callback: IRecyclerViewCallback) : RecyclerViewCustomAdapter<ICKLongTermProgram>(callback) {

    override fun getItemType(position: Int): Int {
        return ICKViewType.ITEM_TYPE
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(listData[position])
        } else {
            super.onBindViewHolder(holder, position)
        }
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICKLongTermProgram>(R.layout.item_long_term_program, parent) {
        override fun bind(obj: ICKLongTermProgram) {
            WidgetHelper.loadImageUrlRounded(itemView.imgBanner, obj.banner?.original, SizeHelper.size10)

            WidgetHelper.loadImageUrlRounded6(itemView.imgAvatar, obj.user?.logo?.medium)

            checkNullOrEmpty(itemView.tvNameShop, obj.user?.name)

            checkNullOrEmpty(itemView.tvPointName, obj.point_name)

            itemView.tvCount.text = TextHelper.formatMoneyPhay(obj.point?.points)

            itemView.setOnClickListener {
                SharedLoyaltyHelper(itemView.context).putString(ConstantsLoyalty.OWNER_NAME, obj.user?.name
                        ?: "")
                itemView.context.startActivity(Intent(itemView.context, HomePageEarnPointsActivity::class.java).apply {
                    putExtra(ConstantsLoyalty.DATA_1, obj)
                    putExtra(ConstantsLoyalty.DATA_2, obj.user_id)
                })
            }
        }
    }
}