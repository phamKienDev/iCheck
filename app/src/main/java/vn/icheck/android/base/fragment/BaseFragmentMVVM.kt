package vn.icheck.android.base.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.dialog.reward_login.RewardLoginCallback
import vn.icheck.android.base.dialog.reward_login.RewardLoginDialog
import vn.icheck.android.constant.Constant
import vn.icheck.android.ichecklibs.util.icRemoveOtherFragment
import vn.icheck.android.network.base.ICRequireLogin
import vn.icheck.android.screen.account.icklogin.IckLoginActivity
import vn.icheck.android.util.FragmentUtils
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import java.io.Serializable

abstract class BaseFragmentMVVM : Fragment(), ICRequireLogin {
    private val requestLogin = 101
    private var requestRequireLogin = 0

    var job: Job? = null

    inline fun delayAction(crossinline action: () -> Unit, timeout: Long = 200) {
        job = if (job?.isActive == true) {
            job?.cancel()
            lifecycleScope.launch {
                delay(timeout)
                action()
            }
        } else {
            lifecycleScope.launch {
                delay(timeout)
                action()
            }
        }
    }

    open val getStatusBarHeight: Int
        get() {
            var result = 0
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

    fun focusView(view: View) {
        Handler().postDelayed({
            KeyboardUtils.showSoftInput(view)
        }, 600)
    }

    /**
     * Activity Function
     * */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isRegisterEventBus()) EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        activity?.let {
            KeyboardUtils.hideSoftInput(it)
        }

        if (isRegisterEventBus()) EventBus.getDefault().unregister(this)

        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == requestLogin) {
            if (resultCode == Activity.RESULT_OK) {
                onRequireLoginSuccess(requestRequireLogin)
            } else {
                onRequireLoginCancel()
            }
        }
    }
    /**
     * End Activity Function
     * */


    /**
     * Public Function
     * */

    open fun isRegisterEventBus(): Boolean {
        return false
    }
    /**
     * End Public Function
     * */

    /**
     * ICRequireLogin
     * */
    override fun onRequireLogin(requestCode: Int) {
        if (!isVisible) {
            return
        }
        requestRequireLogin = requestCode

        activity?.supportFragmentManager?.let {
            RewardLoginDialog.show(it, object : RewardLoginCallback {
                override fun onLogin() {
                    startActivityForResult<IckLoginActivity>(requestLogin)
                }

                override fun onRegister() {
                    startActivityForResult<IckLoginActivity>(Constant.DATA_1, Constant.REGISTER_TYPE, requestLogin)
                }

                override fun onDismiss() {
                    onRequireLoginCancel()
                }
            })
        }
    }

    override fun onRequireLoginSuccess(requestCode: Int) {
    }

    override fun onRequireLoginCancel() {
    }
    /**
     * End ICRequireLogin
     * */


    /**
     * Toast Control
     * */
    fun showShortSuccess(messageID: Int) {
        ToastUtils.showShortSuccess(context, messageID)
    }

    fun showShortSuccess(message: String) {
        ToastUtils.showShortSuccess(context, message)
    }

    fun showLongSuccess(message: String) {
        ToastUtils.showLongSuccess(context, message)
    }

    fun showLongSuccess(messageID: Int) {
        ToastUtils.showLongSuccess(context, getString(messageID))
    }

    fun showShortWarning(message: String) {
        ToastUtils.showShortWarning(context, message)
    }

    fun showShortWarning(messageID: Int) {
        ToastUtils.showShortWarning(context, messageID)
    }

    fun showLongWarning(message: String) {
        ToastUtils.showLongWarning(context, message)
    }

    fun showLongWarning(messageID: Int) {
        ToastUtils.showLongWarning(context, messageID)
    }

    fun showShortError(errorMessage: String) {
        ToastUtils.showShortError(context, errorMessage)
    }

    fun showShortError(messageID: Int) {
        ToastUtils.showShortError(context, messageID)
    }

    fun showLongError(errorMessage: String) {
        ToastUtils.showLongError(context, errorMessage)
    }

    fun showLongError(messageID: Int) {
        ToastUtils.showLongError(context, getString(messageID))
    }
    /**
     * End Toast Control
     * */


    /**
     * Activity Control
     * */
    inline fun <reified T : FragmentActivity> Fragment.startActivity() {
        ActivityUtils.startActivity<T>(this)
    }

    inline fun <reified T : FragmentActivity> Fragment.startActivity(key: String, value: String) {
        ActivityUtils.startActivity<T>(this, key, value)
    }

    inline fun <reified T : FragmentActivity, O : Serializable> Fragment.startActivity(key: String, value: O) {
        ActivityUtils.startActivity<T, O>(this, key, value)
    }

    inline fun <reified T : FragmentActivity> Fragment.startActivityForResult(requestCode: Int) {
        ActivityUtils.startActivityForResult<T>(this, requestCode)
    }

    inline fun <reified T : FragmentActivity> Fragment.startActivityForResult(key: String, value: String, requestCode: Int) {
        ActivityUtils.startActivityForResult<T>(this, key, value, requestCode)
    }

    inline fun <reified T : FragmentActivity, O : Serializable> Fragment.startActivityForResult(key: String, value: O, requestCode: Int) {
        ActivityUtils.startActivityForResult<T, O>(this, key, value, requestCode)
    }

    inline fun <reified T : FragmentActivity> Fragment.startActivityAndFinish() {
        ActivityUtils.startActivityAndFinish<T>(this)
    }
    /**
     * End Activity Control
     * */


    /**
     * Fragment Control
     * */
    fun addFragment(fragment: Fragment) {
        activity?.let {
            if (it.window != null) {
                it.window.currentFocus?.clearFocus()
            }

            KeyboardUtils.hideSoftInput(it)
            FragmentUtils.add(it.supportFragmentManager,
                    fragment, WidgetUtils.FRAME_FRAGMENT_ID,
                    true,
                    R.anim.right_to_left_enter,
                    R.anim.right_to_left_exit,
                    R.anim.left_to_right_pop_enter,
                    R.anim.left_to_right_pop_exit)
        }
    }

    fun replaceFragment(fragment: Fragment) {
        activity?.let {
            if (it.window != null) {
                it.window.currentFocus?.clearFocus()
            }

            KeyboardUtils.hideSoftInput(it)
            FragmentUtils.replace(it.supportFragmentManager,
                    fragment, WidgetUtils.FRAME_FRAGMENT_ID,
                    false,
                    R.anim.right_to_left_enter,
                    R.anim.none,
                    R.anim.left_to_right_pop_enter,
                    R.anim.left_to_right_pop_exit)
        }
    }

    fun removeFragment(fragment: Fragment) {
        activity?.let {
            if (it.window != null) {
                it.window.currentFocus?.clearFocus()
            }

            KeyboardUtils.hideSoftInput(it)
            FragmentUtils.remove(fragment)
        }
    }

    fun removeAllFragments() {
        activity?.let {
            if (it.window != null) {
                it.window.currentFocus?.clearFocus()
            }

            KeyboardUtils.hideSoftInput(it)
            FragmentUtils.removeAll(it.supportFragmentManager)
        }
    }

    fun removeOtherFragments() {
        activity?.let {
            if (it.window != null) {
                it.window.currentFocus?.clearFocus()
            }

            KeyboardUtils.hideSoftInput(it)
            it.supportFragmentManager.icRemoveOtherFragment(this)
        }
    }
    /**
     * End Fragment Control
     * */
}