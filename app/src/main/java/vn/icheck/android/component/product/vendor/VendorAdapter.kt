package vn.icheck.android.component.product.vendor

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.component.product.enterprise.ICSocialNetworkModel
import vn.icheck.android.component.product.enterprise.SocialNetworkAdapter
import vn.icheck.android.databinding.ItemPageInfoBinding
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.beGone
import vn.icheck.android.ichecklibs.beVisible
import vn.icheck.android.network.models.ICPage
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.util.kotlin.WidgetUtils

class VendorAdapter(val listData: List<ICPage>) : RecyclerView.Adapter<VendorAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VendorAdapter.ViewHolder {
        return ViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: VendorAdapter.ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ViewHolder(parent: ViewGroup, val binding: ItemPageInfoBinding = ItemPageInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)) : RecyclerView.ViewHolder(binding.root) {

        fun bind(obj: ICPage) {
            if (listData.size < 2) {
                binding.container.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
            } else {
                binding.container.layoutParams = RecyclerView.LayoutParams(SizeHelper.size331, RecyclerView.LayoutParams.WRAP_CONTENT).also {
                    it.setMargins(SizeHelper.size10, 0, 0, 0)
                }
            }

            WidgetUtils.loadImageUrlRounded4(binding.imgAvatar, obj.avatar, R.drawable.ic_business_v2, R.drawable.ic_business_v2)

            binding.tvNamePage.apply {
                text = obj.name
                setCompoundDrawablesWithIntrinsicBounds(0, 0, obj.icon, 0)
            }

            binding.imgDetail.setImageResource(if (obj.background == R.color.colorDisableText) {
                R.drawable.ic_arrow_right_gray_28px
            } else {
                R.drawable.ic_arrow_right_white_bg_blue_28px
            })

            binding.backgroundVerified.setBackgroundColor(ContextCompat.getColor(binding.root.context, obj.background))

            binding.tvPageCategory.text = obj.title

            if (!obj.phone.isNullOrEmpty()) {
                binding.tvPhone.visibility = View.VISIBLE
                binding.tvDangCapNhatSDT.visibility = View.GONE

                binding.tvPhone.text = obj.phone
                binding.tvPhone.setOnClickListener {
                    ICheckApplication.currentActivity()?.startActivity(Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${obj.phone}")
                    })
                }
            } else {
                binding.tvPhone.visibility = View.GONE
                binding.tvDangCapNhatSDT.visibility = View.VISIBLE
            }

            binding.tvAddress.text = obj.address

            if (!obj.tax.isNullOrEmpty()) {
                binding.tvMST.text = "Mã số thuế: " + obj.tax
                binding.tvDangCapNhatMST.visibility = View.GONE
            } else {
                binding.tvMST.text = "Mã số thuế: "
                binding.tvDangCapNhatMST.visibility = View.VISIBLE
            }

            val list = mutableListOf<ICSocialNetworkModel>().apply {
                if (!obj.facebook.isNullOrEmpty()) {
                    add(ICSocialNetworkModel("Facebook", R.drawable.ic_facebook_20_dp, obj.facebook))
                }

                if (!obj.email.isNullOrEmpty()) {
                    add(ICSocialNetworkModel("Mail", R.drawable.ic_mail, obj.email))
                }

                if (!obj.website.isNullOrEmpty()) {
                    add(ICSocialNetworkModel("Website", R.drawable.ic_website, obj.website))
                }

                if (!obj.youtube.isNullOrEmpty()) {
                    add(ICSocialNetworkModel("Youtube", R.drawable.ic_youtube, obj.youtube))
                }
            }

            binding.recyclerView.apply {
                if (list.isNullOrEmpty()) {
                    beGone()
                } else {
                    beVisible()
                    layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                    adapter = SocialNetworkAdapter("Linear").apply {
                        setData(list)
                    }
                }
            }

            binding.root.setOnClickListener {
                val pageID = obj.pageId ?: obj.owner?.pageId
                if (pageID != null && pageID != 0L) {
                    ICheckApplication.currentActivity()?.let { activity ->
                        PageDetailActivity.start(activity, pageID)
                    }
                }
            }
        }
    }
}