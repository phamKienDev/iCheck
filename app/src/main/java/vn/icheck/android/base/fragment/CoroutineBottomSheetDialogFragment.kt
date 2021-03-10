package vn.icheck.android.base.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

open class CoroutineBottomSheetDialogFragment: BottomSheetDialogFragment() {
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

    inline fun <T> LiveData<T>.observe(crossinline action: () -> Unit) {
       this.observe(viewLifecycleOwner, {
           action()
       })
    }
}