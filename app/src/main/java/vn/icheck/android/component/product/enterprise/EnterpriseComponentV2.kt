package vn.icheck.android.component.product.enterprise

import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.databinding.ItemEnterpriseComponentV2Binding
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.beGone
import vn.icheck.android.ichecklibs.beVisible
import vn.icheck.android.network.models.ICOwner
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.util.kotlin.WidgetUtils

class EnterpriseComponentV2(parent: ViewGroup, val binding: ItemEnterpriseComponentV2Binding = ItemEnterpriseComponentV2Binding.inflate(LayoutInflater.from(parent.context), parent, false)) :
        BaseViewHolder<EnterpriseModelV2>(binding.root) {

    override fun bind(obj: EnterpriseModelV2) {
        initListener(obj.business!!)

        WidgetUtils.loadImageUrlRounded4(binding.imgAvatar, obj.business.avatar, R.drawable.ic_business_v2, R.drawable.ic_business_v2)

        binding.tvNamePage.text = if (obj.business.name.isNullOrEmpty()) {
            binding.root.context.getString(R.string.dang_cap_nhat)
        } else {
            obj.business.name
        }

        binding.imgDetail.setImageResource(if (obj.background == R.color.colorDisableText) {
            R.drawable.ic_arrow_right_gray_28px
        } else {
            R.drawable.ic_arrow_right_white_bg_blue_28px
        })

        binding.backgroundVerified.setBackgroundColor(getColor(obj.background))

        if (obj.icon != null) {
            binding.tvNamePage.setCompoundDrawablesWithIntrinsicBounds(0, 0, obj.icon, 0)
        } else {
            binding.tvNamePage.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }

        binding.tvPageCategory.text = "Doanh nghiệp sở hữu"

        if (!obj.business.phone.isNullOrEmpty()) {
            binding.tvPhone.visibility = View.VISIBLE
            binding.tvDangCapNhatSDT.visibility = View.GONE

            val ssb = SpannableString(obj.business.phone)

            for (item in obj.business.phone!!.split("-")) {
                val position = obj.business.phone!!.indexOf(item)
                ssb.setSpan(MyClickableSpan(item), position, position + item.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            binding.tvPhone.movementMethod = LinkMovementMethod.getInstance()
            binding.tvPhone.setText(ssb, TextView.BufferType.SPANNABLE)
        } else {
            binding.tvPhone.visibility = View.GONE
            binding.tvDangCapNhatSDT.visibility = View.VISIBLE
        }

        if (obj.business.address.isNullOrEmpty()) {
            binding.tvAddress.text = binding.root.context.getString(R.string.dia_chi_dang_cap_nhat)
        } else {
            binding.tvAddress.text = obj.business.address
        }

        if (!obj.business.tax.isNullOrEmpty()) {
            binding.tvMST.text = "Mã số thuế: " + obj.business.tax
            binding.tvDangCapNhatMST.visibility = View.GONE
        } else {
            binding.tvMST.text = "Mã số thuế: "
            binding.tvDangCapNhatMST.visibility = View.VISIBLE
        }

        val list = mutableListOf<ICSocialNetworkModel>().apply {
            if (!obj.business.facebook.isNullOrEmpty()) {
                add(ICSocialNetworkModel("Facebook", R.drawable.ic_facebook_20_dp, obj.business.facebook))
            }

            if (!obj.business.email.isNullOrEmpty()) {
                add(ICSocialNetworkModel("Mail", R.drawable.ic_mail, obj.business.email))
            }

            if (!obj.business.website.isNullOrEmpty()) {
                add(ICSocialNetworkModel("Website", R.drawable.ic_website, obj.business.website))
            }

            if (!obj.business.youtube.isNullOrEmpty()) {
                add(ICSocialNetworkModel("Youtube", R.drawable.ic_youtube, obj.business.youtube))
            }
        }

        binding.recyclerView.apply {
            if (list.isNullOrEmpty()) {
                beVisible()
            } else {
                beGone()
                layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = SocialNetworkAdapter("Linear").apply {
                    setData(list)
                }
            }
        }
    }

    inner class MyClickableSpan constructor(var text: String) : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {

        }

        override fun onClick(view: View) {
            DialogHelper.showConfirm(binding.root.context, binding.root.context.getString(R.string.thong_bao), binding.root.context.getString(R.string.ban_co_muon_goi_dien_thoai_den_so, text), binding.root.context.getString(R.string.huy), binding.root.context.getString(R.string.dong_y), true, object : ConfirmDialogListener {
                override fun onDisagree() {
                }

                override fun onAgree() {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:${text}")
                    ICheckApplication.currentActivity()?.startActivity(intent)
                }
            })
        }
    }

    private fun initListener(obj: ICOwner) {
        binding.imgDetail.setOnClickListener {
            openPageDetail(obj.pageId)
        }

        binding.root.setOnClickListener {
            openPageDetail(obj.pageId)
        }
    }

    private fun openPageDetail(pageID: Long?) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (pageID != null && pageID != 0L) {
                PageDetailActivity.start(activity, pageID)
            }
        }
    }
}