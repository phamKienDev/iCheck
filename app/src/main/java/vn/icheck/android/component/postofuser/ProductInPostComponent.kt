package vn.icheck.android.component.postofuser

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import vn.icheck.android.R
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.network.models.post.ICProductInPost
import vn.icheck.android.util.ick.rText
import vn.icheck.android.util.kotlin.WidgetUtils

class ProductInPostComponent : LinearLayout {

    constructor(context: Context) : this(context, null) {
        createView()
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        createView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        createView()
    }

    private fun createView() {
        orientation = HORIZONTAL
        background = ViewHelper.createShapeDrawable(ContextCompat.getColor(context, vn.icheck.android.ichecklibs.R.color.grayF0), SizeHelper.size4.toFloat())
        setPadding(SizeHelper.size4, SizeHelper.size4, SizeHelper.size4, SizeHelper.size4)

        // Image logo
        addView(AppCompatImageView(context).also { imgLogo ->
            imgLogo.layoutParams = ViewHelper.createLayoutParams(SizeHelper.size88, SizeHelper.size88)
        })

        // Layout info
        addView(LinearLayout(context).also { layoutInfo ->
            layoutInfo.layoutParams = ViewHelper.createLayoutParams(SizeHelper.size8, 0, 0, 0)
            layoutInfo.orientation = VERTICAL

            layoutInfo.addView(ViewHelper.createText(context,
                    ViewHelper.createLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0, 0, 0, SizeHelper.size6),
                    null,
                    ViewHelper.createTypeface(context, R.font.barlow_semi_bold),
                    ContextCompat.getColor(context, R.color.darkGray1),
                    16f,
                    2))

            layoutInfo.addView(ViewHelper.createText(context,
                    ViewHelper.createLayoutParams(),
                    null,
                    ViewHelper.createTypeface(context, R.font.barlow_medium),
                    Constant.getSecondTextColor(context),
                    14f,
                    1))
        })
    }

    fun setData(obj: ICProductInPost) {
        WidgetUtils.loadImageUrlRounded(getChildAt(0) as AppCompatImageView,
            obj.avatar_product, SizeHelper.size4)

        (getChildAt(1) as LinearLayout).run {
            (getChildAt(0) as AppCompatTextView).text = obj.name_product ?: context rText R.string.dang_cap_nhat

            (getChildAt(1) as AppCompatTextView).text = obj.name_busniness ?: context rText R.string.dang_cap_nhat
        }
    }
}