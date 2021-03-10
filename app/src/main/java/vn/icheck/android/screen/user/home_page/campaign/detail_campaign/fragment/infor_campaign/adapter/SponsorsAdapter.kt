package vn.icheck.android.screen.user.home_page.campaign.detail_campaign.fragment.infor_campaign.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_emty_detail_campaign.view.*
import kotlinx.android.synthetic.main.item_sponsors_campaign.view.*
import vn.icheck.android.R
import vn.icheck.android.network.models.ICDetail_Campaign
import vn.icheck.android.util.kotlin.WidgetUtils

class SponsorsAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val itemType = 1
    private val emptyType = 2
    private var errorCode = ""
    val listSponsor = mutableListOf<ICDetail_Campaign.ListSponsors>()

    fun setListData(list: MutableList<ICDetail_Campaign.ListSponsors>?) {
        listSponsor.clear()
        listSponsor.addAll(list!!)
        notifyDataSetChanged()
    }

    fun setError(error: String) {
        listSponsor.clear()
        errorCode = error
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemType -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_sponsors_campaign, parent, false))
            else -> ErrorHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_emty_detail_campaign, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return if (listSponsor.isNotEmpty()) {
            listSponsor.size
        } else
            1
    }

    override fun getItemViewType(position: Int): Int {
        return if (listSponsor.isNotEmpty())
            itemType
        else
            emptyType
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = listSponsor[position]
                holder.setData(item)

                holder.itemView.setOnClickListener {
//                    view.onCickSponsor(item)
                }
            }

            is ErrorHolder -> {
                holder.setData(errorCode)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(item: ICDetail_Campaign.ListSponsors) {
            val image = item.logo
            if (image != null && image.isNotEmpty()) {
                WidgetUtils.loadImageUrlRounded4(itemView.imgSponsors, image)
            } else {
                WidgetUtils.loadImageUrlRounded4(itemView.imgSponsors, null)
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