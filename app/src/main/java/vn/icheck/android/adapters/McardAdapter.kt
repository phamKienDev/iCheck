package vn.icheck.android.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.msp_holder.view.*
import vn.icheck.android.R
import vn.icheck.android.fragments.ecard.PurchaseCardFragment
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.network.models.topup.TopupServices
import vn.icheck.android.util.kotlin.WidgetUtils

class McardAdapter : RecyclerView.Adapter<McardAdapter.NetworkHolder>() {
    private val listData = mutableListOf<TopupServices.Service>()

    private var servicePosition = -1
    private var selectedPosition = -1

    fun setMCardData(list: MutableList<TopupServices.Service>) {
        servicePosition = -1
        selectedPosition = 0
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun setDenominationData(position: Int, list: MutableList<TopupServices.Service>) {
        servicePosition = position
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun setServicePosition(position: Int) {
        servicePosition = position
        selectedPosition = -1
        notifyDataSetChanged()
    }

    val getService: TopupServices.Service?
        get() {
            return if (servicePosition == -1) {
                listData[selectedPosition]
            } else {
                null
            }
        }

    val getSelectedPosition: Int
        get() {
            return selectedPosition
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NetworkHolder {
        return NetworkHolder(LayoutInflater.from(parent.context).inflate(R.layout.msp_holder, parent, false))
    }

    override fun getItemCount(): Int = if (servicePosition == -1) {
        listData.size
    } else {
        listData[servicePosition].denomination.size
    }

    override fun onBindViewHolder(holder: NetworkHolder, position: Int) {
        if (servicePosition == -1) {
            holder.bind(listData[position])
        } else {
            holder.bind(listData[servicePosition].denomination[position])
        }

        holder.setupListener()
    }

    inner class NetworkHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(obj: TopupServices.Service) {
            itemView.imgIcon.visibility = View.VISIBLE
            itemView.tvPrice.visibility = View.GONE
            WidgetUtils.loadImageFitCenterUrl(itemView.imgIcon, obj.avatar, R.drawable.ic_default_square)

            checkSelected()
        }

        fun bind(price: String) {
            itemView.imgIcon.visibility = View.GONE
            itemView.tvPrice.visibility = View.VISIBLE
            itemView.tvPrice.text = itemView.context.getString(R.string.xxx_d, TextHelper.formatMoney(price))

            if (selectedPosition != -1) {
                PurchaseCardFragment.INSTANCE?.changePrice(listData[servicePosition].denomination[selectedPosition])
            }

            checkSelected()
        }

        private fun checkSelected() {
            if (selectedPosition == adapterPosition) {
                itemView.layoutContent.setBackgroundResource(R.drawable.bg_choose_card_loyalty)
                itemView.tvPrice.setTextColor(Color.parseColor("#434343"))
            } else {
                itemView.layoutContent.setBackgroundResource(R.drawable.bg_default_card_loyalty)
                itemView.tvPrice.setTextColor(ColorManager.getDisableTextColor(itemView.context))
            }
        }

        fun setupListener() {
            itemView.setOnClickListener {
                if (selectedPosition != adapterPosition) {
                    selectedPosition = adapterPosition

                    if (servicePosition == -1) {
                        PurchaseCardFragment.INSTANCE?.changeMCard(selectedPosition)
                    } else {
                        PurchaseCardFragment.INSTANCE?.changePrice(listData[servicePosition].denomination[selectedPosition])
                    }

                    notifyDataSetChanged()
                }
            }
        }
    }
}