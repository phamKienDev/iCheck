package vn.icheck.android.base.activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.chat.icheckchat.screen.conversation.ListConversationFragment
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

abstract class BaseFragmentActivity : AppCompatActivity(), BaseActivityView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutID)

        addFragment(getFirstFragment, isAnimation = false, isAddToBackStack = false)

        onInitView()
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(event: ICMessageEvent) {
        if (event.type == ICMessageEvent.Type.GO_TO_HOME) {
            ActivityUtils.finishActivity(this)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.none, R.anim.left_to_right_pop_exit)
    }

    override fun onDestroy() {
        KeyboardUtils.hideSoftInput(this)
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


    protected abstract val getLayoutID: Int
    protected abstract val getFirstFragment: Fragment
    protected abstract fun onInitView()

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

    fun finishActivity() {
        finish()
        overridePendingTransition(R.anim.none, R.anim.left_to_right_pop_exit)
    }


    /**
     * Toast Control
     * */
    fun showShortSuccess(message: String) {
        ToastUtils.showShortSuccess(this, message)
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

    fun showLongError(errorMessage: String) {
        ToastUtils.showLongError(this, errorMessage)
    }

    fun showLongError(messageID: Int) {
        ToastUtils.showLongError(this, getString(messageID))
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

    inline fun <reified T : FragmentActivity> FragmentActivity.startActivityForResult(requestCode: Int) {
        ActivityUtils.startActivityForResult<T>(this, requestCode)
    }

    inline fun <reified T : FragmentActivity> FragmentActivity.startActivityForResult(key: String, value: String, requestCode: Int) {
        ActivityUtils.startActivityForResult<T>(this, key, value, requestCode)
    }

    inline fun <reified T : FragmentActivity> FragmentActivity.startActivityAndFinish() {
        ActivityUtils.startActivityAndFinish<T>(this)
    }


    fun addFragment(fragment: Fragment) {
        addFragment(fragment, true)
    }

    fun addFragment(fragment: Fragment, isAnimation: Boolean) {
        addFragment(fragment, isAnimation, true)
    }

    fun addFragment(fragment: Fragment, isAnimation: Boolean, isAddToBackStack: Boolean) {
        ActivityUtils.addFragment(supportFragmentManager, WidgetUtils.FRAME_FRAGMENT_ID, fragment, isAnimation, isAddToBackStack)
    }

    fun replaceFragment(fragment: Fragment) {
        ActivityUtils.replaceFragment(supportFragmentManager, WidgetUtils.FRAME_FRAGMENT_ID, fragment)
    }
}