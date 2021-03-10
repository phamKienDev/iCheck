package vn.icheck.android.screen.user.mall.adapter

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
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
import vn.icheck.android.util.kotlin.WidgetUtils

class MallCategoryVerticalAdapter : BaseMallCategoryAdapter<ICCategory, MallCategoryVerticalAdapter.ViewHolder>() {

    override fun getViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(ViewHelper.createMallCategoryVertical(parent.context))
    }

    inner class ViewHolder(view: View) : BaseViewHolder<ICCategory>(view) {

        override fun bind(obj: ICCategory) {
            (itemView as ViewGroup).run {
                (getChildAt(0) as AppCompatImageButton).run {
                    WidgetUtils.loadImageUrlRounded6(this, obj.thumbnails?.small)
                }

                (getChildAt(1) as AppCompatTextView).run {
                    if (obj.name.isNullOrEmpty()) {
                        text = null
                    } else {
                        text = obj.name
                    }
                }

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