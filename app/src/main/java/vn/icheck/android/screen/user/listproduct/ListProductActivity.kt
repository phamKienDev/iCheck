package vn.icheck.android.screen.user.listproduct

import android.app.Activity
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_list_product.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.models.ICProduct
import vn.icheck.android.network.models.ICProductTrend
import vn.icheck.android.screen.user.listproduct.adapter.ListProductAdapter
import vn.icheck.android.screen.user.listproduct.presenter.ListProductPresenter
import vn.icheck.android.screen.user.listproduct.view.IListProductView
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity

/**
 * Created by VuLCL on 10/23/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class ListProductActivity : BaseActivity<ListProductPresenter>(), IListProductView {
    private val adapter = ListProductAdapter(this)

    companion object {

        /**
         * @param url - Request Api (GET)
         * @param hashMap - VD collection_id, page_id, ko cần truyền offset và limit
         * @param name - Tiêu đề của màn
         * @param activity - Ngữ cảnh hiện tại
         */
        fun start(activity: Activity?, url: String?, hashMap: HashMap<String, Any>?, name: String? = null) {
            if (url.isNullOrEmpty()) {
                return
            }

            val intent = Intent(activity, ListProductActivity::class.java)
            intent.putExtra(Constant.DATA_1, url)

            if (!hashMap.isNullOrEmpty()) {
                intent.putExtra(Constant.DATA_2, hashMap)
            }

            if (!name.isNullOrEmpty()) {
                intent.putExtra(Constant.DATA_3, name)
            }

            activity?.startActivity(intent)
            activity?.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
        }
    }

    override val getLayoutID: Int
        get() = R.layout.activity_list_product

    override val getPresenter: ListProductPresenter
        get() = ListProductPresenter(this)

    override fun onInitView() {
        initToolbar()
        setupRecyclerView()
        initSwipeLayout()

        presenter.getData(intent)
    }

    /**
     * Hiển thị tiêu đề của bản tin và lắng nghe người dùng nhấn quay lại
     */
    private fun initToolbar() {
        txtTitle.setText(R.string.danh_sach_san_pham_title)

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    /**
     * Lắng nghe người dùng refresh data
     */
    private fun initSwipeLayout() {
        swipeLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.blue), ContextCompat.getColor(this, R.color.blue), ContextCompat.getColor(this, R.color.lightBlue))

        swipeLayout.isRefreshing = true

        swipeLayout.setOnRefreshListener {
            getListProduct()
        }
    }

    /**
     * Khởi tạo listener hiển thị danh sách
     */
    private fun setupRecyclerView() {
        val layoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

//        val verticalDecoration = DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL)
//        val verticalDivider = ContextCompat.getDrawable(this, R.drawable.vertical_divider_more_business_stamp) as Drawable
//        verticalDecoration.setDrawable(verticalDivider)
//        recyclerView.addItemDecoration(verticalDecoration)
//
//        val horizontalDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
//        val horizontalDivider = ContextCompat.getDrawable(this, R.drawable.horizontal_divider_more_business_stamp) as Drawable
//        horizontalDecoration.setDrawable(horizontalDivider)
//        recyclerView.addItemDecoration(horizontalDecoration)

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position != adapter.totalItem) {
                    1
                } else {
                    2
                }
            }
        }
    }

    /**
     * Lấy danh sách sản phẩm
     */
    private fun getListProduct() {
        swipeLayout.isRefreshing = true
        adapter.disableLoadMore()
        presenter.getListProduct(false)
    }

    override fun onSetCollectionName(name: String) {
        txtTitle.text = name
    }

    /**
     * Thông báo lỗi khi không lấy được Collection ID truyền sang
     */
    override fun onGetCollectionIDError() {
        swipeLayout.isRefreshing = false

        DialogHelper.showNotification(this, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
            override fun onDone() {
                onBackPressed()
            }
        })
    }

    /**
     * Thông báo lỗi khi không lấy được danh sách sản phẩm
     * @param error Nội dung thông báo
     */
    override fun onGetProductError(error: String) {
        swipeLayout.isRefreshing = false

        if (adapter.isEmpty) {
            adapter.setError(error)
        } else {
            showLongError(error)
        }
    }

    /**
     * Hiểm thị danh sách sản phẩm
     * @param list Danh sách sản phẩm
     */
    override fun onGetProductSuccess(list: MutableList<ICProductTrend>, isLoadMore: Boolean) {
        swipeLayout.isRefreshing = false

        if (isLoadMore) {
            adapter.addData(list)
        } else {
            adapter.setData(list)
        }
    }

    /**
     * Tải về thêm sản phẩm
     */
    override fun onLoadMoreProduct() {
        presenter.getListProduct(true)
    }

    override fun onLayoutMessageClicked() {
        getListProduct()
    }

    override fun onProductClicked(product: ICProduct) {
        if (!product.barcode.isNullOrEmpty()) {
            IckProductDetailActivity.start(this, product.barcode!!)
        }
    }
}