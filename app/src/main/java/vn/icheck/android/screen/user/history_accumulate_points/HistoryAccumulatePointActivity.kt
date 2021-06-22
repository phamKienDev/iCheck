package vn.icheck.android.screen.user.history_accumulate_points

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_history_accumulate_point.*
import kotlinx.android.synthetic.main.toolbar_blue_v2.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.adapter.ViewPagerAdapter
import vn.icheck.android.base.model.ICFragment
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.screen.user.history_accumulate_points.fragment.received.HistoryPointsReceivedFragment
import vn.icheck.android.screen.user.history_accumulate_points.fragment.used.HistoryPointUsedFragment

class HistoryAccumulatePointActivity : BaseActivityMVVM() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_accumulate_point)

        initToolbar()
        setupView()
        initTabLayout(intent?.getIntExtra(Constant.DATA_1, -1))
    }

    private fun initToolbar(){
        txtTitle.setText(R.string.lich_su_diem_tich_luy)
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupView() {
        tabLayout.setBackgroundColor(vn.icheck.android.ichecklibs.ColorManager.getAppBackgroundWhiteColor(this))
    }

    val listFragment: MutableList<ICFragment>
        get() {
            val list = mutableListOf<ICFragment>()

            list.add(ICFragment(ICheckApplication.getString(R.string.diem_da_nhan), HistoryPointsReceivedFragment()))
            list.add(ICFragment(ICheckApplication.getString(R.string.diem_da_dung), HistoryPointUsedFragment()))

            return list
        }

    private fun initTabLayout(selectTab: Int?) {
        tabLayout.setIndicatorWidth(90)
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, listFragment)
        tabLayout.setupWithViewPager(viewPager)
        viewPager.post {
            if (selectTab != null && selectTab != -1) {
                viewPager.currentItem = selectTab
            }
        }
    }
}