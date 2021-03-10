package vn.icheck.android.component.experience_new_products

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.component.experience_new_products.adapter.ExperienceNewProductsAdapter
import vn.icheck.android.component.experience_new_products.adapter.HomeCategoryHorizontalAdapter
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.product.ProductInteractor
import vn.icheck.android.network.models.ICExperienceCategory
import vn.icheck.android.network.models.ICProduct
import vn.icheck.android.network.models.ICThumbnail
import vn.icheck.android.ui.layout.CustomGridLayoutManager
import vn.icheck.android.util.kotlin.ToastUtils

class ExperienceNewProductsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(ViewHelper.createExperienceNewProducts(parent.context)) {
    private val productInteractor = ProductInteractor()

    lateinit var adapter: ExperienceNewProductsAdapter
    private lateinit var categoryAdapter: HomeCategoryHorizontalAdapter
    private var listener: ExperienceNewProductListener? = null

    fun bind(obj: ICExperienceNewProducts, listener: ExperienceNewProductListener) {
        this.listener = listener

        val parent = itemView as LinearLayout
        val recyclerViewHorizontalAdapter = parent.getChildAt(1) as RecyclerView
        val recyclerView = parent.getChildAt(2) as RecyclerView
        val more = parent.getChildAt(3) as AppCompatTextView

        adapter = ExperienceNewProductsAdapter()

        categoryAdapter = HomeCategoryHorizontalAdapter(object : IExperienceNewProducts {
            override fun onClickItemCategory(id: Long) {
                getListProduct(id, obj)
            }
        })

        more.setOnClickListener {
            ToastUtils.showLongWarning(itemView.context, "onClickViewAll")
        }
        recyclerViewHorizontalAdapter.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewHorizontalAdapter.adapter = categoryAdapter
        categoryAdapter.setData(obj.listCategory!!)

        recyclerView.layoutManager = CustomGridLayoutManager(itemView.context, 2).also {
            it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (adapter.isEmpty()) {
                        2
                    } else {
                        1
                    }
                }
            }
        }
        recyclerView.adapter = adapter

        if (!obj.listProduct.isNullOrEmpty()) {
            adapter.setData(obj.listProduct!!)
        } else {
            getListProduct(obj.listCategory!![0].id, obj)
        }
    }

    private fun getListProduct(categoryID: Long, baseObj: ICExperienceNewProducts) {
        if (NetworkHelper.isNotConnected(itemView.context)) {
            adapter.setError(itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))

            return
        }
        productInteractor.getProductCategory(categoryID, object : ICNewApiListener<ICResponse<ICListResponse<ICProduct>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICProduct>>) {
                    adapter.setData(obj.data?.rows!!)

                    baseObj.listProduct = obj.data?.rows

            }

            override fun onError(error: ICResponseCode?) {
                adapter.setError(itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    interface ExperienceNewProductListener {
        fun onRemoveView()
    }
}