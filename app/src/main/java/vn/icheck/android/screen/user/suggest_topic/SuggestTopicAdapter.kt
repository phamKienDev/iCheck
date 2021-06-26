package vn.icheck.android.screen.user.suggest_topic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_suggest_topic.view.*
import vn.icheck.android.R
import vn.icheck.android.base.adapter.RecyclerViewAdapter
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.ICSuggestTopic
import vn.icheck.android.util.kotlin.WidgetUtils

class SuggestTopicAdapter(val callback: ISuggestTopicView) : RecyclerViewAdapter<ICSuggestTopic>(callback) {
    val listSelected = mutableListOf<ICSuggestTopic>()
    override fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        if (holder is ViewHolder) {
            holder.bind(listData[position])
        }
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICSuggestTopic>(LayoutInflater.from(parent.context).inflate(R.layout.item_suggest_topic, parent, false)) {
        override fun bind(obj: ICSuggestTopic) {
            itemView.bg_selected.background=ViewHelper.bgOutlineAccentGreen3Corners4(itemView.context)
            WidgetUtils.loadImageUrlRounded(itemView.img_topic, obj.avatar, R.drawable.img_default_loading_icheck, SizeHelper.size6)

            itemView.tv_name_topic.text = if (obj.name.isNullOrEmpty()) {
                itemView.context.getString(R.string.dang_cap_nhat)
            } else {
                obj.name
            }

            if (obj.selected) {
                itemView.bg_selected.visibility = View.VISIBLE
                itemView.img_selected.visibility = View.VISIBLE
            } else {
                itemView.bg_selected.visibility = View.INVISIBLE
                itemView.img_selected.visibility = View.INVISIBLE
            }

            itemView.setOnClickListener {
                if (!obj.selected) {
                    itemView.bg_selected.visibility = View.VISIBLE
                    itemView.img_selected.visibility = View.VISIBLE

                    listSelected.add(obj)
                } else {
                    itemView.bg_selected.visibility = View.INVISIBLE
                    itemView.img_selected.visibility = View.INVISIBLE

                    listSelected.remove(obj)
                }
                obj.selected = !obj.selected
                callback.onGetListTopicSelected(listSelected)
            }
        }
    }
}