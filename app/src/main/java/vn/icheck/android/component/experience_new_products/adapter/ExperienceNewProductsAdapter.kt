package vn.icheck.android.component.experience_new_products.adapter

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.component.collection.vertical.AdsProductVerticalHolder
import vn.icheck.android.component.tendency.holder.MessageShortHolder
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.ICProduct

class ExperienceNewProductsAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val listData = mutableListOf<ICProduct>()

    private val typeItem = 1
    private val typeMessage = 2

    var errorMessage: String? = null

    fun setData(obj: MutableList<ICProduct>) {
        listData.clear()

        listData.addAll(obj)
        notifyDataSetChanged()
    }

    fun setError(error: String) {
        listData.clear()

        errorMessage = error
        notifyDataSetChanged()
    }

    fun isEmpty(): Boolean {
        return listData.isEmpty()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            typeItem -> AdsProductVerticalHolder(parent)
            else -> MessageShortHolder(parent, R.drawable.ic_default_product_empty)
        }
    }

    override fun getItemCount(): Int {
        return if (listData.isNullOrEmpty()) {
            if (errorMessage != null) {
                1
            } else {
                0
            }
        } else {
            listData.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData.isNullOrEmpty()) {
            if (errorMessage != null) {
                typeMessage
            } else {
                super.getItemViewType(position)
            }
        } else {
            typeItem
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AdsProductVerticalHolder -> {
                holder.bind(listData[position])

                holder.itemView.background = ViewHelper.bgWhiteStrokeLineColor0_1(holder.itemView.context)
            }
            is MessageShortHolder -> {
                if (errorMessage.isNullOrEmpty()) {
                    holder.bind("")
                } else {
                    holder.bind(errorMessage!!)
                }
            }
        }
    }
}