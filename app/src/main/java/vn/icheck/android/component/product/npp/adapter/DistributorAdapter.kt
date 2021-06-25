package vn.icheck.android.component.product.npp.adapter

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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_distributor.view.*
import kotlinx.android.synthetic.main.item_distributor.view.tvDangCapNhatMST
import kotlinx.android.synthetic.main.item_distributor.view.tvDangCapNhatSDT
import kotlinx.android.synthetic.main.item_distributor.view.tvPhone
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.product.enterprise.ICSocialNetworkModel
import vn.icheck.android.component.product.enterprise.SocialNetworkAdapter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.ViewHelper.fillDrawableStartText
import vn.icheck.android.network.models.ICPage
import vn.icheck.android.screen.user.listdistributor.ListDistributorActivity
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class DistributorAdapter(val listData: MutableList<ICPage>, val url: String) : RecyclerView.Adapter<DistributorAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return if (listData.size > 3) {
            4
        } else {
            listData.size
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (listData.size < 2) {
            holder.itemView.findViewById<ConstraintLayout>(R.id.constraintLayout1).layoutParams =
                ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT).also {
                    it.setMargins(SizeHelper.size10, 0, SizeHelper.size10, 0)
                }
        } else {
            holder.itemView.findViewById<ConstraintLayout>(R.id.constraintLayout1).layoutParams =
                ConstraintLayout.LayoutParams(SizeHelper.size320, ConstraintLayout.LayoutParams.WRAP_CONTENT).also {
                    it.setMargins(SizeHelper.size10, 0, 0, 0)
                }
        }

        if (holder.adapterPosition == 3) {
            holder.itemView.findViewById<ConstraintLayout>(R.id.constraintLayout1).layoutParams =
                ConstraintLayout.LayoutParams(SizeHelper.size170, ConstraintLayout.LayoutParams.WRAP_CONTENT).also {
                    it.setMargins(SizeHelper.size10, 0, 0, 0)
                }
            holder.itemView.findViewById<LinearLayout>(R.id.layoutMore).visibility = View.VISIBLE
            holder.itemView.findViewById<ConstraintLayout>(R.id.constraintLayout).visibility = View.INVISIBLE

            holder.itemView.layoutMore.setOnClickListener {
                ICheckApplication.currentActivity()?.let { act ->
                    ActivityUtils.startActivity<ListDistributorActivity, String>(act, Constant.DATA_1, url)
                }
            }
        } else {
            holder.itemView.findViewById<LinearLayout>(R.id.layoutMore).visibility = View.GONE
            holder.itemView.findViewById<ConstraintLayout>(R.id.constraintLayout).visibility = View.VISIBLE

            holder.bind(listData[position])
        }
    }

    class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICPage>(LayoutInflater.from(parent.context).inflate(R.layout.item_distributor, parent, false)) {

        var adapter = SocialNetworkAdapter("Grid")

        override fun bind(obj: ICPage) {

            itemView.tvPhone.fillDrawableStartText(R.drawable.ic_list_blue_12dp)
            itemView.tvDangCapNhatSDT.fillDrawableStartText(R.drawable.ic_list_blue_12dp)
            itemView.tvAddress.fillDrawableStartText(R.drawable.ic_list_blue_12dp)
            itemView.tvMST.fillDrawableStartText(R.drawable.ic_list_blue_12dp)

            initListener(obj)

            WidgetUtils.loadImageUrlRounded4(itemView.findViewById<AppCompatImageView>(R.id.imgAvatar), obj.avatar, R.drawable.ic_business_v2, R.drawable.ic_business_v2)

            itemView.findViewById<AppCompatTextView>(R.id.tvNamePage).text = if (obj.name.isNullOrEmpty()) {
                itemView.context.getString(R.string.dang_cap_nhat)
            } else {
                obj.name
            }

//            if (obj.verified == true) {
            itemView.findViewById<View>(R.id.viewBackground).setBackgroundColor(vn.icheck.android.ichecklibs.ColorManager.getPrimaryColor(itemView.context))
            itemView.findViewById<AppCompatImageView>(R.id.imgDetail).setImageResource(R.drawable.ic_arrow_right_white_bg_blue_28px)
//            } else {
//                itemView.findViewById<View>(R.id.viewBackground).setBackgroundResource(R.color.darkGray2)
//                itemView.findViewById<AppCompatImageView>(R.id.imgDetail).setImageResource(R.drawable.ic_arrow_right_white_bg_gray_28px)
//            }

            if (!obj.title.isNullOrEmpty()) {
                itemView.findViewById<AppCompatTextView>(R.id.tvPageCategory).text = obj.title
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
                itemView.findViewById<AppCompatTextView>(R.id.tvAddress).setText(R.string.dang_cap_nhat)
            } else {
                itemView.findViewById<AppCompatTextView>(R.id.tvAddress).text = obj.address
            }

            if (!obj.tax.isNullOrEmpty()) {
                itemView.findViewById<AppCompatTextView>(R.id.tvMST).setText(R.string.ma_so_thue_s, obj.tax!!)
                itemView.tvDangCapNhatMST.visibility = View.GONE
            } else {
                itemView.findViewById<AppCompatTextView>(R.id.tvMST).setText(R.string.ma_so_thue)
                itemView.tvDangCapNhatMST.visibility = View.VISIBLE
            }

            val list = mutableListOf<ICSocialNetworkModel>()

            if (!obj.facebook.isNullOrEmpty()) {
                list.add(ICSocialNetworkModel("Facebook", R.drawable.ic_facebook_20_dp, obj.facebook))
            }

            if (!obj.email.isNullOrEmpty()) {
                list.add(ICSocialNetworkModel("Mail", R.drawable.ic_mail, obj.email))
            }

            if (!obj.website.isNullOrEmpty()) {
                list.add(ICSocialNetworkModel("Website", R.drawable.ic_website, obj.website))
            }

            if (!obj.youtube.isNullOrEmpty()) {
                list.add(ICSocialNetworkModel("Youtube", R.drawable.ic_youtube, obj.youtube))
            }

            if (list.isNullOrEmpty()) {
                itemView.findViewById<RecyclerView>(R.id.recyclerView).visibility = View.INVISIBLE
            } else {
                itemView.findViewById<RecyclerView>(R.id.recyclerView).visibility = View.VISIBLE
                itemView.findViewById<RecyclerView>(R.id.recyclerView).layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                itemView.findViewById<RecyclerView>(R.id.recyclerView).adapter = adapter
                adapter.setData(list)
            }
        }

        inner class MyClickableSpan constructor(var text: String) : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {

            }

            override fun onClick(view: View) {

                    itemView.context.apply {
                    DialogHelper.showConfirm(
                        this,
                        getString(R.string.thong_bao),
                    getString(R.string.ban_co_muon_goi_dien_thoai_den_so_s_nay_khong, text),
                    getString(R.string.huy),
                    getString(R.string.dong_y),
                    true,
                    object : ConfirmDialogListener {
                        override fun onDisagree() {
                        }

                        override fun onAgree() {
                            val intent = Intent(Intent.ACTION_DIAL)
                            intent.data = Uri.parse("tel:${text}")
                            ICheckApplication.currentActivity()?.startActivity(intent)
                        }
                    })}
            }
        }

        private fun initListener(obj: ICPage) {
            itemView.findViewById<AppCompatImageView>(R.id.imgDetail).setOnClickListener {
                startPageDetail(obj)
            }

            itemView.findViewById<View>(R.id.viewBackground).setOnClickListener {
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
//                        obj.phone?.startCallPhone()
//                    }
//                })
//            }
        }

        private fun startPageDetail(obj: ICPage) {
            ICheckApplication.currentActivity()?.let { act ->
                if (obj.pageId != null) {
                    ActivityUtils.startActivity<PageDetailActivity, Long>(act, Constant.DATA_1, obj.pageId!!)
                } else if (obj.owner?.pageId != null) {
                    ActivityUtils.startActivity<PageDetailActivity, Long>(act, Constant.DATA_1, obj.owner?.pageId!!)
                }
            }
        }
    }
}