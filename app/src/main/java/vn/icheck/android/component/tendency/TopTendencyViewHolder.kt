package vn.icheck.android.component.tendency

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.tendency.adapter.TopTendencyCategoryAdapter
import vn.icheck.android.component.tendency.adapter.TopTendencyPageAdapter
import vn.icheck.android.component.tendency.adapter.TopTendencyProductAdapter
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.product.ProductInteractor
import vn.icheck.android.network.models.ICExperienceCategory
import vn.icheck.android.network.models.ICPageTrend
import vn.icheck.android.network.models.ICProductTrend

class TopTendencyViewHolder(parent: ViewGroup) : BaseViewHolder<ICTopTrend>(ViewHelper.createTopTendency(parent.context)) {
    private val productInteraction = ProductInteractor()

    lateinit var parent: LinearLayout
    lateinit var recyclerViewHorizontal: RecyclerView
    lateinit var recyclerView: RecyclerView

    lateinit var adapter: TopTendencyProductAdapter
    lateinit var adapterBusiness: TopTendencyPageAdapter
    lateinit var categoryAdapter: TopTendencyCategoryAdapter

    override fun bind(obj: ICTopTrend) {
        parent = itemView as LinearLayout
        recyclerViewHorizontal = parent.getChildAt(1) as RecyclerView
        recyclerView = parent.getChildAt(2) as RecyclerView

        adapter = TopTendencyProductAdapter()

        categoryAdapter = TopTendencyCategoryAdapter(object : ITopTendencyListener {
            override fun onClickItemCategory(name: String) {
                when (name) {
                    "Sản phẩm" -> {
                        getProductsTrend(obj)
                    }
                    "Doanh nghiệp" -> {
                        adapterBusiness = TopTendencyPageAdapter()

                        getPagesTrend(obj)
                    }
                    else -> {
                        adapterBusiness = TopTendencyPageAdapter()

                        getExpertsTrend(obj)
                    }
                }
            }
        })

        recyclerViewHorizontal.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewHorizontal.adapter = categoryAdapter
        categoryAdapter.setData(obj.listCategory!!)

        recyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)

        if (!obj.listProduct.isNullOrEmpty()) {
            adapter.setData(obj.listProduct!!)
        } else {
            getProductsTrend(obj)
        }
    }

    private fun getProductsTrend(data: ICTopTrend) {
        recyclerView.adapter = adapter

        if (NetworkHelper.isNotConnected(itemView.context)) {
            adapter.setError(itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        productInteraction.getProductsTrend(object : ICNewApiListener<ICResponse<ICListResponse<ICProductTrend>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICProductTrend>>) {
                if (!obj.data?.rows.isNullOrEmpty()) {
                    adapter.setData(obj.data?.rows!!)

                    data.listProduct = obj.data?.rows
                }
            }

            override fun onError(error: ICResponseCode?) {
                adapter.setError(itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    private fun getPagesTrend(data: ICTopTrend) {
        recyclerView.adapter = adapterBusiness

        if (NetworkHelper.isNotConnected(itemView.context)) {
            adapterBusiness.setError(itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        productInteraction.getPagesTrend(object : ICNewApiListener<ICResponse<ICListResponse<ICPageTrend>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICPageTrend>>) {
                if (!obj.data?.rows.isNullOrEmpty()) {
                    adapterBusiness.setData(obj.data?.rows!!)

                    data.listPageTrend = obj.data?.rows
                }
            }

            override fun onError(error: ICResponseCode?) {
                adapterBusiness.setError(itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    private fun getExpertsTrend(data: ICTopTrend){
        recyclerView.adapter = adapterBusiness

        if (NetworkHelper.isNotConnected(itemView.context)) {
            adapterBusiness.setError(itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        productInteraction.getExpertsTrend(object : ICNewApiListener<ICResponse<ICListResponse<ICPageTrend>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICPageTrend>>) {
                if (!obj.data?.rows.isNullOrEmpty()) {
                    adapterBusiness.setData(obj.data?.rows!!)

                    data.listPageTrend = obj.data?.rows
                }
            }

            override fun onError(error: ICResponseCode?) {
                adapterBusiness.setError(itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }
}