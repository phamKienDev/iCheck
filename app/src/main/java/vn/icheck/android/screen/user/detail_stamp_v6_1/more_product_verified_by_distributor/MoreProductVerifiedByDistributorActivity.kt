package vn.icheck.android.screen.user.detail_stamp_v6_1.more_product_verified_by_distributor

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_more_product_verified_by_distributor.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.constant.Constant
import vn.icheck.android.ichecklibs.DialogHelper
import vn.icheck.android.ichecklibs.util.showLongErrorToast
import vn.icheck.android.network.models.detail_stamp_v6_1.ICObjectListMoreProductVerified
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.StampDetailActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.more_product_verified_by_distributor.adapter.MoreProductVerifiedByDistributorAdapter
import vn.icheck.android.screen.user.detail_stamp_v6_1.more_product_verified_by_distributor.presenter.MoreProductVerifiedByDistributorPresenter
import vn.icheck.android.screen.user.detail_stamp_v6_1.more_product_verified_by_distributor.view.IMoreProductVerifiedByDistributorView
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.util.ick.rText

class MoreProductVerifiedByDistributorActivity : BaseActivityMVVM(), IMoreProductVerifiedByDistributorView {

    val presenter = MoreProductVerifiedByDistributorPresenter(this@MoreProductVerifiedByDistributorActivity)
    private lateinit var adapter: MoreProductVerifiedByDistributorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_product_verified_by_distributor)
        onInitView()
    }

    fun onInitView() {
        initRecyclerView()
        if (StampDetailActivity.isVietNamLanguage == false){
            txtTitle rText R.string.related_product
        } else {
            txtTitle rText R.string.san_pham_lien_quan
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
                    adapter.setErrorCode(rText(R.string.occurred_please_try_again))
                } else {
                    adapter.setErrorCode(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            }

            Constant.ERROR_EMPTY -> {
                if (StampDetailActivity.isVietNamLanguage == false){
                    adapter.setErrorCode(rText(R.string.no_data))
                } else {
                    adapter.setErrorCode(getString(R.string.khong_co_du_lieu))
                }
            }

            Constant.ERROR_INTERNET -> {
                if (StampDetailActivity.isVietNamLanguage == false){
                    adapter.setErrorCode(rText(R.string.checking_network_please_try_again))
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

    override fun showError(errorMessage: String) {
        showLongErrorToast(errorMessage)
    }

    override val mContext: Context
        get() = this@MoreProductVerifiedByDistributorActivity

    override fun onShowLoading(isShow: Boolean) {
        DialogHelper.showLoading(this@MoreProductVerifiedByDistributorActivity, isShow)
    }

    override fun onRefresh() {
        presenter.getDataIntent(intent)
    }
}
