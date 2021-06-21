package vn.icheck.android.component.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import vn.icheck.android.R
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICCountry

class LayoutInputPhone : LinearLayout {

    constructor(context: Context) : super(context) {
        setupView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setupView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setupView()
    }

    private fun setupView() {
        orientation = VERTICAL

        addView(ViewHelper.createText(context,
                ViewHelper.createLayoutParams(),
                ViewHelper.outValue.resourceId,
                ViewHelper.createTypeface(context, R.font.barlow_semi_bold),
                ContextCompat.getColor(context, R.color.colorPrimary),
                18f).also {
            it.setPadding(0, SizeHelper.size10, 0, SizeHelper.size10)
            it.setText(R.string.viet_nam)
            it.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_right_gray_24dp, 0)
        })

        addView(View(context).also {
            it.layoutParams = ViewHelper.createLayoutParams(LayoutParams.MATCH_PARENT, SizeHelper.size1)
            it.setBackgroundColor(ContextCompat.getColor(context, R.color.darkGray6))
        })

        val layoutPhone = LinearLayout(context)
        layoutPhone.layoutParams = ViewHelper.createLayoutParams(LayoutParams.MATCH_PARENT, SizeHelper.size52, 0, SizeHelper.size6, 0, 0)
        layoutPhone.orientation = HORIZONTAL
        addView(layoutPhone)

        layoutPhone.addView(ViewHelper.createText(context,
                ViewHelper.createLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 0, 0, SizeHelper.size5, 0),
                ContextCompat.getDrawable(context, R.drawable.under_line_gray_1dp),
                ViewHelper.createTypeface(context, R.font.barlow_medium),
                ContextCompat.getColor(context, R.color.colorSecondText),
                24f).also {
            it.setPadding(SizeHelper.size4, 0, SizeHelper.size4, 0)
            it.gravity = Gravity.CENTER
            it.setText(R.string.phone)
        })

        layoutPhone.addView(ViewHelper.createEditText(context,
                ViewHelper.createLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, SizeHelper.size5, 0, 0, 0),
                ContextCompat.getDrawable(context, R.drawable.under_line_light_blue_1dp),
                ViewHelper.createTypeface(context, R.font.barlow_medium),
                ContextCompat.getColor(context, R.color.colorNormalText),
                24f).also {
            it.setPadding(SizeHelper.size4, 0, SizeHelper.size4, 0)
            it.gravity = Gravity.CENTER
            it.setHintTextColor(ContextCompat.getColor(context, R.color.colorDisableText))
            it.setHint(R.string.nhap_so_dien_thoai)
        })
    }

    fun setSelectedCountry(obj: ICCountry) {
        (getChildAt(0) as AppCompatTextView).text = obj.name
        ((getChildAt(2) as LinearLayout).getChildAt(0) as AppCompatTextView).text = obj.code
    }

    val getInputPhone: String
        get() {
            val layoutPhone = getChildAt(2) as LinearLayout
            return (layoutPhone.getChildAt(0) as AppCompatTextView).text.toString() + (layoutPhone.getChildAt(1) as AppCompatEditText).text.toString()
        }

    fun setOnSelectCountry(listener: OnClickListener) {
        getChildAt(0).setOnClickListener(listener)
    }
}