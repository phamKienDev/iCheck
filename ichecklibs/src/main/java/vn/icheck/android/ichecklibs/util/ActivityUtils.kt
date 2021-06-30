package vn.icheck.android.ichecklibs.util

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import vn.icheck.android.ichecklibs.R
import java.io.Serializable

/*
* Function in Activity
* */
// No animation
fun Activity.icStartActivityWithoutAnimation(intent: Intent) {
    startActivity(intent)
    overridePendingTransition(R.anim.none, R.anim.none)
}

inline fun <reified T : Activity> Activity.icStartActivityWithoutAnimation() {
    startActivity(Intent(this, T::class.java))
    overridePendingTransition(R.anim.none, R.anim.none)
}

inline fun <reified T : Activity, O : Serializable> Activity.icStartActivityWithoutAnimation(key: String, value: O) {
    startActivity(Intent(this, T::class.java).apply {
        putExtra(key, value)
    })
    overridePendingTransition(R.anim.none, R.anim.none)
}

inline fun <reified T : Activity, O : Serializable> Activity.icStartActivityWithoutAnimation(key: String, value: O, requestCode: Int) {
    startActivityForResult(Intent(this, T::class.java).apply {
        putExtra(key, value)
    }, requestCode)
    overridePendingTransition(R.anim.none, R.anim.none)
}

fun Activity.icFinishActivityWithoutAnimation() {
    finish()
    overridePendingTransition(R.anim.none, R.anim.none)
}


// With animation
fun Activity.icStartActivity(intent: Intent) {
    startActivity(intent)
    overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
}

inline fun <reified T : Activity> Activity.icStartActivity() {
    startActivity(Intent(this, T::class.java))
    overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
}

inline fun <reified T : Activity> Activity.icStartActivity(key: String, value: String) {
    icStartActivity<T, String>(key, value)
}

inline fun <reified T : Activity, O : Serializable> Activity.icStartActivity(key: String, value: O) {
    startActivity(Intent(this, T::class.java).apply {
        putExtra(key, value)
    })
    overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
}


fun Activity.icStartActivityForResult(intent: Intent, requestCode: Int) {
    startActivityForResult(intent, requestCode)
    overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
}

inline fun <reified T : Activity> Activity.icStartActivityForResult(requestCode: Int) {
    startActivityForResult(Intent(this, T::class.java), requestCode)
    overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
}

inline fun <reified T : Activity> Activity.icStartActivityForResult(key: String, value: String, requestCode: Int) {
    icStartActivityForResult<T, String>(key, value, requestCode)
}

inline fun <reified T : Activity, O : Serializable> Activity.icStartActivityForResult(key: String, value: O, requestCode: Int) {
    startActivityForResult(Intent(this, T::class.java).apply {
        putExtra(key, value)
    }, requestCode)
    overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
}


fun Activity.icStartActivityAndFinish(intent: Intent) {
    startActivity(intent)
    overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
    finish()
}

inline fun <reified T : Activity> Activity.icStartActivityAndFinish() {
    icStartActivity<T>()
    finish()
}

inline fun <reified T : Activity, O : Serializable> Activity.icStartActivityAndFinish(key: String, value: O) {
    icStartActivity<T, O>(key, value)
    finish()
}

fun Activity.icFinishActivity() {
    finish()
    overridePendingTransition(R.anim.none, R.anim.left_to_right_pop_exit)
}


/*
* Function in Fragment
* */
fun Fragment.icStartActivity(intent: Intent) {
    startActivity(intent)
    activity?.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
}

inline fun <reified T : Activity> Fragment.icStartActivity() {
    startActivity(Intent(context, T::class.java))
    activity?.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
}

inline fun <reified T : Activity> Fragment.icStartActivity(key: String, value: String) {
    icStartActivity<T, String>(key, value)
}

inline fun <reified T : Activity, O : Serializable> Fragment.icStartActivity(key: String, value: O) {
    startActivity(Intent(context, T::class.java).apply {
        putExtra(key, value)
    })
    activity?.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
}


fun Fragment.icStartActivityForResult(intent: Intent, requestCode: Int) {
    startActivityForResult(intent, requestCode)
    activity?.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
}

inline fun <reified T : Activity> Fragment.icStartActivityForResult(requestCode: Int) {
    startActivityForResult(Intent(context, T::class.java), requestCode)
    activity?.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
}

inline fun <reified T : Activity> Fragment.icStartActivityForResult(key: String, value: String, requestCode: Int) {
    icStartActivityForResult<T, String>(key, value, requestCode)
}

inline fun <reified T : Activity, O : Serializable> Fragment.icStartActivityForResult(key: String, value: O, requestCode: Int) {
    startActivityForResult(Intent(context, T::class.java).apply {
        putExtra(key, value)
    }, requestCode)
    activity?.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
}


inline fun <reified T : Activity> Fragment.icStartActivityAndFinish() {
    startActivity(Intent(context, T::class.java))
    activity?.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
    activity?.finish()
}

fun Fragment.icStartActivityAndFinish(intent: Intent) {
    startActivity(intent)
    activity?.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
    activity?.finish()
}

fun Fragment.icFinishActivity() {
    activity?.finish()
    activity?.overridePendingTransition(R.anim.none, R.anim.left_to_right_pop_exit)
}


/*
* Add Fragment
* */
fun AppCompatActivity.icAddFragment(frameID: Int, fragment: Fragment) {
    icAddFragment(frameID, fragment, isAnimation = true)
}

fun AppCompatActivity.icAddFragment(frameID: Int, fragment: Fragment, isAnimation: Boolean) {
    icAddFragment(frameID, fragment, isAnimation, isAddToBackStack = true)
}

fun AppCompatActivity.icAddFragment(frameID: Int, fragment: Fragment, isAnimation: Boolean, isAddToBackStack: Boolean) {
    supportFragmentManager.inTransaction {
        if (isAnimation) {
            setCustomAnimations(R.anim.right_to_left_enter, R.anim.right_to_left_exit, R.anim.left_to_right_pop_enter, R.anim.left_to_right_pop_exit)
        }

        if (isAddToBackStack) {
            addToBackStack(fragment.tag)
        }

        if (supportFragmentManager.findFragmentByTag(fragment.tag) == null) {
            add(frameID, fragment, fragment.tag)
        }
    }
}

fun AppCompatActivity.icReplaceFragment(frameID: Int, fragment: Fragment) {
    supportFragmentManager.inTransaction {
        setCustomAnimations(R.anim.right_to_left_enter, R.anim.right_to_left_exit, R.anim.left_to_right_pop_enter, R.anim.left_to_right_pop_exit)
        replace(frameID, fragment, fragment.tag)
    }
}

fun AppCompatActivity.icReplaceFragment(frameID: Int, fragment: Fragment, isAnimation: Boolean) {
    icReplaceFragment(frameID, fragment, isAnimation, isAddToBackStack = true)
}

fun AppCompatActivity.icReplaceFragment(frameID: Int, fragment: Fragment, isAnimation: Boolean, isAddToBackStack: Boolean) {
    supportFragmentManager.inTransaction {
        if (isAnimation) {
            setCustomAnimations(R.anim.right_to_left_enter, R.anim.right_to_left_exit, R.anim.left_to_right_pop_enter, R.anim.left_to_right_pop_exit)
        }

        if (isAddToBackStack) {
            addToBackStack(fragment.tag)
        }

        replace(frameID, fragment, fragment.tag)
    }
}

fun AppCompatActivity.icRemoveFragments(fragment: Fragment) {
    supportFragmentManager.inTransaction {
        remove(fragment)
    }
}

fun AppCompatActivity.icRemoveAllFragments() {
    for (fragment in supportFragmentManager.fragments) {
        supportFragmentManager.inTransaction {
            remove(fragment)
            supportFragmentManager.popBackStack()
        }
    }
}

fun AppCompatActivity.icRemoveOtherFragment(fragment: Fragment) {
    for (fragments in supportFragmentManager.fragments) {
        supportFragmentManager.inTransaction {
            if (fragments != fragment) {
                remove(fragment)
                supportFragmentManager.popBackStack()
            }
        }
    }
}

fun AppCompatActivity.icGetFragmentCount(): Int {
    return supportFragmentManager.fragments.size
}

private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
    val fragmentTransaction = beginTransaction()
    fragmentTransaction.func()
    fragmentTransaction.commitAllowingStateLoss()
}