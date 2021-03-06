package vn.icheck.android.screen.user.share_post_of_page.bottom_sheet_option_page

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_choose_page_share.view.*
import kotlinx.android.synthetic.main.item_service_shop_variant.view.*
import vn.icheck.android.R
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.network.model.ICNameId
import vn.icheck.android.network.model.ICNameValue
import vn.icheck.android.network.models.ICChoosePage
import vn.icheck.android.network.models.ICItemReward
import vn.icheck.android.network.models.ICPageUserManager
import vn.icheck.android.network.models.detail_stamp_v6_1.ICServiceShopVariant
import vn.icheck.android.util.kotlin.WidgetUtils

class OptionPageShareAdapter (val context: Context?) : RecyclerView.Adapter<OptionPageShareAdapter.ViewHolder>() {

    private val listData = mutableListOf<ICPageUserManager>()

    private var listener: ItemClickListener<ICPageUserManager>? = null

    fun setListData(list: MutableList<ICPageUserManager>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_choose_page_share,parent,false))
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            listener?.onItemClick(position,item)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ICPageUserManager) {
            WidgetUtils.loadImageUrl(itemView.imgLogoPage,item.avatar)
            itemView.tvNamePage.text = item.name
        }
    }

    fun setOnClickItemListener(listener: ItemClickListener<ICPageUserManager>) {
        this.listener = listener
    }
}