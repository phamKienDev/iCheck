package vn.icheck.android.screen.user.product_of_shop_history

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_error_history_topup.view.*
import kotlinx.android.synthetic.main.item_load_more.view.*
import kotlinx.android.synthetic.main.item_product_of_shop_history.view.*
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.history.ICProductOfShopHistory
import vn.icheck.android.util.kotlin.WidgetUtils

class ProductOfShopHistoryAdapter constructor(val view: ProductOfShopHistoryView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listData = mutableListOf<ICProductOfShopHistory>()

    private var errorCode = 0
    private var isLoadMore = true
    private var isLoading = true

    private val itemType = 1
    private val showType = 2
    private val loadType = 3

    fun setListData(list: MutableList<ICProductOfShopHistory>) {
        isLoadMore = list.size >= Constant.DEFAULT_ITEM_COUNT
        isLoading = false
        errorCode = 0
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addListData(list: MutableList<ICProductOfShopHistory>) {
        isLoadMore = list.size >= Constant.DEFAULT_ITEM_COUNT
        isLoading = false
        errorCode = 0
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun clearListData() {
        errorCode = 0
        isLoadMore = false
        isLoading = false
        listData.clear()
        notifyDataSetChanged()
    }

    fun removeDataWithoutUpdate() {
        listData.clear()
    }

    fun showLoading() {
        listData.clear()
        errorCode = 0
        isLoadMore = true
        notifyDataSetChanged()
    }

    fun setErrorCode(error: Int) {
        listData.clear()
        errorCode = error
        isLoadMore = false
        notifyDataSetChanged()
    }

    fun disableLoadMore() {
        isLoading = true
        notifyItemChanged(0, itemCount)
    }

    val isListNotEmpty: Boolean
        get() {
            return listData.isNotEmpty()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            itemType -> ViewHolder(inflater.inflate(R.layout.item_product_of_shop_history, parent, false))
            loadType -> LoadHolder(inflater.inflate(R.layout.item_load_more, parent, false))
            else -> MessageHolder(inflater.inflate(R.layout.item_error_history_topup, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return if (listData.size > 0) {
            if (isLoadMore) listData.size + 1
            else listData.size
        } else {
            if (errorCode == 0) 0
            else 1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData.size > 0) {
            if (position < listData.size) itemType
            else loadType
        } else {
            if (isLoadMore) loadType
            else showType
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

    @SuppressLint("SetTextI18n")
    class ViewHolder constructor(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: ICProductOfShopHistory) {
            WidgetUtils.loadImageUrl(itemView.imgAvatar, item.image, R.drawable.img_error_ads_product, R.drawable.img_error_ads_product)

            if (item.verified == true) {
                if (!item.name.isNullOrEmpty()) {
                    itemView.tvNameShop.text = item.name
                    itemView.tvNameShop.typeface = ResourcesCompat.getFont(itemView.context, R.font.barlow_regular)
                    itemView.tvNameShop.setTextColor(vn.icheck.android.ichecklibs.ColorManager.getNormalTextColor(itemView.context))
                    itemView.tvNameShop.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home_verify_16dp, 0, 0, 0)
                    itemView.tvNameShop.compoundDrawablePadding = SizeHelper.size5
                } else {
                    itemView.tvNameShop.text = itemView.context.getString(R.string.ten_dang_cap_nhat)
                    itemView.tvNameShop.typeface = ResourcesCompat.getFont(itemView.context, R.font.barlow_semi_bold_italic)
                    itemView.tvNameShop.setTextColor(vn.icheck.android.ichecklibs.ColorManager.getDisableTextColor(itemView.context))
                    itemView.tvNameShop.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home_verify_16dp, 0, 0, 0)
                    itemView.tvNameShop.compoundDrawablePadding = SizeHelper.size5
                }
            } else {
                if (!item.name.isNullOrEmpty()) {
                    itemView.tvNameShop.text = item.name
                    itemView.tvNameShop.typeface = ResourcesCompat.getFont(itemView.context, R.font.barlow_regular)
                    itemView.tvNameShop.setTextColor(vn.icheck.android.ichecklibs.ColorManager.getNormalTextColor(itemView.context))
                    itemView.tvNameShop.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                } else {
                    itemView.tvNameShop.text = itemView.context.getString(R.string.ten_dang_cap_nhat)
                    itemView.tvNameShop.typeface = ResourcesCompat.getFont(itemView.context, R.font.barlow_semi_bold_italic)
                    itemView.tvNameShop.setTextColor(vn.icheck.android.ichecklibs.ColorManager.getNormalTextColor(itemView.context))
                    itemView.tvNameShop.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            }

            if (item.rating != null && item.rating != 0f) {
                itemView.tvCountRating.text = String.format("%.1f", item.rating!! * 2)
            } else {
                itemView.tvCountRating.text = "0"
            }

            itemView.tvBarcodeProduct.text = item.barcode

            if (item.reviewCount != null){
                if (item.reviewCount!! < 1000 ){
                    itemView.tvCountReview.text = "(${item.reviewCount})"
                } else {
                    itemView.tvCountReview.setText(R.string.count_999)
                }
            }
        }
    }

    class LoadHolder constructor(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            view.progressBar.indeterminateDrawable.setColorFilter(vn.icheck.android.ichecklibs.ColorManager.getPrimaryColor(view.context), android.graphics.PorterDuff.Mode.MULTIPLY)
        }
    }

    class MessageHolder constructor(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(errorCode: Int) {
            itemView.txtMessage2.visibility = View.VISIBLE
            when (errorCode) {
                Constant.ERROR_EMPTY -> {
                    itemView.imgIcon.setImageResource(R.drawable.ic_error_emty_history_topup)
                    itemView.txtMessage2.setText(R.string.hien_tai_khong_co_cua_hang_nao_gan_ban)
                }

                Constant.ERROR_SERVER -> {
                    itemView.imgIcon.setImageResource(R.drawable.ic_error_request)
                    itemView.txtMessage2.text = itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                }

                Constant.ERROR_INTERNET -> {
                    itemView.imgIcon.setImageResource(R.drawable.ic_error_network)
                    itemView.txtMessage2.text = itemView.context.getString(R.string.ket_noi_mang_cua_ban_co_van_de_vui_long_thu_lai)
                }
            }
        }
    }

}