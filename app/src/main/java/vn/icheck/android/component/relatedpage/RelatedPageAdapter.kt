package vn.icheck.android.component.relatedpage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_city.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.ItemRelatedPageBinding
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.helper.TextHelper.setDrawbleNextEndText
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.ICRelatedPage
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class RelatedPageAdapter() : RecyclerView.Adapter<RelatedPageAdapter.ViewHolder>() {
    private val listData = mutableListOf<ICRelatedPage>()

    fun setData(list: MutableList<ICRelatedPage>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(ItemRelatedPageBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ViewHolder(val binding: ItemRelatedPageBinding) : BaseViewHolder<ICRelatedPage>(binding.root) {

        override fun bind(obj: ICRelatedPage) {
            WidgetUtils.loadImageUrlRounded(binding.imgBanner, obj.corver, R.drawable.ic_default_horizontal)

            WidgetUtils.loadImageUrl(binding.imgAvatar, obj.avatar, R.drawable.ic_business_v2)

            binding.tvName.text = obj.name
            if (obj.isVerify) {
                binding.tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_16px, 0)
            } else {
                binding.tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }

            if (obj.followCount > 0) {
                binding.tvFollowCount.text = TextHelper.formatMoneyPhay(obj.followCount) + " Người theo dõi"
            }

            binding.tvAction.apply {
                background = ViewHelper.bgOutlinePrimary1Corners4(context)
                setOnClickListener {
                    ICheckApplication.currentActivity()?.let { act ->
                        ActivityUtils.startActivity<PageDetailActivity, Long>(act, Constant.DATA_1, obj.id)
                    }
                }
            }
        }

    }
}