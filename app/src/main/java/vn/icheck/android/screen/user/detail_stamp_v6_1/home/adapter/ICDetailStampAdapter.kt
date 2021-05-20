package vn.icheck.android.screen.user.detail_stamp_v6_1.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.base.adapter.RecyclerViewCustomAdapter
import vn.icheck.android.base.holder.ListStampECommerceHolder
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.`null`.NullHolder
import vn.icheck.android.component.product.enterprise.ICPageInfoHolder
import vn.icheck.android.component.product.infor.ProductInformationHolder
import vn.icheck.android.loyalty.model.ICKLoyalty
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.detail_stamp_v6_1.ICStampConfig
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.holder.*

class ICDetailStampAdapter(listener: IRecyclerViewCallback) : RecyclerViewCustomAdapter<ICLayout>(listener) {
    private var bannerClicked: ((obj: ICKLoyalty) -> Unit)? = null
    private var checkCode: ((obj: ICKLoyalty) -> Unit)? = null

    fun setCampaignListener(bannerClicked: (obj: ICKLoyalty) -> Unit, checkCode: (obj: ICKLoyalty) -> Unit) {
        this.bannerClicked = bannerClicked
        this.checkCode = checkCode
    }

    fun addCampaign(obj: ICLayout) {
        if (listData.isNotEmpty()) {
            if (listData[0].viewType == ICViewTypes.CAMPAIGN_TYPE) {
                listData.removeAt(0)
                listData.add(0, obj)
                notifyItemChanged(0)
            } else {
                listData.add(0, obj)
                notifyItemInserted(0)
            }
        }
    }

    fun removeCampaign() {
        if (listData.isNotEmpty()) {
            if (listData[0].viewType == ICViewTypes.CAMPAIGN_TYPE) {
                listData.removeAt(0)
                notifyItemRemoved(0)
            }
        }
    }

    override fun getItemType(position: Int): Int {
        return if (listData[position].data != null) {
            listData[position].viewType
        } else {
            ICViewTypes.NULL_HOLDER
        }
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ICViewTypes.PRODUCT_IMAGE_TYPE -> ICProductImageHolder(parent)
            ICViewTypes.PRODUCT_TYPE -> ICProductHolder(parent)
            ICViewTypes.MESSAGE_TYPE -> ICMessageResultHolder(parent)
            ICViewTypes.STAMP_INFO_TYPE -> ICStampInfoHolder(parent)
            ICViewTypes.SCAN_INFO_TYPE -> ICScanInfoHolder(parent)
            ICViewTypes.GUARANTEE_INFO_TYPE -> ICGuaranteeHolder(parent)
            ICViewTypes.LAST_GUARANTEE_INFO_TYPE -> ICLastGuaranteeHolder(parent)
            ICViewTypes.VENDOR_TYPE -> ICPageInfoHolder(parent)
            ICViewTypes.PRODUCT_INFO_TYPE -> ProductInformationHolder(parent)
            ICViewTypes.PRODUCT_ECCOMMERCE_TYPE -> ListStampECommerceHolder(parent)
            ICViewTypes.CAMPAIGN_TYPE -> ICCampaignV61Holder(parent)
            ICViewTypes.ERROR_STAMP_TYPE -> ICWrongStampHolder(parent)
            else -> NullHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ICProductImageHolder -> holder.bind(listData[position].data as List<ICMedia>)
            is ICProductHolder -> holder.bind(listData[position].data as ICWidgetData)
            is ICMessageResultHolder -> holder.bind(listData[position].data as ICWidgetData)
            is ICStampInfoHolder -> holder.bind(listData[position].data as String)
            is ICScanInfoHolder -> holder.bind(listData[position].data as ICWidgetData)
            is ICGuaranteeHolder -> holder.bind(listData[position].data as ICWidgetData)
            is ICLastGuaranteeHolder -> holder.bind(listData[position].data as ICWidgetData)
            is ICPageInfoHolder -> holder.bind(listData[position].data as ICWidgetData)
            is ProductInformationHolder -> holder.bind(listData[position].data as ICInfo)
            is ListStampECommerceHolder -> holder.bind(listData[position].data as MutableList<ICProductLink>)
            is ICCampaignV61Holder -> {
                holder.bind(listData[position].data as ICKLoyalty, {
                    bannerClicked?.invoke(it)
                }, {
                    checkCode?.invoke(it)
                })
            }
            is ICWrongStampHolder -> holder.bind(listData[position].data as ICStampConfig)
            else -> super.onBindViewHolder(holder, position)
        }
    }
}