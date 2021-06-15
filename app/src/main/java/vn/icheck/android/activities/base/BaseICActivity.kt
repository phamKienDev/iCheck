package vn.icheck.android.activities.base

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.CompositeDisposable
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.screen.account.home.AccountActivity
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.util.kotlin.ActivityUtils

abstract class BaseICActivity : AppCompatActivity() {

    val compositeDisposable = CompositeDisposable()
    var requiresLogin = true

    override fun onResume() {
        super.onResume()
        if (requiresLogin) {
            if (!SessionManager.isUserLogged) {
                finish()
                val it = Intent(this, AccountActivity::class.java)
                startActivity(it)
            }
        }
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.none, R.anim.left_to_right_pop_exit)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.none, R.anim.left_to_right_pop_exit)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    fun getRcv(@IdRes id: Int): RecyclerView {
        return findViewById(id)
    }
    fun getImg(@IdRes id: Int): ImageView {
        return findViewById(id)
    }
    fun getTv(@IdRes id: Int): TextView {
        return findViewById(id)
    }

    fun login() {
        val it = Intent(this, AccountActivity::class.java)
        startActivity(it)
    }



    fun isPackageInstalled(packageName: String): Boolean {
        val pm = packageManager
        return try {
            pm.getPackageGids(packageName)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun dial(phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse(getString(R.string.tel_phone_number, phoneNumber)))
            startActivity(intent)
        } catch (e: Exception) {
        }
    }

    fun email(address: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.type = getString(R.string.vnd_android_cursor_item_email)
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
            val mailer = Intent.createChooser(intent,  getString(R.string.send_email_using))
            startActivity(mailer)
        } catch (e: Exception) {
        }
    }

    fun web(address: String) {
        ActivityUtils.startActivity<WebViewActivity>(this, Constant.DATA_1, address)
    }
}