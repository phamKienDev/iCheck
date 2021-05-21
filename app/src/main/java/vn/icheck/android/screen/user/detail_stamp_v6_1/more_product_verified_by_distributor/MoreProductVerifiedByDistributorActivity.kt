package vn.icheck.android.screen.user.detail_stamp_v6_1.more_product_verified_by_distributor

import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_more_product_verified_by_distributor.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.detail_stamp_v6_1.ICObjectListMoreProductVerified
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.StampDetailActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.more_product_verified_by_distributor.adapter.MoreProductVerifiedByDistributorAdapter
import vn.icheck.android.screen.user.detail_stamp_v6_1.more_product_verified_by_distributor.presenter.MoreProductVerifiedByDistributorPresenter
import vn.icheck.android.screen.user.detail_stamp_v6_1.more_product_verified_by_distributor.view.IMoreProductVerifiedByDistributorView
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity

class MoreProductVerifiedByDistributorActivity : BaseActivity<MoreProductVerifiedByDistributorPresenter>(), IMoreProductVerifiedByDistributorView {

    override val getLayoutID: Int
        get() = R.layout.activity_more_product_verified_by_distributor

    override val getPresenter: MoreProductVerifiedByDistributorPresenter
        get() = MoreProductVerifiedByDistributorPresenter(this)

    private lateinit var adapter: MoreProductVerifiedByDistributorAdapter

    override fun onInitView() {
        initRecyclerView()
        if (StampDetailActivity.isVietNamLanguage == false){
            txtTitle.text = "Related Product"
        } else {
            txtTitle.text = "Sản phẩm liên quan"
        }

        presenter.getDataIntent(intent)
        listener()
    }

    private fun initRecyclerView() {
        adapter = MoreProductVerifiedByDistributorAdapter(this,StampDetailActivity.isVietNamLanguage)
        val layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        rcvMoreProductVerifiedByDistributor.layoutManager = layoutManager

        val verticalDecoration = DividerItemDecoration(rcvMoreProductVerifiedByDistributor.context, DividerItemDecoration.HORIZONTAL)
        val verticalDivider = ContextCompat.getDrawable(this, R.drawable.vertical_divider_more_business_stamp) as Drawable
        verticalDecoration.setDrawable(verticalDivider)
        rcvMoreProductVerifiedByDistributor.addItemDecoration(verticalDecoration)

        val horizontalDecoration = DividerItemDecoration(rcvMoreProductVerifiedByDistributor.context, DividerItemDecoration.VERTICAL)
        val horizontalDivider = ContextCompat.getDrawable(this, R.drawable.horizontal_divider_more_business_stamp) as Drawable
        horizontalDecoration.setDrawable(horizontalDivider)
        rcvMoreProductVerifiedByDistributor.addItemDecoration(horizontalDecoration)

        rcvMoreProductVerifiedByDistributor.adapter = adapter

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapter.listData.isNullOrEmpty()) {
                    2
                } else {
                    1
                }
            }
        }
    }

    private fun listener() {
        imgBack.setOnClickListener { onBackPressed() }
    }

    override fun onGetDataError(errorType: Int) {
        when (errorType) {
            Constant.ERROR_UNKNOW -> {
                if (StampDetailActivity.isVietNamLanguage == false){
                    adapter.setErrorCode("Occurred. Please try again")
                } else {
                    adapter.setErrorCode(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            }

            Constant.ERROR_EMPTY -> {
                if (StampDetailActivity.isVietNamLanguage == false){
                    adapter.setErrorCode("No Data")
                } else {
                    adapter.setErrorCode(getString(R.string.khong_co_du_lieu))
                }
            }

            Constant.ERROR_INTERNET -> {
                if (StampDetailActivity.isVietNamLanguage == false){
                    adapter.setErrorCode("Checking network. Please try again")
                } else {
                    adapter.setErrorCode(getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
                }
            }
        }
    }

    override fun onGetDataMoreProductVerifiedSuccess(products: MutableList<ICObjectListMoreProductVerified>, isLoadMore: Boolean) {
        if (isLoadMore)
            adapter.addListData(products)
        else
            adapter.setListData(products)
    }

    override fun onLoadMore() {
        presenter.onGetProductVerified(presenter.id, true)
    }

    override fun onClickItem(item: ICObjectListMoreProductVerified) {
        if (!item.sku.isNullOrEmpty()) {
            IckProductDetailActivity.start(this, item.sku!!)
        }
    }

    override fun onRefresh() {
        presenter.getDataIntent(intent)
    }
}
