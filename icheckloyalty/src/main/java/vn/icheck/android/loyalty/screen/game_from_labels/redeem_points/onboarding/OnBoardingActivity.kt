package vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.onboarding

import android.app.Activity
import android.content.Intent
import kotlinx.android.synthetic.main.activity_on_boarding.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.viewpager.ICKFragment
import vn.icheck.android.loyalty.base.viewpager.ViewPagerAdapter
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.helper.StatusBarHelper
import vn.icheck.android.loyalty.room.database.LoyaltyDatabase
import vn.icheck.android.loyalty.room.entity.ICKCampaignId
import vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.home.HomeRedeemPointActivity
import vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.onboarding.fragment.StepOneFragment
import vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.onboarding.fragment.StepTwoThreeFragment

class OnBoardingActivity : BaseActivityGame(), IOnboardingListener {

    companion object {
        fun startActivity(activity: Activity, campaignID: Long, banner: String?, description: String?) {
            val intent = Intent(activity, OnBoardingActivity::class.java)
            intent.putExtra(ConstantsLoyalty.DATA_1, campaignID)
            intent.putExtra(ConstantsLoyalty.DATA_2, banner)
            intent.putExtra(ConstantsLoyalty.DATA_3, description)
            activity.startActivity(intent)
        }
    }

    private var campaignID: Long? = null
    private var banner: String? = null
    private var description: String? = null

    override val getLayoutID: Int
        get() = R.layout.activity_on_boarding

    override fun onInitView() {
        campaignID = intent?.getLongExtra(ConstantsLoyalty.DATA_1, -1)
        banner = intent?.getStringExtra(ConstantsLoyalty.DATA_2)
        description = intent?.getStringExtra(ConstantsLoyalty.DATA_3)

        if (campaignID != null && campaignID != -1L) {
            val id = LoyaltyDatabase.getDatabase(ApplicationHelper.getApplicationByReflect()).idCampaignDao().getIDCampaignByID(campaignID!!)?.id

            if (campaignID == id) {
                finish()
                HomeRedeemPointActivity.startActivity(this, campaignID!!, banner, description)
            }
        }

        StatusBarHelper.setOverStatusBarLight(this@OnBoardingActivity)

        initViewPager()
    }

    private fun initViewPager() {
        val listPage = mutableListOf<ICKFragment>()

        listPage.add(ICKFragment(null, StepOneFragment(this)))
        listPage.add(ICKFragment(null, StepTwoThreeFragment(2, this)))
        listPage.add(ICKFragment(null, StepTwoThreeFragment(3, this)))

        viewPager.offscreenPageLimit = 3
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, listPage)

        selectTab(1)
    }

    private fun selectTab(position: Int) {
        when (position) {
            1 -> {
                viewPager.setCurrentItem(0, false)
            }
            2 -> {
                viewPager.setCurrentItem(1, false)
            }
            3 -> {
                viewPager.setCurrentItem(2, false)
            }
        }
    }

    override fun onNextStep(i: Int) {
        selectTab(i)
    }

    override fun onBackStep(i: Int) {
        selectTab(i)
    }

    override fun onClickRedeemPoint() {
        if (campaignID != null && campaignID != -1L) {
            LoyaltyDatabase.getDatabase(ApplicationHelper.getApplicationByReflect()).idCampaignDao().insertIDCampaign(ICKCampaignId(campaignID!!))

            HomeRedeemPointActivity.startActivity(this, campaignID!!, banner, description)
            finish()
        }
    }
}