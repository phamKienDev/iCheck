package vn.icheck.android.base.holder

import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

open class CoroutineViewHolder(val view: View):RecyclerView.ViewHolder(view) {
    var job: Job? = null
    inline fun delayAction(crossinline action: () -> Unit, timeout:Long = 200) {
        job = if (job?.isActive == true) {
            job?.cancel()
            CoroutineScope(Dispatchers.Main).launch{
                delay(timeout)
                action()
            }
        } else {
            CoroutineScope(Dispatchers.Main).launch{
                delay(timeout)
                action()
            }
        }
    }
}