package vn.icheck.android.component.product.enterprise

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.ItemPageInfoBinding
import vn.icheck.android.ichecklibs.util.beGone
import vn.icheck.android.ichecklibs.util.beVisible
import vn.icheck.android.network.models.ICOwner
import vn.icheck.android.network.models.ICWidgetData
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.util.kotlin.WidgetUtils

class ICPageInfoHolder(parent: ViewGroup, val binding: ItemPageInfoBinding = ItemPageInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)) :
        RecyclerView.ViewHolder(binding.root) {

    fun bind(obj: ICOwner) {
        WidgetUtils.loadImageUrlRounded4(binding.imgAvatar, obj.avatar, R.drawable.ic_business_v2, R.drawable.ic_business_v2)

        binding.tvNamePage.text = if (obj.name.isNullOrEmpty()) {
            binding.root.context.getString(R.string.dang_cap_nhat)
        } else {
            obj.name
        }

        binding.imgDetail.setImageResource(if (obj.background == R.color.colorDisableText) {
            R.drawable.ic_arrow_right_gray_28px
        } else {
            R.drawable.ic_arrow_right_white_bg_blue_28px
        })

        binding.backgroundVerified.setBackgroundColor(ContextCompat.getColor(binding.root.context, obj.background))

        binding.tvNamePage.setCompoundDrawablesWithIntrinsicBounds(0, 0, obj.icon, 0)

        binding.tvPageCategory.text = "Doanh nghiệp sở hữu"

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

        binding.tvAddress.text = vn.icheck.android.ichecklibs.Constant.getAddress(obj.address, obj.district?.name, obj.city?.name, null, null)

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
                beVisible()
            } else {
                beGone()
                layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = SocialNetworkAdapter("Linear").apply {
                    setData(list)
                }
            }
        }

        binding.root.setOnClickListener {
            ICheckApplication.currentActivity()?.let { activity ->
                if (obj.pageId != null && obj.pageId != 0L) {
                    PageDetailActivity.start(activity, obj.pageId!!)
                }
            }
        }
    }

    fun bind(obj: ICWidgetData) {
        WidgetUtils.loadImageUrlRounded4(binding.imgAvatar, obj.avatar, R.drawable.ic_business_v2, R.drawable.ic_business_v2)

        binding.tvNamePage.apply {
            text = obj.name ?: context.getString(R.string.dang_cap_nhat)
            setCompoundDrawablesWithIntrinsicBounds(0, 0, obj.icon, 0)
        }

        binding.imgDetail.setImageResource(if (obj.background == R.color.colorDisableText) {
            R.drawable.ic_arrow_right_gray_28px
        } else {
            R.drawable.ic_arrow_right_white_bg_blue_28px
        })

        binding.backgroundVerified.setBackgroundColor(ContextCompat.getColor(binding.root.context, obj.background))

        binding.tvPageCategory.text = obj.category

        if (!obj.phone.isNullOrEmpty()) {
            binding.tvPhone.visibility = View.VISIBLE
            binding.tvDangCapNhatSDT.visibility = View.GONE

            binding.tvPhone.text = obj.phone
            binding.tvPhone.setOnClickListener {
                Constant.callPhone(obj.phone!!)
            }
        } else {
            binding.tvPhone.visibility = View.GONE
            binding.tvDangCapNhatSDT.visibility = View.VISIBLE
        }

        binding.tvAddress.text = vn.icheck.android.ichecklibs.Constant.getAddress(obj.address, obj.district, obj.city, null, null)

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
                beVisible()
            } else {
                beGone()
                layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = SocialNetworkAdapter("Linear").apply {
                    setData(list)
                }
            }
        }

        binding.root.setOnClickListener {
            ICheckApplication.currentActivity()?.let { activity ->
                if (activity is FragmentActivity) {
                    ICPageInfoDialog.show(activity.supportFragmentManager, obj)
                }
            }
        }
    }
}