package vn.icheck.android.screen.user.search_home.main

import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_list_search_onload_v2.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.adapter.RecyclerViewSearchAdapter
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.callback.IRecyclerViewSearchCallback
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.TextColorHelper
import vn.icheck.android.screen.user.search_home.result.SearchResultActivity
import vn.icheck.android.util.kotlin.ActivityUtils

class SearchHomeAdapter(val callback: IRecyclerViewSearchCallback) : RecyclerViewSearchAdapter<String>(callback) {

    override fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return KeySearchHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is KeySearchHolder -> {
                holder.bind(listData[position])
            }
            else -> {
                super.onBindViewHolder(holder, position)
            }
        }
    }


    inner class KeySearchHolder(parent: ViewGroup) : BaseViewHolder<String>(LayoutInflater.from(parent.context).inflate(R.layout.item_list_search_onload_v2, parent, false)) {
        override fun bind(obj: String) {
            if (adapterPosition < listData.size - 1) {
                itemView.tv_key.text = obj
                itemView.tv_key.setTextColor(TextColorHelper.getColorNormalText(itemView.context))
                itemView.tv_key.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search_gray_16, 0, 0, 0)
            } else {
                itemView.tv_key.text = Html.fromHtml(itemView.context.getString(R.string.xem_ket_qua_tim_kiem_cho_x, obj))
                itemView.tv_key.setTextColor(vn.icheck.android.ichecklibs.Constant.getPrimaryColor(itemView.context))
                itemView.tv_key.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }

            itemView.tv_key.setOnClickListener {
                ICheckApplication.currentActivity()?.let {
                    ActivityUtils.startActivity<SearchResultActivity>(it, Constant.DATA_1, obj)
                }
            }
        }
    }
}