package vn.icheck.android.screen.user.shipping.ship.holder

import android.os.Build
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.databinding.HolderShipBinding
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.model.loyalty.ShipAddressResponse
import vn.icheck.android.util.ick.getLayoutInflater
import vn.icheck.android.util.ick.setCustomChecked
import vn.icheck.android.util.ick.simpleText

class ShipAddressHolder(val binding:HolderShipBinding):RecyclerView.ViewHolder(binding.root) {

    fun bind(shipAddressResponse: ShipAddressResponse, onChoose:(Long) -> Unit, onEdit:(Long) -> Unit) {
        setupView()

        binding.tvName simpleText shipAddressResponse.getName()
        val arr = arrayListOf<Char>()
        arr.addAll(shipAddressResponse.phone.toString().toList())
        if (arr.size > 7) {
            arr.add(7, ' ')
            arr.add(4, ' ')
        }
        binding.tvPhone simpleText arr.joinToString(separator = "")
        binding.tvAddress simpleText shipAddressResponse.getFullAddress()
        binding.imgEdit.setOnClickListener {
            onEdit(shipAddressResponse.id ?: 0L)
        }


        binding.addressRadio.setCustomChecked(shipAddressResponse.isChecked) { buttonView, isChecked ->
            onChoose(shipAddressResponse.id ?: 0L)
        }
        binding.tvName.setOnClickListener {
            onChoose(shipAddressResponse.id ?: 0L)
        }
        binding.tvPhone.setOnClickListener {
            onChoose(shipAddressResponse.id ?: 0L)
        }
        binding.tvAddress.setOnClickListener {
            onChoose(shipAddressResponse.id ?: 0L)
        }
    }

    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.addressRadio.buttonTintList = ViewHelper.createColorStateList(
                ContextCompat.getColor(itemView.context, R.color.grayB4), ColorManager.getPrimaryColor(itemView.context)
            )
        }
    }

    companion object{
        fun create(parent: ViewGroup) :ShipAddressHolder{
            val lf = parent.getLayoutInflater()
            return ShipAddressHolder(HolderShipBinding.inflate(lf, parent, false))
        }
    }
}