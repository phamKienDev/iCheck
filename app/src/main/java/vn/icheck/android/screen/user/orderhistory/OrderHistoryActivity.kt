package vn.icheck.android.screen.user.orderhistory

import android.graphics.Color
import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_order_history.*
import kotlinx.android.synthetic.main.toolbar_light_blue.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.base.adapter.ViewPagerAdapter
import vn.icheck.android.base.model.ICFragment
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.ICOrderHistoryV2

/**
 * Created by VuLCL on 1/4/2020.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class OrderHistoryActivity : BaseActivityMVVM() {
    private lateinit var adapter: ViewPagerAdapter
    private var deliveredFragment: OrderHistoryFragment? = null

    companion object {
        const val new = 0
        const val waitForPay = 1
        const val waitForConfirmation = 2
        const val confirmed = 3
        const val delivery = 4
        const val delivered = 5
        const val canceled = 6
        const val error = 7
        const val refund = 8

        var reward: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)
        setupToolbar()
        setupViewPager()
        getData()
    }

    private fun setupToolbar() {
        layoutToolbar.setBackgroundColor(Color.WHITE)
        txtTitle.setText(R.string.quan_ly_don_hang)

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupViewPager() {
        val list = listFragment

        adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.setData(list)
        viewPager.adapter = adapter

        tabLayout.setBackgroundColor(Color.WHITE)
        tabLayout.setupWithViewPager(viewPager)
    }

    private val listFragment: MutableList<ICFragment>
        get() {
            val list = mutableListOf<ICFragment>()

            deliveredFragment = OrderHistoryFragment.newInstance(delivered)
            list.add(ICFragment(getString(R.string.cho_xac_nhan), OrderHistoryFragment.newInstance(waitForConfirmation)))
            list.add(ICFragment(getString(R.string.dang_giao), OrderHistoryFragment.newInstance(delivery)))
            list.add(ICFragment(getString(R.string.da_nhan_hang), deliveredFragment!!))
            list.add(ICFragment(getString(R.string.da_huy), OrderHistoryFragment.newInstance(canceled)))

            return list
        }

    private fun getData() {
        val status = intent?.getIntExtra(Constant.DATA_1, -1)

        if (status != null && status != -1) {
            viewPager.currentItem = getPosition(status)
        } else {
            val data = intent?.data
            val path = data?.path

            when (path) {
                "/cancel" -> {
                    viewPager.currentItem = getPosition(1)
                }
                "/error" -> {
                    viewPager.currentItem = getPosition(1)
                }
            }
        }
    }

    private fun getPosition(status: Int?): Int {
        return when (status) {
            waitForConfirmation -> {
                0
            }
            waitForPay -> {
                1
            }
            delivery -> {
                2
            }
            delivered -> {
                3
            }
            canceled -> {
                4
            }
            else -> {
                0
            }
        }
    }


    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        when (event.type) {
            ICMessageEvent.Type.UPDATE_STATUS_ORDER_HISTORY -> {
                if (event.data != null && event.data is ICOrderHistoryV2) {
                    deliveredFragment?.addOrderDelivered(event.data)
                }
            }
            else -> {
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        reward = null
        Glide.get(this).clearMemory()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Glide.get(this).clearMemory()
        Glide.with(this.applicationContext).onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Glide.get(this).onTrimMemory(level)
        Glide.with(this.applicationContext).onTrimMemory(level)
    }
}