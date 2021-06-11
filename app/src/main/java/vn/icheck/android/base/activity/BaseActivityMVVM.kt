package vn.icheck.android.base.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.confirm.ConfirmDialog
import vn.icheck.android.base.dialog.reward_login.RewardLoginCallback
import vn.icheck.android.base.dialog.reward_login.RewardLoginDialog
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.chat.icheckchat.screen.conversation.ListConversationFragment
import vn.icheck.android.network.base.*
import vn.icheck.android.screen.account.icklogin.IckLoginActivity
import vn.icheck.android.screen.user.home.HomeActivity
import vn.icheck.android.screen.user.home_page.HomePageFragment
import vn.icheck.android.util.ick.simpleStartForResultActivity
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import java.io.Serializable

abstract class BaseActivityMVVM : AppCompatActivity(), ICRequireLogin, ICNetworkCallback,
    TokenTimeoutCallback {

    var onRequestUserLoginSuccess: () -> Unit = {}

    var job: Job? = null
    var confirmLogin: ConfirmDialog? = null
    open val getStatusBarHeight: Int
        get() {
            var result = 0
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    protected fun finishActivity(resultCode: Int? = null, data: Intent? = null) {
        if (resultCode != null) {
            if (data != null) {
                setResult(resultCode, data)
            } else {
                setResult(resultCode)
            }
        }

        finish()
        overridePendingTransition(R.anim.none, R.anim.left_to_right_pop_exit)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.none, R.anim.left_to_right_pop_exit)
    }

    open fun isHomeActivity(): Boolean {
        return false
    }

    open fun isRegisterEventBus(): Boolean {
        return false
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(event: ICMessageEvent) {
        if (event.type == ICMessageEvent.Type.GO_TO_HOME) {
            if (!isHomeActivity()) {
                ActivityUtils.finishActivity(this)
            }
        } else if (event.type == ICMessageEvent.Type.ON_FINISH_ALL_CHAT) {
            if (!isHomeActivity() && ListConversationFragment.isOpenChat) {
                ActivityUtils.finishActivity(this)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        confirmLogin = null

        EventBus.getDefault().unregister(this)
    }

    override fun onResume() {
        super.onResume()
        ICNetworkManager.register(this)
        ICNetworkManager.registerTokenTimeoutCallback(this)
        EventBus.getDefault().post(ICMessageEvent.Type.ON_CHECK_UPDATE_LOCATION)
    }

    override fun onPause() {
        super.onPause()
        ICNetworkManager.unregister(this)
        ICNetworkManager.unregisterTokenTimeoutCallback(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestLogin) {
            if (resultCode == Activity.RESULT_OK) {
                onRequireLoginSuccess(requestLogin)
                loginSuccessCallback?.invoke()
            } else {
                onRequireLoginCancel()
                loginErrorCallback?.invoke()
            }
        }
    }

    /**
     * ICRequireLogin
     * */
    private var requestLogin = 101
    private var loginSuccessCallback: (() -> Unit?)? = null
    private var loginErrorCallback: (() -> Unit?)? = null

    fun onRequireLogin(loginSuccess: () -> Unit, loginCancel: (() -> Unit?)?) {
        loginSuccessCallback = loginSuccess
        loginErrorCallback = loginCancel

        runOnUiThread {
            RewardLoginDialog.show(supportFragmentManager, object : RewardLoginCallback {
                override fun onDismiss() {
                    onRequireLoginCancel()
                }

                override fun onLogin() {
                    startActivityForResult<IckLoginActivity>(requestLogin)
                }

                override fun onRegister() {
                    simpleStartForResultActivity(IckLoginActivity::class.java, 1)
                }
            })
        }
    }

    override fun onRequireLogin(requestCode: Int) {
        requestLogin = requestCode
        runOnUiThread {
            RewardLoginDialog.show(supportFragmentManager, object : RewardLoginCallback {
                override fun onDismiss() {
                    onRequireLoginCancel()
                }

                override fun onLogin() {
                    startActivityForResult<IckLoginActivity>(requestLogin)
                }

                override fun onRegister() {
                    simpleStartForResultActivity(IckLoginActivity::class.java, 1)
                }
            })
        }
    }


    override fun onRequireLoginSuccess(requestCode: Int){

    }

    override fun onRequireLoginCancel() {
    }

    override fun onEndOfToken() {
        onRequireLogin()
    }

    override fun onTokenTimeout() {
        runOnUiThread {
            ICheckApplication.currentActivity()?.let {
                if (confirmLogin == null) {
                    confirmLogin = object : ConfirmDialog(
                        it,
                        "Thông báo",
                        "Phiên đăng nhập hết hạn, vui lòng đăng nhập lại!",
                        "Để sau",
                        "Đăng nhập ngay",
                        false
                    ) {
                        override fun onDisagree() {

                        }

                        override fun onAgree() {
                            startActivityForResult<IckLoginActivity>(requestLogin)
                        }

                        override fun onDismiss() {
//                            HomePageFragment.INSTANCE?.refreshHomeData()
                        }
                    }
                    if (!it.isFinishing && !it.isDestroyed) {
                        confirmLogin?.show()
                        if (it is HomeActivity) {
                            HomeActivity.INSTANCE?.logoutFromHome()
                            lifecycleScope.launch {
                                delay(500)
                                HomePageFragment.INSTANCE?.refreshHomeData()
                                delay(200)
                                HomePageFragment.INSTANCE?.refreshHomeData()
                            }
                        }
                    }
                } else {
                    if (!it.isFinishing && !it.isDestroyed) {
                        if (SessionManager.isUserLogged && confirmLogin?.isShowing == false) {
                            confirmLogin?.show()
                            if (it is HomeActivity) {
                                HomeActivity.INSTANCE?.logoutFromHome()
                                lifecycleScope.launch {
                                    delay(200)
                                    HomePageFragment.INSTANCE?.refreshHomeData()
                                    delay(200)
                                    HomePageFragment.INSTANCE?.refreshHomeData()
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    /**
     * End ICRequireLogin
     * */

    inline fun delayAction(crossinline action: () -> Unit, timeout: Long = 200L) {
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

    /**
     * Toast Control
     * */
    fun showShortSuccess(message: String) {
        ToastUtils.showShortSuccess(this, message)
    }

    fun showShortSuccess(messageID: Int) {
        ToastUtils.showShortSuccess(this, messageID)
    }

    fun showLongSuccess(message: String) {
        ToastUtils.showLongSuccess(this, message)
    }

    fun showLongSuccess(messageID: Int) {
        ToastUtils.showLongSuccess(this, getString(messageID))
    }

    fun showShortWarning(message: String) {
        ToastUtils.showShortWarning(this, message)
    }

    fun showShortWarning(messageID: Int) {
        ToastUtils.showShortWarning(this, messageID)
    }

    fun showLongWarning(message: String) {
        ToastUtils.showLongWarning(this, message)
    }

    fun showLongWarning(messageID: Int) {
        ToastUtils.showLongWarning(this, messageID)
    }

    fun showShortError(errorMessage: String) {
        ToastUtils.showShortError(this, errorMessage)
    }

    fun showShortError(messageID: Int) {
        ToastUtils.showShortError(this, messageID)
    }

    fun showLongError(messageID: Int) {
        ToastUtils.showLongError(this, messageID)
    }

    fun showLongError(errorMessage: String) {
        ToastUtils.showLongError(this, errorMessage)
    }

    fun showShortToast(messageID: Int) {
        ToastUtils.showShortWarning(this, messageID)
    }

    /**
     * End Toast Control
     * */
    inline fun <reified T : FragmentActivity> FragmentActivity.startActivity() {
        ActivityUtils.startActivity<T>(this)
    }

    inline fun <reified T : FragmentActivity> FragmentActivity.startActivity(
        key: String,
        value: String
    ) {
        ActivityUtils.startActivity<T>(this, key, value)
    }

    inline fun <reified T : FragmentActivity, O : Serializable> FragmentActivity.startActivity(
        key: String,
        value: O
    ) {
        ActivityUtils.startActivity<T, O>(this, key, value)
    }

    inline fun <reified T : FragmentActivity> FragmentActivity.startActivityForResult(requestCode: Int) {
        ActivityUtils.startActivityForResult<T>(this, requestCode)
    }

    inline fun <reified T : FragmentActivity> FragmentActivity.startActivityForResult(
        key: String,
        value: String,
        requestCode: Int
    ) {
        ActivityUtils.startActivityForResult<T>(this, key, value, requestCode)
    }

    inline fun <reified T : FragmentActivity, O : Serializable> FragmentActivity.startActivityForResult(
        key: String,
        value: O,
        requestCode: Int
    ) {
        ActivityUtils.startActivityForResult<T, O>(this, key, value, requestCode)
    }

    inline fun FragmentActivity.startActivityAndFinish(intent: Intent) {
        ActivityUtils.startActivityAndFinish(this, intent)
    }

    inline fun <reified T : FragmentActivity> FragmentActivity.startActivityAndFinish() {
        ActivityUtils.startActivityAndFinish<T>(this)
    }

    inline fun <reified T : FragmentActivity, O : Serializable> FragmentActivity.startActivityAndFinish(
        key: String,
        value: O
    ) {
        ActivityUtils.startActivityAndFinish<T, O>(this, key, value)
    }


    fun addFragment(fragment: Fragment) {
        ActivityUtils.addFragment(supportFragmentManager, WidgetUtils.FRAME_FRAGMENT_ID, fragment)
    }

    fun replaceFragment(fragment: Fragment) {
        ActivityUtils.replaceFragment(
            supportFragmentManager,
            WidgetUtils.FRAME_FRAGMENT_ID,
            fragment
        )
    }

    fun removeFragments(fragment: Fragment) {
        ActivityUtils.removeFragments(supportFragmentManager, fragment)
    }

    fun removeAllFragments() {
        ActivityUtils.removeAllFragments(supportFragmentManager)
    }

    fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }
}

fun requestLogin(loginSuccess: () -> Unit, loginCancel: (() -> Unit?)? = null) {
    ICheckApplication.currentActivity()?.let { activity ->
        if (activity is BaseActivityMVVM) {
            activity.onRequireLogin(loginSuccess, loginCancel)
        }
    }
}