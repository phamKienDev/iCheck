package vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.infor_campaign.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_emty_detail_campaign.view.*
import kotlinx.android.synthetic.main.item_rank_campaign.view.*
import vn.icheck.android.R

class UserRankAdapter(val context: Context?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val itemType = 1
    private val emptyType = 2
    private var errorCode = ""
    val listRank = mutableListOf<String>()

    fun setListData(list: MutableList<String>?) {
        listRank.clear()
        listRank.addAll(list!!)
        notifyDataSetChanged()
    }

    fun setError(error: String) {
        listRank.clear()
        errorCode = error
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemType -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_rank_campaign, parent, false))
            else -> ErrorHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_emty_detail_campaign, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return if (listRank.isNotEmpty()) {
            listRank.size
        } else
            1
    }

    override fun getItemViewType(position: Int): Int {
        return if (listRank.isNotEmpty())
            itemType
        else
            emptyType
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = listRank[position]
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
            when (item){
                "Hạng thành viên chuẩn" -> {
                    itemView.imgRank.setImageResource(R.drawable.ic_level_standard_60dp)
                    itemView.tvName.text = "Hạng Chuẩn"
                }
                "Hạng thành viên bạc" -> {
                    itemView.imgRank.setImageResource(R.drawable.ic_level_silver_60dp)
                    itemView.tvName.text = "Hạng Bạc"
                }
                "Hạng thành viên vàng" -> {
                    itemView.imgRank.setImageResource(R.drawable.ic_level_gold_60dp)
                    itemView.tvName.text = "Hạng Vàng"
                }
                "Hạng thành viên bạch kim" -> {
                    itemView.imgRank.setImageResource(R.drawable.ic_level_diamond_60dp)
                    itemView.tvName.text = "Hạng Kim Cương"
                }
            }
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