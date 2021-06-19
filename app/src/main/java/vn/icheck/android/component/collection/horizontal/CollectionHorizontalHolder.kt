package vn.icheck.android.component.collection.horizontal

import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.models.ICCollection
import vn.icheck.android.screen.user.listproduct.ListProductActivity
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.AdsUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class CollectionHorizontalHolder(parent: ViewGroup) : BaseViewHolder<ICCollection>(createView(parent.context)) {

    override fun bind(obj: ICCollection) {
        (itemView as ViewGroup).run {
            (getChildAt(0) as ViewGroup).run {
                (getChildAt(0) as AppCompatTextView).text = obj.title

                getChildAt(1).setOnClickListener {
                    obj.destinationUrl?.let { destination ->

                        val id = try {
                            Uri.parse(destination).getQueryParameter("id")?.toLong()
                        } catch (e: Exception) {
                            null
                        }

                        if (id != null) {
                            ICheckApplication.currentActivity()?.let { activity ->
                                val url = APIConstants.defaultHost + APIConstants.Product.LIST
                                val params = hashMapOf<String, Any>()
                                params["collection_id"] = id

                                TrackingAllHelper.trackCategoryViewed(obj.title,activity.getString(R.string.xem_tat_ca))
                                ListProductActivity.start(activity, url, params, obj.title)
                            }
                        }
                    }
                }
            }

            // Layout banner
            (getChildAt(1) as ConstraintLayout).run {
                removeAllViews()

                obj.bannerUrl?.let { bannerUrl ->
                    ViewHelper.createCollectionBannerImage(this, obj.bannerSize ?: "750x300")
                    WidgetUtils.loadImageUrlRounded(getChildAt(0) as AppCompatImageView, bannerUrl, R.drawable.ic_default_horizontal, SizeHelper.size10)

                    getChildAt(0).setOnClickListener {
                        ICheckApplication.currentActivity()?.let { act ->
                            AdsUtils.bannerClicked(act, obj.destinationUrl, obj.title)
                        }
                    }
                }
            }

            // List
            (getChildAt(2) as RecyclerView).run {
                layoutManager = LinearLayoutManager(itemView.context.applicationContext, LinearLayoutManager.HORIZONTAL, false)
                adapter = CollectionListProductHorizontalAdapter(obj.products ?: mutableListOf())
            }
        }
    }

    companion object {

        private fun createView(context: Context): LinearLayout {
            return LinearLayout(context).also { layoutParent ->
                layoutParent.layoutParams = ViewHelper.createLayoutParams(0, SizeHelper.size6, 0, SizeHelper.size6)
                layoutParent.orientation = LinearLayout.VERTICAL
                layoutParent.setBackgroundColor(ContextCompat.getColor(context, vn.icheck.android.ichecklibs.R.color.grayF0))
                layoutParent.setPadding(0, 0, 0, SizeHelper.size8)

                // Layout title
                layoutParent.addView(ViewHelper.createTitleWidthViewMore(context))

                // Image banner
                layoutParent.addView(ConstraintLayout(context).also { layoutBanner ->
                    layoutBanner.id = R.id.layoutContainer
                    layoutBanner.layoutParams = ViewHelper.createLayoutParams(SizeHelper.size12, 0, SizeHelper.size12, 0)
                })

                // List
                layoutParent.addView(RecyclerView(context).also { recyclerView ->
                    recyclerView.layoutParams = ViewHelper.createLayoutParams()
                    recyclerView.setPadding(SizeHelper.size8, 0, 0, 0)
                    recyclerView.clipToPadding = false
                })
            }
        }
    }
}