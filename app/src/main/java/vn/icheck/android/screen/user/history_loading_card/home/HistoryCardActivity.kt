package vn.icheck.android.screen.user.history_loading_card.home

import android.content.Context
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_history_card.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.adapter.ViewPagerAdapter
import vn.icheck.android.constant.Constant
import vn.icheck.android.ichecklibs.DialogHelper
import vn.icheck.android.ichecklibs.util.showLongErrorToast
import vn.icheck.android.screen.user.history_loading_card.home.presenter.HistoryCardPresenter
import vn.icheck.android.screen.user.history_loading_card.home.view.IHistoryCardView
import vn.icheck.android.tracking.TrackingAllHelper

class HistoryCardActivity : BaseActivityMVVM(), IHistoryCardView {

    val presenter = HistoryCardPresenter(this@HistoryCardActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_card)
        onInitView()
    }

    fun onInitView() {
        TrackingAllHelper.trackTopupHistoryViewed()
        txtTitle.text = getString(R.string.lich_su_nap_the_va_dich_vu)
        tabLayout.setBackgroundColor(vn.icheck.android.ichecklibs.ColorManager.getAppBackgroundWhiteColor(this))

        initTabLayout(intent?.getIntExtra(Constant.DATA_1, -1))
        listener()
    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initTabLayout(selectTab: Int?) {
        tabLayout.setIndicatorWidth(90)
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, presenter.listFragment)
        tabLayout.setupWithViewPager(viewPager)
        viewPager.post {
            if (selectTab != null && selectTab != -1) {
                viewPager.currentItem = selectTab
            }
        }
    }

    override fun showError(errorMessage: String) {
        showLongErrorToast(errorMessage)
    }

    override val mContext: Context
        get() = this@HistoryCardActivity

    override fun onShowLoading(isShow: Boolean) {
        DialogHelper.showLoading(this@HistoryCardActivity, isShow)
    }
}
