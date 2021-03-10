package vn.icheck.android.loyalty.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.loyalty.helper.ActivityHelper
import vn.icheck.android.loyalty.helper.ToastHelper
import java.io.Serializable

abstract class BaseFragmentGame : Fragment() {

    protected abstract val getLayoutID: Int
    protected abstract fun onInitView()

    open fun isRegisterEventBus(): Boolean {
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isRegisterEventBus()) EventBus.getDefault().register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayoutID, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onInitView()
    }

    override fun onDestroy() {
        if (isRegisterEventBus()) EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    fun showShortSuccess(messageID: Int) {
        ToastHelper.showShortSuccess(context, messageID)
    }

    fun showShortSuccess(message: String) {
        ToastHelper.showShortSuccess(context, message)
    }

    fun showLongSuccess(message: String) {
        ToastHelper.showLongSuccess(context, message)
    }

    fun showLongSuccess(messageID: Int) {
        ToastHelper.showLongSuccess(context, getString(messageID))
    }

    fun showShortWarning(message: String) {
        ToastHelper.showShortWarning(context, message)
    }

    fun showShortWarning(messageID: Int) {
        ToastHelper.showShortWarning(context, messageID)
    }

    fun showLongWarning(message: String) {
        ToastHelper.showLongWarning(context, message)
    }

    fun showLongWarning(messageID: Int) {
        ToastHelper.showLongWarning(context, messageID)
    }

    fun showShortError(errorMessage: String) {
        ToastHelper.showShortError(context, errorMessage)
    }

    fun showShortError(messageID: Int) {
        ToastHelper.showShortError(context, messageID)
    }

    fun showLongError(errorMessage: String) {
        ToastHelper.showLongError(context, errorMessage)
    }

    fun showLongError(messageID: Int) {
        ToastHelper.showLongError(context, getString(messageID))
    }

    inline fun <reified T : FragmentActivity> Fragment.startActivity() {
        ActivityHelper.startActivity<T>(this)
    }

    inline fun <reified T : FragmentActivity> Fragment.startActivity(key: String, value: String) {
        ActivityHelper.startActivity<T>(this, key, value)
    }

    inline fun <reified T : FragmentActivity, O : Serializable> Fragment.startActivity(key: String, value: O) {
        ActivityHelper.startActivity<T, O>(this, key, value)
    }

    inline fun <reified T : FragmentActivity> Fragment.startActivityForResult(requestCode: Int) {
        ActivityHelper.startActivityForResult<T>(this, requestCode)
    }

    inline fun <reified T : FragmentActivity> Fragment.startActivityForResult(key: String, value: String, requestCode: Int) {
        ActivityHelper.startActivityForResult<T>(this, key, value, requestCode)
    }

    inline fun <reified T : FragmentActivity, O : Serializable> Fragment.startActivityForResult(key: String, value: O, requestCode: Int) {
        ActivityHelper.startActivityForResult<T, O>(this, key, value, requestCode)
    }

    inline fun <reified T : FragmentActivity> Fragment.startActivityAndFinish() {
        ActivityHelper.startActivityAndFinish<T>(this)
    }
}