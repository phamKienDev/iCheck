package vn.icheck.android.component.page_products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.databinding.HolderPageHorizontalProductBinding
import vn.icheck.android.databinding.ItemProductBinding
import vn.icheck.android.model.page.PageProductViewModel
import vn.icheck.android.model.page.ProductItem
import vn.icheck.android.util.ick.*

class PageHorizontalProductHolder(
        private val binding: HolderPageHorizontalProductBinding,
        val recycledViewPool: RecyclerView.RecycledViewPool?
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.rcvHorizontal.setRecycledViewPool(recycledViewPool)
    }

    fun bind(pageProductViewModel: PageProductViewModel) {
        binding.rcvHorizontal.adapter = HorizontalProductAdapter(pageProductViewModel.listProduct)
    }

    companion object {
        fun create(parent: ViewGroup, recycledViewPool: RecyclerView.RecycledViewPool?): PageHorizontalProductHolder {
            val binding = HolderPageHorizontalProductBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
            )
            return PageHorizontalProductHolder(binding, recycledViewPool)
        }
    }

    class HorizontalProductAdapter(val data: List<ProductItem?>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ProductHolder(ItemProductBinding.inflate(LayoutInflater.from(parent.context)))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as ProductHolder).bindProduct(data[position])
        }

        override fun getItemCount(): Int {
            return data.size
        }

        class ProductHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bindProduct(item: ProductItem?) {
                binding.imgProduct loadSimpleImage item?.media?.firstOrNull()?.content
                binding.tvProductName simpleText  item?.name
                binding.tvPrice priceText  item?.price
                binding.tvRate simpleText item?.reviewCount?.toString()?.getInfo()
                binding.imgVerified visibleIf item?.verified
            }
        }
    }
}