package vn.icheck.android.screen.user.wall.manage_page.my_owner_page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_me_owner_page_holder.view.*
import kotlinx.android.synthetic.main.item_me_owner_page_holder.view.imgAvatar
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.adapter.RecyclerViewAdapter
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.models.ICPage
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class MyOwnerPageAdapter(val typeHome: Boolean, callback: IRecyclerViewCallback? = null) : RecyclerViewAdapter<ICPage>(callback) {
    override fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        if (holder is ViewHolder) {
            holder.bind(listData[position])
        }
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICPage>(LayoutInflater.from(parent.context).inflate(R.layout.item_me_owner_page_holder, parent, false)) {
        override fun bind(obj: ICPage) {
            itemView.layoutParams = (itemView.layoutParams as RecyclerView.LayoutParams).apply {
                topMargin = if (typeHome) {
                    SizeHelper.size10
                } else {
                    SizeHelper.size16
                }
            }
            itemView.tvName.text = obj.name ?: ""
            if (obj.isVerify) {
                itemView.tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_16px, 0)
            } else {
                itemView.tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }

            WidgetUtils.loadImageUrl(itemView.imgAvatar, obj.avatar, R.drawable.ic_business_v2, R.drawable.ic_business_v2)
            itemView.tvCountFollow.apply {
                text = if (obj.followCount ?: 0 > 0) {
                    context.getString(R.string.s_nguoi_dang_theo_doi, TextHelper.formatMoneyPhay(obj.followCount))
                } else {
                    itemView.context.getString(R.string.chua_co_nguoi_theo_doi)
                }
            }

            itemView.setOnClickListener {
                ICheckApplication.currentActivity()?.let {
                    ActivityUtils.startActivity<PageDetailActivity, Long>(it, Constant.DATA_1, obj.id!!)
                }
            }
        }
    }
}