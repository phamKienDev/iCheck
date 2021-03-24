package vn.icheck.android.base.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.base.adapter.RecyclerViewAdapter
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.ItemListProductsEcommerceBinding
import vn.icheck.android.loyalty.helper.ActivityHelper
import vn.icheck.android.network.models.ICProductLink
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.user.listproductecommerce.ListProductsECommerceActivity

class ListStampECommerceHolder(parent: ViewGroup, val binding: ItemListProductsEcommerceBinding = ItemListProductsEcommerceBinding.inflate(LayoutInflater.from(parent.context), parent, false)) : BaseViewHolder<MutableList<ICProductLink>>(binding.root) {
    private val adapter = object : RecyclerViewAdapter<ICProductLink>() {
        override fun getItemCount(): Int {
            return if (listData.size > 3) 3 else listData.size
        }

        override fun viewHolder(parent: ViewGroup) = StampECommerceHolder(parent)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is StampECommerceHolder) {
                holder.bind(listData[position])
            } else {
                super.onBindViewHolder(holder, position)
            }
        }
    }

    override fun bind(obj: MutableList<ICProductLink>) {
        adapter.disableLoading()
        adapter.disableLoadMore()
        binding.recyclerView.adapter = adapter
        adapter.setListData(obj)

        binding.tvViewMore.setOnClickListener {
            ICheckApplication.currentActivity()?.let { activity ->
                ActivityHelper.startActivity<ListProductsECommerceActivity>(activity, Constant.DATA_1, JsonHelper.toJson(adapter.getListData))
            }
        }
    }
}