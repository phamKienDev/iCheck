package vn.icheck.android.screen.user.home_page.campaign.detail_campagin_v2

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatCheckedTextView
import kotlinx.android.synthetic.main.activity_detail_campaign_v2.*
import kotlinx.android.synthetic.main.activity_detail_campaign_v2.viewPager
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.adapter.ViewPagerAdapter
import vn.icheck.android.base.model.ICFragment
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.ICCampaign
import vn.icheck.android.screen.user.home_page.campaign.detail_campagin_v2.fragment.gift_campaign.GiftCampaignV2Fragment
import vn.icheck.android.screen.user.home_page.campaign.detail_campagin_v2.fragment.infor_campaign.InforCampaignV2Fragment
import vn.icheck.android.util.kotlin.WidgetUtils

class DetailCampaignV2Activity : BaseActivityMVVM(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_campaign_v2)
        setupStatusBar()
        initViewPager(intent.getSerializableExtra(Constant.DATA_1) as ICCampaign)
        listener()
    }

    private fun setupStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = window
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    private fun initViewPager(obj: ICCampaign) {
        val listPage = mutableListOf<ICFragment>()

        listPage.add(ICFragment(null, InforCampaignV2Fragment(obj.id.toString())))
        listPage.add(ICFragment(null, GiftCampaignV2Fragment(obj.id.toString(),obj.title ?: "")))
//        listPage.add(ICFragment(null, TheWinnerCampaignV2Fragment(obj.id.toString(),obj.title ?: "")))

        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, listPage)

        selectTab(0)
    }

    private fun listener() {
        WidgetUtils.setClickListener(this,txtInfomation,txtGiftCampaign,txtWinnerCampaign)
    }

    private fun selectTab(position: Int) {
        when (position) {
            1 -> {
                isChecked(txtInfomation)
                viewPager.setCurrentItem(0, false)
            }
            2 -> {
                isChecked(txtGiftCampaign)
                viewPager.setCurrentItem(1, false)
            }
            3 -> {
                isChecked(txtWinnerCampaign)
                viewPager.setCurrentItem(2, false)
            }
        }
    }

    private fun isChecked(view: AppCompatCheckedTextView): Boolean {
        return if (view.isChecked) {
            true
        } else {
            unCheckAll()
            view.isChecked = true
            false
        }
    }

    private fun unCheckAll() {
        txtInfomation.isChecked = false
        txtGiftCampaign.isChecked = false
        txtWinnerCampaign.isChecked = false
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.txtInfomation -> {
                if (!isChecked(v as AppCompatCheckedTextView)) {
                    viewPager.setCurrentItem(0, false)
                }
            }
            R.id.txtGiftCampaign -> {
                if (!isChecked(v as AppCompatCheckedTextView)) {
                    viewPager.setCurrentItem(1, false)
                }
            }
            R.id.txtWinnerCampaign -> {
                if (!isChecked(v as AppCompatCheckedTextView)) {
                    viewPager.setCurrentItem(2, false)
                }
            }
        }
    }
}