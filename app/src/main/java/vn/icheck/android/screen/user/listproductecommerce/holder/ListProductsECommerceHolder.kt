package vn.icheck.android.screen.user.listproductecommerce.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.base.adapter.RecyclerViewAdapter
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.ItemListProductsEcommerceBinding
import vn.icheck.android.loyalty.helper.ActivityHelper
import vn.icheck.android.network.models.ICProductECommerce
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.user.listproductecommerce.ListProductsECommerceActivity
import vn.icheck.android.util.ick.visibleOrInvisible

class ListProductsECommerceHolder(parent: ViewGroup, val binding: ItemListProductsEcommerceBinding = ItemListProductsEcommerceBinding.inflate(LayoutInflater.from(parent.context), parent, false)) : BaseViewHolder<MutableList<ICProductECommerce>>(binding.root) {
    private val adapter = object : RecyclerViewAdapter<ICProductECommerce>() {
        override fun getItemCount(): Int {
            return if (listData.size > 3) 3 else listData.size
        }

        override fun viewHolder(parent: ViewGroup) = ProductsECommerceHolder(parent)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is ProductsECommerceHolder) {
                holder.bind(listData[position])
            } else {
                super.onBindViewHolder(holder, position)
            }
        }
    }

    override fun bind(obj: MutableList<ICProductECommerce>) {
        adapter.disableLoading()
        adapter.disableLoadMore()
        binding.recyclerView.adapter = adapter
        adapter.setListData(obj)

        binding.tvViewMore.visibleOrInvisible(adapter.getListData.size > 3)

        binding.tvViewMore.setOnClickListener {
            ICheckApplication.currentActivity()?.let { activity ->
                ActivityHelper.startActivity<ListProductsECommerceActivity>(activity, Constant.DATA_1, JsonHelper.toJson(adapter.getListData))
            }
        }
    }
}