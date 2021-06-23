package vn.icheck.android.component.collection.horizontal

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.network.models.ICProduct
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.ui.view.RatioImageView
import vn.icheck.android.util.kotlin.WidgetUtils

class CollectionProductHorizontalHolder(parent: ViewGroup) : BaseViewHolder<ICProduct>(createView(parent.context)) {

    override fun bind(obj: ICProduct) {
        // Layout parent
        (itemView as ViewGroup).run {
            // Image product
            (getChildAt(0) as AppCompatImageView).run {
                WidgetUtils.loadImageUrlRoundedFitCenter(this, obj.thumbnails?.medium, SizeHelper.size10)
            }

            // Text product name
            (getChildAt(1) as AppCompatTextView).run {
                text = obj.name
            }

            // layout rating
            (getChildAt(2) as ViewGroup).run {
                // Rating bar
                (getChildAt(0) as AppCompatRatingBar).run {
                    rating = obj.rating
                }

                // Text point
                (getChildAt(1) as AppCompatTextView).run {
                    text = context.getString(R.string.format_1_f, obj.rating * 2)
                }

                // Text total review
                (getChildAt(2) as AppCompatTextView).run {
                    text = when {
                        obj.review_count > 9999 -> {
                            context.getString(R.string.count_9999)
                        }
                        obj.review_count > 999 -> {
                            context.getString(R.string.count_999)
                        }
                        obj.review_count > 0 -> {
                            context.getString(R.string.count, obj.review_count)
                        }
                        else -> {
                            null
                        }
                    }
                }
            }

            // Text price
            (getChildAt(3) as AppCompatTextView).run {
                text = if (obj.price != 0L) itemView.context.getString(R.string.xxx__d, TextHelper.formatMoney(obj.price)) else null
            }

            // Text verified
            (getChildAt(1) as AppCompatTextView).run {
                visibility = if (obj.verified) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }

            setOnClickListener {
                ICheckApplication.currentActivity()?.let { act ->
                    IckProductDetailActivity.start(act, obj.barcode!!)
                }
            }
        }
    }

    companion object {

        fun createView(context: Context): LinearLayout {
            // Layout parent
            return LinearLayout(context).also { layoutParent ->
                layoutParent.layoutParams = ViewHelper.createLayoutParams(SizeHelper.size152, LinearLayout.LayoutParams.WRAP_CONTENT, SizeHelper.size4, 0, SizeHelper.size4, 0)
                layoutParent.orientation = LinearLayout.VERTICAL
                layoutParent.background = ViewHelper.createStateListDrawable(Color.WHITE, ContextCompat.getColor(context, R.color.lightGray), Color.TRANSPARENT, Color.TRANSPARENT, 0, 0f)
                layoutParent.setPadding(SizeHelper.size6, SizeHelper.size6, SizeHelper.size6, SizeHelper.size6)

                // Image product
                layoutParent.addView(RatioImageView(context, 138f, 128f).also { imgProduct ->
                    imgProduct.id = R.id.imgProduct
                    imgProduct.layoutParams = ViewHelper.createLayoutParams()
                    imgProduct.scaleType = ImageView.ScaleType.FIT_CENTER
                })

                // Text product name
                layoutParent.addView(ViewHelper.createText(context, ViewHelper.createLayoutParams().also { params ->
                    params.topMargin = SizeHelper.size10
                }, null, ViewHelper.createTypeface(context, R.font.barlow_medium), ColorManager.getNormalTextColor(context), 14f, 2))

                val primaryColor = vn.icheck.android.ichecklibs.ColorManager.getPrimaryColor(context)

                // layout rating
                layoutParent.addView(LinearLayout(context).also { layoutRating ->
                    layoutRating.layoutParams = ViewHelper.createLayoutParams().also {
                        it.topMargin = SizeHelper.size6
                    }
                    layoutRating.orientation = LinearLayout.HORIZONTAL
                    layoutRating.gravity = Gravity.CENTER_VERTICAL

                    // Rating bar
                    layoutRating.addView(LayoutInflater.from(context).inflate(R.layout.rating_bar_12dp, layoutRating, false))

                    // Text point
                    layoutRating.addView(ViewHelper.createText(context, ViewHelper.createLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT), null, ViewHelper.createTypeface(context, R.font.barlow_medium), primaryColor, 12f))

                    // Text total review
                    layoutRating.addView(ViewHelper.createText(context, ViewHelper.createLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT), null, ViewHelper.createTypeface(context, R.font.barlow_medium), ColorManager.getSecondTextColor(context), 12f))
                })

                // Text price
                layoutParent.addView(ViewHelper.createText(context, ViewHelper.createLayoutParams().also { params ->
                    params.topMargin = SizeHelper.size6
                }, null, ViewHelper.createTypeface(context, R.font.barlow_semi_bold), primaryColor, 14f))

                // Text verified
                layoutParent.addView(ViewHelper.createText(context, ViewHelper.createLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT).also { params ->
                    params.topMargin = SizeHelper.size4
                }, null, ViewHelper.createTypeface(context, R.font.barlow_medium), ColorManager.getAccentGreenColor(context), 14f).also {
                    it.compoundDrawablePadding = SizeHelper.size2
                    it.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home_verify_16dp, 0, 0, 0)
                    it.setText(R.string.verified)
                })
            }
        }
    }
}