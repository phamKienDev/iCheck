package vn.icheck.android.screen.user.shipping.ship.holder

import android.content.Intent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.databinding.HolderAddShipAddressBinding
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.ViewHelper.fillDrawableStartText
import vn.icheck.android.util.ick.getLayoutInflater

class AddShipAddressHolder(val binding:HolderAddShipAddressBinding):RecyclerView.ViewHolder(binding.root) {

    init {
        binding.tvAddAddress.fillDrawableStartText(R.drawable.ic_add_blue_24px)
        binding.root.setOnClickListener {
           it.context.sendBroadcast(Intent("add_addr").apply {
               putExtra("add_addr", 1)
           })
        }
    }

    companion object{
        fun create(parent: ViewGroup) :AddShipAddressHolder{
            val lf = parent.getLayoutInflater()
            return AddShipAddressHolder(HolderAddShipAddressBinding.inflate(lf, parent, false))
        }
    }
}