package vn.icheck.android.component.postofuser

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import vn.icheck.android.R
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.network.models.ICPostMeta
import vn.icheck.android.util.kotlin.WidgetUtils

class ProductInFeedComponent : LinearLayout {

    constructor(context: Context) : this(context, null) {
        createView()
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        createView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        createView()
    }

//    fun setLayoutParams(marginLeft: Int=SizeHelper.size12, marginTop: Int=SizeHelper.size10, marginRight: Int=SizeHelper.size12, marginBottom: Int){
//        layoutParams=ViewHelper.createLayoutParams(marginLeft, marginTop, marginRight, marginBottom)
//    }

    private fun createView() {
        if (childCount == 0) {
            orientation = HORIZONTAL
            background = ContextCompat.getDrawable(context, R.drawable.bg_dark_gray_6_corners_4)
            setPadding(SizeHelper.size4, SizeHelper.size6, SizeHelper.size4, SizeHelper.size6)

            // Image logo
            addView(AppCompatImageView(context).also { imgLogo ->
                imgLogo.layoutParams = ViewHelper.createLayoutParams(SizeHelper.size40, SizeHelper.size40)
            })

            // Layout info
            addView(LinearLayout(context).also { layoutInfo ->
                layoutInfo.layoutParams = ViewHelper.createLayoutParams(SizeHelper.size8, 0, 0, 0)
                layoutInfo.orientation = VERTICAL

                layoutInfo.addView(ViewHelper.createText(context,
                        ViewHelper.createLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0, 0, 0, SizeHelper.size6),
                        null,
                        ViewHelper.createTypeface(context, R.font.barlow_semi_bold),
                        ContextCompat.getColor(context, R.color.colorNormalText),
                        16f,
                        2))

                layoutInfo.addView(ViewHelper.createText(context,
                        ViewHelper.createLayoutParams(),
                        null,
                        ViewHelper.createTypeface(ViewHelper.sansSerif, Typeface.NORMAL),
                        Constant.getSecondTextColor(context),
                        14f,
                        1))
            })
        }
    }

    fun setData(obj: ICPostMeta?) {
        if (obj?.product != null){
            if (!obj.product?.media.isNullOrEmpty()){
                WidgetUtils.loadImageUrlRounded(getChildAt(0) as AppCompatImageView, obj.product?.media!![0].content, SizeHelper.size4)
            }
        }

        (getChildAt(1) as LinearLayout).run {
            (getChildAt(0) as AppCompatTextView).text = obj?.product?.name ?: context.getString(R.string.dang_cap_nhat)

            (getChildAt(1) as AppCompatTextView).text = obj?.product?.owner?.name ?: context.getString(R.string.dang_cap_nhat)
        }
    }
}