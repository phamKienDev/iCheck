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
import vn.icheck.android.base.dialog.reward_login.RewardLoginDialogV2
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.chat.icheckchat.screen.conversation.ListConversationFragment
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.screen.account.icklogin.IckLoginActivity
import vn.icheck.android.screen.user.home.HomeActivity
import vn.icheck.android.screen.user.home_page.HomePageFragment
import vn.icheck.android.util.ick.logError
import vn.icheck.android.util.ick.simpleStartForResultActivity
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import java.io.Serializable

abstract class BaseActivity<P : BaseActivityPresenter> : AppCompatActivity(), BaseActivityView, ICRequireLogin, ICNetworkCallback, TokenTimeoutCallback {
    val presenter = getPresenter
    var job: Job? = null
    var confirmLogin:ConfirmDialog? = null
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutID)
        EventBus.getDefault().register(this)
        onInitView()
    }

    protected fun finishActivity(resultCode: Int?, data: Intent?) {
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

    protected abstract val getLayoutID: Int
    protected abstract val getPresenter: P
    protected abstract fun onInitView()

    open fun isHomeActivity(): Boolean {
        return false
    }

    open fun isRegisterEventBus(): Boolean {
        return false
    }

    override fun showError(errorMessage: String) {

    }

    override val mContext: Context
        get() = this

    override fun onShowLoading(isShow: Boolean) {
        if (isShow) {
            DialogHelper.showLoading(this)
        } else {
            DialogHelper.closeLoading(this)
        }
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

    override fun onPause() {
        super.onPause()
        ICNetworkManager.unregister(this)
        ICNetworkManager.unregisterTokenTimeoutCallback(this)
    }

    override fun onResume() {
        super.onResume()
        ICNetworkManager.registerTokenTimeoutCallback(this)
        ICNetworkManager.register(this)
        try {
            EventBus.getDefault().post(ICMessageEvent.Type.ON_CHECK_UPDATE_LOCATION)
        } catch (e: Exception) {
            logError(e)
        }
    }

    override fun onTokenTimeout() {
        runOnUiThread {
            ICheckApplication.currentActivity()?.let {
                if (SessionManager.isUserLogged && it is HomeActivity) {
                    HomeActivity.INSTANCE?.logoutFromHome()
                }
                if (confirmLogin == null) {
                    confirmLogin = object : ConfirmDialog(it,
                            "Thông báo",
                            "Phiên đăng nhập hết hạn, vui lòng đăng nhập lại!",
                            "Hủy bỏ",
                            "Đăng nhập ngay",
                            false) {
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
                    }
                } else {
                    if (!it.isFinishing && !it.isDestroyed) {
                        if (SessionManager.isUserLogged && confirmLogin?.isShowing == false) {
                            confirmLogin?.show()
                        }
                    }
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
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
     * ICRequireLogin
     * */
    val requestLogin = 101
    private var requestRequireLogin = 0

    override fun onRequireLogin(requestCode: Int) {
        requestRequireLogin = requestCode
        runOnUiThread {
            RewardLoginDialogV2.show(supportFragmentManager, object : RewardLoginCallback {
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

    override fun onRequireLoginSuccess(requestCode: Int) {
    }

    override fun onRequireLoginCancel() {
    }
    /**
     * End ICRequireLogin
     * */


    /**
     * ICNetworkCallback
     * */
    override fun onEndOfToken() {
        onRequireLogin()
    }

    /**
     * End ICNetworkCallback
     * */


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

    inline fun <reified T : FragmentActivity> FragmentActivity.startActivity(key: String, value: String) {
        ActivityUtils.startActivity<T>(this, key, value)
    }

    inline fun <reified T : FragmentActivity, O : Serializable> FragmentActivity.startActivity(key: String, value: O) {
        ActivityUtils.startActivity<T, O>(this, key, value)
    }

    inline fun <reified T : FragmentActivity> FragmentActivity.startActivityForResult(requestCode: Int) {
        ActivityUtils.startActivityForResult<T>(this, requestCode)
    }

    inline fun <reified T : FragmentActivity> FragmentActivity.startActivityForResult(key: String, value: String, requestCode: Int) {
        ActivityUtils.startActivityForResult<T>(this, key, value, requestCode)
    }

    inline fun <reified T : FragmentActivity, O : Serializable> FragmentActivity.startActivityForResult(key: String, value: O, requestCode: Int) {
        ActivityUtils.startActivityForResult<T, O>(this, key, value, requestCode)
    }

    inline fun <reified T : FragmentActivity> FragmentActivity.startActivityAndFinish() {
        ActivityUtils.startActivityAndFinish<T>(this)
    }

    inline fun <reified T : FragmentActivity, O : Serializable> FragmentActivity.startActivityAndFinish(key: String, value: O) {
        ActivityUtils.startActivityAndFinish<T, O>(this, key, value)
    }


    fun addFragment(fragment: Fragment) {
        ActivityUtils.addFragment(supportFragmentManager, WidgetUtils.FRAME_FRAGMENT_ID, fragment)
    }

    fun replaceFragment(fragment: Fragment) {
        ActivityUtils.replaceFragment(supportFragmentManager, WidgetUtils.FRAME_FRAGMENT_ID, fragment)
    }

    fun removeFragments(fragment: Fragment) {
        ActivityUtils.removeFragments(supportFragmentManager, fragment)
    }

    fun removeAllFragments() {
        ActivityUtils.removeAllFragments(supportFragmentManager)
    }

    fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }
}