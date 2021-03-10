package vn.icheck.android.screen.user.listnotification.productnotice

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.base.adapter.RecyclerViewAdapter
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.network.models.ICNotification

class ListProductNoticeAdapter(listener: IRecyclerViewCallback) : RecyclerViewAdapter<ICNotification>(listener) {

    override fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder = ProductNoticeHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        if (holder is ProductNoticeHolder) {
            holder.bind(listData[position])

            holder.setOnRemoveListener(View.OnClickListener {
                listData.removeAt(position)
                notifyDataSetChanged()
            })
        }
    }
}