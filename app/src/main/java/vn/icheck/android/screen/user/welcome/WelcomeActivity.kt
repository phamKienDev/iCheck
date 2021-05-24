package vn.icheck.android.screen.user.welcome

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import kotlinx.android.synthetic.main.activity_welcome.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.CartHelper
import vn.icheck.android.tracking.insider.InsiderHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.network.feature.setting.SettingRepository
import vn.icheck.android.network.models.ICClientSetting
import vn.icheck.android.screen.user.home.HomeActivity
import vn.icheck.android.util.kotlin.ActivityUtils

/**
 * Phạm Hoàng Phi Hùng
 * 0974815770
 * hungphp@icheck.vn
 */

class WelcomeActivity : BaseActivityMVVM() {

    companion object {
        var type1 = 1
        var type2 = 2
        var type3 = 3
//        var type4 = 4
        var type5 = 4
        var type6 = 5

        var isWelcome = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        getDefaultSetting()

        val sharedPreferences: SharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val first = sharedPreferences.getBoolean("first_run", true)

        if (!first) {
            goToHome()
        }else{
            InsiderHelper.onLogin()
        }

        waveLoadingView.setAnimDuration(3500L)
        setupViewPager(viewPager)

        viewPager.offscreenPageLimit = 5
        viewPager.currentItem = 1

        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                indicatorProduct.selection = position

                if (position == 4) {
                    txtNext.text = getString(R.string.dang_nhap)

                    txtNext.setOnClickListener {
                        setupSharedPreferences()
                        waveLoadingView.pauseAnimation()
                        goToHome(true)
                    }

                } else {
                    txtNext.text = getString(R.string.tiep)

                    txtNext.setOnClickListener {
                        viewPager.setCurrentItem(getItem(+1), true)
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        getData()

        txtSkip.setOnClickListener {
            setupSharedPreferences()
            waveLoadingView.pauseAnimation()
            goToHome()
        }
    }

    private fun goToHome(isLogin: Boolean = false) {
        this@WelcomeActivity.apply {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra(Constant.DATA_2, isLogin)
            intent.putExtra(Constant.DATA_3, this.intent?.getStringExtra(Constant.DATA_2))
            intent.putExtra(Constant.DATA_4, this.intent?.getStringExtra(Constant.DATA_3))
            ActivityUtils.startActivityAndFinish(this@WelcomeActivity, intent)
        }
    }

    private fun getDefaultSetting() {
        SettingRepository().getClientSetting(object : ICApiListener<ICClientSetting> {
            override fun onSuccess(obj: ICClientSetting) {
                SettingManager.setClientSetting(obj)
            }

            override fun onError(error: ICBaseResponse?) {

            }
        })

        CartHelper().updateCountCart()
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(WelcomeFragment.newInstance(type1))
        adapter.addFragment(WelcomeFragment.newInstance(type2))
        adapter.addFragment(WelcomeFragment.newInstance(type3))
//        adapter.addFragment(WelcomeFragment.newInstance(type4))
        adapter.addFragment(WelcomeFragment.newInstance(type5))
        adapter.addFragment(WelcomeFragment.newInstance(type6))
        viewPager.adapter = adapter
    }

    private fun getData() {
        intent?.getIntExtra(Constant.DATA_1, 0)?.let { status ->
            viewPager.currentItem = getPosition(status)
        }
    }

    /**
     * setup vị trí cho từng viewpager
     */
    private fun getPosition(status: Int?): Int {
        return when (status) {
            type1 -> {
                0
            }
            type2 -> {
                1
            }
            type3 -> {
                2
            }
//            type4 -> {
//                3
//            }
            type5 -> {
                3
            }
            type6 -> {
                4
            }
            else -> {
                0
            }
        }
    }

    private fun getItem(i: Int): Int {
        return viewPager.currentItem + i
    }

    private fun setupSharedPreferences() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val edit: SharedPreferences.Editor = sharedPreferences.edit()
        edit.putBoolean("first_run", false)
        edit.apply()
    }

    internal class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment) {
            mFragmentList.add(fragment)
        }
    }
}
