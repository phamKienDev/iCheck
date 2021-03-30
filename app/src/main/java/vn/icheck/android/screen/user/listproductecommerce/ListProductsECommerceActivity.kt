package vn.icheck.android.screen.user.listproductecommerce

import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_list_products_ecommerce.*
import kotlinx.android.synthetic.main.toolbar_light_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.adapter.RecyclerViewAdapter
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.holder.StampECommerceHolder
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.models.ICProductECommerce
import vn.icheck.android.network.models.ICProductLink
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.user.listproductecommerce.holder.ProductsECommerceHolder

class ListProductsECommerceActivity : BaseActivityMVVM() {
//    private val viewModel: ListProductsECommerceViewModel by viewModels()

//    private lateinit var adapter: RecyclerViewAdapter<ICProductECommerce>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_products_ecommerce)

        setupToolbar()
        setupRecyclerView()
        getData()
    }

    private fun setupToolbar() {
        imgBack.setOnClickListener {
            finishActivity()
        }

        txtTitle.setText(R.string.thuong_mai_dien_tu)
    }

    private fun setupRecyclerView() {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun getData() {
        val json = intent.getStringExtra(Constant.DATA_1) ?: ""
        val isProductECommerce = intent.getBooleanExtra(Constant.DATA_2, false)

        if (isProductECommerce) {
            val listECommerce = JsonHelper.parseProductECommerce(json)
            if (!listECommerce.isNullOrEmpty()) {
                val adapter = object : RecyclerViewAdapter<ICProductECommerce>() {
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
                recyclerView.adapter = adapter
                adapter.setListData(listECommerce)
                return
            }
        } else {
            val listProductLink = JsonHelper.parseStampECommerce(json)
            if (!listProductLink.isNullOrEmpty()) {
                val adapter = object : RecyclerViewAdapter<ICProductLink>() {
                    override fun viewHolder(parent: ViewGroup) = StampECommerceHolder(parent)

                    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                        if (holder is StampECommerceHolder) {
                            holder.bind(listData[position])
                        } else {
                            super.onBindViewHolder(holder, position)
                        }
                    }
                }
                adapter.disableLoading()
                adapter.disableLoadMore()
                recyclerView.adapter = adapter
                adapter.setListData(listProductLink)
                return
            }
        }

        DialogHelper.showNotification(this@ListProductsECommerceActivity, R.string.co_loi_xay_ra_vui_long_thu_lai, null, false, object : NotificationDialogListener {
            override fun onDone() {
                finishActivity()
            }
        })
    }
}