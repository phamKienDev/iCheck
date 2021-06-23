package vn.icheck.android.component.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.databinding.ItemRecyclerviewWithTitleBinding
import vn.icheck.android.network.models.ICNews
import vn.icheck.android.screen.user.newslistv2.NewsListV2Activity
import vn.icheck.android.util.kotlin.ActivityUtils

class NewsHolder(parent: ViewGroup, val binding: ItemRecyclerviewWithTitleBinding = ItemRecyclerviewWithTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false)) : BaseViewHolder<MutableList<ICNews>>(binding.root) {

    override fun bind(obj: MutableList<ICNews>) {
        binding.tvTitle.setText(R.string.tin_hay_ngay_moi)

        binding.btnMore.setOnClickListener {
            ICheckApplication.currentActivity()?.let { act ->
                ActivityUtils.startActivity<NewsListV2Activity>(act)
            }
        }

        binding.recyclerView.apply {
            onFlingListener = null
            layoutManager = LinearLayoutManager(itemView.context.applicationContext, LinearLayoutManager.HORIZONTAL, false)
            adapter = NewsAdapter(obj)
        }
    }
}