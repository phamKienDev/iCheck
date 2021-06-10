package vn.icheck.android.loyalty.base.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.ICMessageEvent
import vn.icheck.android.loyalty.helper.ActivityHelper
import vn.icheck.android.loyalty.helper.ToastHelper
import java.io.Serializable

abstract class BaseActivityGame : AppCompatActivity() {

    protected abstract val getLayoutID: Int
    protected abstract fun onInitView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutID)

        EventBus.getDefault().register(this)

        onInitView()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.none, R.anim.left_to_right_pop_exit)
    }

    override fun onDestroy() {
        super.onDestroy()

        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(event: ICMessageEvent) {

    }

    fun showShortSuccess(message: String) {
        ToastHelper.showShortSuccess(this, message)
    }

    fun showShortSuccess(messageID: Int) {
        ToastHelper.showShortSuccess(this, messageID)
    }

    fun showLongSuccess(message: String) {
        ToastHelper.showLongSuccess(this, message)
    }

    fun showLongSuccess(messageID: Int) {
        ToastHelper.showLongSuccess(this, getString(messageID))
    }

    fun showShortWarning(message: String) {
        ToastHelper.showShortWarning(this, message)
    }

    fun showShortWarning(messageID: Int) {
        ToastHelper.showShortWarning(this, messageID)
    }

    fun showLongWarning(message: String) {
        ToastHelper.showLongWarning(this, message)
    }

    fun showLongWarning(messageID: Int) {
        ToastHelper.showLongWarning(this, messageID)
    }

    fun showShortError(errorMessage: String) {
        ToastHelper.showShortError(this, errorMessage)
    }

    fun showShortError(messageID: Int) {
        ToastHelper.showShortError(this, messageID)
    }

    fun showLongError(messageID: Int) {
        ToastHelper.showLongError(this, messageID)
    }

    fun showLongError(errorMessage: String) {
        ToastHelper.showLongError(this, errorMessage)
    }

    fun showShortToast(messageID: Int) {
        ToastHelper.showShortWarning(this, messageID)
    }

    /**
     * End Toast Control
     * */
    inline fun <reified T : FragmentActivity> FragmentActivity.startActivity() {
        ActivityHelper.startActivity<T>(this)
    }

    inline fun <reified T : FragmentActivity> FragmentActivity.startActivity(key: String, value: String) {
        ActivityHelper.startActivity<T>(this, key, value)
    }

    inline fun <reified T : FragmentActivity, O : Serializable> FragmentActivity.startActivity(key: String, value: O) {
        ActivityHelper.startActivity<T, O>(this, key, value)
    }

    inline fun <reified T : FragmentActivity> FragmentActivity.startActivityForResult(requestCode: Int) {
        ActivityHelper.startActivityForResult<T>(this, requestCode)
    }

    inline fun <reified T : FragmentActivity> FragmentActivity.startActivityForResult(key: String, value: String, requestCode: Int) {
        ActivityHelper.startActivityForResult<T>(this, key, value, requestCode)
    }

    inline fun <reified T : FragmentActivity, O : Serializable> FragmentActivity.startActivityForResult(key: String, value: O, requestCode: Int) {
        ActivityHelper.startActivityForResult<T, O>(this, key, value, requestCode)
    }

    inline fun <reified T : FragmentActivity> FragmentActivity.startActivityAndFinish() {
        ActivityHelper.startActivityAndFinish<T>(this)
    }

    inline fun <reified T : FragmentActivity, O : Serializable> FragmentActivity.startActivityAndFinish(key: String, value: O) {
        ActivityHelper.startActivityAndFinish<T, O>(this, key, value)
    }
}