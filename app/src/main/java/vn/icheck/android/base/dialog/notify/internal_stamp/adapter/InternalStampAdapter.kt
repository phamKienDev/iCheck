package vn.icheck.android.base.dialog.notify.internal_stamp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_internal_stamp.view.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.internal_stamp.view.IInternalStampView
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.models.ICSuggestApp

class InternalStampAdapter(val view: IInternalStampView) : RecyclerView.Adapter<InternalStampAdapter.ViewHolder>() {

    private var listData = mutableListOf<ICSuggestApp>()

    fun setListData(list: MutableList<ICSuggestApp>, code: String?) {
        listData.clear()
        if (code != null) {
            listData.add(0, ICSuggestApp(getString(R.string.xem_chi_tiet), code, null, "detail"))
        }
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun clearData() {
        listData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_internal_stamp, parent, false))
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            when(item.type){
                "detail" -> {view.onClickGoToDetail(item.target)}
                "phone" -> {view.onClickPhone(item.target)}
                "link" -> {view.onClickLink(item.target,item.content)}
                "email" -> {view.onClickEmail(item.target,item.content)}
                "sms" -> {view.onClickSms(item.target,item.content)}
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: ICSuggestApp) {
            itemView.tvTitle.text = item.title
        }
    }
}