package vn.icheck.android.component.header_page.bottom_sheet_header_page

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialog
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.Constant

class InforPageBottomSheet(val data: String, val context: Context) : BaseBottomSheetDialog(context, true) {

    fun show() {
        dialog.setContentView(createView())
        dialog.show()
    }

    private fun createView() = LinearLayout(context).also { layoutParent ->
        layoutParent.layoutParams = ViewHelper.createLayoutParams()
        layoutParent.orientation = LinearLayout.VERTICAL
        layoutParent.background = ViewHelper.createShapeDrawable(Color.WHITE, 0, Color.TRANSPARENT,
                SizeHelper.size20.toFloat(), SizeHelper.size20.toFloat(), 0f, 0f)

        // Layout header - 0
        layoutParent.addView(LinearLayout(context).also { layoutHeader ->
            layoutHeader.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.size42)
            layoutHeader.orientation = LinearLayout.HORIZONTAL

            layoutHeader.addView(AppCompatTextView(context).also {
                it.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, SizeHelper.size12, 0, 0, 0)
                it.setBackgroundResource(ViewHelper.outValue.resourceId)
                it.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cancel_light_blue_24dp, 0, 0, 0)

                it.setOnClickListener {
                    dialog.dismiss()
                }
            })

            layoutHeader.addView(ViewHelper.createText(context,
                    ViewHelper.createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 0, 0, SizeHelper.size36, 0),
                    null,
                    ViewHelper.createTypeface(context, R.font.barlow_semi_bold),
                    vn.icheck.android.ichecklibs.Constant.getPrimaryColor(context),
                    18f,
                    1).also {
                it.gravity = Gravity.CENTER
                it.setText(R.string.tai_sao_thong_tin_dong_gop_nay_sai)
            })
        })

        // Line gray - 1
        layoutParent.addView(View(context).also {
            it.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.size0_5)
            it.setBackgroundColor(Constant.getLineColor(context))
        })

        // text information
        layoutParent.addView(ViewHelper.createText(context,
                ViewHelper.createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.size12, 0, SizeHelper.size12, 0),
                null,
                ViewHelper.createTypeface(context, R.font.barlow_semi_bold),
                Constant.getNormalTextColor(context),
                14f).also {
            it.gravity = Gravity.CENTER
            it.text = data
        })
    }

}