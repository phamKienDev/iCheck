package vn.icheck.android.screen.user.history_loading_card.home

import kotlinx.android.synthetic.main.activity_history_card.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.adapter.ViewPagerAdapter
import vn.icheck.android.constant.Constant
import vn.icheck.android.tracking.insider.InsiderHelper
import vn.icheck.android.screen.user.history_loading_card.home.presenter.HistoryCardPresenter
import vn.icheck.android.screen.user.history_loading_card.home.view.IHistoryCardView
import vn.icheck.android.tracking.TrackingAllHelper

class HistoryCardActivity : BaseActivity<HistoryCardPresenter>(), IHistoryCardView {

    override val getLayoutID: Int
        get() = R.layout.activity_history_card

    override val getPresenter: HistoryCardPresenter
        get() = HistoryCardPresenter(this)

    override fun onInitView() {
        TrackingAllHelper.trackTopupHistoryViewed()
        txtTitle.text = getString(R.string.lich_su_nap_the_va_dich_vu)
        tabLayout.setBackgroundColor(vn.icheck.android.ichecklibs.Constant.getAppBackgroundWhiteColor(this))

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
}
