package vn.icheck.android.component.tendency.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.component.tendency.holder.MessageShortHolder
import vn.icheck.android.component.tendency.holder.TopTendencyProductHolder
import vn.icheck.android.network.models.ICProduct
import vn.icheck.android.network.models.ICProductTrend

class TopTendencyProductAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val listData = mutableListOf<ICProductTrend>()

    private val typeItem = 1
    private val typeMessage = 2

    var errorMessage: String? = null

    fun setData(obj: MutableList<ICProductTrend>) {
        listData.clear()

        listData.addAll(obj)
        notifyDataSetChanged()
    }

    fun setError(error: String) {
        listData.clear()

        errorMessage = error
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            typeItem -> TopTendencyProductHolder(parent)
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
            is TopTendencyProductHolder -> listData[position].let {
                holder.bind(it)
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