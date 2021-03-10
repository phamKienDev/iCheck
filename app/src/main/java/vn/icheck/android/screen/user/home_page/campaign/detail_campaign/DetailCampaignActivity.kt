package vn.icheck.android.screen.user.home_page.campaign.detail_campaign

import android.annotation.SuppressLint
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_detail_campaign.*
import kotlinx.android.synthetic.main.layout_center_campaign.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.adapter.ViewPagerAdapter
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICFragment
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.models.ICDetail_Campaign
import vn.icheck.android.screen.user.detail_my_reward.DetailMyRewardViewModel
import vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.gift_campaign.GiftCampaignFragment
import vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.infor_campaign.InforCampaignFragment
import vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.winner_campaign.TheWinnerCampaignFragment
import vn.icheck.android.screen.user.home_page.campaign.detail_campaign.presenter.DetailCampaignPresenter
import vn.icheck.android.screen.user.home_page.campaign.detail_campaign.view.IDetailCampaignView
import vn.icheck.android.util.kotlin.WidgetUtils
import kotlin.math.abs

@AndroidEntryPoint
class DetailCampaignActivity : BaseActivity<DetailCampaignPresenter>(), IDetailCampaignView {

    override val getPresenter: DetailCampaignPresenter
        get() = DetailCampaignPresenter(this)

    override val getLayoutID: Int
        get() = R.layout.fragment_detail_campaign

    var isShow = true
    private var appBarExpanded = true

    private val viewModel: DetailMyRewardViewModel by viewModels()

    override fun onInitView() {
        presenter.getDataIntent(intent)
        setUpView()
        listener()
    }

    private fun setUpView() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_back_white_24dp)
        collapsingToolbar.setCollapsedTitleTextColor(resources.getColor(R.color.blue))
        collapsingToolbar.setExpandedTitleColor(resources.getColor(R.color.blue))
        collapsingToolbar.title = " "
    }

    private fun listener() {
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onGetDataError(errorMessage: String) {
        showShortError(errorMessage)
        DialogHelper.showConfirm(this@DetailCampaignActivity, R.string.icheck_thong_bao, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai, false, object : ConfirmDialogListener {
            override fun onDisagree() {
                onBackPressed()
            }

            override fun onAgree() {
                presenter.getCampaignDetail(presenter.id)
            }
        })
    }

    @SuppressLint("SetTextI18n")
    override fun onGetDataSucces(data: ICDetail_Campaign) {
        initTablayout(data)

        //Sau khi get Data thanh cong set Title Toolbar
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) > 200) {
                appBarExpanded = false
                toolbar_title.text = data.title
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_categories_menu_blue_24)
                isShow = true
            } else {
                appBarExpanded = true
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_back_white_24dp)
                toolbar_title.text = ""
                isShow = false
            }
            invalidateOptionsMenu()
        })

        val banner = data.image
        if (banner != null && banner.isNotEmpty()) {
            WidgetUtils.loadImageUrl(imgBannerCampaign, banner)
        } else {
            imgBannerCampaign.setBackgroundResource(R.drawable.ic_default_square)
            WidgetUtils.loadImageUrl(imgBannerCampaign, null)
        }

        val imgLogo = data.logo
        if (imgLogo != null && imgLogo.isNotEmpty()) {
            WidgetUtils.loadImageUrlRounded4(img_logo, imgLogo)
        } else {
            img_logo.setBackgroundResource(R.drawable.ic_default_square)
            WidgetUtils.loadImageUrlRounded4(img_logo, null)
        }

        tv_title.text = data.title
//        tv_begin_at.text = data.begin_at
//        tv_end_at.text = data.ended_at
    }

    private fun initTablayout(data: ICDetail_Campaign) {

        val listFragment = mutableListOf<ICFragment>()
        listFragment.add(ICFragment(getString(R.string.thong_tin), InforCampaignFragment.newInstance(data)))
        listFragment.add(ICFragment(getString(R.string.qua_tang), GiftCampaignFragment.newInstance(data)))
        listFragment.add(ICFragment(getString(R.string.nguoi_trung_thuong), TheWinnerCampaignFragment.newInstance(data)))

        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, listFragment)
        tabLayout.setupWithViewPager(viewPager)
        viewPager.offscreenPageLimit = 3

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position == 1) {
                    appBarLayout.setExpanded(false)
                } else if (position == 2) {
                    appBarLayout.setExpanded(false)
                }
            }
        })

        viewPager.adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
