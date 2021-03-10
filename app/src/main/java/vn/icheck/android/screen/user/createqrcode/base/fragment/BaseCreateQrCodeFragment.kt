package vn.icheck.android.screen.user.createqrcode.base.fragment

import android.os.Handler
import android.view.View
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.base.fragment.BaseFragment
import vn.icheck.android.base.fragment.BaseFragmentPresenter
import vn.icheck.android.base.model.ICMessageEvent

/**
 * Created by VuLCL on 10/8/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
abstract class BaseCreateQrCodeFragment<P : BaseFragmentPresenter> : BaseFragment<P>() {

    fun focusView(view: View) {
        Handler().postDelayed({
            KeyboardUtils.showSoftInput(view)
        }, 600)
    }

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ICMessageEvent) {
        if (event.type == ICMessageEvent.Type.BACK) {
            activity?.onBackPressed()
        }
    }
}