package vn.icheck.android.component.shopping_catalog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import vn.icheck.android.R
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.component.view.text_view.TextViewBarlowMedium
import vn.icheck.android.network.models.ICShoppingCatalog
import vn.icheck.android.util.kotlin.WidgetUtils

class ShoppingCatalogAdapter(private val listData: MutableList<ICShoppingCatalog>) : RecyclerView.Adapter<ShoppingCatalogAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        holder.bind(item)
    }

    class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(ViewHelper.createItemCatalogShopping(parent.context)) {
        fun bind(item: ICShoppingCatalog) {
            (itemView as ViewGroup).run {
                (getChildAt(0) as CircleImageView).run {
                    WidgetUtils.loadImageUrl(this, item.image)
                }

                (getChildAt(1) as TextViewBarlowMedium).run {
                    text = item.name
                }
            }
        }
    }
}