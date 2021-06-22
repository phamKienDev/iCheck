package vn.icheck.android.util.text

import android.graphics.Color
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper

object ReviewPointText {
    fun getText(point: Float): String {
        val total = point * 2
        if (total >= 9) {
            return "Trên cả tuyệt vời"
        }
        if (total >= 8) {
            return "Tuyệt vời"
        }
        if (total >= 7) {
            return "Tốt"
        }
        if (total >= 6) {
            return "Hài Lòng"
        }
        return "Điểm đánh giá"
    }

    fun getTextTotal(point: Float): String {
        val total = point * 2
        return when {
            total >= 9 -> {
                ICheckApplication.getInstance().getString(R.string.x_tren_ca_tuyet_voi, String.format("%.1f", total))
            }
            total >= 8 -> {
                ICheckApplication.getInstance().getString(R.string.x_tuyet_voi, String.format("%.1f", total))
            }
            total >= 7 -> {
                ICheckApplication.getInstance().getString(R.string.x_tot, String.format("%.1f", total))
            }
            total >= 6 -> {
                ICheckApplication.getInstance().getString(R.string.x_hai_long, String.format("%.1f", total))
            }
            else -> ICheckApplication.getInstance().getString(R.string.x_diem_danh_gia, String.format("%.1f", total))
        }
    }

    fun getTextTotalProductDetail(point: Float, textView: AppCompatTextView) {
        val total = point * 2
        textView.apply {
            when {
                total >= 9 -> {
                    text = ICheckApplication.getInstance().getString(R.string.x_tren_ca_tuyet_voi, String.format("%.1f", total))
                    setTextColor(vn.icheck.android.ichecklibs.ColorManager.getPrimaryColor(context))
                }
                total >= 8 -> {
                    text = ICheckApplication.getInstance().getString(R.string.x_tuyet_voi, String.format("%.1f", total))
                    setTextColor(Color.parseColor("#FF2F2A"))
                }
                total >= 7 -> {
                    text = ICheckApplication.getInstance().getString(R.string.x_tot, String.format("%.1f", total))
                    setTextColor(Color.parseColor("#FC9603"))
                }
                total >= 6 -> {
                    text = ICheckApplication.getInstance().getString(R.string.x_hai_long, String.format("%.1f", total))
                    setTextColor(Color.parseColor("#49CA4E"))
                }
                else ->{
                    text = ICheckApplication.getInstance().getString(R.string.x_diem_danh_gia, String.format("%.1f", total))
                    setTextColor(Color.parseColor("#B872AB"))
                }
            }
        }
    }

    fun setText(textView: AppCompatTextView, point: Float) {
        textView.apply {
            when {
                point >= 4.5 -> {
                    text = context.getString(R.string.x_tren_ca_tuyet_voi, String.format("%.1f", point * 2))
                    val primaryColor = vn.icheck.android.ichecklibs.ColorManager.getPrimaryColor(context)
                    setTextColor(primaryColor)
                    background = ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size0_5, primaryColor, SizeHelper.size14.toFloat())
                }
                point >= 4 -> {
                    text = context.getString(R.string.x_tuyet_voi, String.format("%.1f", point * 2))
                    setTextColor(ContextCompat.getColor(context, R.color.red_v2))
                    background = ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size0_5, ContextCompat.getColor(context, R.color.red_v2), SizeHelper.size14.toFloat())
                }
                point >= 3.5 -> {
                    text = context.getString(R.string.x_tot, String.format("%.1f", point * 2))
                    setTextColor(ContextCompat.getColor(context, R.color.orange_v2))
                    background = ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size0_5, ContextCompat.getColor(context, R.color.orange_v2), SizeHelper.size14.toFloat())
                }
                point >= 3 -> {
                    text = context.getString(R.string.x_hai_long, String.format("%.1f", point * 2))
                    setTextColor(ContextCompat.getColor(context, R.color.green_v2))
                    background = ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size0_5, ContextCompat.getColor(context, R.color.green_v2), SizeHelper.size14.toFloat())
                }
                else -> {
                    text = context.getString(R.string.x_diem_danh_gia, String.format("%.1f", point * 2))
                    setTextColor(ContextCompat.getColor(context, R.color.light_purple))
                    background = ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size0_5, ContextCompat.getColor(context, R.color.light_purple), SizeHelper.size14.toFloat())
                }
            }
        }
    }

    fun setText(textView: AppCompatTextView, point: Double) {
        textView.apply {
            when {
                point >= 4.5 -> {
                    text = context.getString(R.string.tren_ca_tuyet_voi)
                    val primaryColor = vn.icheck.android.ichecklibs.ColorManager.getPrimaryColor(context)
                    setTextColor(primaryColor)
                    background = ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size0_5, primaryColor, SizeHelper.size14.toFloat())
                }
                point >= 4 -> {
                    text = context.getString(R.string.tuyet_voi)
                    setTextColor(ContextCompat.getColor(context, R.color.red_v2))
                    background = ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size0_5, ContextCompat.getColor(context, R.color.red_v2), SizeHelper.size14.toFloat())
                }
                point >= 3.5 -> {
                    text = context.getString(R.string.tot)
                    setTextColor(ContextCompat.getColor(context, R.color.orange_v2))
                    background = ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size0_5, ContextCompat.getColor(context, R.color.orange_v2), SizeHelper.size14.toFloat())
                }
                point >= 3 -> {
                    text = context.getString(R.string.hai_long)
                    setTextColor(ContextCompat.getColor(context, R.color.green_v2))
                    background = ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size0_5, ContextCompat.getColor(context, R.color.green_v2), SizeHelper.size14.toFloat())
                }
                else -> {
//                    text = context.getString(R.string.x_diem_danh_gia, String.format("%.1f", (point*2)))
                    text = context.getString(R.string.diem_danh_gia)
                    setTextColor(ContextCompat.getColor(context, R.color.light_purple))
                    background = ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size0_5, ContextCompat.getColor(context, R.color.light_purple), SizeHelper.size14.toFloat())
                }
            }
        }
    }
}