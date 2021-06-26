package vn.icheck.android.component.product.verified

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.network.models.ICClientSetting
import vn.icheck.android.screen.user.webview.WebViewActivity

class ProductVerifiedHolder(parent: ViewGroup) : RecyclerView.ViewHolder(createView(parent.context)) {
    fun bind(icClientSetting: ICClientSetting) {
        (itemView as ViewGroup).run {
            // Text content
            (getChildAt(0) as AppCompatTextView).run {
                if (!icClientSetting.verifyMessage.isNullOrEmpty()) {
                    text = icClientSetting.verifyMessage
                } else {
                    setText(R.string.default_product_verified)
                }
            }
        }
    }

    companion object {

        fun createView(context: Context): LinearLayout {
            return LinearLayout(context).also { layoutParent ->
                layoutParent.layoutParams = ViewHelper.createLayoutParams(0,SizeHelper.size10,0,0)
                layoutParent.orientation = LinearLayout.VERTICAL
                layoutParent.setBackgroundColor(ColorManager.getAccentGreenColor(context))
                layoutParent.setPadding(SizeHelper.size12, SizeHelper.size10, SizeHelper.size12, SizeHelper.size10)

                // Text content
                layoutParent.addView(ViewHelper.createText(context,
                        ViewHelper.createLayoutParams(),
                        null,
                        ViewHelper.createTypeface(context, R.font.barlow_semi_bold),
                        Color.WHITE,
                        14f))

                // Layout bottom
                layoutParent.addView(FrameLayout(context).also { layoutBottom ->
                    layoutBottom.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)

                    // Text verified
                    layoutBottom.addView(ViewHelper.createText(context,
                            FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).also { params ->
                                params.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                            },
                            null,
                            ViewHelper.createTypeface(context, R.font.barlow_medium),
                            Color.WHITE,
                            12f).also {
                        it.compoundDrawablePadding = SizeHelper.size4
                        it.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_verified_white_24dp, 0, 0, 0)
                        it.gravity = Gravity.CENTER_VERTICAL
                        it.setText(R.string.verified)
                    })
                })
            }
        }
    }
}