package vn.icheck.android.screen.user.home_page.my_gift_warehouse.home

import kotlinx.android.synthetic.main.activity_my_gift_ware_house.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.adapter.ViewPagerAdapter
import vn.icheck.android.constant.Constant
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.home.presenter.MyGiftWareHousePresenter
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.home.view.IMyGiftWareHouseView

class MyGiftWareHouseActivity : BaseActivity<MyGiftWareHousePresenter>(), IMyGiftWareHouseView {

    override val getLayoutID: Int
        get() = R.layout.activity_my_gift_ware_house

    override val getPresenter: MyGiftWareHousePresenter
        get() = MyGiftWareHousePresenter(this)

    override fun onInitView() {
        txtTitle.text = getString(R.string.kho_qua_ca_nhan)
        initTabLayout(intent?.getIntExtra(Constant.DATA_1, -1))
        listener()
    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initTabLayout(selectTab: Int?) {
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, presenter.listFragment)
        tabSegment.setupWithViewPager(viewPager)
        viewPager.offscreenPageLimit = 2
        if (selectTab != null && selectTab != -1) {
            viewPager.currentItem = selectTab
        }
    }

}
