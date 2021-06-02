package vn.icheck.android.component.product.enterprise

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.DialogPageInfoBinding
import vn.icheck.android.ichecklibs.base_dialog.BaseBottomSheetDialogFragment
import vn.icheck.android.network.models.ICWidgetData

class ICPageInfoDialog : BaseBottomSheetDialogFragment() {
    private lateinit var binding: DialogPageInfoBinding
    private lateinit var obj: ICWidgetData

    companion object {
        fun show(fragmentManager: FragmentManager, obj: ICWidgetData) {
            if (fragmentManager.findFragmentByTag(ICPageInfoDialog::class.java.simpleName)?.isInLayout != true) {
                ICPageInfoDialog().apply {
                    setData(obj)
                    show(fragmentManager, ICPageInfoDialog::class.java.simpleName)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogPageInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvName.text = obj.name
        binding.tvAddress.text = getSpannable(R.string.dia_chi_xxx_stamp_v61, getContent(obj.address), getColor(R.color.colorNormalText), getColor(R.color.darkGray3))
        binding.tvPhone.text = getSpannable(R.string.dien_thoai_xxx_stamp_v61, getContent(obj.phone), getColor(R.color.colorNormalText), getColor(R.color.colorPrimary))
        binding.tvEmail.text = getSpannable(R.string.email_xxx_stamp_v61, getContent(obj.email), getColor(R.color.colorNormalText), getColor(R.color.colorPrimary))
        binding.tvWebsite.text = getSpannable(R.string.website_xxx_stamp_v61, getContent(obj.website), getColor(R.color.colorNormalText), getColor(R.color.colorPrimary))
        binding.tvGln.text = getSpannable(R.string.ma_gln_xxx_stamp_v61, getContent(obj.code), getColor(R.color.colorNormalText), getColor(R.color.darkGray3))
        binding.tvDescription.text = getSpannable(R.string.gioi_thieu_xxx_stamp_v61, getContent(obj.description), getColor(R.color.colorNormalText), getColor(R.color.darkGray3))

        binding.tvPhone.setOnClickListener {
            Constant.callPhone(obj.phone)
        }
        binding.tvEmail.setOnClickListener {
            Constant.sendEmail(obj.email)
        }
        binding.tvWebsite.setOnClickListener {
            Constant.openUrl(obj.website)
        }
    }

    fun setData(obj: ICWidgetData) {
        this.obj = obj
    }

    private fun getSpannable(title: Int, value: String?, firstColor: Int, secondColor: Int): SpannableString {
        val mValue = getContent(value)

        val spannable = SpannableString(getString(title, mValue))
        spannable.setSpan(ForegroundColorSpan(firstColor), 0, spannable.length - mValue.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(StyleSpan(Typeface.BOLD), 0, spannable.length - mValue.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ForegroundColorSpan(secondColor), spannable.length - mValue.length, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        return spannable
    }

    private fun getContent(text: String?): String {
        return if (text.isNullOrEmpty()) {
            getString(R.string.dang_cap_nhat)
        } else {
            text
        }
    }

    private fun getColor(colorID: Int): Int {
        return ContextCompat.getColor(requireContext(), colorID)
    }
}