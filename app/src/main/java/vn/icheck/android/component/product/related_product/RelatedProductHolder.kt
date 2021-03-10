package vn.icheck.android.component.product.related_product

import android.graphics.Color
import android.graphics.Typeface
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.network.models.ICProductTrend
import vn.icheck.android.tracking.insider.InsiderHelper
import vn.icheck.android.screen.user.listproduct.ListProductActivity
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.ui.layout.CustomGridLayoutManager
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible

class RelatedProductHolder(parent: ViewGroup, val viewPool: RecyclerView.RecycledViewPool?, private val backgroundTitle: Int? = null) : BaseViewHolder<RelatedProductModel>(ViewHelper.createRecyclerViewWithTitleHolderV2(parent.context, null)) {

    override fun bind(obj: RelatedProductModel) {
        (itemView as ViewGroup).run {
            if (backgroundTitle != null) {
                setBackgroundColor(backgroundTitle)
            }

            (getChildAt(0) as ViewGroup).run {
                (getChildAt(0) as AppCompatTextView).run {
                    text = obj.title
                }

                (getChildAt(1) as AppCompatTextView).run {
                    if (obj.listProduct.size > 4) {
                        beVisible()
                        setTextColor(ContextCompat.getColor(context, R.color.blue))
                        typeface = Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf")
                        background = ContextCompat.getDrawable(itemView.context, R.color.transparent)
                        setOnClickListener {
                            ICheckApplication.currentActivity()?.let { act ->
                                TrackingAllHelper.trackCategoryViewed(obj.title, InsiderHelper.CATEGORY)
                                ListProductActivity.start(act, obj.url, obj.params, obj.title)
                            }
                        }
                    } else {
                        beGone()
                    }
                }
            }

            (getChildAt(1) as RecyclerView).run {
                layoutManager = CustomGridLayoutManager(itemView.context, 2)
                setRecycledViewPool(viewPool)
                setBackgroundColor(Color.WHITE)

                val listProduct = mutableListOf<ICProductTrend>()
                // màn Page hiển thị tối đa 4 sp
                val sizeMax = if (ICheckApplication.currentActivity() is PageDetailActivity) 4 else 6
                for (i in 0 until if (obj.listProduct.size > sizeMax) {
                    sizeMax
                } else {
                    obj.listProduct.size
                }) {
                    listProduct.add(obj.listProduct[i])
                }

                adapter = RelatedProductsAdapter(listProduct)
            }
        }
    }
}