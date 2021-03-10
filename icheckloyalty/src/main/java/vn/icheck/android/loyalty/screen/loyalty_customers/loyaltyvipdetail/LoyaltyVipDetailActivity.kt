package vn.icheck.android.loyalty.screen.loyalty_customers.loyaltyvipdetail

import android.os.Handler
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_loyalty_vip_detail.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.viewpager.ICKFragment
import vn.icheck.android.loyalty.base.viewpager.ViewPagerAdapter
import vn.icheck.android.loyalty.dialog.ComingSoonOrOutOfGiftDialog
import vn.icheck.android.loyalty.dialog.DialogNotification
import vn.icheck.android.loyalty.dialog.base.DialogHelperGame
import vn.icheck.android.loyalty.model.ICKCampaignOfBusiness
import vn.icheck.android.loyalty.screen.loyalty_customers.loyaltyvipdetail.fragment.accumulationhistory.AccumulationHistoryFragment
import vn.icheck.android.loyalty.screen.loyalty_customers.loyaltyvipdetail.fragment.landingpage.LandingPageLoyaltyFragment

class LoyaltyVipDetailActivity : BaseActivityGame() {

    private val viewModel by viewModels<LoyaltyVipDetailViewModel>()

    private var obj: ICKCampaignOfBusiness? = null

    override val getLayoutID: Int
        get() = R.layout.activity_loyalty_vip_detail

    override fun onInitView() {
        obj = intent.getSerializableExtra(ConstantsLoyalty.DATA_2) as ICKCampaignOfBusiness?
        viewModel.collectionID = obj?.id ?: intent.getLongExtra(ConstantsLoyalty.DATA_1, -1)

        if (viewModel.collectionID == -1L) {
            object : DialogNotification(this@LoyaltyVipDetailActivity, null, getString(R.string.co_loi_xay_ra_vui_long_thu_lai), "Ok", false) {
                override fun onDone() {
                    onBackPressed()
                }
            }.show()
        }

        if (obj != null) {
            viewModel.onSuccess.postValue(obj)
        } else {
            viewModel.getCampaignDetailLongTime()
        }

        imgBack.setOnClickListener {
            onBackPressed()
        }
        initListener()
    }

    private fun setupViewPager() {
        viewPager.offscreenPageLimit = 2
        viewPager.setPagingEnabled(false)
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, mutableListOf<ICKFragment>().apply {
            add(ICKFragment(null, LandingPageLoyaltyFragment(obj?.description
                    ?: "")))
            add(ICKFragment(null, AccumulationHistoryFragment(viewModel.collectionID, obj?.customer_status?.code
                    ?: "")))
        })
        selectTab(intent.getIntExtra(ConstantsLoyalty.DATA_3, 1))
    }

    private fun selectTab(position: Int) {
        when (position) {
            1 -> {
                isChecked(btnInformation)
                viewPager.setCurrentItem(0, false)
            }
            2 -> {
                isChecked(btnHistory)
                viewPager.setCurrentItem(2, false)
            }
        }
    }

    private fun isChecked(view: AppCompatCheckedTextView): Boolean {
        return if (view.isChecked) {
            true
        } else {
            btnInformation.isChecked = false
            btnAdd.isChecked = false
            btnHistory.isChecked = false
            view.isChecked = true
            false
        }
    }

    private fun initListener() {
        viewModel.onSuccess.observe(this, Observer {
            obj = it
            txtTitle.text = it?.name
            setupViewPager()
        })

        btnInformation.setOnClickListener {
            if (!isChecked(btnInformation)) {
                viewPager.setCurrentItem(0, false)
            }
        }
        btnAdd.setOnClickListener {
            btnAdd.isEnabled = false

            if (obj?.status_time != "RUNNING") {
                if (obj?.customer_status?.code == "COMING_SOON") {
                    ComingSoonOrOutOfGiftDialog(this, R.drawable.ic_coming_soon, "Chương trình chưa diễn ra", "Mời bạn qua lại sau để\ntham gia chương trình nhé!").show()
                } else {
                    if (obj?.type == "accumulation_long_term_point") {
                        DialogHelperGame.scanOrEnterAccumulatePointLongTime(this, obj?.id
                                ?: -1, obj?.name)
                    }
                }
            } else {
                if (obj?.type == "accumulation_long_term_point") {
                    DialogHelperGame.scanOrEnterAccumulatePointLongTime(this, obj?.id
                            ?: -1, obj?.name)
                }
            }

            Handler().postDelayed({
                btnAdd.isEnabled = true
            }, 2000)
        }
        btnHistory.setOnClickListener {
            if (!isChecked(btnHistory)) {
                viewPager.setCurrentItem(2, false)
            }
        }
    }
}