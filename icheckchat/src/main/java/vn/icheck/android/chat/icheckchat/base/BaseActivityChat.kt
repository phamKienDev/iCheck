package vn.icheck.android.chat.icheckchat.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.chat.icheckchat.R
import vn.icheck.android.chat.icheckchat.model.MCMessageEvent

abstract class BaseActivityChat<VB : ViewBinding> : FragmentActivity() {

    abstract val bindingInflater: (LayoutInflater) -> VB

    protected abstract fun onInitView()

    val binding by viewBinding(bindingInflater)

    private inline fun <T : ViewBinding> FragmentActivity.viewBinding(
            crossinline bindingInflater: (LayoutInflater) -> T) =
            lazy(LazyThreadSafetyMode.NONE) {
                bindingInflater.invoke(layoutInflater)
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        EventBus.getDefault().register(this)

        onInitView()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.none, R.anim.left_to_right_pop_exit)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(event: MCMessageEvent) {
        if (event.type == MCMessageEvent.Type.ON_FINISH_ALL_CHAT) {
            finish()
            overridePendingTransition(R.anim.none, R.anim.left_to_right_pop_exit)
        }
    }
}