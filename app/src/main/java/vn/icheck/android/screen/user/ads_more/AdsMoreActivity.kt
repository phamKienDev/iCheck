package vn.icheck.android.screen.user.ads_more

import android.graphics.drawable.Drawable
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_ads_more.*
import kotlinx.android.synthetic.main.toolbar_light_blue.*
import vn.icheck.android.R
import vn.icheck.android.component.ads.product.AdsProductAdapter
import vn.icheck.android.component.view.ViewHelper.setScrollSpeed
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.ExoPlayerManager
import vn.icheck.android.loyalty.base.activity.BaseActivityGame

class AdsMoreActivity : BaseActivityGame() {

    private val viewModel by viewModels<AdsMoreViewModel>()
    private val productAdapter = AdsProductAdapter()

    private var verticalDecoration : DividerItemDecoration? = null
    private var horizontalDecoration : DividerItemDecoration? = null

    override val getLayoutID: Int
        get() = R.layout.activity_ads_more

    override fun onInitView() {
        getData()
        initToolbar()
        initRecyclerView()
        initSwipeLayout()
        initListener()
    }

    private fun initSwipeLayout() {
        swipeLayout.setOnRefreshListener {
            productAdapter.clearData()
            getData()
        }
    }

    private fun initToolbar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initRecyclerView() {
        recyclerView.setHasFixedSize(true)
        recyclerView.setScrollSpeed()
        recyclerView.layoutManager = if (viewModel.adsModel?.type == Constant.HORIZONTAL) {
            LinearLayoutManager(this@AdsMoreActivity, LinearLayoutManager.VERTICAL, false)
        } else {
            GridLayoutManager(this@AdsMoreActivity, 2, GridLayoutManager.VERTICAL, false)
        }
        recyclerView.adapter = productAdapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    ExoPlayerManager.checkPlayVideoBase(recyclerView)
                }
            }
        })
    }

    private fun initListener() {
        viewModel.setError.observe(this@AdsMoreActivity, {
            swipeLayout.isRefreshing = false
            showLongError(it)
        })

        viewModel.setData.observe(this@AdsMoreActivity, {
            swipeLayout.isRefreshing = false
            txtTitle.text = it.name

            if(it.objectType.contains(Constant.PAGE)){

            }else if(it.objectType.contains(Constant.PRODUCT)){
                if (it.type == Constant.GRID) {
                    recyclerView.apply {
                        verticalDecoration = DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)
                        val verticalDivider = ContextCompat.getDrawable(context, R.drawable.vertical_divider_more_business_stamp) as Drawable
                        verticalDecoration!!.setDrawable(verticalDivider)
                        addItemDecoration(verticalDecoration!!)

                        horizontalDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
                        val horizontalDivider = ContextCompat.getDrawable(context, R.drawable.horizontal_divider_more_business_stamp) as Drawable
                        horizontalDecoration!!.setDrawable(horizontalDivider)
                        addItemDecoration(horizontalDecoration!!)
                    }
                    productAdapter.setData(it.data, it.objectType, Constant.ADS_GRID_TYPE, it.targetType, it.targetId, null)
                } else {
                    productAdapter.setData(it.data, it.objectType, Constant.ADS_HORIZONTAL_TYPE, it.targetType, it.targetId, null)
                }
            }else{

            }
        })
    }

    private fun getData() {
        swipeLayout.isRefreshing = true
        viewModel.getDataIntent(intent)
    }
}