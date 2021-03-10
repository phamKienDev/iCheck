package vn.icheck.android.component.fastsurvey

import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import de.hdodenhof.circleimageview.CircleImageView
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICFastSurvey
import vn.icheck.android.ui.view.SquareImageView
import vn.icheck.android.util.kotlin.WidgetUtils

class FastSurveyHolder(parent: ViewGroup) : BaseViewHolder<ICFastSurvey>(ViewHelper.createFastSurvey(parent.context)) {

    override fun bind(obj: ICFastSurvey) {
        (itemView as ViewGroup).run {
            // Layout header
            (getChildAt(0) as ViewGroup).run {
                // Layout image
                (getChildAt(0) as ViewGroup).run {
                    // Image logo
                    (getChildAt(0) as CircleImageView).run {
                        WidgetUtils.loadImageUrl(this, obj.shop?.avatar, R.drawable.img_shop_default)
                    }

                    // Text verified
                    (getChildAt(1) as AppCompatTextView).run {
                        visibility = if (obj.shop?.verified == true) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                    }
                }

                // Layout info
                (getChildAt(1) as ViewGroup).run {
                    // Text name
                    (getChildAt(0) as AppCompatTextView).run {
                        text = obj.shop?.name
                    }

                    // Text note
                    (getChildAt(1) as AppCompatTextView).run {
                        text = obj.shop?.email
                    }
                }
            }

            // Content
            (getChildAt(1) as AppCompatTextView).run {
                text = obj.content
            }

            // Layout product
            (getChildAt(2) as ViewGroup).run {
                // Image product 1
                (getChildAt(0) as SquareImageView).run {
                    setPadding(0, 0, 0, 0)
                    WidgetUtils.loadImageUrlRounded(this, obj.product1?.thumbnails?.medium, SizeHelper.size10)
                }

                // Image product 2
                (getChildAt(1) as SquareImageView).run {
                    setPadding(0, 0, 0, 0)
                    WidgetUtils.loadImageUrlRounded(this, obj.product2?.thumbnails?.medium, SizeHelper.size10)
                }

                if (obj.isRating == true) {
                    if (obj.product1Rating ?: 0 > obj.product2Rating ?: 0) {
                        (getChildAt(0) as SquareImageView).setPadding(SizeHelper.size28, SizeHelper.size28, SizeHelper.size28, SizeHelper.size28)
                    } else {
                        (getChildAt(1) as SquareImageView).setPadding(SizeHelper.size28, SizeHelper.size28, SizeHelper.size28, SizeHelper.size28)
                    }
                }
            }

            // Layout bottom
            if (obj.isRating != true) {
                createButton(this, obj)
            } else {
                createProgress(this, obj)
            }
        }
    }

    private fun createButton(parent: ViewGroup, obj: ICFastSurvey) {
        parent.removeViewAt(3)
        parent.addView(ViewHelper.createFastSurveyButton(parent.context))

        (parent.getChildAt(3) as ViewGroup).run {
            // Text left
            (getChildAt(0) as AppCompatTextView).run {
                text = obj.btnLeft
            }

            // Text right
            (getChildAt(1) as AppCompatTextView).run {
                text = obj.btnRight
            }
        }
    }

    private fun createProgress(parent: ViewGroup, obj: ICFastSurvey) {
        parent.removeViewAt(3)
        parent.addView(ViewHelper.createFastSurveyProgress(parent.context))

        (parent.getChildAt(0) as ViewGroup).run {
            // Layout top
            (getChildAt(0) as ViewGroup).run {
                // Text left
                (getChildAt(0) as AppCompatTextView).run {
                    text = ("${obj.product1Rating}%")
                }

                // Text right
                (getChildAt(1) as AppCompatTextView).run {
                    text = ("${obj.product2Rating}%")
                }
            }

            // ProgressBar
            (getChildAt(1) as ProgressBar).run {
                progress = obj.product1Rating ?: 0
            }

            // Layout top
            (getChildAt(2) as ViewGroup).run {
                // Text left
                (getChildAt(0) as AppCompatTextView).run {
                    text = obj.btnLeft
                }

                // Text right
                (getChildAt(1) as AppCompatTextView).run {
                    text = obj.btnRight
                }
            }
        }
    }
}