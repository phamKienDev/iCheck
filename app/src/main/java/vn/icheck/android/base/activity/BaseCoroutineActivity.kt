package vn.icheck.android.base.activity

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

open class BaseCoroutineActivity:AppCompatActivity() {
    var job:Job? = null

    inline fun delayAction(crossinline action: () -> Unit, timeout:Long = 200L) {
        job = if (job?.isActive == true) {
            job?.cancel()
            lifecycleScope.launch {
                delay(timeout)
                action()
            }
        } else {
            lifecycleScope.launch {
                delay(timeout)
                action()
            }
        }
    }

    inline fun delayAfterAction(crossinline action: () -> Unit, timeout:Long = 200L) {
        job = if (job?.isActive == true) {
            job?.cancel()
            lifecycleScope.launch {
                action()
                delay(timeout)
            }
        } else {
            lifecycleScope.launch {
                action()
                delay(timeout)
            }
        }
    }
}