package vn.icheck.android.screen.user.listproductecommerce

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_list_products_ecommerce.*
import kotlinx.android.synthetic.main.toolbar_light_blue.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.adapter.RecyclerViewAdapter
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.model.ICError
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.base.Status
import vn.icheck.android.network.models.ICProductECommerce
import vn.icheck.android.screen.user.listproductecommerce.viewmodel.ListProductsECommerceViewModel
import vn.icheck.android.screen.user.listproductecommerce.holder.ProductsECommerceHolder

class ListProductsECommerceActivity : BaseActivityMVVM(), IRecyclerViewCallback {
    private val viewModel: ListProductsECommerceViewModel by viewModels()

    private lateinit var adapter: RecyclerViewAdapter<ICProductECommerce>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_products_ecommerce)

        setupToolbar()
        setupRecyclerView()
        getBarcode()
    }

    private fun setupToolbar() {
        imgBack.setOnClickListener {
            finishActivity()
        }

        txtTitle.setText(R.string.thuong_mai_dien_tu)
    }

    private fun setupRecyclerView() {
        adapter = object : RecyclerViewAdapter<ICProductECommerce>(this) {
            override fun viewHolder(parent: ViewGroup) = ProductsECommerceHolder(parent)

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                if (holder is ProductsECommerceHolder) {
                    holder.bind(listData[position])
                } else {
                    super.onBindViewHolder(holder, position)
                }
            }
        }
        adapter.disableLoading()
        adapter.disableLoadMore()

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun getBarcode() {
        viewModel.barcode = intent.getStringExtra(Constant.DATA_1) ?: ""

        if (viewModel.barcode.isNotEmpty()) {
            setupSwipeLayout()
        } else {
            DialogHelper.showNotification(this@ListProductsECommerceActivity, R.string.co_loi_xay_ra_vui_long_thu_lai, null, false, object : NotificationDialogListener {
                override fun onDone() {
                    finishActivity()
                }
            })
        }
    }

    private fun setupSwipeLayout() {
        swipeLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.blue), ContextCompat.getColor(this, R.color.blue), ContextCompat.getColor(this, R.color.lightBlue))

        swipeLayout.setOnRefreshListener {
            getListProductsECommers()
        }

        swipeLayout.post {
            getListProductsECommers()
        }
    }

    private fun getListProductsECommers() {
        viewModel.getProductsECommerce().observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                    swipeLayout.isRefreshing = it.message.isNullOrEmpty()
                }
                Status.ERROR_NETWORK -> {
                    showError(ICError(R.drawable.ic_no_network, ICheckApplication.getError(it.message), null))
                }
                Status.ERROR_REQUEST -> {
                    showError(ICError(R.drawable.ic_error_request, ICheckApplication.getError(it.message), null))
                }
                Status.SUCCESS -> {
                    if (!it.data?.data?.rows.isNullOrEmpty()) {
                        adapter.setListData(it.data!!.data!!.rows)
                    } else {
                        adapter.setError(R.drawable.ic_error_request, getString(R.string.khong_co_du_lieu), 0)
                    }
                }
            }
        })
    }

    private fun showError(obj: ICError) {
        if (adapter.isNotEmpty) {
            showLongError(obj.message!!)
        } else {
            adapter.setError(obj)
        }
    }

    override fun onLoadMore() {

    }

    override fun onMessageClicked() {
        getListProductsECommers()
    }
}