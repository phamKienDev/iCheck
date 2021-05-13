package vn.icheck.android.screen.user.detail_stamp_v6_1.home.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.databinding.ItemProductImageBinding
import vn.icheck.android.ichecklibs.beGone
import vn.icheck.android.ichecklibs.beVisible
import vn.icheck.android.network.models.ICMedia
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.adapter.BannerAdapter

class ICProductImageHolder(parent: ViewGroup, val binding: ItemProductImageBinding = ItemProductImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)) : BaseViewHolder<List<ICMedia>>(binding.root) {
    private val adapter = BannerAdapter()

    override fun bind(obj: List<ICMedia>) {
        binding.viewPager.adapter = adapter
        adapter.setListData(obj)

        binding.indicator.apply {
            if (obj.size > 1) {
                beVisible()
                setupViewPager(binding.viewPager, obj.size)
            } else {
                beGone()
            }
        }
    }
}