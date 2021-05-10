package vn.icheck.android.loyalty.screen.gift_detail_voucher

import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_gift_detail.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.loyalty.helper.StatusBarHelper

class GiftVoucherStaffActivity : BaseActivityGame() {
    private val viewModel by viewModels<GiftVoucherStaffViewModel>()

    private val adapter = GiftVoucherStaffAdapter()

    var voucher = ""

    override val getLayoutID: Int
        get() = R.layout.activity_gift_detail

    override fun onInitView() {
        StatusBarHelper.setOverStatusBarDark(this)

        voucher = intent.getStringExtra(ConstantsLoyalty.DATA_1) ?: ""

        initToolbar()
        initRecyclerView()
        initSwipeLayout()
        initListener()
    }

    private fun initToolbar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initRecyclerView() {
        var backgroundHeight = 0

        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (backgroundHeight <= 0) {
                    backgroundHeight = toolbarAlpha.height + (toolbarAlpha.height / 2)
                }

                val visibility = viewModel.getHeaderAlpha(recyclerView.computeVerticalScrollOffset(), backgroundHeight)
                toolbarAlpha.alpha = visibility
                viewShadow.alpha = visibility
            }
        })
    }

    private fun initSwipeLayout() {
        swipeLayout.setOnRefreshListener {
            getData()
        }
        swipeLayout.post {
            getData()
        }
    }

    private fun getData() {
        swipeLayout.isRefreshing = true

        viewModel.scanVoucher(voucher)
    }

    private fun initListener() {
        viewModel.onError.observe(this, {
            swipeLayout.isRefreshing = false

            if (adapter.isEmpty) {
                adapter.setError(it)
            } else {
                showLongError(it.title)
            }
        })

        viewModel.scanSuccess.observe(this, {
            swipeLayout.isRefreshing = false

            if (it != null) {
                adapter.setData(it)
            }
        })
    }
}