package vn.icheck.android.chat.icheckchat.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import org.greenrobot.eventbus.EventBus

abstract class BaseFragmentChat<VB : ViewBinding>: Fragment() {

    protected abstract fun setBinding(inflater: LayoutInflater, container: ViewGroup?): VB
    protected abstract fun onInitView()

    lateinit var binding: VB

    open val getStatusBarHeight: Int
        get() {
            var result = 0
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

    open fun isRegisterEventBus(): Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.binding = this.setBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isRegisterEventBus()) {
            EventBus.getDefault().register(this)
        }

        onInitView()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRegisterEventBus()) {
            EventBus.getDefault().unregister(this)
        }
    }
}