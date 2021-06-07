package vn.icheck.android.component.product.enterprise

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.core.content.ContextCompat
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialog
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.DialogPageInfoBinding
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.ICWidgetData

class ICPageInfoDialog(context: Context) : BaseBottomSheetDialog(context, true) {

    fun show(obj: ICWidgetData) {
        val binding = DialogPageInfoBinding.inflate(dialog.layoutInflater)
        dialog.setContentView(binding.root)

        binding.root.background = ViewHelper.bgWhiteCornersTop16(dialog.context)

        binding.tvName.text = obj.name
        binding.tvAddress.text = getSpannable(R.string.dia_chi_xxx_stamp_v61, getContent(obj.address), getColor(R.color.colorNormalText), getColor(R.color.darkGray3))
        binding.tvPhone.text = getSpannable(R.string.dien_thoai_xxx_stamp_v61, getContent(obj.phone), getColor(R.color.colorNormalText), getColor(R.color.colorPrimary))
        binding.tvEmail.text = getSpannable(R.string.email_xxx_stamp_v61, getContent(obj.email), getColor(R.color.colorNormalText), getColor(R.color.colorPrimary))
        binding.tvWebsite.text = getSpannable(R.string.website_xxx_stamp_v61, getContent(obj.website), getColor(R.color.colorNormalText), getColor(R.color.colorPrimary))
        binding.tvGln.text = getSpannable(R.string.ma_gln_xxx_stamp_v61, getContent(obj.code), getColor(R.color.colorNormalText), getColor(R.color.darkGray3))
        binding.tvDescription.text =
            getSpannable(R.string.gioi_thieu_xxx_stamp_v61, getContent(obj.description), getColor(R.color.colorNormalText), getColor(R.color.darkGray3))

        binding.tvPhone.setOnClickListener {
            Constant.callPhone(obj.phone)
        }
        binding.tvEmail.setOnClickListener {
            Constant.sendEmail(obj.email)
        }
        binding.tvWebsite.setOnClickListener {
            Constant.openUrl(obj.website)
        }

        dialog.show()
    }

    private fun getSpannable(title: Int, value: String?, firstColor: Int, secondColor: Int): SpannableString {
        val mValue = getContent(value)

        val spannable = SpannableString(dialog.context.getString(title, mValue))
        spannable.setSpan(ForegroundColorSpan(firstColor), 0, spannable.length - mValue.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(StyleSpan(Typeface.BOLD), 0, spannable.length - mValue.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ForegroundColorSpan(secondColor), spannable.length - mValue.length, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        return spannable
    }

    private fun getContent(text: String?): String {
        return if (text.isNullOrEmpty()) {
            dialog.context.getString(R.string.dang_cap_nhat)
        } else {
            text
        }
    }

    private fun getColor(colorID: Int): Int {
        return when (colorID) {
            R.color.colorPrimary -> {
                vn.icheck.android.ichecklibs.Constant.getPrimaryColor(dialog.context)
            }
            R.color.colorNormalText -> {
                vn.icheck.android.ichecklibs.Constant.getNormalTextColor(dialog.context)
            }
            else -> {
                ContextCompat.getColor(dialog.context, colorID)
            }
        }
    }
}