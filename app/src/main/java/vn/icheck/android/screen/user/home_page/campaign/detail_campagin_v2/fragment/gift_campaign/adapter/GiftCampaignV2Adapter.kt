package vn.icheck.android.screen.user.home_page.campaign.detail_campagin_v2.fragment.gift_campaign.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_gift_campain_all.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.network.models.ICCampaign
import vn.icheck.android.screen.user.campaign.calback.IMessageListener
import vn.icheck.android.screen.user.home_page.campaign.detail_campagin_v2.fragment.gift_campaign.CampaignModel
import vn.icheck.android.screen.user.home_page.campaign.detail_campagin_v2.fragment.gift_campaign.GiftCampaignV2Fragment
import vn.icheck.android.screen.user.page_details.fragment.page.widget.message.MessageHolder
import vn.icheck.android.ui.layout.CustomGridLayoutManager

class GiftCampaignV2Adapter(val listener: IMessageListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val listData = mutableListOf<CampaignModel>()

    val typeProduct = 1
    val typeiCoin = 2
    val typeMorale = 3

    var addProduct = 0
    var addiCoin = 0
    var addMorale = 0

    private var mMessageError: String? = null
    private var iconMessage = R.drawable.ic_error_request

    fun setData(obj: CampaignModel) {
        addProduct = 1

        if (listData.isNotEmpty()) {
            listData.add(0, obj)
        } else {
            listData.add(obj)
        }
        notifyDataSetChanged()
    }

    fun addiCoin(obj: CampaignModel) {
        addiCoin = 1

        listData.add(addProduct, obj)
        notifyItemInserted(addProduct)
    }

    fun addMorale(obj: CampaignModel) {
        addMorale = 1

        listData.add(addProduct + addiCoin, obj)
        notifyItemInserted(addProduct + addiCoin)
    }

    fun resetData() {
        addProduct = 0
        addiCoin = 0
        addMorale = 0

        listData.clear()
        notifyDataSetChanged()
    }

    fun setError(icon: Int, error: String) {
        iconMessage = icon
        mMessageError = error

        listData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            typeProduct -> GiftProductHolder(parent)
            typeiCoin -> GiftiCoinHolder(parent)
            typeMorale -> GiftMoraleHolder(parent)
            else -> MessageHolder(parent)
        }
    }

    override fun getItemCount(): Int {
        return if (listData.isEmpty()) {
            if (mMessageError != null) {
                1
            } else {
                0
            }
        } else {
            listData.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData.isNotEmpty()) {
            listData[position].type
        } else {
            if (mMessageError != null) {
                ICViewTypes.MESSAGE_TYPE
            } else {
                return super.getItemViewType(position)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GiftProductHolder -> {
                listData[position].listData?.let {
                    holder.bind(it)
                }
            }
            is GiftiCoinHolder -> {
                listData[position].listData?.let {
                    holder.bind(it)
                }
            }
            is GiftMoraleHolder -> {
                listData[position].listData?.let {
                    holder.bind(it)
                }
            }
            is MessageHolder -> {
                if (mMessageError.isNullOrEmpty()) {
                    holder.bind(iconMessage, "")
                } else {
                    holder.bind(iconMessage, mMessageError!!)
                }

                holder.listener(View.OnClickListener {
                    listener.onMessageClicked()
                })
            }
        }
    }

    inner class GiftProductHolder(parent: ViewGroup) : BaseViewHolder<MutableList<ICCampaign>>(LayoutInflater.from(parent.context).inflate(R.layout.item_gift_campain_all, parent, false)) {
        override fun bind(obj: MutableList<ICCampaign>) {
            itemView.tvTitle.text = "Quà hiện vật"
            itemView.recyclerView.layoutManager = CustomGridLayoutManager(itemView.context, 2)
            itemView.recyclerView.adapter = GiftCampaignAllAdapter(obj, 1)
        }
    }

    inner class GiftiCoinHolder(parent: ViewGroup) : BaseViewHolder<MutableList<ICCampaign>>(LayoutInflater.from(parent.context).inflate(R.layout.item_gift_campain_all, parent, false)) {
        override fun bind(obj: MutableList<ICCampaign>) {
            itemView.tvTitle.text = "Quà thưởng xu"
            itemView.recyclerView.layoutManager = CustomGridLayoutManager(itemView.context, 2)
            itemView.recyclerView.adapter = GiftCampaignAllAdapter(obj, 2)
        }
    }

    inner class GiftMoraleHolder(parent: ViewGroup) : BaseViewHolder<MutableList<ICCampaign>>(LayoutInflater.from(parent.context).inflate(R.layout.item_gift_campain_all, parent, false)) {
        override fun bind(obj: MutableList<ICCampaign>) {
            itemView.tvTitle.text = "Quà tinh thần"
            itemView.recyclerView.layoutManager = CustomGridLayoutManager(itemView.context, 2)
            itemView.recyclerView.adapter = GiftCampaignAllAdapter(obj, 3)
        }
    }
}