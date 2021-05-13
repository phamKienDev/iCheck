package vn.icheck.android.screen.user.page_details.fragment.product

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.`null`.NullHolder
import vn.icheck.android.component.product.horizontal_product.ListProductHorizontalHolder
import vn.icheck.android.component.product.related_product.RelatedProductHolder
import vn.icheck.android.component.product.related_product.RelatedProductModel
import vn.icheck.android.screen.user.campaign.holder.base.LongMessageHolder

class ProductOfPageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val listData = mutableListOf<RelatedProductModel>()

    private var iconMessage = R.drawable.ic_error_request
    private var errorMessage: String? = null

    var addHorizontal = 0
    var addVertical = 0

    fun addHorizontalProduct(obj: RelatedProductModel) {
        addHorizontal = 1
        errorMessage = null

        if (listData.isNullOrEmpty()) {
            listData.add(obj)
        } else {
            listData.add(0, obj)
        }
        notifyDataSetChanged()
    }

    fun addVerticalProduct(obj: MutableList<RelatedProductModel>) {
        addVertical = obj.size
        errorMessage = null

        listData.addAll(addHorizontal, obj)
        notifyItemRangeChanged(addHorizontal, obj.size)
    }

    fun resetData() {
        addHorizontal = 0
        addVertical = 0
        errorMessage = null

        listData.clear()
        notifyDataSetChanged()
    }

    fun setError(icon: Int, error: String) {
        iconMessage = icon
        errorMessage = error

        listData.clear()
        notifyDataSetChanged()
    }

    fun isEmpty(): Boolean {
        return listData.isEmpty()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ICViewTypes.HIGLIGHT_PRODUCTS_PAGE -> ListProductHorizontalHolder(parent, null)
            ICViewTypes.CATEGORIES_PRODUCTS_PAGE -> RelatedProductHolder(parent, null)
            ICViewTypes.MESSAGE_TYPE -> LongMessageHolder(parent)
            else -> NullHolder(parent)
        }
    }

    override fun getItemCount(): Int {
        return if (listData.isNotEmpty()) {
            listData.size
        } else {
            if (errorMessage != null) {
                1
            } else {
                0
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (!errorMessage.isNullOrEmpty()) {
            ICViewTypes.MESSAGE_TYPE
        } else {
            listData[position].getViewType()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListProductHorizontalHolder -> {
                holder.bind(listData[position])
            }
            is RelatedProductHolder -> {
                holder.bind(listData[position])

                holder.itemView.setBackgroundColor(Color.WHITE)
            }
            is LongMessageHolder -> {
                if (errorMessage.isNullOrEmpty()) {
                    holder.bind(iconMessage, "", -1)
                } else {
                    holder.bind(iconMessage, errorMessage!!, -1)
                }

                holder.setListener(View.OnClickListener {
                    ProductOfPageFragment.INSTANCE?.getData()
                })
            }
        }
    }
}