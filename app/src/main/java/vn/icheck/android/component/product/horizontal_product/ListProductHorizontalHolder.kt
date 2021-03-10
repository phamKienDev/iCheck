package vn.icheck.android.component.product.horizontal_product

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.component.product.related_product.RelatedProductModel
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.tracking.insider.InsiderHelper
import vn.icheck.android.screen.user.listproduct.ListProductActivity
import vn.icheck.android.screen.user.listproductcategory.ListProductCategoryActivity
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.kotlin.ActivityUtils

class ListProductHorizontalHolder(parent: ViewGroup, val viewPool: RecyclerView.RecycledViewPool?, private val backgroundTitle: Int? = null) : RecyclerView.ViewHolder(ViewHelper.createListProductHorizontalNew(parent.context, null)) {

    fun bind(obj: RelatedProductModel) {
        (itemView as ViewGroup).run {
            (getChildAt(0) as LinearLayout).run {
                backgroundTitle?.let { setBackgroundColor(it) }

                (getChildAt(0) as AppCompatTextView).run {
                    text = obj.title
                }
                (getChildAt(1) as AppCompatTextView).run {
                    visibility = View.GONE
                    setOnClickListener {
                        ICheckApplication.currentActivity()?.let { activity ->
                            TrackingAllHelper.trackCategoryViewed(obj.title, InsiderHelper.CATEGORY)
                            if (obj.url.isNotEmpty()) {
                                ListProductActivity.start(activity, obj.url, obj.params, obj.title)
                            } else {
                                ActivityUtils.startActivity<ListProductCategoryActivity, Long>(activity, Constant.DATA_1, -1L)
                            }
                        }
                    }
                }
            }

            ((getChildAt(2) as ViewGroup).getChildAt(1) as RecyclerView).run {
                layoutManager = LinearLayoutManager(itemView.context.applicationContext, LinearLayoutManager.HORIZONTAL, false)
//                setRecycledViewPool(viewPool)
                adapter = ListProductHorizontalAdapter(obj.url, obj.params, obj.title, obj.listProduct)
            }
        }
    }
}