package vn.icheck.android.base.fragment

import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

open class CoroutineDialogFragment:DialogFragment() {
    var job: Job? = null

   inline fun delayAction(crossinline action: () -> Unit, timeout:Long = 200) {
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
}