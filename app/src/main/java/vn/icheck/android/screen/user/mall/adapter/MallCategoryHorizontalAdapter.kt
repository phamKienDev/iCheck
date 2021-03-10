package vn.icheck.android.screen.user.mall.adapter

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.tracking.insider.InsiderHelper
import vn.icheck.android.network.models.ICCategory
import vn.icheck.android.screen.user.listproductcategory.ListProductCategoryActivity
import vn.icheck.android.screen.user.mall.MallFragment
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.kotlin.ActivityUtils

class MallCategoryHorizontalAdapter : BaseMallCategoryAdapter<ICCategory, MallCategoryHorizontalAdapter.ViewHolder>() {

    override fun getViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(ViewHelper.createMallCategoryHorizontal(parent.context))
    }

    inner class ViewHolder(view: View) : BaseViewHolder<ICCategory>(view) {

        override fun bind(obj: ICCategory) {
            (itemView as AppCompatTextView).run {
                text = obj.name

                setOnClickListener {
                    ICheckApplication.currentActivity()?.let { activity ->
                        MallFragment.nameCategory = obj.name
                        TrackingAllHelper.trackCategoryViewed(obj.name, InsiderHelper.CATEGORY)
                        ActivityUtils.startActivity<ListProductCategoryActivity, Long>(activity, Constant.DATA_1, obj.id)
                    }
                }
            }
        }
    }
}