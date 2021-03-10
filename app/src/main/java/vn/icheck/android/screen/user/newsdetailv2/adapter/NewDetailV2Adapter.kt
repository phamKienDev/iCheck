package vn.icheck.android.screen.user.newsdetailv2.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.component.news.NewsListV2Holder
import vn.icheck.android.network.models.ICNews

class NewDetailV2Adapter(val listData: List<ICNews>) : RecyclerView.Adapter<NewsListV2Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsListV2Holder {
        return NewsListV2Holder.create(parent)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: NewsListV2Holder, position: Int) {
        holder.bind(listData[position])
    }
}