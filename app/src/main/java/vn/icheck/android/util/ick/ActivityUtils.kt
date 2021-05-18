package vn.icheck.android.util.ick

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import vn.icheck.android.BuildConfig
import vn.icheck.android.base.dialog.reward_login.RewardLoginCallback
import vn.icheck.android.base.dialog.reward_login.RewardLoginDialog
import vn.icheck.android.loyalty.helper.ActivityHelper
import vn.icheck.android.screen.account.icklogin.IckLoginActivity

inline infix fun <reified T : FragmentActivity> Context.simpleStartActivity(activity: Class<T>) {
    val i = Intent(this, activity)
    this.startActivity(i)
}

inline infix fun <reified T : FragmentActivity> Context.startClearTopActivity(activity: Class<T>) {
    val i = Intent(this, activity)
    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    this.startActivity(i)
}

inline infix fun <reified T : FragmentActivity> Context.startPreviousTopActivity(activity: Class<T>) {
    val i = Intent(this, activity)
    i.flags = Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
    this.startActivity(i)
}

inline infix fun <reified T : FragmentActivity> Context.startClearSingleTopActivity(activity: Class<T>) {
    val i = Intent(this, activity)
    i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
    this.startActivity(i)
}

inline fun <reified T : FragmentActivity> AppCompatActivity.simpleStartForResultActivity(activity: Class<T>, requestCode: Int) {
    val i = Intent(this, activity)
    i.putExtra("requestCode", requestCode)
    ActivityHelper.startActivityForResult(this, i, requestCode)
//    this.startActivityForResult(i, requestCode)
}

inline fun <reified T : FragmentActivity> FragmentActivity.simpleStartForResultActivity(activity: Class<T>, requestCode: Int) {
    val i = Intent(this, activity)
    i.putExtra("requestCode", requestCode)
    this.startActivityForResult(i, requestCode)
}

inline fun <reified T : FragmentActivity> AppCompatActivity.simpleStartForResultActivity(activity: Class<T>, key: String, value: String, requestCode: Int) {
    val i = Intent(this, activity)
    i.putExtra(key, value)
    this.startActivityForResult(i, requestCode)
}

infix fun FragmentActivity.hideKeyBoard(view: View?) {
    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(view?.windowToken, 0)
}

infix fun FragmentActivity.makeCall(number: String?) {
    startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null)))
}

infix fun FragmentActivity.openFb(link: String?) {
    val i = Intent(Intent.ACTION_VIEW)
    i.data = Uri.parse(link)
    val pageId = link?.substringAfter("fb://page/")
    val pm = packageManager
    val query = pm.queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY)
    if (query.size == 0) {
        val urlBrowser = "https://www.facebook.com/$pageId"
        i.data = Uri.parse(urlBrowser)
    }
    startActivity(i)
}

fun Activity.openAppInGooglePlay() {
    val appId = BuildConfig.APPLICATION_ID.replace(".develop", "")
    try {
        this.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appId")))
    } catch (anfe: ActivityNotFoundException) {
        this.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appId")))
    }
}

fun FragmentActivity.forceShowKeyboard() {
    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun FragmentActivity.forceShowKeyboard(edt: EditText) {
    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(edt, InputMethodManager.SHOW_FORCED)
}

fun FragmentActivity.forceHideKeyboard() {
    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.currentFocus?.getWindowToken(), 0)
}

fun FragmentActivity.forceHideKeyboard(view: View?) {
    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)
}

fun FragmentActivity.launchActivityForResult(launchIntent: Intent, onSuccess: (ActivityResult) -> Unit, onFail: ((ActivityResult) -> Unit)? = null) {
    this.registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {
        if (it.resultCode == Activity.RESULT_OK) {
            onSuccess(it)
        } else {
            onFail?.invoke(it)
        }
    }).launch(launchIntent)
}

fun FragmentActivity.getHeightDevice(): Int {
    return this.resources.displayMetrics.heightPixels
}

fun Activity.addKeyboardToggleListener(onKeyboardToggleAction: (shown: Boolean) -> Unit): KeyboardToggleListener? {
    val root = findViewById<View>(android.R.id.content)
    val listener = KeyboardToggleListener(root, onKeyboardToggleAction)
    return root?.viewTreeObserver?.run {
        addOnGlobalLayoutListener(listener)
        listener
    }
}

open class KeyboardToggleListener(
        private val root: View?,
        private val onKeyboardToggleAction: (shown: Boolean) -> Unit
) : ViewTreeObserver.OnGlobalLayoutListener {
    private var shown = false
    override fun onGlobalLayout() {
        root?.run {
            val heightDiff = rootView.height - height
            val keyboardShown = heightDiff > 200.toPx()
            if (shown != keyboardShown) {
                onKeyboardToggleAction.invoke(keyboardShown)
                shown = keyboardShown
            }
        }
    }
}

fun FragmentActivity.showLogin() {
    RewardLoginDialog.show(this.supportFragmentManager, object : RewardLoginCallback {
        override fun onLogin() {
            this@showLogin simpleStartActivity IckLoginActivity::class.java

        }

        override fun onRegister() {
            this@showLogin.simpleStartForResultActivity(IckLoginActivity::class.java, 1)

        }

        override fun onDismiss() {

        }
    })

}


