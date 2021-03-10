package vn.icheck.android.component.image_assets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_image_assets.view.*
import vn.icheck.android.R
import vn.icheck.android.component.header_page.bottom_sheet_header_page.IListReportView
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICMedia
import vn.icheck.android.network.models.ICMediaPage
import vn.icheck.android.util.kotlin.WidgetUtils

class ImageAssetsAdapter(private val listData: MutableList<ICMediaPage>,val view: IListReportView) : RecyclerView.Adapter<ImageAssetsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_image_assets, parent, false))
    }

    override fun getItemCount(): Int = if (listData.size > 7) 7 else listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            view.onClickImage(item)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ICMediaPage) {
            WidgetUtils.loadImageUrlRoundedCenterCrop(itemView.imageView, item.content, R.drawable.ic_default_square, SizeHelper.size4)
        }
    }
}