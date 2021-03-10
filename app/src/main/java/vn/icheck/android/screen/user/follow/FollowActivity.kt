package vn.icheck.android.screen.user.follow

import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_follow.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.base.adapter.ViewPagerAdapter
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.model.ICFragment
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.TabLayoutIndicatorHelper
import vn.icheck.android.screen.user.follow.follower.ListFollowerFragment
import vn.icheck.android.screen.user.follow.following.ListFollowingFragment

class FollowActivity : BaseActivity<BaseActivityPresenter>(), BaseActivityView {
    private val tabLayoutIndicator = TabLayoutIndicatorHelper()

    override val getLayoutID: Int
        get() = R.layout.activity_follow

    override val getPresenter: BaseActivityPresenter
        get() = BaseActivityPresenter(this)

    override fun onInitView() {
        initToolbar()
        getData()
    }

    private fun initToolbar() {
        txtTitle.setText(R.string.dang_theo_doi)

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getData() {
        val userID: Long = try {
            intent.getLongExtra(Constant.DATA_1, -1)
        } catch (e: Exception) {
            -1
        }

        if (userID != -1L) {
            setupViewPager(userID)
        } else {
            onGetDataError()
        }
    }

    private fun onGetDataError() {
        DialogHelper.showNotification(this@FollowActivity, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
            override fun onDone() {
                onBackPressed()
            }
        })
    }

    private fun setupViewPager(userID: Long) {
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, listFragment(userID))
        tabLayout.setupWithViewPager(viewPager)
        viewPager.offscreenPageLimit = 2

        viewPager.post {
            viewIndicator?.let {
                tabLayoutIndicator.calculateIndicator(viewIndicator, tabLayout)
            }
            viewPager?.let {
                viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                    override fun onPageScrollStateChanged(state: Int) {}

                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                        tabLayoutIndicator.slideIndicator(viewIndicator, position, positionOffset)
                    }

                    override fun onPageSelected(position: Int) {
                        txtTitle.text = viewPager.adapter?.getPageTitle(position)
                    }
                })
            }
        }
    }

    private fun listFragment(userID: Long): MutableList<ICFragment> {
        val list = mutableListOf<ICFragment>()

        list.add(ICFragment(getString(R.string.dang_theo_doi), ListFollowingFragment.newInstance(userID)))
        list.add(ICFragment(getString(R.string.nguoi_theo_doi), ListFollowerFragment.newInstance(userID)))

        return list
    }
}