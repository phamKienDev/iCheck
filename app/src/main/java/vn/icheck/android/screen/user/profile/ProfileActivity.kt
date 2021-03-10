package vn.icheck.android.screen.user.profile

import androidx.fragment.app.Fragment
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseFragmentActivity
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.screen.user.profile.myprofile.MyProfileFragment
import vn.icheck.android.screen.user.profile.userprofile.UserProfileFragment

class ProfileActivity : BaseFragmentActivity() {

    override val getLayoutID: Int
        get() = R.layout.layout_frame

    override val getFirstFragment: Fragment
        get() = if (intent.getLongExtra(Constant.DATA_1, -1) != -1L && intent.getLongExtra(Constant.DATA_1, -1) != SessionManager.session.user?.id) {
            UserProfileFragment.newInstance(intent.getLongExtra(Constant.DATA_1, -1))
        } else {
            MyProfileFragment()
        }

    override fun onInitView() {
    }
}