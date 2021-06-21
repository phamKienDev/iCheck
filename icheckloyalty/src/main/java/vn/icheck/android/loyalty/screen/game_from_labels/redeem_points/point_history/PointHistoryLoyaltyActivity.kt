package vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.point_history

import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_point_history_loyalty.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.util.rText
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.viewpager.ICKFragment
import vn.icheck.android.loyalty.base.viewpager.ViewPagerAdapter
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.helper.TabLayoutIndicatorHelper
import vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.point_history.fragment.PointHistoryAllFragment

class PointHistoryLoyaltyActivity : BaseActivityGame() {
    private val tabLayoutIndicator = TabLayoutIndicatorHelper()

    override val getLayoutID: Int
        get() = R.layout.activity_point_history_loyalty

    override fun onInitView() {
        initToolbar()
        initTabLayout(intent.getIntExtra(ConstantsLoyalty.DATA_1, -1))
    }

    private fun initToolbar() {
        txtTitle rText R.string.lich_su_diem

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initTabLayout(selectTab: Int?) {
        val listPage = mutableListOf<ICKFragment>()

        val id = intent.getLongExtra(ConstantsLoyalty.DATA_2, -1)

        val target = if (SharedLoyaltyHelper(this@PointHistoryLoyaltyActivity).getBoolean(ConstantsLoyalty.HAS_CHANGE_CODE_REDEEM_POINTS)) {
            "code"
        } else {
            "stamp"
        }

        listPage.add(ICKFragment(getString(R.string.tat_ca), PointHistoryAllFragment(1, id, target)))
        listPage.add(ICKFragment(getString(R.string.tich_diem), PointHistoryAllFragment(2, id, target)))
        listPage.add(ICKFragment(getString(R.string.tieu_diem), PointHistoryAllFragment(3, id, target)))

        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, listPage)
        tabLayout.setupWithViewPager(viewPager)
        viewPager.offscreenPageLimit = 3

        viewPager.post {
            for (i in 0 until tabLayout.tabCount) {
                val tabTextView = ((tabLayout.getChildAt(0) as LinearLayout).getChildAt(i) as LinearLayout).getChildAt(1) as TextView
                tabTextView.isAllCaps = false
            }

            indicator?.let {
                tabLayoutIndicator.calculateIndicator(indicator, tabLayout)
            }

            viewPager?.let {
                viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                    override fun onPageScrollStateChanged(state: Int) {}

                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                        tabLayoutIndicator.slideIndicator(indicator, position, positionOffset)
                    }

                    override fun onPageSelected(position: Int) {
                    }
                })
            }

            if (selectTab != null && selectTab != -1) {
                viewPager.currentItem = selectTab
            }
        }
    }
}