package vn.icheck.android.ichecklibs.util

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity

inline infix fun <reified T : FragmentActivity> Context.simpleStartActivity(activity: Class<T>) {
    val i = Intent(this, activity)
    this.startActivity(i)
}