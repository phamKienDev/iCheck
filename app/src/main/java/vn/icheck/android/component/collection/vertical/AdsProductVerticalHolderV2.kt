package vn.icheck.android.component.collection.vertical

import android.content.Context
import android.graphics.Color
import android.text.Html
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
import vn.icheck.android.helper.TextHelper.setTextNameProduct
import vn.icheck.android.helper.TextHelper.setTextPriceProduct
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.view.second_text.TextSecondBarlowMedium
import vn.icheck.android.network.models.ICProductTrend
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.ui.view.RatioImageView
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beInvisible
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.WidgetUtils
//import vn.teko.android.vnshop.di.module.GlideApp

class AdsProductVerticalHolderV2(parent: ViewGroup) : BaseViewHolder<ICProductTrend>(createView(parent.context)) {

    override fun bind(obj: ICProductTrend) {
        (itemView as ViewGroup).run {
            background=vn.icheck.android.ichecklibs.ViewHelper.bgTransparentStrokeLineColor0_5(context)
            // Image
            (getChildAt(0) as AppCompatImageView).let {
                if (!obj.media.isNullOrEmpty()) {
                    WidgetUtils.loadImageUrlNotCrop(it, obj.media?.get(0)?.content, R.drawable.img_default_product_big)
                } else {
                    WidgetUtils.loadImageUrl(it, "", R.drawable.img_default_product_big)
                }
            }

            // Text name
            (getChildAt(1) as AppCompatTextView).setTextNameProduct(obj.name)

            val tvNotReview = getChildAt(2) as AppCompatTextView
            val layoutRating = getChildAt(3) as ViewGroup

            if (obj.rating == 0f || obj.rating == null) {
                tvNotReview.beVisible()
                layoutRating.beGone()
            } else {
                tvNotReview.beGone()
                layoutRating.beVisible()
                layoutRating.run {
                    // Rating bar
                    (getChildAt(0) as AppCompatRatingBar).run {
                        rating = obj.rating
                    }

                    // Text point
                    (getChildAt(1) as AppCompatTextView).run {
                        text = String.format("%.1f", (obj.rating * 2))
                    }

                    // Text total review
                    (getChildAt(2) as AppCompatTextView).run {
                        text = when {
                            obj.reviewCount > 9999 -> {
                                "(9.999+)"
                            }
                            obj.reviewCount > 999 -> {
                                "(999+)"
                            }
                            obj.reviewCount > 0 -> {
                                "(${obj.reviewCount})"
                            }
                            else -> {
                                null
                            }
                        }
                    }
                }
            }


            // Text price
            (getChildAt(4) as AppCompatTextView).run {
                setTextPriceProduct(obj.price)
            }

            // Text verified
            (getChildAt(5) as AppCompatTextView).run {
                if (obj.verified) {
                    beVisible()
                } else {
                    beInvisible()
                }
            }

            setOnClickListener {
                ICheckApplication.currentActivity()?.let { act ->
                    IckProductDetailActivity.start(act, obj.id)
                }
            }
        }
    }

    companion object {
        fun createView(context: Context): View {
            val layoutParent = LinearLayout(context)
            layoutParent.layoutParams = ViewHelper.createLayoutParams()
            layoutParent.orientation = LinearLayout.VERTICAL
            layoutParent.background = ViewHelper.createStateListDrawable(
                    Color.WHITE, ContextCompat.getColor(context, R.color.lightGray),
                    Color.TRANSPARENT, Color.TRANSPARENT, 0, 0f)
            layoutParent.setPadding(SizeHelper.size6, SizeHelper.size6, SizeHelper.size6, SizeHelper.size6)

            // Layout image
            layoutParent.addView(RatioImageView(context, 175f, 160f).also { imgProduct ->
                imgProduct.id = R.id.imgProduct
                imgProduct.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                imgProduct.scaleType = ImageView.ScaleType.CENTER_CROP
                imgProduct.adjustViewBounds = true
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
                it.gravity = Gravity.TOP
            })

            //text chưa có đánh giá
            layoutParent.addView(ViewHelper.createText(context,
                    ViewHelper.createLayoutParams(SizeHelper.size6, SizeHelper.size2, SizeHelper.size6, 0),
                    null,
                    ViewHelper.createTypeface(context, R.font.barlow_semi_bold_italic),
                    Constant.getDisableTextColor(context),
                    12f,
                    1).also {
                it.text = Html.fromHtml(context.getString(R.string.chua_co_danh_gia_i))
            })

            val primaryColor = vn.icheck.android.ichecklibs.Constant.getPrimaryColor(context)

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
                tvVerified.setTextColor(ContextCompat.getColor(context, R.color.green_text_verified_product))
                tvVerified.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                tvVerified.gravity = Gravity.CENTER_VERTICAL
            })

            return layoutParent
        }
    }
}