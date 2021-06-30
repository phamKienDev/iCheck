package vn.icheck.android.util.kotlin

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import vn.icheck.android.R
import vn.icheck.android.ichecklibs.util.icStartActivity
import vn.icheck.android.ichecklibs.util.icStartActivityAndFinish
import vn.icheck.android.ichecklibs.util.icStartActivityForResult
import vn.icheck.android.ichecklibs.util.icStartActivityWithoutAnimation
import java.io.Serializable

object ActivityUtils {

    inline fun <reified T : FragmentActivity> startActivityWithoutAnimation(activity: Activity) {
        activity.icStartActivityWithoutAnimation<T>()
    }

    fun startActivityWithoutAnimation(activity: Activity, intent: Intent) {
        activity.icStartActivityWithoutAnimation(intent)
    }

    inline fun <reified T : FragmentActivity, O : Serializable> startActivityWithoutAnimation(activity: Activity, key: String, value: O) {
        activity.icStartActivityWithoutAnimation<T, O>(key, value)
    }

    inline fun <reified T : FragmentActivity, O : Serializable> startActivityWithoutAnimation(activity: Activity, key: String, value: O, requestCode: Int) {
        activity.icStartActivityWithoutAnimation<T, O>(key, value, requestCode)
    }

    inline fun <reified T : FragmentActivity> startActivity(fragment: Fragment) {
        fragment.icStartActivity<T>()
    }

    inline fun <reified T : FragmentActivity> startActivity(fragment: Fragment, key: String, value: String) {
        fragment.icStartActivity<T>(key, value)
    }

    inline fun <reified T : FragmentActivity, O : Serializable> startActivity(fragment: Fragment, key: String, value: O) {
        fragment.icStartActivity<T, O>(key, value)
    }

    fun startActivityForResult(fragment: Fragment, intent: Intent, requestCode: Int) {
        fragment.icStartActivityForResult(intent, requestCode)
    }

    inline fun <reified T : FragmentActivity> startActivityForResult(fragment: Fragment, requestCode: Int) {
        fragment.icStartActivityForResult<T>(requestCode)
    }

    inline fun <reified T : FragmentActivity> startActivityForResult(fragment: Fragment, key: String, value: String, requestCode: Int) {
        fragment.icStartActivityForResult<T>(key, value, requestCode)
    }

    inline fun <reified T : FragmentActivity, O : Serializable> startActivityForResult(fragment: Fragment, key: String, value: O, requestCode: Int) {
        fragment.icStartActivityForResult<T, O>(key, value, requestCode)
    }

    inline fun <reified T : FragmentActivity> startActivityAndFinish(fragment: Fragment) {
        fragment.icStartActivityAndFinish<T>()
    }

    fun startActivityAndFinish(fragment: Fragment, intent: Intent) {
        fragment.icStartActivityAndFinish(intent)
    }


    inline fun <reified T : FragmentActivity> startActivity(activity: FragmentActivity) {
        activity.icStartActivity<T>()
    }

    inline fun <reified T : FragmentActivity> startActivity(activity: FragmentActivity, key: String, value: String) {
        activity.icStartActivity<T>(key, value)
    }

    inline fun <reified T : FragmentActivity, O : Serializable> startActivity(activity: FragmentActivity, key: String, value: O) {
        activity.icStartActivity<T, O>(key, value)
    }

    fun startActivity(activity: FragmentActivity, intent: Intent) {
        activity.icStartActivity(intent)
    }


    inline fun <reified T : FragmentActivity> startActivity(activity: Activity) {
        activity.icStartActivity<T>()
    }

    inline fun <reified T : FragmentActivity> startActivity(activity: Activity, key: String, value: String) {
        activity.icStartActivity<T>(key, value)
    }

    inline fun <reified T : FragmentActivity, O : Serializable> startActivity(activity: Activity, key: String, value: O) {
        activity.icStartActivity<T, O>(key, value)
    }

    fun startActivity(activity: Activity, intent: Intent) {
        activity.icStartActivity(intent)
    }


    fun startActivity(fragment: Fragment, intent: Intent) {
        fragment.icStartActivity(intent)
    }

    fun startActivityForResult(activity: Activity, intent: Intent, requestCode: Int) {
        activity.icStartActivityForResult(intent, requestCode)
    }

    inline fun <reified T : FragmentActivity> startActivityForResult(activity: FragmentActivity, requestCode: Int) {
        activity.icStartActivityForResult<T>(requestCode)
    }

    inline fun <reified T : FragmentActivity> startActivityForResult(activity: FragmentActivity, key: String, value: String, requestCode: Int) {
        activity.icStartActivityForResult<T>(key, value, requestCode)
    }

    inline fun <reified T : FragmentActivity, O : Serializable> startActivityForResult(activity: FragmentActivity, key: String, value: O, requestCode: Int) {
        activity.icStartActivityForResult<T, O>(key, value, requestCode)
    }

    inline fun <reified T : FragmentActivity, O : Serializable> startActivityForResult(activity: Activity, key: String, value: O, requestCode: Int) {
        activity.icStartActivityForResult<T, O>(key, value, requestCode)
    }

    inline fun <reified T : FragmentActivity> startActivityAndFinish(activity: FragmentActivity) {
        activity.icStartActivityAndFinish<T>()
    }

    inline fun <reified T : Activity> startActivityAndFinish(activity: Activity) {
        activity.icStartActivityAndFinish<T>()
    }

    inline fun <reified T : FragmentActivity, O : Serializable> startActivityAndFinish(activity: FragmentActivity, key: String, value: O) {
        activity.icStartActivityAndFinish<T, O>(key, value)
    }

    fun startActivityAndFinish(activity: FragmentActivity, intent: Intent) {
        activity.icStartActivityAndFinish(intent)
    }

    fun replaceFragment(fragmentManager: FragmentManager, frameID: Int, fragment: Fragment, isAnimation: Boolean, isAddToBackStack: Boolean) {
        fragmentManager.inTransaction {
            if (isAnimation) {
                setCustomAnimations(R.anim.right_to_left_enter, R.anim.right_to_left_exit, R.anim.left_to_right_pop_enter, R.anim.left_to_right_pop_exit)
            }

            if (isAddToBackStack) {
                addToBackStack(fragment.tag)
            }

            replace(frameID, fragment, fragment.tag)
        }
    }

    fun removeFragments(fragmentManager: FragmentManager, fragment: Fragment) {
        fragmentManager.inTransaction {
            remove(fragment)
        }
    }

    fun removeAllFragments(fragmentManager: FragmentManager) {
        for (fragment in fragmentManager.fragments) {
            fragmentManager.inTransaction {
                remove(fragment)
                fragmentManager.popBackStack()
            }
        }
    }

    fun removeOtherFragment(fragmentManager: FragmentManager, fragment: Fragment) {
        for (fragments in fragmentManager.fragments) {
            fragmentManager.inTransaction {
                if (fragments != fragment) {
                    remove(fragment)
                    fragmentManager.popBackStack()
                }
            }
        }
    }

    fun getFragmentCount(fragmentManager: FragmentManager): Int {
        return fragmentManager.fragments.size
    }

    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
        val fragmentTransaction = beginTransaction()
        fragmentTransaction.func()
        fragmentTransaction.commitAllowingStateLoss()
    }

    fun finishActivity(fragmentActivity: FragmentActivity?) {
        fragmentActivity?.finish()
        fragmentActivity?.overridePendingTransition(R.anim.none, R.anim.left_to_right_pop_exit)
    }

    fun finishActivityWithoutAnimation(fragmentActivity: FragmentActivity?) {
        fragmentActivity?.finish()
        fragmentActivity?.overridePendingTransition(R.anim.none_no_time, R.anim.none_no_time)
    }

}



