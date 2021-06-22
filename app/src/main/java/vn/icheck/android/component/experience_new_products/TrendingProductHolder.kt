package vn.icheck.android.component.experience_new_products

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.component.experience_new_products.adapter.ExperienceNewProductsAdapter
import vn.icheck.android.component.experience_new_products.adapter.HomeCategoryHorizontalAdapter
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.ACTION_PRODUCT_DETAIL
import vn.icheck.android.databinding.HolderTrendingProductBinding
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.util.ick.getLayoutInflater

class TrendingProductHolder(parent: ViewGroup, val binding: HolderTrendingProductBinding = HolderTrendingProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)):RecyclerView.ViewHolder(binding.root) {
    var mData:ICExperienceNewProducts? = null
    val homeCategoryHorizontalAdapter = HomeCategoryHorizontalAdapter(object : IExperienceNewProducts{
        override fun onClickItemCategory(id: Long) {
            binding.root.context.sendBroadcast(Intent("home").apply {
                putExtra("select_category", id)
            })
            mData?.let {icExperienceNewProducts->
                if (!icExperienceNewProducts.listCategory.isNullOrEmpty()) {
                    val cate = icExperienceNewProducts.listCategory!!.first {
                        it.isSelected
                    }
                    if (!icExperienceNewProducts.listProduct.isNullOrEmpty()) {
                        val obj = icExperienceNewProducts.listProduct!!
                                .filter {
                                    it.categories?.firstOrNull {category ->
                                        category.id == cate.id
                                    } != null
                                }
                                .take(4)
                                .toMutableList()
                        experienceNewProductsAdapter.setData(obj)
                    }
                }
            }
        }
    })
    val experienceNewProductsAdapter = ExperienceNewProductsAdapter()
    fun bind(icExperienceNewProducts: ICExperienceNewProducts) {
        mData = icExperienceNewProducts
        if (!icExperienceNewProducts.listCategory.isNullOrEmpty()) {
            homeCategoryHorizontalAdapter.setData(icExperienceNewProducts.listCategory!!)
            binding.rcvContainer.adapter = homeCategoryHorizontalAdapter
            val cate = icExperienceNewProducts.listCategory!!.first {
                it.isSelected
            }
            if (!icExperienceNewProducts.listProduct.isNullOrEmpty()) {
                val obj = icExperienceNewProducts.listProduct!!
                        .filter {
                            it.categories?.firstOrNull {category ->
                                category.id == cate.id
                            } != null
                        }
                        .take(4)
                        .toMutableList()

                experienceNewProductsAdapter.setData(obj)
                binding.rcvTrendingProduct.adapter = experienceNewProductsAdapter
            }
            binding.tvMore.setCompoundDrawablesWithIntrinsicBounds(null,null,ViewHelper.getDrawableFillColor(R.drawable.ic_arrow_right_light_blue_24dp, ColorManager.getAccentBlueCode),null)
            binding.tvMore.setOnClickListener {
                it.context.sendBroadcast(Intent("home").apply {
                    putExtra("home_action", icExperienceNewProducts.listCategory?.firstOrNull {category ->
                        category.isSelected
                    }?.id ?: 1L)
                })
            }
        }
    }
}