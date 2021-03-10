package vn.icheck.android.screen.user.search_home.page

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.base.adapter.RecyclerViewSearchAdapter
import vn.icheck.android.callback.IRecyclerViewSearchCallback
import vn.icheck.android.network.models.ICPageQuery
import vn.icheck.android.screen.user.search_home.result.holder.PageSearchHolder

class SearchPageAdapter(val callback:IRecyclerViewSearchCallback) : RecyclerViewSearchAdapter<ICPageQuery>(callback){
    override fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return PageSearchHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        if(holder is PageSearchHolder){
            holder.bind(listData[position])
        }
    }
}