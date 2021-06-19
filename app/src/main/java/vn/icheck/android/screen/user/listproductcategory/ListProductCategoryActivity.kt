package vn.icheck.android.screen.user.listproductcategory

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_list_product_category.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.util.showLongErrorToast
import vn.icheck.android.network.models.ICCategory
import vn.icheck.android.network.models.ICProduct
import vn.icheck.android.screen.user.listproductcategory.adapter.ListProductCategoryAdapter
import vn.icheck.android.screen.user.listproductcategory.presenter.ListProductCategoryPresenter
import vn.icheck.android.screen.user.listproductcategory.view.IListProductCategoryView
import vn.icheck.android.screen.user.mall.adapter.MallCategoryHorizontalAdapter

/**
 * Phạm Hoàng Phi Hùng
 * 0974815770
 * hungphp@icheck.vn
 */
class ListProductCategoryActivity : BaseActivityMVVM(), IListProductCategoryView {
    private val adapter = ListProductCategoryAdapter(this)
    private val categoryHorizontalAdapter = MallCategoryHorizontalAdapter()
    private val presenter = ListProductCategoryPresenter(this@ListProductCategoryActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_product_category)
        onInitView()
    }

    fun onInitView() {
        setupToolBar()
        setupRecyclerView()
        setupSwipeLayout()
    }

    private fun setupToolBar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        val verticalDecoration = DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL)
        val verticalDivider = ContextCompat.getDrawable(this, R.drawable.vertical_divider_more_business_stamp) as Drawable
        verticalDecoration.setDrawable(verticalDivider)
        recyclerView.addItemDecoration(verticalDecoration)

        val horizontalDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        val horizontalDivider = ContextCompat.getDrawable(this, R.drawable.horizontal_divider_more_business_stamp) as Drawable
        horizontalDecoration.setDrawable(horizontalDivider)
        recyclerView.addItemDecoration(horizontalDecoration)

        (recyclerView.layoutManager as GridLayoutManager).spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapter.isEmpty()) {
                    2
                } else {
                    if (adapter.setLoadMore(position)) {
                        2
                    } else {
                        1
                    }
                }
            }
        }
        recyclerView.adapter = adapter
    }

    private fun setupSwipeLayout() {
        swipe.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorSecondary), ContextCompat.getColor(this, R.color.colorSecondary), ContextCompat.getColor(this, R.color.colorPrimary))

        swipe.setOnRefreshListener {
            adapter.disableLoadMore()
            getData()
        }

        swipe.post {
            swipe.isRefreshing = true
            presenter.getCollectionID(intent)
        }
    }

    private fun getData() {
        swipe.isRefreshing = true
        presenter.checkInternet()
    }

    override fun onGetCollectionIDError() {
        swipe.isRefreshing = false

        DialogHelper.showNotification(this, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
            override fun onDone() {
                onBackPressed()
            }
        })
    }

    override fun onGetDataError(error: String) {
        swipe.isRefreshing = false

        if (adapter.isEmpty()) {
            adapter.setError(error)
        } else {
            showLongError(error)
        }
    }

    override fun onGetProductSuccess(obj: MutableList<ICProduct>, isLoadMore: Boolean) {
        swipe.isRefreshing = false

        if (!isLoadMore) {
            adapter.setData(obj)
        } else {
            adapter.addData(obj)
        }
    }

    override fun onLoadMore() {
        presenter.getProduct(true)
    }

    override fun onGetDataTryAgain() {
        getData()
    }

    override fun onGetListCategorySuccess(obj: MutableList<ICCategory>) {
        if (obj.isNullOrEmpty()) {
            recyclerViewHorizontal.visibility = View.GONE
        } else {
            recyclerViewHorizontal.visibility = View.VISIBLE
            recyclerViewHorizontal.adapter = categoryHorizontalAdapter
            categoryHorizontalAdapter.setData(obj)
        }
    }

    override fun showError(errorMessage: String) {
        showLongErrorToast(errorMessage)
    }

    override val mContext: Context
        get() = this@ListProductCategoryActivity

    override fun onShowLoading(isShow: Boolean) {
        vn.icheck.android.ichecklibs.DialogHelper.showLoading(this@ListProductCategoryActivity, isShow)
    }
}
