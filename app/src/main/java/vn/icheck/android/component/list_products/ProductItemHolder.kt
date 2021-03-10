package vn.icheck.android.component.list_products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.databinding.ItemProductBinding
import vn.icheck.android.model.page.ProductItem

class ProductItemHolder(val binding:ItemProductBinding):RecyclerView.ViewHolder(binding.root) {

    fun bind(productItem: ProductItem) {
        binding.imgProduct
    }

    companion object{
        fun create(parent: ViewGroup) :ProductItemHolder{
            val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ProductItemHolder(binding)
        }
    }
}