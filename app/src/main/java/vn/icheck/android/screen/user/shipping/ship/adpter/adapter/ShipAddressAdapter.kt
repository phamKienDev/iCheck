package vn.icheck.android.screen.user.shipping.ship.adpter.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.network.model.loyalty.ShipAddressResponse
import vn.icheck.android.screen.user.shipping.ship.holder.AddShipAddressHolder
import vn.icheck.android.screen.user.shipping.ship.holder.ShipAddressHolder

class ShipAddressAdapter(val listData: List<ShipAddressResponse?>, val onChoose:(Long) -> Unit, val onEdit:(Long) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val addAddress = 1
    private val address = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == address) ShipAddressHolder.create(parent)
        else AddShipAddressHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ShipAddressHolder -> {
                holder.bind(listData[position]!!, onChoose, onEdit)
            }
            is AddShipAddressHolder -> {

            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData[position] == null) {
            addAddress
        } else {
            address
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}