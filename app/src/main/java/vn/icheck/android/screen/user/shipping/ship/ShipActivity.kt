package vn.icheck.android.screen.user.shipping.ship

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.campaign.DetailRewardResponse
import vn.icheck.android.screen.user.shipping.ship.ui.main.AddShipAddressFragment
import vn.icheck.android.screen.user.shipping.ship.ui.main.CartFragment
import vn.icheck.android.screen.user.shipping.ship.ui.confirmshipfragment.ConfirmShipFragment
import vn.icheck.android.screen.user.shipping.ship.ui.main.SelectShipAddressFragment
import vn.icheck.android.screen.user.shipping.ship.adpter.vm.ShipViewModel

@AndroidEntryPoint
class ShipActivity : BaseActivityMVVM() {

    companion object {
        const val CART = 1000
        fun startForResult(fragmentActivity: FragmentActivity, onSuccess: ((ActivityResult) -> Unit)? = null) {
//            fragmentActivity.launchActivityForResult(Intent(fragmentActivity, ShipActivity::class.java).apply {
//                putExtra(Constant.CART, true)
//            }, onSuccess)
            fragmentActivity.startActivity(Intent(fragmentActivity, ShipActivity::class.java).apply {
                putExtra(Constant.CART, true)
            })
        }

        fun startDetailOrder(context: Context, orderId: Long) {
            context.startActivity(Intent(context, ShipActivity::class.java).apply {
                putExtra("detail_order", orderId)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
        }

        fun startDetailOrder(fragmentActivity: FragmentActivity, orderId: Long) {
            fragmentActivity.startActivity(Intent(fragmentActivity, ShipActivity::class.java).apply {
                putExtra("detail_order", orderId)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
        }

    }


    private val viewModel: ShipViewModel by viewModels()
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.getIntExtra("add_addr", 0) != 0) {
                viewModel.removeUpdate()
                viewModel.moveToCreate()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ship_activity)
        if (intent.getBooleanExtra(Constant.CART, false)) {
            viewModel.moveToCart()
        } else {
            if (intent.getLongExtra("detail_order", 0L) != 0L) {
                viewModel.detailOrderId = intent.getLongExtra("detail_order", 0L)
                viewModel.moveToConfirm()
            } else {
                viewModel.moveToChoose()
            }
        }
        registerReceiver(broadcastReceiver, IntentFilter("add_addr"))
        viewModel.currentFragmentPosLiveData.observe(this, Observer { pos ->
            when (pos) {
                1 -> {
                    moveToCart()
                }
                2 -> {
                    moveToChooseAddr()
                }
                3 -> {
                    moveToCreateOrUpdateAddr()
                }
                4 -> {
                    moveToConfirm()
                }
            }
        })
        viewModel.detailRewardResponse = intent.getParcelableExtra<DetailRewardResponse>("gift")
    }

    fun moveToCart() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, CartFragment())
                .commitNow()
    }

    private fun moveToConfirm() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, ConfirmShipFragment.newInstance())
                .commitNow()
    }

    private fun moveToCreateOrUpdateAddr() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, AddShipAddressFragment.newInstance())
                .commitNow()
    }

    private fun moveToChooseAddr() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, SelectShipAddressFragment.newInstance())
                .commitNow()
    }

    override fun onBackPressed() {
        if (intent.getLongExtra("detail_order", 0L) != 0L || viewModel.detailRewardResponse != null) {
            finish()
        } else {
            when (viewModel.currentPos) {
                2 -> {
                    moveToCart()
                    viewModel.currentPos = 1
                }
                3 -> {
                    moveToChooseAddr()
                    viewModel.currentPos = 2
                }
                4 -> {
                    moveToChooseAddr()
                    viewModel.currentPos = 2
                }
                else -> {
                    super.onBackPressed()
                }
            }
        }
    }

}