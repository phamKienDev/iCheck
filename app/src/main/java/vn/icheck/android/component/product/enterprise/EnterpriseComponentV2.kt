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
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_enterprise_component_v2.view.*
import kotlinx.android.synthetic.main.item_enterprise_component_v2.view.imgAvatar
import kotlinx.android.synthetic.main.item_enterprise_component_v2.view.imgDetail
import kotlinx.android.synthetic.main.item_enterprise_component_v2.view.recyclerView
import kotlinx.android.synthetic.main.item_enterprise_component_v2.view.tvAddress
import kotlinx.android.synthetic.main.item_enterprise_component_v2.view.tvDangCapNhatMST
import kotlinx.android.synthetic.main.item_enterprise_component_v2.view.tvDangCapNhatSDT
import kotlinx.android.synthetic.main.item_enterprise_component_v2.view.tvMST
import kotlinx.android.synthetic.main.item_enterprise_component_v2.view.tvNamePage
import kotlinx.android.synthetic.main.item_enterprise_component_v2.view.tvPageCategory
import kotlinx.android.synthetic.main.item_enterprise_component_v2.view.tvPhone
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.models.ICOwner
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class EnterpriseComponentV2(parent: ViewGroup, val recycledViewPool: RecyclerView.RecycledViewPool?) : BaseViewHolder<EnterpriseModelV2>(LayoutInflater.from(parent.context).inflate(R.layout.item_enterprise_component_v2, parent, false)) {
    var facebook = 0
    var mail = 0
    var website = 0
    var youtube = 0

    override fun bind(obj: EnterpriseModelV2) {
        initListener(obj.business!!)

        WidgetUtils.loadImageUrlRounded4(itemView.imgAvatar, obj.business.avatar, R.drawable.ic_business_v2, R.drawable.ic_business_v2)

        itemView.tvNamePage.text = if (obj.business.name.isNullOrEmpty()) {
            itemView.context.getString(R.string.dang_cap_nhat)
        } else {
            obj.business.name
        }

        itemView.imgDetail.setImageResource(if (obj.background == R.color.colorDisableView) {
            R.drawable.ic_arrow_right_gray_28px
        } else {
            R.drawable.ic_arrow_right_white_bg_blue_28px
        })

        itemView.backgroundVerified.setBackgroundColor(getColor(obj.background))

        if (obj.icon != null) {
            itemView.tvNamePage.setCompoundDrawablesWithIntrinsicBounds(0, 0, obj.icon, 0)
        } else {
            itemView.tvNamePage.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }

        itemView.tvPageCategory.text = "Doanh nghiệp sở hữu"

        if (!obj.business.phone.isNullOrEmpty()) {
            itemView.tvPhone.visibility = View.VISIBLE
            itemView.tvDangCapNhatSDT.visibility = View.GONE

            val ssb = SpannableString(obj.business.phone)

            for (item in obj.business.phone!!.split("-")) {
                val position = obj.business.phone!!.indexOf(item)
                ssb.setSpan(MyClickableSpan(item), position, position + item.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            itemView.tvPhone.movementMethod = LinkMovementMethod.getInstance()
            itemView.tvPhone.setText(ssb, TextView.BufferType.SPANNABLE)
        } else {
            itemView.tvPhone.visibility = View.GONE
            itemView.tvDangCapNhatSDT.visibility = View.VISIBLE
        }

        if (obj.business.address.isNullOrEmpty()) {
            itemView.tvAddress.text = itemView.context.getString(R.string.dia_chi_dang_cap_nhat)
        } else {
            itemView.tvAddress.text = obj.business.address
        }

        if (!obj.business.tax.isNullOrEmpty()) {
            itemView.tvMST.text = "Mã số thuế: " + obj.business.tax
            itemView.tvDangCapNhatMST.visibility = View.GONE
        } else {
            itemView.tvMST.text = "Mã số thuế: "
            itemView.tvDangCapNhatMST.visibility = View.VISIBLE
        }

        val list = mutableListOf<ICSocialNetworkModel>()

        if (!obj.business.facebook.isNullOrEmpty()) {
            facebook = 1
            list.add(ICSocialNetworkModel("Facebook", R.drawable.ic_facebook_20_dp, obj.business.facebook))
        }

        if (!obj.business.email.isNullOrEmpty()) {
            mail = 1
            list.add(facebook, ICSocialNetworkModel("Mail", R.drawable.ic_mail, obj.business.email))
        }

        if (!obj.business.website.isNullOrEmpty()) {
            website = 1
            list.add(facebook + mail, ICSocialNetworkModel("Website", R.drawable.ic_website, obj.business.website))
        }

        if (!obj.business.youtube.isNullOrEmpty()) {
            youtube = 1
            list.add(facebook + mail + website, ICSocialNetworkModel("Youtube", R.drawable.ic_youtube, obj.business.youtube))
        }

        if (list.isNullOrEmpty()) {
            itemView.recyclerView.visibility = View.GONE
        } else {
            itemView.recyclerView.visibility=View.VISIBLE
            val adapter = SocialNetworkAdapter("Linear")
            itemView.recyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            itemView.recyclerView.setRecycledViewPool(recycledViewPool)
            itemView.recyclerView.adapter = adapter
            adapter.setData(list)
        }
    }

    inner class MyClickableSpan constructor(var text: String) : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {

        }

        override fun onClick(view: View) {
            DialogHelper.showConfirm(itemView.context,
                    itemView.context.getString(R.string.thong_bao),
                    itemView.context.getString(R.string.ban_co_muon_goi_dien_thoai_den_so, text),
                    itemView.context.getString(R.string.huy), itemView.context.getString(R.string.dong_y), true, object : ConfirmDialogListener {
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
        itemView.imgDetail.setOnClickListener {
            ICheckApplication.currentActivity()?.let { act ->
                if (obj.pageId != null && obj.pageId != 0L) {
                    ActivityUtils.startActivity<PageDetailActivity, Long>(act, Constant.DATA_1, obj.pageId!!)
                }
            }
        }

        itemView.setOnClickListener {
            ICheckApplication.currentActivity()?.let { act ->
                if(obj.pageId != null && obj.pageId != 0L){
                    ActivityUtils.startActivity<PageDetailActivity, Long>(act, Constant.DATA_1, obj.pageId!!)
                }
            }
        }
    }
}