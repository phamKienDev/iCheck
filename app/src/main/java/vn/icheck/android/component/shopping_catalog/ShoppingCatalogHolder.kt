package vn.icheck.android.component.shopping_catalog

import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.network.models.ICShoppingCatalog
import vn.icheck.android.ui.StartSnapHelper

class ShoppingCatalogHolder(parent: ViewGroup) : BaseViewHolder<MutableList<ICShoppingCatalog>>(ViewHelper.createShoppingCatalog(parent.context)) {
    override fun bind(obj: MutableList<ICShoppingCatalog>) {
        (itemView as ViewGroup).run {
            (getChildAt(2) as RecyclerView).run {
                onFlingListener = null
                layoutManager = GridLayoutManager(itemView.context.applicationContext, 2, GridLayoutManager.HORIZONTAL, false)
                val snapHelper = StartSnapHelper(2,false)
                snapHelper.attachToRecyclerView(this)
                adapter = ShoppingCatalogAdapter(obj)
            }
        }
    }
}