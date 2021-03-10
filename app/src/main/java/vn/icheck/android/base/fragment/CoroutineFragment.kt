package vn.icheck.android.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.icheck.android.util.ick.showSimpleErrorToast

open class CoroutineFragment: Fragment() {

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

    fun showError(msg: String?) {
        requireContext().showSimpleErrorToast(msg)
    }
}