package vn.icheck.android.component.collection.vertical

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
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
import vn.icheck.android.ichecklibs.view.second_text.TextSecondBarlowMedium
import vn.icheck.android.network.models.ICProduct
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.ui.view.RatioImageView
import vn.icheck.android.util.kotlin.WidgetUtils

class AdsProductVerticalHolder(parent: ViewGroup) : BaseViewHolder<ICProduct>(createView(parent.context)) {

    override fun bind(obj: ICProduct) {
        (itemView as ViewGroup).run {
            // Image
            (getChildAt(0) as AppCompatImageView).let {
                WidgetUtils.loadImageUrlRoundedFitCenter(it, obj.thumbnails?.medium, SizeHelper.size10)
            }

            // Text name
            (getChildAt(1) as AppCompatTextView).let {
                it.text = obj.name
            }

            // Layout rating
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
                text = if (obj.price != 0L) itemView.context.getString(R.string.s_d, TextHelper.formatMoney(obj.price)) else null
            }

            // Text verified
            (getChildAt(4) as AppCompatTextView).let {
                it.visibility = if (obj.verified) {
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
        private fun createView(context: Context): View {
            val layoutParent = LinearLayout(context)
            layoutParent.layoutParams = ViewHelper.createLayoutParams(SizeHelper.size0_5, SizeHelper.size0_5, SizeHelper.size0_5, SizeHelper.size0_5)
            layoutParent.orientation = LinearLayout.VERTICAL
            layoutParent.background = ViewHelper.createStateListDrawable(
                    Color.WHITE, ContextCompat.getColor(context, R.color.lightGray),
                    Color.TRANSPARENT, Color.TRANSPARENT, 0, 0f)
            layoutParent.setPadding(SizeHelper.size6, SizeHelper.size6, SizeHelper.size6, SizeHelper.size6)

            // Layout image
            layoutParent.addView(RatioImageView(context, 175f, 160f).also { imgProduct ->
                imgProduct.id = R.id.imgProduct
                imgProduct.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                imgProduct.scaleType = ImageView.ScaleType.FIT_CENTER
            })

            //layout name
            layoutParent.addView(ViewHelper.createText(context,
                    ViewHelper.createLayoutParams(SizeHelper.size6, SizeHelper.size8, SizeHelper.size6, 0),
                    null,
                    ViewHelper.createTypeface(context, R.font.barlow_medium),
                    ContextCompat.getColor(context, R.color.darkGray1),
                    14f,
                    2).also {
                it.minLines = 2
            })

            val primaryColor = vn.icheck.android.ichecklibs.ColorManager.getPrimaryColor(context)

            /* Layout Rating */
            layoutParent.addView(LinearLayout(context).also { layoutRating ->
                layoutRating.layoutParams = ViewHelper.createLayoutParams(SizeHelper.size6, SizeHelper.size5, SizeHelper.size6, 0)
                layoutRating.orientation = LinearLayout.HORIZONTAL
                layoutRating.gravity = Gravity.CENTER_VERTICAL

                // Rating bar
                layoutRating.addView(LayoutInflater.from(context).inflate(R.layout.rating_bar_12dp, layoutRating, false))

                // Text point
                layoutRating.addView(AppCompatTextView(context).also { tvRate ->
                    val rateParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                        it.setMargins(SizeHelper.size4, 0, 0, 0)
                    }
                    tvRate.layoutParams = rateParams
                    tvRate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    tvRate.typeface = ViewHelper.createTypeface(context, R.font.barlow_medium)
                    tvRate.setTextColor(primaryColor)
                    tvRate.includeFontPadding = false
                    tvRate.isSingleLine = true
                    tvRate.text = "0,0"
                })

                // Text total review
                layoutRating.addView(TextSecondBarlowMedium(context).also { tvReviewCount ->
                    tvReviewCount.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                        it.setMargins(SizeHelper.size4, 0, 0, 0)
                    }
                    tvReviewCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    tvReviewCount.includeFontPadding = false
                    tvReviewCount.isSingleLine = true
                })
            })

            // Text price
            layoutParent.addView(AppCompatTextView(context).also { tvPrice ->
                val priceParams = ViewHelper.createLayoutParams(SizeHelper.size6, SizeHelper.size5, SizeHelper.size6, 0)
                tvPrice.layoutParams = priceParams
                tvPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                tvPrice.typeface = ViewHelper.createTypeface(context, R.font.barlow_semi_bold)
                tvPrice.setTextColor(primaryColor)
                tvPrice.includeFontPadding = false
                tvPrice.isSingleLine = true
                tvPrice.ellipsize = TextUtils.TruncateAt.END
            })

            // Text verified
            layoutParent.addView(AppCompatTextView(context).also { tvVerified ->
                tvVerified.layoutParams = ViewHelper.createLayoutParams(SizeHelper.size6, SizeHelper.size5, SizeHelper.size6, 0)
                tvVerified.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home_verify_16dp, 0, 0, 0)
                tvVerified.compoundDrawablePadding = SizeHelper.size4
                tvVerified.typeface = ViewHelper.createTypeface(context, R.font.barlow_medium)
                tvVerified.includeFontPadding = false
                tvVerified.setText(R.string.verified)
                tvVerified.setTextColor(ColorManager.getAccentGreenColor(context))
                tvVerified.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                tvVerified.gravity = Gravity.CENTER_VERTICAL
            })

            return layoutParent
        }
    }
}