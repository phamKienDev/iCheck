package vn.icheck.android.screen.user.page_details.fragment.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_product.*
import kotlinx.android.synthetic.main.fragment_product.imgBack
import kotlinx.android.synthetic.main.fragment_product.recyclerView
import kotlinx.android.synthetic.main.fragment_product.swipeLayout
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.screen.user.page_details.PageDetailViewModel
import vn.icheck.android.util.kotlin.ToastUtils

/**
 * Phạm Hoàng Phi Hùng
 * 0974815770
 * hungphp@icheck.vn
 */
class ProductOfPageFragment : BaseFragmentMVVM() {
    private val viewModel: PageDetailViewModel by activityViewModels()
    private val adapter = ProductOfPageAdapter()

    companion object {
        var INSTANCE: ProductOfPageFragment? = null

        fun newInstance(): ProductOfPageFragment {
            return ProductOfPageFragment()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        INSTANCE = this

        initToolbar()
        initRecyclerView()
        initSwipeLayout()
        initListener()
    }

    private fun initToolbar() {
        imgBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
    }

    private fun initSwipeLayout() {
        swipeLayout.setOnRefreshListener {
            adapter.resetData()
            getData()
        }
        activity?.let {
            val primaryColor = vn.icheck.android.ichecklibs.Constant.getPrimaryColor(it)
            swipeLayout.setColorSchemeColors(primaryColor, primaryColor, primaryColor)
        }

        swipeLayout.post {
            getData()
        }
    }

    fun getData() {
        swipeLayout.isRefreshing = true
        viewModel.countError = 0
        viewModel.getHigtlightProducts()
        viewModel.getCategoriesProduct()
    }

    private fun initListener() {
        viewModel.onErrorProduct.observe(viewLifecycleOwner) {
            swipeLayout.isRefreshing = false
            if (adapter.isEmpty()) {
                it.message?.let { it1 -> adapter.setError(it.icon, it1) }
            } else {
                ToastUtils.showLongError(context, it.message)
            }
        }

        viewModel.addHorizontal.observe(viewLifecycleOwner) {
            swipeLayout.isRefreshing = false
            adapter.addHorizontalProduct(it)
        }

        viewModel.addVertical.observe(viewLifecycleOwner) {
            swipeLayout.isRefreshing = false
            adapter.addVerticalProduct(it)
        }

        viewModel.onPageName.observe(viewLifecycleOwner) {
            txtTitle.text = it
        }
    }

}