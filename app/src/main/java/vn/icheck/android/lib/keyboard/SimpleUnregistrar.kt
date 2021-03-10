package vn.icheck.android.lib.keyboard

import android.app.Activity
import android.view.ViewTreeObserver
import java.lang.ref.WeakReference


class SimpleUnregistrar(activity: Activity, globalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener) : Unregistrar {
    private val mActivityWeakReference = WeakReference<Activity>(activity)

    private val mOnGlobalLayoutListenerWeakReference = WeakReference<ViewTreeObserver.OnGlobalLayoutListener>(globalLayoutListener)

    override fun unregister() {
        val activity = mActivityWeakReference.get()
        val globalLayoutListener = mOnGlobalLayoutListenerWeakReference.get()

        if (null != activity && null != globalLayoutListener) {
            val activityRoot = KeyboardVisibilityEvent.getActivityRoot(activity)
            activityRoot.viewTreeObserver
                    .removeOnGlobalLayoutListener(globalLayoutListener)
        }

        mActivityWeakReference.clear()
        mOnGlobalLayoutListenerWeakReference.clear()
    }
}