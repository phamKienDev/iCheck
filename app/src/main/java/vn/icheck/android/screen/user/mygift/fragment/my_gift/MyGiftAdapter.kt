package vn.icheck.android.screen.user.mygift.fragment.my_gift

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_my_gift.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.models.ICItemReward
import vn.icheck.android.base.holder.LoadingHolder
import vn.icheck.android.screen.user.page_details.fragment.page.widget.message.MessageHolder

class MyGiftAdapter () : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val listData = mutableListOf<ICItemReward>()

    private val itemType = 1
    private val itemLoadMore = 2

    private var mMessageError: String? = null
    private var iconMessage = R.drawable.ic_default_gift
    private var isLoading = false
    private var isLoadMore = false

    private var listener: ItemClickListener<ICItemReward>? = null

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

                holder.itemView.setOnClickListener {
                    listener?.onItemClick(position,listData[position])
                }
            }
            is LoadingHolder -> {
                if (!isLoading) {
                    isLoading = true
                    MyGiftFragment.INSTANCE?.onLoadMore()
                }
            }
            is MessageHolder -> {
                if (mMessageError.isNullOrEmpty()) {
                    holder.bind(iconMessage, "Bạn chưa có quà tặng nào")
                } else {
                    holder.bind(iconMessage, mMessageError!!)
                }

                holder.listener(View.OnClickListener {
                    MyGiftFragment.INSTANCE?.getData()
                })
            }
        }
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICItemReward>(LayoutInflater.from(parent.context).inflate(R.layout.item_my_gift, parent, false)) {
        override fun bind(obj: ICItemReward) {

            itemView.tvProduct.text = if (!obj.campaignName.isNullOrEmpty()) {
                obj.campaignName
            } else {
                itemView.context.getString(R.string.dang_cap_nhat)
            }

            if (!obj.remainTime.isNullOrEmpty()) {
                itemView.tvDate.text = obj.remainTime
            } else {
                itemView.tvDate.visibility = View.INVISIBLE
            }

            itemView.tvPage.text = if (!obj.businessName.isNullOrEmpty()) {
                Html.fromHtml("<font color=${Constant.getSecondTextCode}>Từ </font>" + "<b>" + obj.businessName + "</b>")
            } else {
                itemView.context.getString(R.string.dang_cap_nhat)
            }

            itemView.tvCount.text = "x${obj.number}"
        }
    }

    fun setOnClickItemListener(listener: ItemClickListener<ICItemReward>) {
        this.listener = listener
    }
}