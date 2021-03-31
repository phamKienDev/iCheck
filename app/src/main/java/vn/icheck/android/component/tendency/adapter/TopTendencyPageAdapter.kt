package vn.icheck.android.component.tendency.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.component.tendency.holder.MessageShortHolder
import vn.icheck.android.component.tendency.holder.TopTendencyBusinessHolder
import vn.icheck.android.network.models.ICBusiness
import vn.icheck.android.network.models.ICPageTrend

class TopTendencyPageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val listData = mutableListOf<ICPageTrend>()
    private val typeItem = 1
    private val typeMessage = 2

    var errorMessage: String? = null

    fun setData(obj: MutableList<ICPageTrend>) {
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
            typeItem -> TopTendencyBusinessHolder(parent)
            else -> MessageShortHolder(parent, R.drawable.ic_business_v2)
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
            is TopTendencyBusinessHolder -> listData[position].let {
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