package vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.infor_campaign.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_condition_campaign.view.*
import kotlinx.android.synthetic.main.item_emty_detail_campaign.view.*
import vn.icheck.android.R

class ConditionsAdapter(val context: Context?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val listCondition = mutableListOf<String>()

    private val itemType = -1
    private val emptyType = -2
    private var errorCode = ""

    fun setListData(list: MutableList<String>?) {
        listCondition.clear()
        listCondition.addAll(list!!)
        notifyDataSetChanged()
    }

    fun setError(error: String) {
        listCondition.clear()
        errorCode = error
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemType -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_condition_campaign, parent, false))
            else -> ErrorHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_emty_detail_campaign, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return if (listCondition.isNotEmpty()) {
            listCondition.size
        } else
            1
    }

    override fun getItemViewType(position: Int): Int {
        return if (listCondition.isNotEmpty())
            itemType
        else
            emptyType
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = listCondition[position]
                holder.setData(item)
            }

            is ErrorHolder -> {
                holder.setData(errorCode)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(item: String) {
            itemView.tvCondition.text = "- $item"
        }
    }

    class ErrorHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(errorCode: String) {
            itemView.txtMessage.text = if (errorCode.isNotEmpty())
                errorCode
            else
                "Chương trình đang cập nhật"
        }
    }

}