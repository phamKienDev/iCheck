package vn.icheck.android.screen.user.mygift.fragment.reward_item_v2

import android.net.Uri
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_item_reward_v2.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.constant.Constant
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.helper.ActivityHelper
import vn.icheck.android.loyalty.screen.gift_detail_from_app.GiftDetailFromAppActivity
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.models.ICItemReward
import vn.icheck.android.screen.user.campaign.holder.base.LoadingHolder
import vn.icheck.android.screen.user.detail_my_reward.DetailMyRewardActivity
import vn.icheck.android.screen.user.page_details.fragment.page.widget.message.MessageHolder
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.util.ick.*
import vn.icheck.android.util.kotlin.ActivityUtils

class RewardItemV2Adapter(private val listenerRecyclerView: IRecyclerViewCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val listData = mutableListOf<ICItemReward>()

    private val itemType = 1
    private val itemLoadMore = 2

    private var mMessageError: String? = null
    private var iconMessage = R.drawable.ic_error_request
    private var isLoading = false
    private var isLoadMore = false

    private fun checkLoadMore(listCount: Int) {
        isLoadMore = listCount >= APIConstants.LIMIT
        isLoading = false
        mMessageError = ""
    }

    fun setError(error: String, icon: Int) {
        listData.clear()

        isLoadMore = false
        isLoading = false
        iconMessage = icon
        mMessageError = error
        notifyDataSetChanged()
    }

    fun isEmpty(): Boolean {
        return listData.isEmpty()
    }

    fun disableLoadMore() {
        isLoading = true
        notifyItemChanged(0, itemCount)
    }

    fun setData(obj: List<ICItemReward>) {
        checkLoadMore(obj.size)

        listData.clear()
        listData.addAll(obj)
        notifyDataSetChanged()
    }

    fun addData(obj: List<ICItemReward>) {
        checkLoadMore(obj.size)

        listData.addAll(obj)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemType -> ViewHolder(parent)
            itemLoadMore -> LoadingHolder(parent)
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
            if (isLoadMore) {
                listData.size + 1
            } else {
                listData.size
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData.isNotEmpty()) {
            if (position < listData.size) {
                itemType
            } else {
                itemLoadMore
            }
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
            is ViewHolder -> {
                holder.bind(listData[position])
            }
            is LoadingHolder -> {
                if (!isLoading) {
                    isLoading = true
                    listenerRecyclerView.onLoadMore()
                }
            }
            is MessageHolder -> {
                if (mMessageError.isNullOrEmpty()) {
                    holder.bind(iconMessage, "")
                } else {
                    holder.bind(iconMessage, mMessageError!!)
                }

                holder.listener(View.OnClickListener {
                    listenerRecyclerView.onMessageClicked()
                })
            }
        }
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICItemReward>(LayoutInflater.from(parent.context).inflate(R.layout.item_item_reward_v2, parent, false)) {
        override fun bind(obj: ICItemReward) {
            if (bindingAdapterPosition == 0 && obj.totalGifts != 0) {
                itemView.tv_total_gifts.beVisible()
                itemView.tv_total_gifts simpleText "Sản phẩm quà: ${obj.totalGifts}"
            } else {
                itemView.tv_total_gifts.beGone()
            }
            itemView.imgProduct.loadRoundedImage(obj.image, R.drawable.default_product_image, corner = 4)

            itemView.tvProduct.text = if (!obj.name.isNullOrEmpty()) {
                obj.name + if (obj.rewardType == "CODE") {
                    " - ${obj.code}"
                } else {
                    ""
                }
            } else {
                itemView.context.getString(R.string.dang_cap_nhat)
            }
            when (obj.state) {
                2 -> {
                    itemView.tv_gift_state simpleText "Đã xác nhận giao quà"
                }
                3 -> {
                    itemView.tv_gift_state simpleText "Bạn đã từ chối nhận quà này"
                }
                4 -> {
                    itemView.tv_gift_state simpleText "Giao quà thành công"
                }
                else -> {
                    itemView.tv_gift_state simpleText "Hết hạn"
                }
            }
            itemView.tvPage.text = if (!obj.businessName.isNullOrEmpty()) {
                Html.fromHtml("<font color=#757575>Từ </font>" + "<b>" + obj.businessName + "</b>")
            } else {
                itemView.context.getString(R.string.dang_cap_nhat)
            }

            when (obj.rewardType) {
                "PRODUCT_SHIP" -> {
                    itemView.tvState simpleText "Giao tận nơi"
                }
                "PRODUCT_IN_SHOP" -> {
                    itemView.tvState simpleText "Nhận tại cửa hàng"
                }
                "CARD" -> {
                    itemView.tvState simpleText "Quà thẻ cào"
                }
                "LUCKY" -> {
                    itemView.tvState simpleText "Quà tinh thần"
                }
                "CODE" -> {
                    itemView.tvState simpleText "Mã dự thưởng"
                }
                "VOUCHER" -> {
                    itemView.tvState simpleText "Voucher"
                }
                else -> {
                    itemView.tvState simpleText "Quà hiện vật"
                }
            }

            itemView.setOnClickListener {
                ICheckApplication.currentActivity()?.let { activity ->
                    if (obj.businessLoyalty == true) {
                        ActivityHelper.startActivity<GiftDetailFromAppActivity, Long>(activity, ConstantsLoyalty.DATA_1, (obj.id
                                ?: "-1").toLong())
                    } else {
                        if (obj.rewardType == "CODE") {
                            if (!obj.landingCode.isNullOrEmpty()) {

                                val url = Uri.parse(if (obj.landingCode!!.contains("https://", true) || obj.landingCode!!.contains("http://", true)) {
                                    obj.landingCode
                                } else {
                                    "https://${obj.landingCode}"
                                })
                                        .buildUpon()
                                        .appendQueryParameter("code", obj.code ?: "")
                                        .build()
                                WebViewActivity.start(activity, url.toString(), title = obj.name)
                            }
                        } else {
                            ActivityUtils.startActivity<DetailMyRewardActivity, String>(activity, Constant.DATA_1, obj.id
                                    ?: "")
                        }
                    }
                }
            }
        }
    }
}