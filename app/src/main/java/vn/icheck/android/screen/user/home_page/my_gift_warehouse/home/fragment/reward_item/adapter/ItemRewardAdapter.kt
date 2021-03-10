package vn.icheck.android.screen.user.home_page.my_gift_warehouse.home.fragment.reward_item.adapter

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_item_reward.view.*
import kotlinx.android.synthetic.main.item_item_reward.view.imgGiftFromVendor
import kotlinx.android.synthetic.main.item_item_reward.view.tvTimeEndGifBox
import kotlinx.android.synthetic.main.item_item_reward.view.tvTitleGift
import kotlinx.android.synthetic.main.item_item_reward.view.tvVendor
import kotlinx.android.synthetic.main.item_load_more.view.*
import kotlinx.android.synthetic.main.layout_gift_message.view.*
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.ICItemReward
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.home.fragment.reward_item.view.IRewardItemView
import vn.icheck.android.util.kotlin.WidgetUtils

class ItemRewardAdapter constructor(val view: IRewardItemView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val listData = mutableListOf<ICItemReward>()

    private var errorCode = ""
    private var isLoadMore = false
    private var isLoading = false

    private val itemType = 1
    private val showType = 2
    private val loadType = 3

    fun setListData(list: MutableList<ICItemReward>) {
        isLoadMore = list.size >= Constant.DEFAULT_ITEM_COUNT
        isLoading = false
        errorCode = ""

        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addListData(list: MutableList<ICItemReward>) {
        isLoadMore = list.size >= Constant.DEFAULT_ITEM_COUNT
        isLoading = false
        errorCode = ""

        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun clearListData() {
        errorCode = ""
        isLoadMore = false
        isLoading = false
        listData.clear()
        notifyDataSetChanged()
    }

    fun showLoading() {
        listData.clear()
        errorCode = ""
        isLoadMore = true

        notifyDataSetChanged()
    }

    fun setErrorCode(error: String) {
        listData.clear()
        errorCode = error
        isLoadMore = false

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            itemType -> ViewHolder(inflater.inflate(R.layout.item_item_reward, parent, false))
            loadType -> LoadHolder(inflater.inflate(R.layout.item_load_more, parent, false))
            else -> MessageHolder(inflater.inflate(R.layout.layout_gift_message, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return if (listData.size > 0) {
            if (isLoadMore)
                listData.size + 1
            else
                listData.size
        } else {
            1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData.size > 0) {
            if (position < listData.size)
                itemType
            else
                loadType
        } else {
            if (isLoadMore)
                loadType
            else
                showType
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = listData[position]
                holder.bind(item)

                holder.itemView.setOnClickListener {
                    view.onClickItem(item)
                }
            }
            is LoadHolder -> {
                holder.bind()
                if (!isLoading) {
                    view.onLoadMore()
                    isLoading = true
                }
            }
            is MessageHolder -> {
                holder.bind(errorCode)

                holder.itemView.setOnClickListener {
                    view.onRefresh()
                }
            }
        }
    }

    class ViewHolder constructor(val view: View) : RecyclerView.ViewHolder(view) {
        @SuppressLint("SetTextI18n")
        fun bind(item: ICItemReward) {
            val image = item.image
            if (image != null && image.isNotEmpty()) {
                WidgetUtils.loadImageUrlRounded4(itemView.imgGiftFromVendor, image)
            } else {
                WidgetUtils.loadImageUrlRounded4(itemView.imgGiftFromVendor, null)
            }

            if (!item.title.isNullOrEmpty()) {
                itemView.tvTitleGift.text = item.title
            } else {
                itemView.tvTitleGift.text = itemView.context.getString(R.string.dang_cap_nhat)
            }

            if (!item.business_name.isNullOrEmpty()) {
                itemView.tvVendor.text = Html.fromHtml("<font color=#828282>Từ </font>" + "<b>" + item.business_name + "</b>")
            } else {
                itemView.tvVendor.text = itemView.context.getString(R.string.dang_cap_nhat)
            }

            if (!item.remain_time.isNullOrEmpty() && !item.remain_time.equals("0 ")) {
                itemView.tvTimeEndGifBox.text = "Còn " + item.remain_time
            } else {
                itemView.tvTimeEndGifBox.text = itemView.context.getString(R.string.da_het_han)
            }

            when (item.state) {
                // Chua nhan
                1 -> {
                    //chua nhan qua
                    if (item.rewardType == "reward") {
                        itemView.tvWaitAcceptGift.visibility = View.VISIBLE
                        itemView.tvUseGift.visibility = View.GONE
                        itemView.tvReceivedGift.visibility = View.GONE
                        itemView.tvRefuseGift.visibility = View.GONE
                    } else if (item.rewardType == "receive_store") {
                        itemView.tvWaitAcceptGift.visibility = View.VISIBLE
                        itemView.tvUseGift.visibility = View.GONE
                        itemView.tvReceivedGift.visibility = View.GONE
                        itemView.tvRefuseGift.visibility = View.GONE
                    } else {
                        itemView.tvUseGift.visibility = View.GONE
                        itemView.tvReceivedGift.visibility = View.GONE
                        itemView.tvRefuseGift.visibility = View.GONE
                        itemView.tvWaitAcceptGift.visibility = View.GONE
                    }
                }
                // Da ship
                2 -> {
                    // da nhan qua
                    itemView.tvReceivedGift.visibility = View.VISIBLE
                    itemView.tvWaitAcceptGift.visibility = View.GONE
                    itemView.tvUseGift.visibility = View.GONE
                    itemView.tvRefuseGift.visibility = View.GONE
                }
                // Tu choi
                3 -> {
                    // tu choi
                    itemView.tvRefuseGift.visibility = View.VISIBLE
                    itemView.tvReceivedGift.visibility = View.GONE
                    itemView.tvWaitAcceptGift.visibility = View.GONE
                    itemView.tvUseGift.visibility = View.GONE
                }
                // Chua xac nhan
                4 -> {
                    itemView.tvWaitAcceptGift.visibility = View.GONE
                    itemView.tvUseGift.visibility = View.GONE
                    itemView.tvReceivedGift.visibility = View.VISIBLE
                    itemView.tvRefuseGift.visibility = View.GONE
                }
            }
        }
    }

    class LoadHolder constructor(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            view.progressBar.indeterminateDrawable.setColorFilter(ContextCompat.getColor(itemView.context, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY)
        }
    }

    class MessageHolder constructor(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(errorCode: String) {
            itemView.txtMessage.text = if (errorCode.isNotEmpty())
                errorCode
            else
                itemView.context.getString(R.string.ban_chua_co_mon_qua_nao)
        }
    }

}