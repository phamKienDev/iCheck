package vn.icheck.android.component.collection.vertical

import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.models.ICAds
import vn.icheck.android.screen.user.listproduct.ListProductActivity
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.ui.layout.CustomGridLayoutManager
import vn.icheck.android.util.AdsUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class CollectionListProductVerticalHolder(parent: ViewGroup) : BaseViewHolder<ICAds>(createView(parent.context)) {

    override fun bind(obj: ICAds) {
        (itemView as ViewGroup).run {
            (getChildAt(0) as ViewGroup).run {
                (getChildAt(0) as AppCompatTextView).text = obj.collection?.name

                getChildAt(1).setOnClickListener {
                    obj.destination_url?.let { destination ->

                        val id = try {
                            Uri.parse(destination).getQueryParameter("id")?.toLong()
                        } catch (e: Exception) {
                            null
                        }

                        if (id != null) {
                            ICheckApplication.currentActivity()?.let { activity ->
                                val url = APIConstants.defaultHost + APIConstants.Product.LIST
                                val params = hashMapOf<String, Any>()
                                params.put("collection_id", id)

                                TrackingAllHelper.trackCategoryViewed(obj.collection?.name, (it as AppCompatTextView).text.toString())
                                ListProductActivity.start(activity, url, params, obj.collection?.name)
                            }
                        }
                    }
                }
            }

            (getChildAt(1) as ConstraintLayout).run {
                removeAllViews()

                if (obj.type == Constant.BANNER) {
                    visibility = View.VISIBLE
                    ViewHelper.createCollectionBannerImage(this, obj.banner_size ?: "750x300")
                    WidgetUtils.loadImageUrlRounded(getChildAt(0) as AppCompatImageView, obj.banner_thumbnails?.original, R.drawable.ic_default_horizontal, SizeHelper.size10)

                    getChildAt(0).setOnClickListener {
                        ICheckApplication.currentActivity()?.let { act ->
                            AdsUtils.bannerClicked(act, obj)
                        }
                    }
                } else {
                    visibility = View.GONE
                }
            }

            (getChildAt(2) as RecyclerView).run {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                    it.bottomMargin = -SizeHelper.size2
                }

                layoutManager = CustomGridLayoutManager(itemView.context.applicationContext, 2)
                adapter = CollectionListProductVerticalAdapter(obj.collection?.products
                        ?: mutableListOf())
            }
        }
    }

    companion object {

        fun createView(context: Context): LinearLayout {
            val layoutParent = LinearLayout(context)
            layoutParent.layoutParams = ViewHelper.createLayoutParams(0, SizeHelper.size6, 0, SizeHelper.size6)
            layoutParent.orientation = LinearLayout.VERTICAL
            layoutParent.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBackgroundGra1y))

            // Layout title
            layoutParent.addView(ViewHelper.createTitleWidthViewMore(context))

            // Layout banner
            layoutParent.addView(ConstraintLayout(context).also { layoutBanner ->
                layoutBanner.id = R.id.layoutContainer
                layoutBanner.layoutParams = ViewHelper.createLayoutParams(SizeHelper.size12, 0, SizeHelper.size12, 0)
            })

            // List
            layoutParent.addView(RecyclerView(context).also { recyclerView ->
                recyclerView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            })

            return layoutParent
        }
    }
}