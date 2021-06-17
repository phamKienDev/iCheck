package vn.icheck.android.screen.user.createqrcode.createlocation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_search_location.view.*
import vn.icheck.android.R
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.ICPoints
import vn.icheck.android.screen.user.createqrcode.createlocation.view.ICreateLocationQrCodeView

class SearchLocationAdapter(private val listener: ICreateLocationQrCodeView) : RecyclerView.Adapter<SearchLocationAdapter.ViewHolder>() {
    private val listData = mutableListOf<ICPoints.Predictions>()

    fun setData(list: MutableList<ICPoints.Predictions>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun clearData() {
        listData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_search_location, parent, false))
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(result: ICPoints.Predictions) {
            itemView.rootView.background=ViewHelper.btnWhite(itemView.context)
            itemView.txtTitle.text = result.mainText
            itemView.txtContent.text = result.secondaryText

            itemView.setOnClickListener {
                listener.onLocationClicked(result)
            }
        }
    }


}