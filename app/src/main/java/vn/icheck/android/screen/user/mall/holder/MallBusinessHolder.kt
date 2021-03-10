package vn.icheck.android.screen.user.mall.holder

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.network.models.ICBusiness

class MallBusinessHolder(parent: ViewGroup) : BaseViewHolder<MutableList<ICBusiness>>(ViewHelper.createRecyclerViewWithTitleHolder(parent.context)) {

    override fun bind(obj: MutableList<ICBusiness>) {
        (itemView as ViewGroup).run {
            (getChildAt(0) as ViewGroup).run {
                (getChildAt(0) as AppCompatTextView).run {
                    setText(R.string.doanh_nghiep_noi_bat)
                }

                (getChildAt(1) as AppCompatTextView).run {
                    visibility = View.GONE
                }
            }

            (getChildAt(1) as RecyclerView).run {
                onFlingListener = null
                layoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
                adapter = MallBusinessAdapter(obj)
            }
        }
    }
}