package vn.icheck.android.component.product.vendor

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
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_vendor_holder.view.constraintLayout1
import kotlinx.android.synthetic.main.item_vendor_holder.view.imgAvatar
import kotlinx.android.synthetic.main.item_vendor_holder.view.tvAddress
import kotlinx.android.synthetic.main.item_vendor_holder.view.tvDangCapNhatMST
import kotlinx.android.synthetic.main.item_vendor_holder.view.tvDangCapNhatSDT
import kotlinx.android.synthetic.main.item_vendor_holder.view.tvNamePage
import kotlinx.android.synthetic.main.item_vendor_holder.view.tvPageCategory
import kotlinx.android.synthetic.main.item_vendor_holder.view.tvPhone
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.product.enterprise.ICSocialNetworkModel
import vn.icheck.android.component.product.enterprise.SocialNetworkAdapter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICPage
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class VendorAdapter(val listData: MutableList<ICPage>, val icon: Int?) : RecyclerView.Adapter<VendorAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VendorAdapter.ViewHolder {
        return ViewHolder(parent, icon)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: VendorAdapter.ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ViewHolder(parent: ViewGroup, val icon: Int?) : BaseViewHolder<ICPage>(LayoutInflater.from(parent.context).inflate(R.layout.item_vendor_holder, parent, false)) {
        var facebook = 0
        var mail = 0
        var website = 0
        var youtube = 0

        override fun bind(obj: ICPage) {
            initListener(obj)

            if (listData.size < 2) {
                itemView.constraintLayout1.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT).also {
                    it.setMargins(SizeHelper.size10, 0, SizeHelper.size10, 0)
                }
            } else {
                itemView.constraintLayout1.layoutParams = ConstraintLayout.LayoutParams(SizeHelper.size331, ConstraintLayout.LayoutParams.WRAP_CONTENT).also {
                    it.setMargins(SizeHelper.size10, 0, 0, 0)
                }
            }

            WidgetUtils.loadImageUrlRounded4(itemView.imgAvatar, obj.avatar, R.drawable.ic_business_v2, R.drawable.ic_business_v2)

            itemView.tvNamePage.text = if (obj.name.isNullOrEmpty()) {
                itemView.context.getString(R.string.dang_cap_nhat)
            } else {
                obj.name
            }

            if (icon != null) {
                itemView.tvNamePage.setCompoundDrawablesWithIntrinsicBounds(0, 0, icon, 0)
            }

            if (!obj.title.isNullOrEmpty()) {
                itemView.tvPageCategory.text = obj.title
            }

            if (!obj.phone.isNullOrEmpty()) {
                itemView.tvPhone.visibility = View.VISIBLE
                itemView.tvDangCapNhatSDT.visibility = View.GONE

                val ssb = SpannableString(obj.phone)

                for (item in obj.phone!!.split("-")) {
                    val position = obj.phone!!.indexOf(item)
                    ssb.setSpan(MyClickableSpan(item), position, position + item.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                itemView.tvPhone.movementMethod = LinkMovementMethod.getInstance()
                itemView.tvPhone.setText(ssb, TextView.BufferType.SPANNABLE)
            } else {
                itemView.tvPhone.visibility = View.GONE
                itemView.tvDangCapNhatSDT.visibility = View.VISIBLE
            }

            if (obj.address.isNullOrEmpty()) {
                itemView.tvAddress.text = itemView.context.getString(R.string.dang_cap_nhat)
            } else {
                itemView.tvAddress.text = obj.address
            }

            if (!obj.tax.isNullOrEmpty()) {
                itemView.findViewById<AppCompatTextView>(R.id.tvMST).text = obj.tax
                itemView.tvDangCapNhatMST.visibility = View.GONE
            } else {
                itemView.findViewById<AppCompatTextView>(R.id.tvMST).text = "Mã số thuế: "
                itemView.tvDangCapNhatMST.visibility = View.VISIBLE
            }

            val list = mutableListOf<ICSocialNetworkModel>()

            if (!obj.facebook.isNullOrEmpty()) {
                facebook = 1
                list.add(ICSocialNetworkModel("Facebook", R.drawable.ic_facebook_20_dp, obj.facebook))
            }

            if (!obj.email.isNullOrEmpty()) {
                mail = 1
                list.add(facebook, ICSocialNetworkModel("Mail", R.drawable.ic_mail, obj.email))
            }

            if (!obj.website.isNullOrEmpty()) {
                website = 1
                list.add(facebook + mail, ICSocialNetworkModel("Website", R.drawable.ic_website, obj.website))
            }

            if (!obj.youtube.isNullOrEmpty()) {
                youtube = 1
                list.add(facebook + mail + website, ICSocialNetworkModel("Youtube", R.drawable.ic_youtube, obj.youtube))
            }

            if (list.isNullOrEmpty()) {
                itemView.findViewById<RecyclerView>(R.id.recyclerView).visibility = View.INVISIBLE
            } else {
                val adapter = SocialNetworkAdapter("Grid")
                itemView.findViewById<RecyclerView>(R.id.recyclerView).layoutManager = GridLayoutManager(itemView.context, 3)
                itemView.findViewById<RecyclerView>(R.id.recyclerView).adapter = adapter
                adapter.setData(list)
            }
        }

        inner class MyClickableSpan constructor(var text: String) : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {

            }

            override fun onClick(view: View) {
                DialogHelper.showConfirm(itemView.context, itemView.context.getString(R.string.thong_bao), itemView.context.getString(R.string.ban_co_muon_goi_dien_thoai_den_so, text), itemView.context.getString(R.string.huy), itemView.context.getString(R.string.dong_y), true, object : ConfirmDialogListener {
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

        private fun initListener(obj: ICPage) {
            itemView.findViewById<AppCompatImageView>(R.id.imgDetail).setOnClickListener {
                startPageDetail(obj)
            }

            itemView.setOnClickListener {
                startPageDetail(obj)
            }

//            itemView.findViewById<AppCompatTextView>(R.id.tvPhone).setOnClickListener {
//                DialogHelper.showConfirm(itemView.context,
//                        itemView.context.getString(R.string.thong_bao),
//                        itemView.context.getString(R.string.ban_co_muon_goi_dien_thoai_den_so, obj.phone),
//                        itemView.context.getString(R.string.huy), itemView.context.getString(R.string.dong_y), true, object : ConfirmDialogListener {
//                    override fun onDisagree() {
//                        DialogHelper.closeDialog()
//                    }
//
//                    override fun onAgree() {
//                        val intent = Intent(Intent.ACTION_DIAL)
//                        intent.data = Uri.parse("tel:${obj.phone}")
//                        ICheckApplication.currentActivity()?.let { act ->
//                            act.startActivity(intent)
//                        }
//                    }
//                })
//            }
        }
    }

    private fun startPageDetail(obj: ICPage) {
        ICheckApplication.currentActivity()?.let { act ->
            if (obj.pageId != null) {
                ActivityUtils.startActivity<PageDetailActivity, Long>(act, Constant.DATA_1, obj.pageId!!)
            } else {
                if (obj.owner?.pageId != null){
                    ActivityUtils.startActivity<PageDetailActivity, Long>(act, Constant.DATA_1, obj.owner?.pageId!!)
                }
            }
        }
    }
}