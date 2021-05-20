package vn.icheck.android.component.product.enterprise

import android.content.Context
import android.text.Html
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialog
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.DialogPageInfoBinding
import vn.icheck.android.network.models.ICWidgetData

class ICPageInfoDialog(context: Context) : BaseBottomSheetDialog(context, true) {

    fun show(obj: ICWidgetData) {
        val binding = DialogPageInfoBinding.inflate(dialog.layoutInflater)
        dialog.setContentView(binding.root)

        binding.tvName.text = obj.name
        binding.tvAddress.text = Html.fromHtml(binding.root.context.getString(R.string.dia_chi_xxx_stamp_v61, getContent(obj.address)))
        binding.tvPhone.text = Html.fromHtml(binding.root.context.getString(R.string.dien_thoai_xxx_stamp_v61, getContent(obj.phone)))
        binding.tvEmail.text = Html.fromHtml(binding.root.context.getString(R.string.email_xxx_stamp_v61, getContent(obj.email)))
        binding.tvWebsite.text = Html.fromHtml(binding.root.context.getString(R.string.website_xxx_stamp_v61, getContent(obj.website)))
        binding.tvGln.text = Html.fromHtml(binding.root.context.getString(R.string.ma_gln_xxx_stamp_v61, getContent(obj.gln)))
        binding.tvDescription.text = Html.fromHtml(binding.root.context.getString(R.string.gioi_thieu_xxx_stamp_v61, getContent(obj.description)))
        binding.tvContent.text = Html.fromHtml(getContent(obj.description))

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

    private fun getContent(text: String?) : String {
        return if (text.isNullOrEmpty()) {
            dialog.context.getString(R.string.dang_cap_nhat)
        } else {
            text
        }
    }
}