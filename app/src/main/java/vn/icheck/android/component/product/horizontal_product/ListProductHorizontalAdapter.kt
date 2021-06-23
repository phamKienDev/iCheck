package vn.icheck.android.component.product.horizontal_product

import android.graphics.Color
import android.graphics.Typeface
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper.setTextNameProduct
import vn.icheck.android.helper.TextHelper.setTextPriceProduct
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.network.models.ICProductTrend
import vn.icheck.android.screen.user.listproduct.ListProductActivity
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.kotlin.WidgetUtils

class ListProductHorizontalAdapter(val url: String, val params: HashMap<String, Any>?, val title: String, val listProduct: MutableList<ICProductTrend>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewProductHorizontalHolder {
        return NewProductHorizontalHolder(parent)
    }

    override fun getItemCount(): Int = if (listProduct.size > 7) {
        8
    } else {
        listProduct.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NewProductHorizontalHolder) {
            if (position <= 6) {
                holder.bind(listProduct[position])
            } else {
                holder.bindLoadMore(url, params, title)
            }
        }
    }

    inner class NewProductHorizontalHolder(parent: ViewGroup) : BaseViewHolder<ICProductTrend>(ViewHelper.createProductHorizontalNew(parent.context)) {

        override fun bind(obj: ICProductTrend) {
            (itemView as ViewGroup).run {
                layoutParams = ViewHelper.createLayoutParams(SizeHelper.size150, LinearLayout.LayoutParams.WRAP_CONTENT, 0, 0, SizeHelper.size1, 0)
                (getChildAt(1) as AppCompatTextView).run {
                    visibility = View.VISIBLE
                }

                (getChildAt(2) as ViewGroup).run {
                    visibility = View.VISIBLE
                }

                (getChildAt(3) as AppCompatTextView).run {
                    visibility = View.VISIBLE
                }
                (getChildAt(4) as AppCompatTextView).run {
                    visibility = View.VISIBLE
                }

                (getChildAt(0) as AppCompatImageView).run {
                    layoutParams = LinearLayout.LayoutParams(SizeHelper.size150, SizeHelper.size140)
                    if (!obj.media.isNullOrEmpty()) {
                        WidgetUtils.loadImageUrlNotCrop(this, obj.media?.get(0)?.content, R.drawable.img_default_product_big)
                    } else {
                        WidgetUtils.loadImageUrl(this, "", R.drawable.img_default_product_big)
                    }
                }

                (getChildAt(1) as AppCompatTextView).setTextNameProduct(obj.name)

                (getChildAt(2) as ViewGroup).run {
                    if (obj.rating != 0F) {
                        (getChildAt(0) as AppCompatRatingBar).run {
                            visibility = View.VISIBLE
                            rating = obj.rating
                        }
                        (getChildAt(1) as AppCompatTextView).run {
                            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                                it.setMargins(SizeHelper.size6, 0, SizeHelper.size6, 0)
                            }
                            setTextColor(vn.icheck.android.ichecklibs.ColorManager.getPrimaryColor(context))
                            typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
                            text = "${obj.rating * 2}"
                        }
                        (getChildAt(2) as AppCompatTextView).run {
                            visibility = View.VISIBLE
                            text = if (obj.reviewCount < 999) {
                                "(${obj.reviewCount})"
                            } else {
                                "(999+)"
                            }
                        }
                    } else {
                        (getChildAt(0) as AppCompatRatingBar).run {
                            visibility = View.GONE
                        }
                        (getChildAt(1) as AppCompatTextView).run {
                            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                                it.setMargins(0, 0, 0, 0)
                            }
                            setText(Html.fromHtml(ICheckApplication.getInstance().getString(R.string.chua_co_danh_gia_i)))
                            typeface = ViewHelper.createTypeface(ICheckApplication.getInstance(), R.font.barlow_semi_bold_italic)
                            setTextColor(ColorManager.getDisableTextColor(ICheckApplication.getInstance()))
                        }
                        (getChildAt(2) as AppCompatTextView).run {
                            visibility = View.GONE
                        }
                    }
                }

                (getChildAt(3) as AppCompatTextView).setTextPriceProduct(obj.price)
                (getChildAt(4) as AppCompatTextView).run {
                    visibility = if (obj.verified) {
                        View.VISIBLE
                    } else {
                        View.INVISIBLE
                    }
                }

                setOnClickListener {
                    ICheckApplication.currentActivity()?.let { act ->
                        IckProductDetailActivity.start(act, obj.barcode ?: "")
                    }
                }
            }
        }

        fun bindLoadMore(url: String, params: HashMap<String, Any>?, title: String) {
            (itemView as ViewGroup).run {
                layoutParams = ViewHelper.createLayoutParams(SizeHelper.size150, LinearLayout.LayoutParams.MATCH_PARENT, 0, 0, SizeHelper.size1, 0)

                itemView.setBackgroundColor(ColorManager.getAppBackgroundWhiteColor(itemView.context))
                (getChildAt(0) as AppCompatImageView).run {
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                    setImageResource(R.drawable.group_more_product)
                }

                (getChildAt(1) as AppCompatTextView).run {
                    visibility = View.GONE
                }

                (getChildAt(2) as ViewGroup).run {
                    visibility = View.GONE
                }

                (getChildAt(3) as AppCompatTextView).run {
                    visibility = View.GONE
                }
                (getChildAt(4) as AppCompatTextView).run {
                    visibility = View.GONE
                }

                setOnClickListener {
                    ICheckApplication.currentActivity()?.let { activity ->
                        TrackingAllHelper.trackCategoryViewed(title, activity.getString(R.string.xem_tat_ca))
                        ListProductActivity.start(activity, url, params, title)
                    }
                }
            }
        }
    }
}