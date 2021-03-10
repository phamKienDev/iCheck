package vn.icheck.android.screen.user.search_home.shop

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.base.adapter.RecyclerViewSearchAdapter
import vn.icheck.android.callback.IRecyclerViewSearchCallback
import vn.icheck.android.network.models.ICPageQuery
import vn.icheck.android.network.models.ICShopQuery
import vn.icheck.android.screen.user.search_home.result.holder.ShopSearchHolder

class SearchShopAdapter(val callback: IRecyclerViewSearchCallback) : RecyclerViewSearchAdapter<ICShopQuery>(callback){
    override fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ShopSearchHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        if(holder is ShopSearchHolder){
           holder.bind(listData[position])
        }
    }
}