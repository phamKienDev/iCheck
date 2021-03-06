package vn.icheck.android.screen.user.newslistv2

import android.os.Bundle
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.constant.Constant
import vn.icheck.android.ichecklibs.util.icFinishActivity
import vn.icheck.android.util.kotlin.ActivityUtils

/**
 * Phạm Hoàng Phi Hùng
 * 0974815770
 * hungphp@icheck.vn
 */

class NewsListV2Activity : BaseActivityMVVM() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_frame)

        addFragment(ListNewsFragment.newInstance(true, intent.getLongExtra(Constant.ID, -1)))
    }

    override fun onBackPressed() {
        icFinishActivity()
    }
}