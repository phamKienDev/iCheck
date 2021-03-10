package vn.icheck.android.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat

/**
 * Created by lecon on 11/26/2017
 */
object PermissionHelper {

    fun isAllowPermission(permission: String, context: Context?): Boolean {
        return context != null && ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun isAllowPermission(context: Context?, permission: String): Boolean {
        return context != null && ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun isAllowPermission(activity: Activity, permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun isAllowPermission(permission: Array<String>, activity: Activity): Boolean {

        for (i in permission.size - 1 downTo 0) {
            if (ActivityCompat.checkSelfPermission(activity, permission[i]) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }

        return true
    }

    fun isAllowPermission(context: Context?, permission: Array<String>): Boolean {
        if (context == null) {
            return false
        }

        for (i in permission.size - 1 downTo 0) {
            if (ActivityCompat.checkSelfPermission(context, permission[i]) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }

        return true
    }

    fun isAllowPermission(activity: Activity, permission: Array<String>): Boolean {

        for (i in permission.size - 1 downTo 0) {
            if (ActivityCompat.checkSelfPermission(activity, permission[i]) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }

        return true
    }

    fun checkPermission(activity: Activity, permission: Array<String>, REQUEST_CODE: Int): Boolean {
        var isAllow = true

        for (i in permission.size - 1 downTo 0) {
            if (ActivityCompat.checkSelfPermission(activity, permission[i]) != PackageManager.PERMISSION_GRANTED) {
                isAllow = false
                break
            }
        }

        if (!isAllow) {
            var isShould = true
            for (i in permission.size - 1 downTo 0) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission[i])) {
                    isShould = false
                    break
                }
            }
            // Should we showDialog an explanation?
            if (isShould) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(activity, permission, REQUEST_CODE)
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity, permission, REQUEST_CODE)
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return false
        }

        return true
    }

    fun checkPermission(permission: String, activity: Activity, REQUEST_CODE: Int): Boolean {
        if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(activity, arrayOf(permission), REQUEST_CODE)
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity, arrayOf(permission), REQUEST_CODE)
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return false
        }
        return true
    }

    fun checkPermission(activity: Activity, permission: String, REQUEST_CODE: Int): Boolean {
        if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            // Should we showDialog an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(activity, arrayOf(permission), REQUEST_CODE)
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity, arrayOf(permission), REQUEST_CODE)
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return false
        }
        return true
    }

    fun checkResult(grantResults: IntArray): Boolean {

        for (i in grantResults.indices.reversed()) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                return false
        }

        return true
    }

    fun openPermissionSetting(context: Context) {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivity(intent)
    }
}