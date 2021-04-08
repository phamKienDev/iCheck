package vn.icheck.android.loyalty.helper

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import vn.icheck.android.loyalty.R
import java.io.Serializable

object ActivityHelper {

    inline fun <reified T : FragmentActivity> startActivity(fragment: Fragment) {
        fragment.startActivity(Intent(fragment.context, T::class.java))
        fragment.activity?.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
    }

    inline fun <reified T : FragmentActivity> startActivity(fragment: Fragment, key: String, value: String) {
        startActivity<T, String>(fragment, key, value)
    }

    inline fun <reified T : FragmentActivity, O : Serializable> startActivity(fragment: Fragment, key: String, value: O) {
        val intent = Intent(fragment.context, T::class.java)
        intent.putExtra(key, value)
        fragment.startActivity(intent)
        fragment.activity?.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
    }

    fun startActivityForResult(fragment: Fragment, intent: Intent, requestCode: Int) {
        fragment.startActivityForResult(intent, requestCode)
        fragment.activity?.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
    }

    inline fun <reified T : FragmentActivity> startActivityForResult(fragment: Fragment, requestCode: Int) {
        fragment.startActivityForResult(Intent(fragment.context, T::class.java), requestCode)
        fragment.activity?.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
    }

    inline fun <reified T : Activity> startActivityForResult(activity: Activity, requestCode: Int) {
        activity.startActivityForResult(Intent(activity, T::class.java), requestCode)
        activity.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
    }

    inline fun <reified T : FragmentActivity> startActivityForResult(fragment: Fragment, key: String, value: String, requestCode: Int) {
        startActivityForResult<T, String>(fragment, key, value, requestCode)
    }

    inline fun <reified T : FragmentActivity, O : Serializable> startActivityForResult(fragment: Fragment, key: String, value: O, requestCode: Int) {
        val intent = Intent(fragment.context, T::class.java)
        intent.putExtra(key, value)
        fragment.startActivityForResult(intent, requestCode)
        fragment.activity?.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
    }

    inline fun <reified T : FragmentActivity> startActivityAndFinish(fragment: Fragment) {
        fragment.startActivity(Intent(fragment.context, T::class.java))
        fragment.activity?.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
        fragment.activity?.finish()
    }

    fun startActivityAndFinish(fragment: Fragment, intent: Intent) {
        fragment.startActivity(intent)
        fragment.activity?.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
        fragment.activity?.finish()
    }


    inline fun <reified T : FragmentActivity> startActivity(activity: FragmentActivity) {
        activity.startActivity(Intent(activity, T::class.java))
        activity.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
    }

    inline fun <reified T : FragmentActivity> startActivity(activity: FragmentActivity, key: String, value: String) {
        val intent = Intent(activity, T::class.java)
        intent.putExtra(key, value)
        activity.startActivity(intent)
        activity.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
    }

    inline fun <reified T : FragmentActivity, O : Serializable> startActivity(activity: FragmentActivity, key: String, value: O) {
        val intent = Intent(activity, T::class.java)
        intent.putExtra(key, value)
        activity.startActivity(intent)
        activity.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
    }

    fun startActivity(activity: FragmentActivity, intent: Intent) {
        activity.startActivity(intent)
        activity.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
    }



    inline fun <reified T : FragmentActivity> startActivity(activity: Activity) {
        activity.startActivity(Intent(activity, T::class.java))
        activity.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
    }

    inline fun <reified T : FragmentActivity> startActivity(activity: Activity, key: String, value: String) {
        val intent = Intent(activity, T::class.java)
        intent.putExtra(key, value)
        activity.startActivity(intent)
        activity.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
    }

    inline fun <reified T : FragmentActivity, O : Serializable> startActivity(activity: Activity, key: String, value: O) {
        val intent = Intent(activity, T::class.java)
        intent.putExtra(key, value)
        activity.startActivity(intent)
        activity.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
    }

    fun startActivity(activity: Activity, intent: Intent) {
        activity.startActivity(intent)
        activity.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
    }



    fun startActivity(fragment: Fragment, intent: Intent) {
        fragment.startActivity(intent)
        fragment.activity?.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
    }

    fun startActivityForResult(activity: Activity, intent: Intent, requestCode: Int) {
        activity.startActivityForResult(intent, requestCode)
        activity.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
    }

    fun startActivityForResult(activity: FragmentActivity, intent: Intent, requestCode: Int) {
        activity.startActivityForResult(intent, requestCode)
        activity.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
    }

    inline fun <reified T : FragmentActivity> startActivityForResult(activity: FragmentActivity, requestCode: Int) {
        activity.startActivityForResult(Intent(activity, T::class.java), requestCode)
        activity.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
    }

    inline fun <reified T : FragmentActivity> startActivityForResult(activity: FragmentActivity, key: String, value: String, requestCode: Int) {
        val intent = Intent(activity, T::class.java)
        intent.putExtra(key, value)
        activity.startActivityForResult(intent, requestCode)
        activity.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
    }

    inline fun <reified T : FragmentActivity, O : Serializable> startActivityForResult(activity: FragmentActivity, key: String, value: O, requestCode: Int) {
        val intent = Intent(activity, T::class.java)
        intent.putExtra(key, value)
        activity.startActivityForResult(intent, requestCode)
        activity.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
    }

    inline fun <reified T : Activity, O : Serializable> startActivityForResult(activity: Activity, key: String, value: O, requestCode: Int) {
        val intent = Intent(activity, T::class.java)
        intent.putExtra(key, value)
        activity.startActivityForResult(intent, requestCode)
        activity.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
    }

    inline fun <reified T : Activity> startActivityAndFinish(activity: Activity) {
        activity.startActivity(Intent(activity, T::class.java))
        activity.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
        activity.finish()
    }

    inline fun <reified T : FragmentActivity, O : Serializable> startActivityAndFinish(activity: FragmentActivity, key: String, value: O) {
        val intent = Intent(activity, T::class.java)
        intent.putExtra(key, value)
        activity.startActivity(intent)
        activity.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
        activity.finish()
    }

    fun startActivityAndFinish(activity: FragmentActivity, intent: Intent) {
        activity.startActivity(intent)
        activity.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
        activity.finish()
    }

    fun addFragment(fragmentManager: FragmentManager, frameID: Int, fragment: Fragment) {
        addFragment(fragmentManager, frameID, fragment, isAnimation = true)
    }

    fun addFragment(fragmentManager: FragmentManager, frameID: Int, fragment: Fragment, isAnimation: Boolean) {
        addFragment(fragmentManager, frameID, fragment, isAnimation, isAddToBackStack = true)
    }

    fun addFragment(fragmentManager: FragmentManager, frameID: Int, fragment: Fragment, isAnimation: Boolean, isAddToBackStack: Boolean) {
        fragmentManager.inTransaction {
            if (isAnimation) {
                setCustomAnimations(R.anim.right_to_left_enter, R.anim.right_to_left_exit, R.anim.left_to_right_pop_enter, R.anim.left_to_right_pop_exit)
            }

            if (isAddToBackStack) {
                addToBackStack(fragment.tag)
            }

            if (fragmentManager.findFragmentByTag(fragment.tag) == null) {
                add(frameID, fragment, fragment.tag)
            }
        }
    }

    fun replaceFragment(fragmentManager: FragmentManager, frameID: Int, fragment: Fragment) {
        fragmentManager.inTransaction {
            setCustomAnimations(R.anim.right_to_left_enter, R.anim.right_to_left_exit, R.anim.left_to_right_pop_enter, R.anim.left_to_right_pop_exit)
            replace(frameID, fragment, fragment.tag)
        }
    }

    fun replaceFragment(fragmentManager: FragmentManager, frameID: Int, fragment: Fragment, isAnimation: Boolean) {
        replaceFragment(fragmentManager, frameID, fragment, isAnimation, isAddToBackStack = true)
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
}