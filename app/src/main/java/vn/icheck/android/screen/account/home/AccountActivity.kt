package vn.icheck.android.screen.account.home

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseFragmentActivity
import vn.icheck.android.base.fragment.BaseFragment
import vn.icheck.android.constant.Constant
import vn.icheck.android.screen.account.icklogin.IckLoginActivity
import vn.icheck.android.screen.account.login.LoginFragment

class AccountActivity : BaseFragmentActivity() {

    override val getLayoutID: Int
        get() = R.layout.layout_frame

    override val getFirstFragment: Fragment
        get() = LoginFragment.newInstance(intent?.getStringExtra(Constant.DATA_1), intent?.getStringExtra(Constant.DATA_2))

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, AccountActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onInitView() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//        }
//        IckLoginActivity.create(this)
    }
}